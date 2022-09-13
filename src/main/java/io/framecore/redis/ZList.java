package io.framecore.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

public class ZList {

	// 从小到大 排序
	public static Boolean add(String key, Map<String, Double> map) {
		if (map == null || map.size() == 0) {
			return false;
		}
		Jedis jedis = RedisUtil.getJedis();

		Long l = jedis.zadd(key, map);

		jedis.close();

		return l > 0;
	}
	
	//并发性，不保证
	public static Boolean addUnique(String key, Map<String, Double> map) {
		if (map == null || map.size() == 0) {
			return false;
		}
		Jedis jedis = RedisUtil.getJedis();
		
		Set<String> mpKeys = new HashSet<String>();
		
		for (String mpKey : map.keySet())
		{
			mpKeys.add(mpKey);
		}
		
		for (String mpKey : mpKeys) {
			
			Set<String> s = jedis.zrangeByScore(key, map.get(mpKey), map.get(mpKey));
			
			if(s!=null && s.size()>0)
			{
				map.remove(mpKey);
			}			
		}

		Long l = jedis.zadd(key, map);

		jedis.close();

		return l > 0;
	}

	// score > 1.3 and <= 5
	public static Set<String> getRangeByScore(String key, Double min, Double max) {
		Jedis jedis = RedisUtil.getJedis();

		Set<String> s = jedis.zrangeByScore(key, min, max);

		jedis.close();

		return s;
	}

	// 从小到大 排序，从min或者之后拿出第一个值。
	public static String pop(String key, Double min, Double max) {
		Jedis jedis = RedisUtil.getJedis();

		Set<String> s = jedis.zrangeByScore(key, min, max);
		if (s.size() > 0) {

			for (String str : s) {
				Long l = jedis.zrem(key, str);
				if (l > 0) {
					jedis.close();
					return str;
				}
			}

		}
		jedis.close();
		return null;

	}

}
