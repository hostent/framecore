package io.framecore.Frame;

import java.util.Date;

//@Configuration(value="JobEvent")
public interface IJobEvent {
	
	void onInit();
	
 
	void onStart(String jobName, String args, String cron, Integer intervalSeconds, String jobDesc, String jobInterface,
			Date nextRunTime, String siteTag);

	void onExeced(String jobName, Date lastRunTimeFrom, Date lastRunTimeTo, Integer lastSucceed, Date nextRunTime);

	

}
