package io.framecore.Frame;

import java.lang.management.ManagementFactory;

import io.framecore.Tool.AesHelp;
import io.framecore.Tool.PropertiesHelp;
import io.netty.util.internal.MacAddressUtil;

public class Environment {
	
	private static Boolean _isDev=false;
	
	static {
		
		if(PropertiesHelp.getAppConf("_isDev")!=null && !PropertiesHelp.getAppConf("_isDev").isEmpty())
		{
			_isDev=Boolean.parseBoolean(PropertiesHelp.getAppConf("_isDev"));
		}
		else
		{

			String osName = System.getProperties().getProperty("os.name");
			if(osName.toLowerCase().startsWith("windows"))
			{
				_isDev= true;
			}
			else
			{
				_isDev= false;
			}
		}
		
		 
		
		
	}
	
	public static Boolean isDev()
	{
		return _isDev;
	}
	
	 static String addZeroForNum(String str,int strLength) {  
		  int strLen =str.length();  
		  if (strLen <strLength) {  
		   while (strLen< strLength) {  
		    StringBuffer sb = new StringBuffer();  
		    sb.append("0").append(str);//左补0  
//		    sb.append(str).append("0");//右补0  
		    str= sb.toString();  
		    strLen= str.length();  
		   }  
		  }  

		  return str;  
		 }  
	
	public static String getCurrentGroupId()
	{
		String machineKey =addZeroForNum(AesHelp.parseByte2HexStr(MacAddressUtil.defaultMachineId()),16) ;

		String name = ManagementFactory.getRuntimeMXBean().getName();

		int pid = Integer.valueOf(name.split("@")[0]) ;
		
		String pidKey =addZeroForNum(AesHelp.intToHex(pid),8);
		
		return (machineKey+"-"+pidKey).toLowerCase();
	}

}
