package io.framecore.Mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import io.framecore.Tool.PropertiesHelp;

public class ConnectionManager {
	
	
	
	static String userName =PropertiesHelp.get("log_mongodb", "userName");
	static String password =PropertiesHelp.get("log_mongodb", "password");
	static String address =PropertiesHelp.get("log_mongodb", "address");
	static String port =PropertiesHelp.get("log_mongodb", "port");
	static String db =PropertiesHelp.get("log_mongodb", "db");
	
	static String timeout =PropertiesHelp.get("log_mongodb", "timeout");
	static String connectionsPerHost =PropertiesHelp.get("log_mongodb", "connectionsPerHost");
	
	
	static Object lock = new Object();

	public static MongoClient mongoClient = null; 

	
	static  MongoClient getClient()
	{		
		MongoCredential credential = MongoCredential.createCredential(userName, db, password.toCharArray());
		
		//Builder option = MongoClientOptions.builder();
		
		//option.threadsAllowedToBlockForConnectionMultiplier(2000);
		
		int connectionsPerHostInt =100;
		
		if(connectionsPerHost!=null && (!connectionsPerHost.isEmpty()))
		{
			connectionsPerHostInt =Integer.valueOf(connectionsPerHost);
		}
		
		int timeoutInt =5000;
		
		if(timeout!=null && (!timeout.isEmpty()))
		{
			timeoutInt =Integer.valueOf(timeout);
		}
		
		MongoClientOptions options = MongoClientOptions.builder()
				.connectionsPerHost(connectionsPerHostInt) //最大连接数
				.connectTimeout(30000)//链接超时
				.maxWaitTime(timeoutInt)      //15000  15s
				.socketTimeout(0)   //套接字超时时间， 0无限制
				//.maxConnectionLifeTime(timeoutInt)  //15000
				.threadsAllowedToBlockForConnectionMultiplier(5000)
				.maxConnectionIdleTime(30000)
				.maxConnectionLifeTime(10*60000) //10分钟
				.build();   //15000  
		
		if (mongoClient == null) {
			synchronized (lock) {
				if (mongoClient == null) {
					mongoClient =  new MongoClient(new ServerAddress(address, Integer.parseInt(port)), credential,options);
					java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.SEVERE);
				}
			}
		}	
		
		
		//mongoClient.op
	 
		
		return mongoClient ;
		
	}
	
	public static MongoDatabase getDb(String dbName)
	{
		return getClient().getDatabase(dbName);
	}
}
