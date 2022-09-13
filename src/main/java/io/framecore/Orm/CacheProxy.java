package io.framecore.Orm;

import org.springframework.cglib.proxy.Enhancer;

public class CacheProxy {
	
	   //给目标对象生成代理对象
	@SuppressWarnings("unchecked")
	public static <T> T getProxyInstance(Class<T> cls, Object...arguments){
		Enhancer enhancer=new Enhancer();  
		enhancer.setSuperclass(cls);  
		enhancer.setCallback(new MyMethodInterceptor());  
		
		@SuppressWarnings("rawtypes")
		Class[] clsList = new Class[arguments.length];
		
		for (int i = 0; i < arguments.length; i++) {
			clsList[i]=arguments[i].getClass();
		}

		T t=(T)enhancer.create(clsList, arguments);
	 
		
		return t;
 }

}
