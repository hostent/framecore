package io.framecore.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.framecore.Saas.SaasHander;
import io.framecore.Saas.Site;
import redis.clients.jedis.Jedis;

public class CacheHelp {
	
	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "EX";
	
	
	public static String getSaasKey(String key)
	{
		if(SaasHander.currentHander()==null)
		{
			return key;
		}
		return SaasHander.currentHander().getSiteTag()+"-:"+key;
	}
	public static String[] getSaasKey(String... key)
	{
		if(SaasHander.currentHander()==null)
		{
			return key;
		}
		String[] arr = new String[key.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i]=SaasHander.currentHander().getSiteTag()+"-:"+key[i];
		}
		return arr;
	}
     //temp cache
	 public static String get(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 String result = jedis.get(getSaasKey(key));
		 jedis.close();
		 return result;
	 }
	 
	 public static HashMap<String, String> mget(String... key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 String[] keyArr =getSaasKey(key);
		 List<String> result = jedis.mget(keyArr);
		 jedis.close();
		 
		 HashMap<String, String> hm = new HashMap<String, String>();
		 
		 for (int i = 0; i < keyArr.length; i++) {
			 if(result.get(i)!=null && result.get(i).equals("nil"))
			 {
				 result.set(i, null);
			 }
			 hm.put(keyArr[i], result.get(i));
		 }
		 
		 return hm;
	 }
	 
	 public static void set(String key,String val)
	 {
		 Jedis jedis =  RedisUtil.getJedis();	
		
		 jedis.set(getSaasKey(key), val);
		 jedis.close();
	 }
	 
	 public static Set<String> keys(String pattern)
	 {
		 Jedis jedis =  RedisUtil.getJedis();			 
		 Set<String> result = jedis.keys(pattern);		 
		 jedis.close();
		 return  result;
	 }
	 
	 public static Map<String, Boolean> setNXBatch(Map<String, String> map,int expireSecond)
	 {		 
		 Map<String, Boolean> mapResult = new HashMap<String, Boolean>();
		 
		 Jedis jedis =  RedisUtil.getJedis();	
		 

		 for (String key : map.keySet()) {
			 
			 String result =jedis.set(getSaasKey(key), map.get(getSaasKey(key)), SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireSecond);

			 if (LOCK_SUCCESS.equals(result)) {
				 mapResult.put(getSaasKey(key), true);
			 }
			 else
			 {
				 mapResult.put(getSaasKey(key), false);
			 }
				
		 }
		 
		 jedis.close();
		 
		 return mapResult;
		
 
	 }
	 
	 public static Boolean setNX(String key,String val,int expireSecond)
	 {
		 Jedis jedis =  RedisUtil.getJedis();		 
		 String result =jedis.set(getSaasKey(key), val, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireSecond);
		 jedis.close();
		 
		 if (LOCK_SUCCESS.equals(result)) {
				return true;
		 }
		 return false;
	 }
	 public static Boolean setNX(String key,String val)
	 {
		 Jedis jedis =  RedisUtil.getJedis();		 
		 Long result =jedis.setnx(getSaasKey(key), val);
		 jedis.close();
		 
		 if (result.equals(1)) {
				return true;
		 }
		 return false;
	 }
	 
	 public static void set(String key,String val,int expireSecond)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.set(getSaasKey(key), val);
		 jedis.expire(getSaasKey(key), expireSecond);
		 jedis.close();
	 }
	 
	 public static void delete(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.del(getSaasKey(key));
		 jedis.close();
	 }
 
	 
	 public static void expire(String key,int expireSecond)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.expire(getSaasKey(key),expireSecond);
		 jedis.close();
	 }
	 
	 public static void pexpire(String key,long milliseconds)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.pexpire(getSaasKey(key), milliseconds);
		 jedis.close();
	 }
	 
	 //hash
	 public static Long hset(String key,String fieldId,String val)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 Long result = jedis.hset(getSaasKey(key), fieldId, val);
		 jedis.close();		 
		 return  result;
	 }
	 
	 public static Long hlen(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 Long result = jedis.hlen(getSaasKey(key));
		 jedis.close();		 
		 return  result;
	 }
	 
	 public static String hget(String key,String field)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 String result = jedis.hget(getSaasKey(key), field);
		 jedis.close();
		 return result;
	 }
	 public static long hdel(String key,String field)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 long result = jedis.hdel(getSaasKey(key), field).longValue();
		 jedis.close();
		 return result;
	 }
	 public static Map<String, String> hget(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 Map<String, String> result = jedis.hgetAll(getSaasKey(key));
		 jedis.close();
		 return result;
	 }
	 public static void hmset(String key,Map<String,String> hash)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.hmset(getSaasKey(key), hash);
		 jedis.close();
	 }
	 public static void hdelAll(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.hdel(getSaasKey(key));
		 jedis.close();
	 }
	 public static List<String> hmget(String key,String... fieldIds)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 List<String> result = jedis.hmget(getSaasKey(key), fieldIds);
		 jedis.close();
		 return result;
	 }
	 
	 //队列
	 public static void rpush(String key,String val)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.rpush(getSaasKey(key), val);
		 jedis.close();
	 }
	 public static void rpush(String key,String... val)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 jedis.rpush(getSaasKey(key), val);
		 jedis.close();
	 }
	 
	 public static String rpop(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 String result =jedis.rpop(getSaasKey(key));
		 jedis.close();
		 if(result.equals("nil"))
		 {
			 return null;
		 }
		 return result;
	 }
	 
	 public static Long llen(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 Long result =jedis.llen(getSaasKey(key));
		 jedis.close();		  	
		 return result;
	 }
	 
	 public static String lpop(String key)
	 {
		 Jedis jedis =  RedisUtil.getJedis();
		 String result =jedis.lpop(getSaasKey(key));
		 jedis.close();
		 if(result==null)
		 {
			 return null;
		 }
		 if(result.equals("nil"))
		 {
			 return null;
		 }		
		 return result;
	 }
	 

	 
	 
	 
}
