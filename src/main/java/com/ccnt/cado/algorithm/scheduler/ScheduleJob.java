package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.web.bean.PlatformInfo;

/**
 * 定时调度任务
 * @author LS
 *
 */
public class ScheduleJob implements Job{

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		JobDataMap jobDataMap = ctx.getJobDetail().getJobDataMap();
		PlatformInfo platformInfo = (PlatformInfo)jobDataMap.get("platformInfo");
		AppScheduler scheduler = (AppScheduler)jobDataMap.get("appScheduler");
		SystemMonitor monitor = (SystemMonitor)jobDataMap.get("monitor");
		List<Deploy> result = scheduler.doSchedule(monitor, platformInfo);
	}
}
