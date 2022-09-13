package io.framecore.Tool;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import io.framecore.Aop.Holder;
import io.framecore.Frame.AsyncHelp;
import io.framecore.Frame.CallerContext;
import io.framecore.Frame.IJobEvent;
import io.framecore.Frame.Log;
import io.framecore.Saas.IFaceJob;
import io.framecore.Saas.SaasHander;
import io.framecore.Saas.Site;
import io.framecore.Saas.SiteKeeper;
import io.framecore.redis.CacheHelp;

public class JobHelp {

	public static void main(String[] args) throws SchedulerException, InterruptedException {

		AsyncHelp.runAsyncSingle(() -> {
			try {
				
				Map<String, Object> map =  new HashMap<String, Object>();				
				map.put("pflag", "4344434");
				
				newRunningJob("测试Job","0 0/1 * * * ?", TestJob.class, map);
				
				
			 
			
				
				//newRunningJob("测试Job",50, 4, TestJob.class, map);
				
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		

		AsyncHelp.runAsyncSingle(() -> {
			
			try {
				Thread.sleep(3*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");

			Scheduler scheduler;
			try {
				
				for (int i = 0; i < 5000; i++) {
					
					scheduler = StdSchedulerFactory.getDefaultScheduler();

					Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.anyGroup());

					for (JobKey jobKey : keys) {

						JobDetail jobDetail = scheduler.getJobDetail(jobKey);

						@SuppressWarnings("unchecked")
						List<Trigger> triggerList = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

						String format ="Job名称([jobName]);JobKey([JobKey]);参数([args]);上一次时间([PreviousFireTime]);下一次时间([NextFireTime])";
						
						format = format.replace("[jobName]", jobDetail.getDescription());
						format = format.replace("[JobKey]", jobDetail.getKey().getName());
						
						format = format.replace("[args]", JsonHelp.toJson(jobDetail.getJobDataMap()) );
						
						for (Trigger trigger : triggerList) {
							if(trigger.getPreviousFireTime()!=null)
							{								
								format = format.replace("[PreviousFireTime]",df.format(trigger.getPreviousFireTime()));

							}

							format = format.replace("[NextFireTime]",df.format(trigger.getNextFireTime()));

						}
						
						System.out.println(format);

					}
					
					Thread.sleep(3*1000);
					
				}
				
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		// deleteMyjob(TestJob.class);
		
		Thread.sleep(3000*1000);

	}

	public static boolean newMyJobSaas(String JobDescription,Class<? extends IFaceJob> jobClass) throws SchedulerException {

		List<Site> siteList = SiteKeeper.getList();
		for (Site site : siteList) {

			
			SaasHander.set(site.getSiteTag());
			
			try {
				new DriveJob().start(JobDescription, jobClass, site.getSiteTag());
			} catch (Exception e) {
				e.printStackTrace();
			}


		}

		return false;

	}

 
	public static Date newRunningJob(String JobDescription,int intervalSeconds, int startAtSeconds, Class<? extends Job> jobClass,
			Map<String, Object> par, String siteTag) throws SchedulerException {
		
		
		String className =jobClass.getName();
		if(par.containsKey("ClassLoader"))
		{
			Class<?> classLoader =(Class<?>) par.get("ClassLoader");
			
			className = classLoader.getName();
		}	

		String jobName = className + "-" + siteTag;
		if (par != null) {
			jobName = jobName + "-" + par.hashCode();
		}

		Log.msgLog.info("Job start(newRunningJob):[" + jobName + "]");

		JobDetail job = newJob(jobClass).withDescription(JobDescription).withIdentity(jobName, "group_" + jobName).build();
		if(par!=null)
		{
			job.getJobDataMap().putAll(par); 
		}
		
		job.getJobDataMap().put("siteTag", siteTag);

		Trigger trigger = newTrigger().withIdentity("trigger_" + jobName, "group_" + jobName)
				.startAt(new Date(new Date().getTime() + startAtSeconds * 1000))
				.withSchedule(simpleSchedule().withIntervalInSeconds(intervalSeconds).repeatForever()).build();
		
		
		
		IJobEvent jobEvent = (IJobEvent) Holder.getBean(JobHelp.class,"JobEvent");
		if(jobEvent!=null)
		{			
			jobEvent.onStart(jobName, JsonHelp.toJson(par) , null, intervalSeconds, JobDescription, className, trigger.getNextFireTime(), siteTag);
		}	

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		scheduler.scheduleJob(job, trigger);

		scheduler.getListenerManager().addJobListener(new JobListener() {

			@Override
			public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
				if (jobException != null) {
					Log.logError(jobException, context.getJobDetail().getJobClass().getName());
				}

				CallerContext.dispose();
			}

			@Override
			public void jobToBeExecuted(JobExecutionContext context) {
				CallerContext.setCallerID("job:" + IdGenerater.newId());
				SaasHander.set(context.getJobDetail().getJobDataMap().get("siteTag").toString());
			}

			@Override
			public void jobExecutionVetoed(JobExecutionContext context) {

			}

			@Override
			public String getName() {
				return "JobListener";
			}
		});

		if (!scheduler.isShutdown()) {
			scheduler.start();
		}

		return trigger.getNextFireTime();
		
	}

	public static Date newRunningJob(String JobDescription,int intervalSeconds, int startAtSeconds, Class<? extends Job> jobClass,
			Map<String, Object> par) throws SchedulerException {

		String jobName = jobClass.getName();
		if (par != null) {
			jobName = jobName + "-" + par.hashCode();
		}

		//Log.msgLog.info("Job start:[" + jobName + "]");

		JobDetail job = newJob(jobClass).withDescription(JobDescription).withIdentity(jobName, "group_" + jobName).build();

		if(par!=null)
		{
			job.getJobDataMap().putAll(par); 
		}

		Trigger trigger = newTrigger().withIdentity("trigger_" + jobName, "group_" + jobName)
				.startAt(new Date(new Date().getTime() + startAtSeconds * 1000))
				.withSchedule(simpleSchedule().withIntervalInSeconds(intervalSeconds).repeatForever()).build();

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		scheduler.scheduleJob(job, trigger);

		scheduler.getListenerManager().addJobListener(new JobListener() {

			@Override
			public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
				if (jobException != null) {
					Log.logError(jobException, context.getJobDetail().getJobClass().getName());
				}

				CallerContext.dispose();
			}

			@Override
			public void jobToBeExecuted(JobExecutionContext context) {
				CallerContext.setCallerID("job:" + IdGenerater.newId());
			}

			@Override
			public void jobExecutionVetoed(JobExecutionContext context) {

			}

			@Override
			public String getName() {
				return "JobListener";
			}
		});

		if (!scheduler.isShutdown()) {
			scheduler.start();
		}

		return trigger.getNextFireTime();
	}

	public static Date newRunningJob(String JobDescription,String cron, Class<? extends Job> jobClass, Map<String, Object> par,
			String siteTag) throws SchedulerException {

		String className =jobClass.getName();
		if(par.containsKey("ClassLoader"))
		{
			Class<?> classLoader =(Class<?>) par.get("ClassLoader");
			
			className = classLoader.getName();
		}	

		String jobName = className + "-" + siteTag;
		if (par != null) {
			jobName = jobName + "-" + par.hashCode();
		}

		// Log.msgLog.info("Job start:[" + jobName + "]");

		JobDetail job = newJob(jobClass).withDescription(JobDescription).withIdentity(jobName, "group_" + jobName).build();

		 
		if(par!=null)
		{
			job.getJobDataMap().putAll(par); 
		}
		job.getJobDataMap().put("siteTag", siteTag);
		
		Trigger trigger = newTrigger().withIdentity("trigger_" + jobName, "group_" + jobName)
				// .startAt(new Date(new Date().getTime() + startAtSeconds * 1000))
				.startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND))
				.withSchedule(CronScheduleBuilder.cronSchedule(cron)).startNow().build();

		IJobEvent jobEvent =  Holder.getBean(IJobEvent.class, "JobEvent");
		if(jobEvent!=null)
		{			
			jobEvent.onStart(jobName, JsonHelp.toJson(par) , cron, null, JobDescription, className, trigger.getNextFireTime(), siteTag);
		}	
		
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		scheduler.scheduleJob(job, trigger);

		scheduler.getListenerManager().addJobListener(new JobListener() {

			@Override
			public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
				if (jobException != null) {
					Log.logError(jobException, context.getJobDetail().getJobClass().getName());
				}

				CallerContext.dispose();
			}

			@Override
			public void jobToBeExecuted(JobExecutionContext context) {
				CallerContext.setCallerID("job:" + IdGenerater.newId());
				SaasHander.set(context.getJobDetail().getJobDataMap().get("siteTag").toString());
			}

			@Override
			public void jobExecutionVetoed(JobExecutionContext context) {

			}

			@Override
			public String getName() {
				return "JobListener";
			}
		});

		if (!scheduler.isShutdown()) {
			scheduler.start();
		}

		return trigger.getNextFireTime();
	}

	public static Date newRunningJob(String JobDescription,String cron, Class<? extends Job> jobClass, Map<String, Object> par)
			throws SchedulerException {

		String jobName = jobClass.getName();
		if (par != null) {
			jobName = jobName + "-" + par.hashCode();
		}

		// Log.msgLog.info("Job start:[" + jobName + "]");

		JobDetail job = newJob(jobClass).withDescription(JobDescription).withIdentity(jobName, "group_" + jobName).build();

		if(par!=null)
		{
			job.getJobDataMap().putAll(par); 
		}

		Trigger trigger = newTrigger().withIdentity("trigger_" + jobName, "group_" + jobName)
				// .startAt(new Date(new Date().getTime() + startAtSeconds * 1000))
				.startAt(DateBuilder.futureDate(1, IntervalUnit.SECOND))
				.withSchedule(CronScheduleBuilder.cronSchedule(cron)).startNow().build();

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		scheduler.scheduleJob(job, trigger);

		scheduler.getListenerManager().addJobListener(new JobListener() {

			@Override
			public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
				if (jobException != null) {
					Log.logError(jobException, context.getJobDetail().getJobClass().getName());
				}

				CallerContext.dispose();
			}

			@Override
			public void jobToBeExecuted(JobExecutionContext context) {
				CallerContext.setCallerID("job:" + IdGenerater.newId());

			}

			@Override
			public void jobExecutionVetoed(JobExecutionContext context) {

			}

			@Override
			public String getName() {
				return "JobListener";
			}
		});

		if (!scheduler.isShutdown()) {
			scheduler.start();
		}

		return trigger.getNextFireTime();
	}


	public static void deleteMyjob(Class<? extends Job> jobClass) throws SchedulerException {

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		JobKey deleteJobKey = null;

		Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.anyGroup());

		for (JobKey jobKey : keys) {

			if (jobKey.getName().contains(jobClass.getName())) {
				deleteJobKey = jobKey;
			}
		}

		if (deleteJobKey != null) {
			deleteMyjob(deleteJobKey);
		}

	}

	public static void deleteMyjob(JobKey jobKey) throws SchedulerException {

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		// scheduler.getJobKeys(GroupMatcher.anyGroup())

		scheduler.deleteJob(jobKey);

		Log.msgLog.info("Job deleteJob:[" + jobKey.getName() + "]");
	}

}
