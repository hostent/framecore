package io.framecore.Tool;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import io.framecore.Frame.Environment;
import io.framecore.Frame.Log;
import io.framecore.Saas.SaasHander;

public class PropertiesHelp {

	static Properties dbPro = null;

	private static Object flagLock="1";
	
	//static  HashMap<String, Properties> appPro=new HashMap<String, Properties>();
	
	
	static Properties appPro= null;
	

	private static Object flagLock2="2";
	
	static Properties applicationPro = null;
	
	private static Object flagLock3="3";

	//读数据库配置文件db.properties
	public static String getDbConf(String key) throws IOException {
		if (dbPro == null) {
			synchronized (flagLock) {
				if(dbPro == null)
				{		
					
					dbPro = new Properties();					
					
					dbPro.load(getInputStream("db.properties"));
 
					
					flagLock=11;
				}				
			}
		}

		return dbPro.getProperty(key, "");

	}

	//读appSetting.properties文件
	public static String getAppConf(String key)  {
		
		try
		{

			if (appPro == null) {
				appPro = new Properties();
				if (Environment.isDev()) {
					appPro.load(getInputStream("appSetting.properties"));
				} else {
					appPro.load(getUserInputStream("config", "appSetting.properties"));
				}
			}

			if (appPro.containsKey(key)) {
				return appPro.getProperty(key, "");
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}
	
	//读appSetting.properties文件
//		public static Properties getAppConf() throws IOException {
//			String siteTag = SaasHander.currentHander().getSiteTag();
//			if (!appPro.containsKey(siteTag)) {
//				synchronized (flagLock2) {
//					if(!appPro.containsKey(siteTag))
//					{	
//						Properties appProItem = new Properties();					
//						
//						appProItem.load(getUserInputStream("config",siteTag+"_appSetting.properties"));
//						appPro.put(siteTag, appProItem);
//						flagLock2=2;
//					}				
//				}
//			}
//			return appPro.get(siteTag);			 
//
//		}
	
	public static Properties getConf(String name) 
	{
		try {
			Properties props = new Properties();
			if (Environment.isDev()) {
				props.load(getInputStream(name+".properties"));
			} else {
				props.load(getUserInputStream("config", name+".properties"));
			}
			return props;
			
		} catch (Exception e) {
			return  new Properties();
		}	
		
		
		
	}

	//读application.properties文件
	public static String getApplicationConf(String key) throws IOException {
		if (applicationPro == null) {
			synchronized (flagLock3) {
				if(applicationPro == null)
				{	
					applicationPro = new Properties();					
					
					applicationPro.load(getInputStream("application.properties"));
					
					flagLock3=3;
				}				
			}
		}
		
		if(applicationPro.containsKey(key))
		{
			return applicationPro.getProperty(key, "");
		}
		else
		{
			return null;
		}		
		
	}

	public static String get(String propertiesName,String key)   
	{
		Properties pro = new Properties();	
		try {
			pro.load(getInputStream( propertiesName+".properties"));
		} catch (IOException e) {

			return "";
		}
		
		return pro.getProperty(key, "");
		
	 
	}
	
	public static String getRootPath()
	{
		String path=System.getProperty("java.class.path").split(";")[0];
		if(!path.endsWith(".jar"))
		{
			 return path;
		}
		else
		{
			return new File(path).getParent();
		}
	}
	
	public static InputStream getUserInputStream(String filePath,String propertiesFileName)
	{
		 
		try {
			 						
			File file = new File(System.getProperty("user.dir")+"/"+filePath+"/"+propertiesFileName);
			
 
			
			if (!file.exists()) {
				System.out.println(System.getProperty("user.dir")+"/"+filePath+"/"+propertiesFileName+"--locale----------------------------");
				return PropertiesHelp.class.getClassLoader().getResourceAsStream(propertiesFileName);
			}
			else
			{
				System.out.println(System.getProperty("user.dir")+"/"+filePath+"/"+propertiesFileName+"-config----------------------------");
				InputStream input = new FileInputStream(file);
				return input;
			}
		
		} catch (IOException e) {			 
			Log.logError(e);
		}
		return null;
	}
	
	public static InputStream getInputStream(String propertiesFileName)
	{
		 
		try {
			 			
			String path=System.getProperty("java.class.path").split(";")[0];
			if(!path.endsWith(".jar"))
			{
				return PropertiesHelp.class.getClassLoader().getResourceAsStream(propertiesFileName);
			}
		
			File file = new File(new File(path).getParent()+"/"+propertiesFileName);
			
			if (!file.exists()) {
				return PropertiesHelp.class.getClassLoader().getResourceAsStream(propertiesFileName);
			}
			else
			{
				InputStream input = new FileInputStream(file);
				return input;
			}
		
		} catch (IOException e) {			 
			Log.logError(e);
		}
		return null;
	}
	
	public static String getFilePath(String fileName)
	{
		String path=System.getProperty("java.class.path").split(";")[0];
		if(!path.endsWith(".jar"))
		{
			return PropertiesHelp.class.getClassLoader().getResource(fileName).getPath();
		}
		
		//System.out.println("fasfdafdsa:"+new File(path).getAbsolutePath());

		File file = new File(new File(new File(path).getAbsolutePath()).getParent()+"/"+fileName);
		
		if (!file.exists()) {
			return PropertiesHelp.class.getClassLoader().getResource(fileName).getPath();
		}
		else
		{				 
			return file.getPath();
		}
	}
	
}
