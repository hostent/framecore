package io.framecore.Frame;

import java.util.concurrent.TimeUnit;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

 
 

public class LocaleCahce<T> {
	
	
	static Cache<String, Object> groupCache = null;
	
	static
	{		
		
		//组，只有100个。 超过就覆盖旧的
		groupCache = Caffeine.newBuilder()		
				.expireAfterAccess(3*60*60, TimeUnit.SECONDS)
				.maximumSize(100)
				.build();
		
		
		 
	}
		
	
	
	Cache<String, T> caffeine=null;
	
	
	@SuppressWarnings("unchecked")
	public static <T> LocaleCahce<T> build(String group,LocaleCahceEnum localeCahceEnum,int size,int second)
	{
		LocaleCahce<T> localeCahce = new LocaleCahce<T>();			
		
		if(groupCache.getIfPresent(group)!=null)
		{
			localeCahce.caffeine= (Cache<String, T>)groupCache.getIfPresent(group);
		}
		else
		{
			Caffeine<Object, Object> cache = Caffeine.newBuilder()				 
					.maximumSize(size);  //大小size 个，超过会产生覆盖行为			
			
			if(localeCahceEnum.equals(LocaleCahceEnum.read))
			{
				localeCahce.caffeine = cache.expireAfterAccess(second, TimeUnit.SECONDS).build();
			}
			else
			{
				localeCahce.caffeine = cache.expireAfterWrite(second, TimeUnit.SECONDS).build();
			}
			
			groupCache.put(group, localeCahce.caffeine);			
			
		}
		
		
		return localeCahce;
	}
	
 	
	public void set(String key,T t)
	{ 		 
		caffeine.put(key, t);				 
	}
	 
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String group,String key,Class<T> cls)
	{
		if(groupCache.getIfPresent(group)==null)
		{
			return null;
		}		
		
		return ((Cache<String, T>)groupCache.getIfPresent(group)).getIfPresent(key);
				  
	}
	
	public static void remove(String group,String key)
	{
		if(groupCache.getIfPresent(group)==null)
		{
			return ;
		}		 
		
		 ((Cache<?, ?>)groupCache.getIfPresent(group)).invalidate(key);
				  
	}
	
	
	public static void stat(String group)
	{
		if(groupCache.getIfPresent(group)==null)
		{
			return ;
		}
		 
		
		System.out.println(((Cache<?, ?>)groupCache.getIfPresent(group)).asMap()); 
				  
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		
		
		
		LocaleCahce<String> localeCahce= LocaleCahce.build("g1", LocaleCahceEnum.read, 110000, 5);
		
	
		
		for (int i = 0; i < 110000; i++) {
			
			localeCahce.set("aaa"+i, String.valueOf(i));
			 
		}
		
		
		//LocaleCahce.stat("g1");
		
		
		
		for (int i = 0; i < 100; i++) {
			
			Thread.sleep(1000);

			
			System.out.println(LocaleCahce.get("g1","aaa23", String.class)); 
			
			//System.out.println(LocaleCahce.get("g1","aaa", String.class)); 
			
		}
		 
		
		
	}
 
	


	

}
