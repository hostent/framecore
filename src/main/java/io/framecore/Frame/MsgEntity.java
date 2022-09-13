package io.framecore.Frame;


import java.util.Date;

 
import com.fasterxml.jackson.annotation.JsonProperty;
 

public class MsgEntity extends ViewStore 
{	
	
	

	/*   siteTag      */
	@JsonProperty(value = "siteTag")
	public String getSiteTag() {
		return (String)get("siteTag");
	}
	@JsonProperty(value = "siteTag")
	public void setSiteTag(String siteTag)
	{
		set("siteTag", siteTag);
	}
	/*   logType      */
	@JsonProperty(value = "logType")
	public String getLogType() {
		return (String)get("logType");
	}
	@JsonProperty(value = "logType")
	public void setLogType(String logType)
	{
		set("logType", logType);
	}
	/*   msg    */
	@JsonProperty(value = "msg")
	public String getMsg() {
		return (String)get("msg");
	}
	@JsonProperty(value = "msg")
	public void setMsg(String msg)
	{
		set("msg", msg);
	}
	/*   logTime     */
	@JsonProperty(value = "logTime")
	public Date getLogTime() {
		return (Date)get("logTime");
	}
	@JsonProperty(value = "logTime")
	public void setLogTime(Date logTime)
	{
		set("logTime", logTime);
	}
	/*   contextString      */
	@JsonProperty(value = "contextString")
	public String getContextString() {
		return (String)get("contextString");
	}
	@JsonProperty(value = "contextString")
	public void setContextString(String contextString)
	{
		set("contextString", contextString);
	}
	/*   level      */
	@JsonProperty(value = "level")
	public String getLevel() {
		return (String)get("level");
	}
	@JsonProperty(value = "level")
	public void setLevel(String level)
	{
		set("level", level);
	}
	/*   projectInfo      */
	@JsonProperty(value = "projectInfo")
	public String getProjectInfo() {
		return (String)get("projectInfo");
	}
	@JsonProperty(value = "projectInfo")
	public void setProjectInfo(String projectInfo)
	{
		set("projectInfo", projectInfo);
	}
	/*   pidInfo      */
	@JsonProperty(value = "pidInfo")
	public String getPidInfo() {
		return (String)get("pidInfo");
	}
	@JsonProperty(value = "pidInfo")
	public void setPidInfo(String pidInfo)
	{
		set("pidInfo", pidInfo);
	}
	/*   callerID      */
	@JsonProperty(value = "callerID")
	public String getCallerID() {
		return (String)get("callerID");
	}
	@JsonProperty(value = "callerID")
	public void setCallerID(String callerID)
	{
		set("callerID", callerID);
	}
}

