package io.framecore.Frame;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiLogEntity extends ViewStore {
	
 

	// ipAddress
	@JsonProperty(value = "ipAddress")
	public String getIpAddress() {
		return (String) get("ipAddress");
	}

	@JsonProperty(value = "ipAddress")
	public void setIpAddress(String ipAddress) {
		set("ipAddress", ipAddress);
	}

	// userId
	@JsonProperty(value = "userId")
	public Integer getUserId() {
		return (Integer) get("userId");
	}

	@JsonProperty(value = "userId")
	public void setUserId(Integer userId) {
		set("userId", userId);
	}

	// apiAddress
	@JsonProperty(value = "apiAddress")
	public String getApiAddress() {
		return (String) get("apiAddress");
	}

	@JsonProperty(value = "apiAddress")
	public void setApiAddress(String apiAddress) {
		set("apiAddress", apiAddress);
	}

	// visitTime
	@JsonProperty(value = "visitTime")
	public Date getVisitTime() {
		return (Date) get("visitTime");
	}

	@JsonProperty(value = "visitTime")
	public void setVisitTime(Date visitTime) {
		set("visitTime", visitTime);
	}

	// requestPar
	@JsonProperty(value = "requestPar")
	public String getRequestPar() {
		return (String) get("requestPar");
	}

	@JsonProperty(value = "requestPar")
	public void setRequestPar(String requestPar) {
		set("requestPar", requestPar);
	}
	
	@JsonProperty(value = "ctime")
	public Date getCtime() {
		return (Date) get("ctime");
	}
	@JsonProperty(value = "ctime")
	public void setCtime(Date ctime) {
		set("ctime", ctime);
	}

	// responseResult
	@JsonProperty(value = "responseResult")
	public String getResponseResult() {
		return (String) get("responseResult");
	}

	@JsonProperty(value = "responseResult")
	public void setResponseResult(String responseResult) {
		set("responseResult", responseResult);
	}

	// execTimeLong
	@JsonProperty(value = "execTimeLong")
	public Long getExecTimeLong() {
		return (Long) get("execTimeLong");
	}

	@JsonProperty(value = "execTimeLong")
	public void setExecTimeLong(Long execTimeLong) {
		set("execTimeLong", execTimeLong);
	}

	// callerId
	@JsonProperty(value = "callerId")
	public String getCallerId() {
		return (String) get("callerId");
	}

	@JsonProperty(value = "callerId")
	public void setCallerId(String callerId) {
		set("callerId", callerId);
	}

	// clientEnvi
	@JsonProperty(value = "clientEnvi")
	public String getClientEnvi() {
		return (String) get("clientEnvi");
	}

	@JsonProperty(value = "clientEnvi")
	public void setClientEnvi(String clientEnvi) {
		set("clientEnvi", clientEnvi);
	}

	
	
	
	

}
