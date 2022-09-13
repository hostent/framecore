package io.framecore.Frame;

import java.io.*;

import org.slf4j.*;

import io.framecore.Kafka.KafkaLog;
import io.framecore.Tool.JsonHelp;
import io.framecore.Tool.PropertiesHelp;

public class Log {

	public final static Logger msgLog = getLogger("com.jcore.log.info");

	public final static Logger apiLog = getLogger("com.jcore.log.api");

	public final static Logger proxyLog = getLogger("com.jcore.log.proxy");
	
	public final static Logger errorLog = getLogger("com.jcore.log.error");
	
	public final static Logger getLogger(String logName)
	{
		String logType = "";
		try {
			logType = PropertiesHelp.getApplicationConf("com.jcore.log.type");
		} catch (IOException e) {
			logType="";
		}
		
		if("NULL".equals(logType))
		{
			return new NULLLog(logName);
		}
		
		return new KafkaLog(logName);
		
//		if (logType.equals("mongodbLog")) {
//			return new MongodbLog(logName);
//		}
//		return LoggerFactory.getLogger(logName);
		
	}
	


	public final static void logError(Exception e)
	{		
		errorLog.error(e.getMessage(), e);
		
	}
	public final static void logError(Exception e,String msg)
	{
		errorLog.error(msg, e);
	}
	


}
