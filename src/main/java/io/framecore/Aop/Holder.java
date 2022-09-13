package io.framecore.Aop;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
 

import com.fasterxml.jackson.databind.ObjectMapper;

import io.framecore.Frame.ClientCall;
import io.framecore.Frame.Log;
import io.framecore.Tool.JsonHelp;
import io.framecore.Tool.PropertiesHelp;
import io.framecore.Web.ApiBeanStore;
import io.framecore.Web.ClientConfig;
import io.framecore.Web.CloudClient;
import io.framecore.redis.Cache;
import io.framecore.redis.CacheHelp;

public class Holder implements InvocationHandler {

	public Holder() {
		super();

	}

	public Holder(Object objImp) {
		super();
		this.objImp = objImp;
	}
	
	public static <T> T getBean(Class<T> cls)
	{
		String pn="io.rhino";
		try {
			pn = PropertiesHelp.getApplicationConf("package-name");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 		
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(pn);
 
		return (T)appContext.getBean(cls);
	}
	
	public static Object getBean(String name)
	{
		String pn="io.rhino";
		try {
			pn = PropertiesHelp.getApplicationConf("package-name");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 		
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(pn);
 
		return appContext.getBean(name);
	}
	
	
	public static <T> T getBean(Class<T> cls,String name)
	{
		String pn="io.rhino";
		try {
			pn = PropertiesHelp.getApplicationConf("package-name");
		} catch (IOException e) {
			e.printStackTrace();
		}
		 		
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(pn.split(","));
 
		return (T)appContext.getBean(name);
	}
	public static <T> T getBean(Class<T> cls,String name,Object... args)
	{
		String pn="io.rhino";
		try {
			pn = PropertiesHelp.getApplicationConf("package-name");
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext(pn);
		
		return (T)appContext.getBean(name, args);
	}

	private Object objImp;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Cache cache = method.getAnnotation(Cache.class);
		if (cache != null && cache.key() != null && (!cache.key().isEmpty())) {

			ObjectMapper map = JsonHelp.getJack();
			String fieldId = method.getName() + "-";
			if (args != null) {
				for (Object argItem : args) {
					fieldId = fieldId + argItem.toString() + "-";
				}
			}

			Object result = null;
			String val = CacheHelp.get(cache.key()+":"+fieldId);
			if (val == null || val.trim().isEmpty()) {
				result = invokeMethod(proxy, method, args);
				if (result == null) {
					return result; // 空值，不写缓存
				}
				val = map.writeValueAsString(result);
				CacheHelp.set(cache.key()+":"+fieldId, val, 20 * 60);

			} else {
				result = JsonHelp.toObject(val, method.getGenericReturnType(), null);
			}
			return result;
		}

		return invokeMethod(proxy, method, args);

	}

	private Object invokeMethod(Object proxy, Method method, Object[] args) {
		try {
			ClientCall clientCall = method.getAnnotation(ClientCall.class);
			
			if (clientCall != null && clientCall.service()
					.equals(PropertiesHelp.getApplicationConf("spring.application.name").replace("/", ""))) {
				
				ApiBeanStore apiBean = ApiBeanStore.StoreList.get(clientCall.method().toLowerCase().replace("/api/", ""));
				if (apiBean != null) {
					objImp = apiBean.getBeanInstance();
				}
				
			}
 
			if (objImp != null) { //本地调用
				return method.invoke(objImp, args);
			}

			if (clientCall != null) {
				if (Holder.isMock) {
					return method.invoke(objImp, args);
				}

				ClientConfig config = new ClientConfig(clientCall.service(),clientCall.method());

				CloudClient client = new CloudClient(config);

				return client.post(method.getGenericReturnType(), clientCall.method(), args);
			}
		} catch (Exception e) {
			Log.logError(e);
		}

		return null;
	}

	//这个方法只能本地调用
	public static <T> T getService(Class<T> mapperInterface, String tag) {
		String key = mapperInterface.getName() + "|" + tag;
		if (dict.containsKey(key)) {
			return (T) dict.get(key);
		}

		lock.lock();
		try {

			Object proxyObj = null;

			String impName = getImpNameForTag(mapperInterface.getName(), tag);

			Class<?> cla = null;

			try {
				cla = Class.forName(impName);

			} catch (Exception e) {
				return null;
			}
			ClassLoader classLoader = mapperInterface.getClassLoader();
			Class<?>[] interfaces = new Class[] { mapperInterface };
			Object obj = null;
			// mock
			if (Holder.isMock) {
				obj = Holder.getMockClass(mapperInterface);
			} else {
				obj = cla.newInstance();
			}
			Holder proxy = new Holder(obj);
			proxyObj = (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);

			dict.put(key, proxyObj);

			return (T) proxyObj;

		} catch (Exception e) {
			Log.logError(e);
			return null;
		} finally {
			lock.unlock();
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> mapperInterface) {

		String key = mapperInterface.getName();
		if (dict.containsKey(key)) {
			return (T) dict.get(key);
		}

		lock.lock();
		try {

			Object proxyObj = null;

			String impName = getImpName(mapperInterface.getName());

			Class<?> cla = null;

			try {
				cla = Class.forName(impName);

			} catch (Exception e) {
				cla = null;
			}

			// mock
			if (Holder.isMock) {
				cla = Holder.getMockClass(mapperInterface).getClass();
			}

			if (cla == null) { // 远程调用
				ClassLoader classLoader = mapperInterface.getClassLoader();
				Class<?>[] interfaces = new Class[] { mapperInterface };
				Holder proxy = new Holder();
				proxyObj = (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);

				dict.put(key, proxyObj);
			} else { // 本地调用

				ClassLoader classLoader = mapperInterface.getClassLoader();
				Class<?>[] interfaces = new Class[] { mapperInterface };
				Object obj = null;
				// mock
				if (Holder.isMock) {
					obj = Holder.getMockClass(mapperInterface);
				} else {
					obj = cla.newInstance();
				}
				Holder proxy = new Holder(obj);
				proxyObj = (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);

				dict.put(key, proxyObj);
			}

			return (T) proxyObj;

		} catch (Exception e) {
			// TODO: handle exception
			return null;
		} finally {
			lock.unlock();
		}

	}

	static String getImpName(String interfaceName) {
		String[] strs = interfaceName.split("\\.");

		String result = "";
		for (int i = 0; i < strs.length; i++) {

			if (i == strs.length - 1) {
				result = result + "Imp." + strs[i].substring(1, strs[i].length()) + "Imp";
			} else {
				result = result + strs[i] + ".";
			}
		}
		return result;
	}

	static String getImpNameForTag(String interfaceName, String tag) {
		String[] strs = interfaceName.split("\\.");

		String result = "";
		for (int i = 0; i < strs.length; i++) {

			if (i == strs.length - 1) {
				result = result + "Imp." + strs[i].substring(1, strs[i].length()) + "Imp_" + tag;
			} else {
				result = result + strs[i] + ".";
			}
		}
		return result;
	}

	static Hashtable<String, Object> dict = new Hashtable<String, Object>();

	static Lock lock = new ReentrantLock();

	static HashMap<Class<?>, Object> ObjList = new HashMap<Class<?>, Object>();

	static Boolean isMock = false;

	public static void addMockClass(Class<?> interfaceClass, Object impObj) {

		isMock = true;

		ObjList.put(interfaceClass, impObj);

	}

	@SuppressWarnings("unchecked")
	public static <T> T getMockClass(Class<T> mapperInterface) {
		if (ObjList.containsKey(mapperInterface)) {
			return (T) ObjList.get(mapperInterface);
		}

		return null;
	}

}