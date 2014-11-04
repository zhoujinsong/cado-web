package com.ccnt.cado.algorithm.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.service.CadoService;
import com.ccnt.cado.web.bean.PlatformInfo;

@Service("quartzServer")
public class SchedulerService implements CadoService{
	private Scheduler scheduler;
	private Map<Integer,JobKey> keys;
	private DataStorer dataStorer;
	private DataFetcher dataFetcher;
	private SystemMonitor sysMonitor;
	
	@Autowired
	public SchedulerService(@Qualifier("mongoDataStorer") DataStorer dataStorer) {
		super();
		this.dataStorer = dataStorer;
		this.dataFetcher = new DataFetcher(dataStorer);
		this.sysMonitor = new SystemMonitor(dataFetcher);
		keys = new HashMap<Integer,JobKey>();
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	

	public SystemMonitor getSysMonitor() {
		return sysMonitor;
	}


	//@PostConstruct 
	public void start() {
		List<PlatformInfo> platformInfos = dataStorer.getAllPlatforms();
		for (PlatformInfo platformInfo : platformInfos){
			addSchudulerJob(platformInfo);
		}
	}
	
	//@PreDestroy
	public void stop() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public void schedulerPlatform(PlatformInfo platformInfo){
		addSchudulerJob(platformInfo);
	}
	
	public void stopSchedulerPlatform(int platformInfoId){
		try {
			scheduler.deleteJob(keys.get(platformInfoId));
			keys.remove(platformInfoId);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateSchedulerPlatform(PlatformInfo platformInfo){
		stopSchedulerPlatform(platformInfo.get_id());
		schedulerPlatform(platformInfo);
	}
	
	private void addSchudulerJob(PlatformInfo platformInfo){
		JobDetail job = JobBuilder.newJob(ScheduleJob.class).
				withIdentity("job1","default_group")
				.build();
		Unit weigh = new Unit(platformInfo.getCpuWeight(), platformInfo.getMemoryWeight(), platformInfo.getIoWeight(), platformInfo.getNetworkWeight());
		AppScheduler appScheduler = new AHPScheduler(weigh);
		JobDataMap jobDataMap = job.getJobDataMap();
		jobDataMap.put("platformInfo", platformInfo);
		jobDataMap.put("appScheduler", appScheduler);
		jobDataMap.put("monitor", sysMonitor);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, platformInfo.getHostDataFetchCycle());
		Trigger trigger = TriggerBuilder
				.newTrigger().withIdentity("trigger1","default_group")
				.startAt(cal.getTime())
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withIntervalInSeconds(20)
				.repeatForever())
				.build();
		try {
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		keys.put(platformInfo.get_id(), job.getKey());
	}
}
