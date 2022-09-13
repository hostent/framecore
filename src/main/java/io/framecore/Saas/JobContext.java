package io.framecore.Saas;

import java.util.Map;

import org.quartz.JobExecutionContext;

public class JobContext {
	
	private Map<String, Object> args;
	
	private Site site;
	
 

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

 

}
