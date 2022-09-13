package io.framecore.Orm;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import io.framecore.Tool.JsonHelp;
import io.framecore.redis.Cache;
import io.framecore.redis.CacheHelp;

// 这种做法不雅观，以后重构
public class MyMethodInterceptor implements MethodInterceptor
{

	@Override
	public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy) throws Throwable {
		
		String key ="dbset:"+method.getDeclaringClass().getName();
		if(method.getName().equals("Add") || method.getName().equals("Update")||method.getName().equals("Delete"))
		{
			//清理对于的service 缓存
			//CacheHelp.deletePatternAll("service-"+obj.getClass().getDeclaringClass().getSimpleName());
			// 清理 表缓存			
			CacheHelp.delete(key);
		}		
		
		Object result =null;
		
		Cache cache = method.getAnnotation(Cache.class);

		if(cache!=null)
		{
			 
			String fieldId = method.getName()+"-";
			for (Object argItem : arg) {
				fieldId = fieldId + argItem.toString()+"-";
			}
			
			String val = CacheHelp.hget(key, fieldId);
			if(val==null || val.trim().isEmpty())
			{
				result=proxy.invokeSuper(obj, arg); 
				val = JsonHelp.toJson(result);
				CacheHelp.hset(key, fieldId, val);
				CacheHelp.expire(key, 10*60);
			}
			else
			{
				result = JsonHelp.toObject(val, method.getGenericReturnType(),null);
			}
			 
		}
		else
		{
			result=proxy.invokeSuper(obj, arg); 
		}
				
		return result;
	}
	
}