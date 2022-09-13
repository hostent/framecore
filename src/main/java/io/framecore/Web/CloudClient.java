package io.framecore.Web;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.framecore.Frame.CallerContext;
import io.framecore.Frame.Log;
import io.framecore.Frame.PageDataT;
import io.framecore.Frame.Result;
import io.framecore.Saas.SaasHander;
import io.framecore.Tool.JsonHelp;
import io.framecore.Tool.PropertiesHelp;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class  CloudClient{

	static final MediaType JSON_med = MediaType.parse("application/json;charset=utf-8");
	ClientConfig _config;
	
	public CloudClient(ClientConfig config)
	{
		_config = config;
	}
	
	private String getUrl(String methods)
	{
		return "http://"+_config.get_url()+methods;
	}
	
	static OkHttpClient httpClient = null;

	static ObjectMapper objectMapper = JsonHelp.getJack();
	
	public Object post(Type returnType,String methods,  Object[] args) throws IOException {
		
		if(_config==null || _config.get_url()==null || _config.get_url().isEmpty())
		{
			Log.logError(new Exception(String.format( "微服务(%s)地址为空",methods)));
			return null;
		}
		
		if(httpClient==null)
		{
			httpClient = new OkHttpClient.Builder()
					.connectTimeout(60, TimeUnit.SECONDS)
					.readTimeout(60, TimeUnit.SECONDS)
					.writeTimeout(60, TimeUnit.SECONDS)
					.retryOnConnectionFailure(false).build();
 
		}
		
		 
		
		
		String json="[]";
		if(args!=null)
		{
			json = objectMapper.writeValueAsString(args);
		}
		
		RequestBody requestBody = RequestBody.create(JSON_med, json);
		
		String accessToken = PropertiesHelp.getApplicationConf("cloud.auth.accesstoken");
		if(accessToken==null)
		{
			accessToken="111111";
		}
		
		String siteTag="";
		if(SaasHander.currentHander()!=null)
		{
			siteTag= SaasHander.currentHander().getSiteTag();
		}		 
				
		okhttp3.Request request = new okhttp3.Request.Builder()
				.url(getUrl(methods))
				.addHeader("CallerID", CallerContext.getCallerID())
				.addHeader("SaasHanderTag", siteTag)
				.addHeader("AccessToken", accessToken)
				.addHeader("Connection", "close")
				.post(requestBody)
				.build();
		

		okhttp3.Response response = httpClient.newCall(request).execute();

		 
		//TODO 
		String str = response.body().string();
		
		response.close();
		if(str==null || returnType.toString().equals("void"))
		{
			return null;
		}
		
		Object  obj=null;
		if(returnType.toString().startsWith("java.util.List<"))
		 {
			Type lt =((ParameterizedType) returnType).getActualTypeArguments()[0];
			if(lt.getTypeName().startsWith("java.util.HashMap"))
			{
				JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, HashMap.class);
				obj =  objectMapper.readValue(str, javaType);
			}
			else
			{
				JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, (Class<?>)lt);				
				obj =  objectMapper.readValue(str, javaType);
			}		 
			
		 }
		else if(returnType.toString().startsWith("io.framecore.Frame.PageDataT") )
		{
			Class<?>  elementClasses = (Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
			 
			JavaType javaType = objectMapper.getTypeFactory().constructParametricType(PageDataT.class, elementClasses);
			
			try {
				obj =  objectMapper.readValue(str, javaType);
			} catch (IOException e) {
				Log.logError(e);
			}
		}
		else
		{
			obj = objectMapper.readValue(str, (Class<?>)returnType);
		}
		
		return obj;
		 

	}
}




