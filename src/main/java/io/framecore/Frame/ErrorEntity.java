package io.framecore.Frame;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorEntity extends ViewStore  
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
	/*   errorMsg   错误内容   */
	@JsonProperty(value = "errorMsg")
	public String getErrorMsg() {
		return (String)get("errorMsg");
	}
	@JsonProperty(value = "errorMsg")
	public void setErrorMsg(String errorMsg)
	{
		set("errorMsg", errorMsg);
	}
	/*   stackMsg   堆栈消息   */
	@JsonProperty(value = "stackMsg")
	public String getStackMsg() {
		return (String)get("stackMsg");
	}
	@JsonProperty(value = "stackMsg")
	public void setStackMsg(String stackMsg)
	{
		set("stackMsg", stackMsg);
	}
	/*   logTime   id路径   */
	@JsonProperty(value = "logTime")
	public Date getLogTime() {
		return (Date)get("logTime");
	}
	@JsonProperty(value = "logTime")
	public void setLogTime(Date logTime)
	{
		set("logTime", logTime);
	}
	/*   contextString   名称   */
	@JsonProperty(value = "contextString")
	public String getContextString() {
		return (String)get("contextString");
	}
	@JsonProperty(value = "contextString")
	public void setContextString(String contextString)
	{
		set("contextString", contextString);
	}
	/*   level   名称   */
	@JsonProperty(value = "level")
	public String getLevel() {
		return (String)get("level");
	}
	@JsonProperty(value = "level")
	public void setLevel(String level)
	{
		set("level", level);
	}
	/*   projectInfo   名称   */
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
	/*   callerID   调用链id   */
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

