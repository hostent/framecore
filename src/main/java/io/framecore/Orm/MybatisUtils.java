package io.framecore.Orm;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import io.framecore.Frame.Log;
import io.framecore.Tool.PropertiesHelp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 生成mybatis的session对象
 **/
public class MybatisUtils {
	
	
	private static String resource = "config-dao-{connKey}.xml";
	private static Map<String,SqlSessionFactory> sqlSessionFactoryMap = null;
	
	
	private static Lock lock= new ReentrantLock();
 

		
	static ThreadLocal<HashMap<String, SqlSession>> threadSession = new ThreadLocal<HashMap<String, SqlSession>>();
	
	
	static ThreadLocal<Boolean> isAutoCommit=new ThreadLocal<Boolean>();

	private static void init(String connKey) {
 
			
			if(sqlSessionFactoryMap==null || !sqlSessionFactoryMap.containsKey(connKey))
			{				
				try {
					lock.lockInterruptibly();
					
					if(sqlSessionFactoryMap==null)
					{
						sqlSessionFactoryMap = new HashMap<String, SqlSessionFactory>();
					}
					if(!sqlSessionFactoryMap.containsKey(connKey))
					{
						
						InputStream inputStream = Resources.getResourceAsStream(resource.replace("{connKey}", connKey));						
						
						Properties pro =PropertiesHelp.getConf("jdbc-"+connKey);
						//这里可以做解密
						//TODO
						// 创建工厂
						SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,pro);
													
						sqlSessionFactoryMap.put(connKey, sqlSessionFactory);
					}
					
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					lock.unlock();
				}
			}

 
	}

	public static SqlSession getSession(String connKey) {

		if (sqlSessionFactoryMap == null|| !sqlSessionFactoryMap.containsKey(connKey)) {
			init(connKey);
		}
		
		if(isAutoCommit.get()==null)
		{
			isAutoCommit.set(true);
		}
		if (threadSession.get() == null)
		{
			threadSession.set(new HashMap<String, SqlSession>());
		}
		
		try {
			if( threadSession.get().get(connKey)!=null && !threadSession.get().get(connKey).getConnection().isClosed())
			{
				return threadSession.get().get(connKey);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// 创建session对象
		SqlSession session = sqlSessionFactoryMap.get(connKey).openSession(isAutoCommit.get());
		
		 
		threadSession.get().put(connKey, session);
		
		return session;

	}


	public static void close() {
		
		if(isAutoCommit.get()!=null && !isAutoCommit.get()) //事务了
		{
			return; 
		}
		
		if (threadSession.get() != null) {
			if (threadSession.get().size()>0) {				
				for (String connKey : threadSession.get().keySet()) {
					threadSession.get().get(connKey).close();					
				}				
			}
			
			threadSession.remove();
			isAutoCommit.remove();
		}

	}
	
	public static Connection getConn(String connKey)
	{
		return getSession(connKey).getConnection();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getProxyMapper(String connKey,Class<T> tClass) {

		//代理模式	
		T t =getMapper(connKey,tClass);
		return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new HandlerMybatis(t));
		
	}

	public static <T> T getMapper(String connKey,Class<T> tClass) {
		
		return getSession(connKey).getMapper(tClass);
	}
	
	public static void beginTran()
	{
		isAutoCommit.set(false);
	}
	
	public static void rollback() {
		
		isAutoCommit.remove();

		if (threadSession.get() != null) {
			for (String connKey : threadSession.get().keySet()) {
				threadSession.get().get(connKey).rollback();	

				Log.msgLog.info("事务回滚,"+connKey);
			}
		}
		
		close();

	}

	public static void commit() {
		
		isAutoCommit.remove();
		
		if (threadSession.get() != null) {			
			for (String connKey : threadSession.get().keySet()) {
				threadSession.get().get(connKey).commit();	
 				
				Log.msgLog.info("事务提交,"+connKey);
			}

		}
		
		close();

	}

}