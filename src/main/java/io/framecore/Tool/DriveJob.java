package io.framecore.Tool;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import io.framecore.Aop.Holder;
import io.framecore.Frame.IJobEvent;
import io.framecore.Frame.Log;
import io.framecore.Frame.Result;
import io.framecore.Saas.ICronFaceJob;
import io.framecore.Saas.IFaceJob;
import io.framecore.Saas.IIntervalFaceJob;
import io.framecore.Saas.JobContext;
import io.framecore.Saas.SaasHander;

@DisallowConcurrentExecution
public class DriveJob implements Job {

	public void start(String desc, Class<?> jobInterface, String siteTag) throws SchedulerException {

		if (IIntervalFaceJob.class.isAssignableFrom(jobInterface)) {

			IIntervalFaceJob faceJob = (IIntervalFaceJob) Holder.getService(jobInterface);

			int intervalSeconds = faceJob.getIntervalSeconds();
			int startAtSeconds = faceJob.getStartAtSeconds();

			List<HashMap<String, Object>> listArgs = faceJob.getParList();

			if (listArgs == null || listArgs.size() == 0) {
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("siteTag", siteTag);
				args.put("args", null);
				args.put("ClassLoader", jobInterface);

				JobHelp.newRunningJob(desc, intervalSeconds, startAtSeconds, DriveJob.class, args, siteTag);

			} else {
				for (Map<String, Object> map : listArgs) {

					Map<String, Object> args = new HashMap<String, Object>();
					args.put("siteTag", siteTag);
					args.put("args", map);
					args.put("ClassLoader", jobInterface);

					JobHelp.newRunningJob(desc, intervalSeconds, startAtSeconds, DriveJob.class, args, siteTag);

				}
			}

		} else if (ICronFaceJob.class.isAssignableFrom(jobInterface)) {

			ICronFaceJob faceJob = (ICronFaceJob) Holder.getService(jobInterface);
			String cron = faceJob.getCron();

			List<HashMap<String, Object>> listArgs = faceJob.getParList();

			if (listArgs == null || listArgs.size() == 0) {
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("siteTag", siteTag);
				args.put("args", null);
				args.put("ClassLoader", jobInterface);

				JobHelp.newRunningJob(desc, cron, DriveJob.class, args, siteTag);
			} else {
				for (Map<String, Object> map : listArgs) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("siteTag", siteTag);
					args.put("args", map);
					args.put("ClassLoader", jobInterface);

					JobHelp.newRunningJob(desc, cron, DriveJob.class, args, siteTag);

				}
			}

		}

	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		String desc = context.getJobDetail().getDescription();

		String siteTag = (String) context.getJobDetail().getJobDataMap().get("siteTag");

		Class<?> classLoader = (Class<?>) context.getJobDetail().getJobDataMap().get("ClassLoader");

		IJobEvent jobEvent = Holder.getBean(IJobEvent.class, "JobEvent");

		@SuppressWarnings("unchecked")
		Map<String, Object> args = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("args");
		Date from = new Date();
		try {

			IFaceJob faceJob = (IFaceJob) Holder.getService(classLoader);

			JobContext jobContext = new JobContext();

			jobContext.setArgs(args);

			String argsStr = "";
			if (args != null) {
				argsStr = args.toString();
			}

			Log.msgLog.info(SaasHander.currentHander().getSiteTag() + "执行Job("+desc+") 开始：%s,参数：%s" ,
					classLoader.getName(), argsStr);

			Result jobResult = faceJob.execute(jobContext);

			if (jobResult == null) {
				Log.msgLog.info(SaasHander.currentHander().getSiteTag() + "执行Job("+desc+") 错误：%s,参数：%s",
						classLoader.getName(), argsStr);

				if (jobEvent != null) {
					jobEvent.onExeced(context.getJobDetail().getKey().getName(), from, new Date(), -1,
							context.getTrigger().getNextFireTime());
				}
			} else {
				if (jobEvent != null) {
					jobEvent.onExeced(context.getJobDetail().getKey().getName(), from, new Date(),
							jobResult.getStatus(), context.getTrigger().getNextFireTime());
				}

			}

			Log.msgLog.info(SaasHander.currentHander().getSiteTag() + "执行Job("+desc+") 完成："+classLoader.getName()+",参数："+argsStr+"，结果：%s,%s"
					, jobResult.getStatus(), jobResult.getMessage());

		} catch (Exception e) {
			Log.logError(e);

			if (jobEvent != null) {
				jobEvent.onExeced(context.getJobDetail().getKey().getName(), from, new Date(), -1,
						context.getTrigger().getNextFireTime());
			}

		}

	}

}
