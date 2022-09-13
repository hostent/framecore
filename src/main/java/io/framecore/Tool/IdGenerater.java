package io.framecore.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import io.framecore.Frame.Result;
import io.framecore.redis.CacheHelp;
import io.framecore.redis.RedisLock;


public class IdGenerater {
	
	
	
	
	
	public static String newShortId(String module)
	{
		SimpleDateFormat sf = new SimpleDateFormat("dd-");
		
		String key ="IdGenerater:"+sf.format(new Date())+module;
		
		String res = CacheHelp.lpop(key);
		
		if(res==null)
		{
			RedisLock.lock(key+".nx", ()->
			{

				String ss[] = new String[99998] ;
				for (int i = 2; i < 100000; i++) {
					ss[i-2]=String.valueOf(i);				
				}
				CacheHelp.rpush(key, ss);
				
				CacheHelp.expire(key, 24*60*60+10);
				
				
				return Result.succeed();
				
			});
			
 				
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		
		if(res==null)
		{
			res=df.format(new Date())+"00001";
		}
		else
		{
			res=df.format(new Date())+ String.format("%05d", Integer.parseInt(res))  ;
		}
		
		return res;
	}
	
	 
	public static String newId()
	{
		 
		String id = System.currentTimeMillis()+getByUUId();
		
		return id;
	}
	
	
	private static String getByUUId() {
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        
       long rad = new Random().nextInt(900000000);
        
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return String.format("%010d", hashCodeV+rad).substring(0,5);
    }
	


	
}

