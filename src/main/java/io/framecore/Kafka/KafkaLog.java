package io.framecore.Kafka;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.Marker;

import io.framecore.Frame.ApiLogEntity;
import io.framecore.Frame.CallerContext;
import io.framecore.Frame.Environment;
import io.framecore.Frame.ErrorEntity;
import io.framecore.Frame.MsgEntity;
import io.framecore.Saas.SaasHander;
import io.framecore.Tool.JsonHelp;
import io.framecore.Tool.PropertiesHelp;

public class KafkaLog  implements Logger{
	
	public static String errorTopic="errorTopic-7363";
	
	public static String msgTopic="msgTopic-2653";
	
	public static String apiTopic="apiTopic-5869";
	
	static String projectInfo="";
	static String pidInfo="";
	
	
	String _logName = "";

	public KafkaLog(String logName) {
		_logName = logName;
	}
	
	
 
	static {
		
		try {
			projectInfo=PropertiesHelp.getApplicationConf("spring.application.name");
		} catch (IOException e) {
			projectInfo= System.getProperty("user.dir");
		}
		pidInfo=ManagementFactory.getRuntimeMXBean().getName();
			
	}
	
	

 
	private void saveLog(String msg, String level) { 

		if (Environment.isDev()) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println(df.format(new Date()) +"---" + msg);
			return;
		}
		if(!(level.equals("info") || level.equals("error")))
		{			
			return;
		}
		
		MsgEntity entity = new MsgEntity();
		entity.setLogType(_logName);
		entity.setLevel(level);
		entity.setLogTime(new Date());
		entity.setMsg(msg);
		entity.setCallerID(CallerContext.getCallerID());
		entity.setProjectInfo(projectInfo);
		entity.setPidInfo(pidInfo);
		if(SaasHander.currentHander()!=null)
		{
			entity.setSiteTag(SaasHander.currentHander().getSiteTag());
		}
		
		
		String json = JsonHelp.toJson(entity);
				
		try
		{
			MqProducerHelp.send(msgTopic, json);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		 

	}
	
	
	public final static void logApi(ApiLogEntity apiLogEntity)
	{	
		
		String json = JsonHelp.toJson(apiLogEntity);
		if (Environment.isDev()) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println(df.format(new Date()) +"---  " + json);
			return;
		}
		
    	
		try
		{
			MqProducerHelp.send(apiTopic, json);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		 
		
	}
	
	private void saveErrorLog(String errorMsg, String trackMsg, String level) {  
		 
		if (Environment.isDev()) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println(df.format(new Date()) +"---" + errorMsg+"\r\n"+trackMsg);
			return;
		}
		
		ErrorEntity entity = new ErrorEntity();
		entity.setLevel(level);
		entity.setLogTime(new Date());
		entity.setErrorMsg(errorMsg);
		entity.setStackMsg(trackMsg);
		entity.setCallerID(CallerContext.getCallerID());
		entity.setProjectInfo(projectInfo);
		entity.setPidInfo(pidInfo);
		if(SaasHander.currentHander()!=null)
		{
			entity.setSiteTag(SaasHander.currentHander().getSiteTag());
		}
		
		
		String json = JsonHelp.toJson(entity);
		
		try
		{
			MqProducerHelp.send(errorTopic, json);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
				
 
	}
	

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void trace(String msg) {
		if (isTraceEnabled()) {
			saveLog(msg, "trace");
		}
	}

	@Override
	public void trace(String format, Object arg) {
		 
		String msg = String.format(format, arg);
		trace(msg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		String msg = String.format(format, arg1,arg2);
		trace(msg);
	}

	@Override
	public void trace(String format, Object... arguments) {
		String msg = String.format(format, arguments);
		trace(msg);
		
	}

	@Override
	public void trace(String msg, Throwable t) {
		 
		trace(getErrorMsg((Exception)t));
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void debug(String msg) {
		if (isDebugEnabled()) {
			saveLog(msg, "debug");
		}
		
	}

	@Override
	public void debug(String format, Object arg) {
		String msg = String.format(format,arg);
		debug(msg);
		
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		String msg = String.format(format, arg1,arg2);
		debug(msg);
		
	}

	@Override
	public void debug(String format, Object... arguments) {
		String msg = String.format(format, arguments);
		debug(msg);		
	}

	@Override
	public void debug(String msg, Throwable t) {
		debug(getErrorMsg((Exception)t));
		
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void debug(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void info(String msg) {
		if (isInfoEnabled()) {
			saveLog(msg, "info");
		}
		
	}

	@Override
	public void info(String format, Object arg) {
		String msg = String.format(format,arg);
		info(msg);
		
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		String msg = String.format(format, arg1,arg2);
		info(msg);
		
	}

	@Override
	public void info(String format, Object... arguments) {
		String msg = String.format(format, arguments);
		info(msg);	
		
	}

	@Override
	public void info(String msg, Throwable t) {
		info(getErrorMsg((Exception)t));
		
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void info(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void warn(String msg) {
		if (isWarnEnabled()) {
			saveLog(msg, "warn");
		}
		
	}

	@Override
	public void warn(String format, Object arg) {
		String msg = String.format(format,arg);
		warn(msg);
		
	}

	@Override
	public void warn(String format, Object... arguments) {
		
		String msg = String.format(format, arguments);
		warn(msg);
		
		
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		
		String msg = String.format(format, arg1,arg2);
		warn(msg);
	}

	@Override
	public void warn(String msg, Throwable t) {
		warn(getErrorMsg((Exception)t));
		
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void warn(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void error(String msg) {
		if (isErrorEnabled()) {
			saveErrorLog(msg,"", "error");
		}
		
	}
	
	public final static String getErrorMsg(Exception e)
	{
		StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw =  new PrintWriter(sw);
            //将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
	}


	@Override
	public void error(String format, Object arg) {
		String msg = String.format(format,arg);
		error(msg);
		
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		String msg = String.format(format, arg1,arg2);
		error(msg);
		
	}

	@Override
	public void error(String format, Object... arguments) {
		String msg = String.format(format, arguments);
		error(msg);	
		
	}

	@Override
	public void error(String msg, Throwable t) {
		if (isErrorEnabled()) {			 
			 
			saveErrorLog(msg, getErrorMsg((Exception)t), "error");
		}
		
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void error(Marker marker, String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String format, Object... arguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		// TODO Auto-generated method stub
		
	}

}
