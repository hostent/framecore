package io.framecore.Mongodb;


import java.util.Date;

import org.bson.Document;

public class LogEntity implements IEntity  
{	
	Document doc  = new Document();
	
	
	public String getLogType() {
		return doc.getString("logType");
	}
	public void setLogType(String logType) {
		doc.put("logType", logType);
	}
	public String getMsg() {
		return doc.getString("msg");
	}
	public void setMsg(String msg) {
		doc.put("msg", msg);
	}
	public Date getLogTime() {
				 
		return doc.getDate("logTime");
	}
	public void setLogTime(Date logTime) {
		doc.put("logTime", logTime);
	}
	public String getContextString() {
		return doc.getString("contextString");
	}
	public void setContextString(String contextString) {
		doc.put("contextString", contextString);
	}
	public String getLevel() {
		return doc.getString("level");
	}
	public void setLevel(String level) {
		doc.put("level", level);				
	}
	
	public String getProjectInfo() {
		return doc.getString("projectInfo");
	}
	public void setProjectInfo(String projectInfo) {
		doc.put("projectInfo",projectInfo);
	}
	public String getPidInfo() {
		return doc.getString("pidInfo");
	}
	public void setPidInfo(String pidInfo) {
		doc.put("pidInfo", pidInfo);
	}
	
	
	public String getCallerID() {
		return doc.getString("CallerID");
	}
	public void setCallerID(String callerID) {
		doc.put("CallerID", callerID);
	}


	
 
	@Override
	public Document getDoc() {
		 
		return this.doc;
	}
	@Override
	public void fill(Document _doc) {
		this.doc=_doc;		
	}
}

