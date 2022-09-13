//package io.framecore.Mongodb;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringWriter;
//import java.lang.management.ManagementFactory;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.bson.Document;
//import org.slf4j.Logger;
//import org.slf4j.Marker;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//import io.framecore.Frame.AsyncHelp;
//import io.framecore.Frame.CallerContext;
//import io.framecore.Frame.Environment;
//import io.framecore.Frame.ErrorEntity;
//import io.framecore.Frame.MsgEntity;
//import io.framecore.Saas.SaasHander;
//import io.framecore.Tool.PropertiesHelp;
//
//public class MongodbLog implements Logger {
//
//	static Object lock = new Object();
//
//	String _logName = "";
//
//	public MongodbLog(String logName) {
//		_logName = logName;
//	}
//	
//
//	static MongoDatabase db = null;
//	
//	static MongoCollection<Document> MsgLogcol= null;
//	
//	MongoCollection<Document> getMsgCollection()
//	{
//	    
//		if (db == null) {
//			synchronized (lock) {
//				if (db == null) {
//					db = ConnectionManager.getDb("MongodbLog");
//				}
//			}
//		}
//		if(MsgLogcol==null )
//		{
//			synchronized (lock) {
//				if (MsgLogcol == null) {
//					MsgLogcol = db.getCollection("MsgLog", Document.class);	
//				}
//			}			
//		}
//		
//		return MsgLogcol;
//
//	}
//	
//	static MongoCollection<Document> ErrorLogcol= null;
//	MongoCollection<Document> getErrorCollection()
//	{
//	    
//		if (db == null) {
//			synchronized (lock) {
//				if (db == null) {
//					db = ConnectionManager.getDb("MongodbLog");
//				}
//			}
//		}
//		if(ErrorLogcol==null )
//		{
//			synchronized (lock) {
//				if (ErrorLogcol == null) {
//					ErrorLogcol = db.getCollection("ErrorLog", Document.class);	
//				}
//			}			
//		}
//		
//		return ErrorLogcol;
//
//	}
//	
//	//static ExecutorService threadPool = Executors.newFixedThreadPool(6);
//	
//	static BlockingQueue<Document> basket = new ArrayBlockingQueue<Document>(500*20);
//	
//	static String projectInfo="";
//	static String pidInfo="";
//	
// 
//	static {
//		
//		try {
//			projectInfo=PropertiesHelp.getApplicationConf("spring.application.name");
//		} catch (IOException e) {
//			projectInfo= System.getProperty("user.dir");
//		}
//		pidInfo=ManagementFactory.getRuntimeMXBean().getName();
//			
//	}
//
//	// 这里应该修改成异步记录
//	private void saveLog(String msg, String level) { //这里线程开太多也不好，需要轻量的异步方法。同时测试下mongodb 的性能，
//		//可以考虑批量作业
//		if (Environment.isDev()) {
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//			System.out.println(df.format(new Date()) +"---" + msg);
//		}
//		if(!(level.equals("info") || level.equals("error")))
//		{			
//			return;
//		}
//		
//		MsgEntity entity = new MsgEntity();
//		entity.setLogType(_logName);
//		entity.setLevel(level);
//		entity.setLogTime(new Date());
//		entity.setMsg(msg);
//		entity.setCallerID(CallerContext.getCallerID());
//		entity.setProjectInfo(projectInfo);
//		entity.setPidInfo(pidInfo);
//		entity.setSiteTag(SaasHander.currentHander().getSiteTag());
//		
//		try {
//			
//			
//			basket.put(entity.getDoc());
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//				 
//		
//		if(basket.size()>200)
//		{ 
//			List<Document> list = new ArrayList<Document>();	
//			for (int i = 0; i < 200; i++) {
//				
//				Document doc = basket.poll();
//				
//				
//				if(doc!=null)
//				{
//					list.add(doc);
//				}
//				
//			}	
//			if(list.size()>0)
//			{
//				AsyncHelp.runAsync((new Runnable() {
//
//					@Override
//					public void run() {													 
//						getMsgCollection().insertMany(list);									
//						
//					}
//
//				}));
//			 		 
//	 
//			}
//																		
//
//		}		
//
//	}
//
//	
//	 
//	private void saveErrorLog(String errorMsg, String trackMsg, String level) {  
//		 
//		if (Environment.isDev()) {
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//			System.out.println(df.format(new Date()) +"---" + errorMsg+"\r\n"+trackMsg);
//			return;
//		}
//		
//		ErrorEntity entity = new ErrorEntity();
//		entity.setLevel(level);
//		entity.setLogTime(new Date());
//		entity.seterrorMsg(errorMsg);
//		entity.setstackMsg(trackMsg);
//		entity.setCallerID(CallerContext.getCallerID());
//		entity.setProjectInfo(projectInfo);
//		entity.setPidInfo(pidInfo);
//		entity.setSiteTag(SaasHander.currentHander().getSiteTag());
//		
//		getErrorCollection().insertOne(entity.getDoc());
//		
//	 
//		
//		
// 
//	}
//	
//	@Override
//	public String getName() {
//		// TODO 自动生成的方法存根
//		return _logName;
//	}
//
//	@Override
//	public boolean isTraceEnabled() {
//		// TODO 自动生成的方法存根
//
//		return true;
//	}
//
//	@Override
//	public void trace(String msg) {
//		if (isTraceEnabled()) {
//			saveLog(msg, "trace");
//		}
//
//	}
//
//	@Override
//	public void trace(String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isTraceEnabled(Marker marker) {
//		// TODO 自动生成的方法存根
//		return false;
//	}
//
//	@Override
//	public void trace(Marker marker, String msg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(Marker marker, String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(Marker marker, String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(Marker marker, String format, Object... argArray) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void trace(Marker marker, String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isDebugEnabled() {
//		// TODO 自动生成的方法存根
//		return true;
//	}
//
//	@Override
//	public void debug(String msg) {
//		if (isDebugEnabled()) {
//			saveLog(msg, "debug");
//		}
//
//	}
//
//	@Override
//	public void debug(String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isDebugEnabled(Marker marker) {
//		// TODO 自动生成的方法存根
//		return false;
//	}
//
//	@Override
//	public void debug(Marker marker, String msg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(Marker marker, String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(Marker marker, String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(Marker marker, String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void debug(Marker marker, String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isInfoEnabled() {
//		// TODO 自动生成的方法存根
//		return true;
//	}
//
//	@Override
//	public void info(String msg) {
//		if (isInfoEnabled()) {
//			saveLog(msg, "info");
//		}
//
//	}
//
//	@Override
//	public void info(String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isInfoEnabled(Marker marker) {
//		// TODO 自动生成的方法存根
//		return false;
//	}
//
//	@Override
//	public void info(Marker marker, String msg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(Marker marker, String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(Marker marker, String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(Marker marker, String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void info(Marker marker, String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isWarnEnabled() {
//		// TODO 自动生成的方法存根
//		return true;
//	}
//
//	@Override
//	public void warn(String msg) {
//		if (isWarnEnabled()) {
//			saveLog(msg, "warn");
//		}
//
//	}
//
//	@Override
//	public void warn(String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isWarnEnabled(Marker marker) {
//		// TODO 自动生成的方法存根
//		return true;
//	}
//
//	@Override
//	public void warn(Marker marker, String msg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(Marker marker, String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(Marker marker, String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(Marker marker, String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void warn(Marker marker, String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public boolean isErrorEnabled() {
//		// TODO 自动生成的方法存根
//		return true;
//	}
//
//	@Override
//	public void error(String msg) {
//		if (isErrorEnabled()) {
//			saveErrorLog(msg,"", "error");
//		}
//
//	}
//
//	@Override
//	public void error(String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void error(String format, Object arg1, Object arg2) {
//		
//
//	}
//
//	@Override
//	public void error(String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//	
//	public final static String getErrorMsg(Exception e)
//	{
//		StringWriter sw = null;
//        PrintWriter pw = null;
//        try {
//            sw = new StringWriter();
//            pw =  new PrintWriter(sw);
//            //将出错的栈信息输出到printWriter中
//            e.printStackTrace(pw);
//            pw.flush();
//            sw.flush();
//        } finally {
//            if (sw != null) {
//                try {
//                    sw.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//            if (pw != null) {
//                pw.close();
//            }
//        }
//        return sw.toString();
//	}
//
//	@Override
//	public void error(String msg, Throwable t) {
//		
//		if (isErrorEnabled()) {			 
//			 
//			saveErrorLog(msg, getErrorMsg((Exception)t), "error");
//		}
//
//	}
//
//	@Override
//	public boolean isErrorEnabled(Marker marker) {
//		// TODO 自动生成的方法存根
//		return true;
//	}
//
//	@Override
//	public void error(Marker marker, String msg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void error(Marker marker, String format, Object arg) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void error(Marker marker, String format, Object arg1, Object arg2) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void error(Marker marker, String format, Object... arguments) {
//		// TODO 自动生成的方法存根
//
//	}
//
//	@Override
//	public void error(Marker marker, String msg, Throwable t) {
//		// TODO 自动生成的方法存根
//
//	}
//
//}
