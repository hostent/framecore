package io.framecore.Web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.framecore.Frame.AsyncHelp;
import io.framecore.Frame.CallerContext;
import io.framecore.Frame.LangType;
import io.framecore.Frame.Log;
import io.framecore.Frame.Response;
import io.framecore.Saas.SaasHander;
import io.framecore.Tool.DataConverter;
import io.framecore.Tool.JsonHelp;
import io.framecore.Tool.Md5Help;
import io.framecore.Tool.PropertiesHelp;
import io.framecore.redis.CacheHelp;

//@WebFilter(filterName = "RestApiFilter", urlPatterns = "/api/*")
public class CloudApiFilter implements Filter {

	String pathRoot = "";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		pathRoot = this.getClass().getAnnotation(WebFilter.class).urlPatterns()[0].replace("*", "");
		
		
		try {
			isaccess=PropertiesHelp.getApplicationConf("cloud.auth.access");
			accessToken=PropertiesHelp.getApplicationConf("cloud.auth.accesstoken");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static String isaccess=""; 
	static String accessToken="";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		Response apiResponse = new Response();

		String callerID = ((HttpServletRequest) request).getHeader("CallerID");

		CallerContext.setCallerID(callerID);
		
		String saasHanderTag = ((HttpServletRequest) request).getHeader("SaasHanderTag");

		SaasHander.set(saasHanderTag);

		String json = getJson(request);
		if (json == null || json.isEmpty()) {
			apiResponse.setError("-90:传输数据错误,Json空");
			outPutResponse(response, apiResponse);
			return;
		}

		String method = ((HttpServletRequest) request).getRequestURI().replace(pathRoot, "");
		
		//如果是call back 就按call 逻辑跳转
		//TODO

		// log
		Log.apiLog.trace("method:" + method + ",json:" + json);
		try {
			ApiBeanStore apiStore = FillApiStore(method);

			if (apiStore == null) {
				apiResponse.setError("-100:找不到方法");
				outPutResponse(response, apiResponse);
				return;
			}

			ObjectMapper objectMapper = JsonHelp.getJack();

			ArrayList<Object> alObj = new ArrayList<Object>();

			ArrayList al = objectMapper.readValue(json, ArrayList.class);

			for (int i = 0; i < al.size(); i++) {

				Type cla = apiStore.getMethod().getGenericParameterTypes()[i];

				Object obj = null;

				if (LangType.isLangType(cla)) // 一般类型
				{
					obj = DataConverter.parse(cla, al.get(i));
				} else {
					String j = objectMapper.writeValueAsString(al.get(i));
					obj = JsonHelp.toObject(j, cla, null);

				}

				alObj.add(obj);
			}

			Object[] pars = alObj.toArray();
			
		
			if(isaccess!=null && isaccess.equals("true") && accessToken!=null && (!accessToken.isEmpty()))
			{
				String accessTokenIn = ((HttpServletRequest) request).getHeader("AccessToken");				
				if(!accessTokenIn.equals(accessToken))
				{
					apiResponse.setError("-104: check accessToken fail");
					outPutResponse(response, apiResponse);
					return;
				}
			}

			// usercheck
			if (apiStore.getBeanInstance() instanceof IAuth) {
				IAuth authBean = (IAuth) apiStore.getBeanInstance();
				boolean checkResult = authBean.userCheck(method, apiStore.getMethod().getName(), pars);
				if (!checkResult) {
					apiResponse.setError("-106:usercheck fail");
					outPutResponse(response, apiResponse);
					return;
				}
			}
			
			// run

			if (apiStore.getDistributedLockSecond() > 0) {
				String key = apiStore.getMethodKey() + "-" + Md5Help.toMD5(JsonHelp.toJson(pars));
				
				
				if (!CacheHelp.setNX(key, "1", apiStore.getDistributedLockSecond())) {
					apiResponse.setError("-107:Locked");
					outPutResponse(response, apiResponse);
					return;
				}
				if (apiStore.isSync()) {
					
					AsyncHelp.runAsync(() -> {
						try {
							apiStore.Invoke(pars);
						} catch (Exception e) {
							Log.logError(e, "method:" + method + ",json:" + json);
						} finally {
							CacheHelp.delete(key);
						}
					});
					
				} else {
					
					try {
						Object obj = apiStore.Invoke(pars);// 执行
						outPutResponse(response, obj);
					} catch (Exception e) {
						Log.logError(e);
					} finally {
						CacheHelp.delete(key);
					}

				}
				

			} else {
				if (apiStore.isSync()) {
					AsyncHelp.runAsync(() -> {
						try {
							apiStore.Invoke(pars);
						} catch (Exception e) {
							Log.logError(e, "method:" + method + ",json:" + json);
						}
					});
				} else {
					Object obj = apiStore.Invoke(pars);// 执行
					outPutResponse(response, obj);
				}
			}
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String siteTag ="";
			if(SaasHander.currentHander()!=null)
			{
				siteTag=SaasHander.currentHander().getSiteTag();
			}
			Log.logError(e,"method:" + method + ",json:" + json+",siteTag:"+siteTag);
			apiResponse.setError("-110:" + e.getMessage());
			outPutResponse(response, apiResponse);
		}

	}

	private void outPutResponse(ServletResponse response, Object apiResponse)
			throws JsonProcessingException, IOException {

		ObjectMapper objectMapper = JsonHelp.getJack();

		String json = objectMapper.writeValueAsString(apiResponse);

		// String json = JSON.toJSONString(apiResponse);

		// log
		// Log.apiLog.info(json);

		response.setContentType("application/json;charset=UTF-8");

		try (PrintWriter out = response.getWriter()) {

			out.write(json);

		}

		response.flushBuffer();
	}

	private ApiBeanStore FillApiStore(String method) {
	
		return ApiBeanStore.StoreList.get(method.toLowerCase());
 
	}

	private String getJson(ServletRequest request) throws IOException {
		int totalbytes = request.getContentLength();
		if (totalbytes <= 0) {
			return "";
		}

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder("");
		try {
			br = request.getReader();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void destroy() {

	}

}
