package io.framecore.redis;

import java.util.Collections;
import io.framecore.Frame.Action;
import io.framecore.Frame.Result;
import redis.clients.jedis.Jedis;

public class RedisLock {

	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "EX";

	private static final Long RELEASE_SUCCESS = 1L;

	final static String pre = "RedisLock.";

	/**
	 * 尝试获取分布式锁
	 * 
	 * @param lockKey    锁
	 * @param requestId  请求标识
	 * @param expireTime 超期时间
	 * @return 是否获取成功
	 */
	public static boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {

		Jedis jedis = RedisUtil.getJedis();
		String result = jedis.set(pre + lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

		jedis.close();

		if (LOCK_SUCCESS.equals(result)) {
			return true;
		}
		return false;

	}

	public static boolean releaseDistributedLock(String lockKey, String requestId) {

		Jedis jedis = RedisUtil.getJedis();

		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		Object result = jedis.eval(script, Collections.singletonList(pre + lockKey),
				Collections.singletonList(requestId));

		jedis.close();
		if (RELEASE_SUCCESS.equals(result)) {
			return true;
		}
		return false;

	}

	public static Result lock(String lockKey, Action<Result> act) {

		int count = 200; // 重试20次
		lockKey = pre + lockKey;
		while (count > 0) {
			if (tryGetDistributedLock(lockKey, lockKey, 3)) {
				Result result = null;
				try {
					result = act.doWork();
				} catch (Exception e) {
					result = Result.failure(e.getMessage());
					io.framecore.Frame.Log.logError(e);
				}
				//release
				try {
					releaseDistributedLock(lockKey, lockKey);
					synchronized (lockKey) {
						lockKey.notify();
					}
				} catch (Exception e) {
					io.framecore.Frame.Log.logError(e);
				}

				return result;

			}
			try {
				synchronized (lockKey) {
					lockKey.wait(110);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count--;
		}

		return act.doWork(); // 等不下去了，强制执行。

	}

}
