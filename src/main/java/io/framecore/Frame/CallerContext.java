package io.framecore.Frame;

import java.util.Date;
import java.util.UUID;

import io.framecore.Saas.SaasHander;

public class CallerContext {
	
	 private  static ThreadLocal<String> CallerID = new ThreadLocal<String>();
	 
	 private  static ThreadLocal<Date> CallerTime = new ThreadLocal<Date>();
	 
	 

	public static String getCallerID() {
		
		String callerid = CallerID.get();
		if(callerid==null)
		{
			callerid = UUID.randomUUID().toString();
			CallerID.set(callerid);
			CallerTime.set(new Date());
		}
		return callerid;
		
	}
	
	public static Date getBeginTime()
	{
		getCallerID();		
		return CallerTime.get();
	}

	public static void setCallerID(String callerID) {
		
		CallerID.set(callerID);
		
	}
	
	
	public static void dispose()
	{
		CallerID.remove();
		
		CallerTime.remove();
		
		SaasHander.dispose();
	}
	   

}
