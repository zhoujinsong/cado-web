package com.ccnt.cado.datafetch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
















import static org.quartz.JobBuilder.newJob;  
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule; 

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.web.bean.PlatformInfo;


public class DataFetchScheduler {
	private Scheduler scheduler;
	private Map<MonitorObject,List<JobKey>> keys;
	private DataStorer dataStorer;
	private static List<String> appMonitor = new ArrayList<String>();
	private static List<String> appMetric = new ArrayList<String>();
	private static List<String> hostMonitor = new ArrayList<String>();
	private static List<String> hostMetric = new ArrayList<String>();
	static{
		appMonitor.add("PLATFORM_APPS");
		appMonitor.add("APP_SERVICES");
		appMonitor.add("SERVICE_INSTANCES");
		appMetric.add("INSTANCE_METRIC");
		appMetric.add("INSTANCE_METRIC_IO");
		hostMonitor.add("PLATFORM_HOSTS");
		hostMetric.add("HOST_METRIC");
	}
	public DataFetchScheduler(DataStorer dataStorer){
		super();
		this.dataStorer = dataStorer;
		keys = new HashMap<MonitorObject,List<JobKey>>();
		SchedulerFactory sf = new StdSchedulerFactory();      
        try {
			scheduler = sf.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}
	public void start(){
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	public void stop(){
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("static-access")
	public void schedule(MonitorObject obj,PlatformInfo platformInfo){
		List<JobKey> list = new ArrayList<JobKey>();
		for(Metric metric : obj.getMetrics()){
			JobDetail job = newJob(DataFetchJob.class)
		    .build();
			JobDataMap jobDataMap = job.getJobDataMap();
			jobDataMap.put("monitorObj", obj);
			jobDataMap.put("metric", metric);
			jobDataMap.put("scheduler", this);
			jobDataMap.put("dataStorer", dataStorer);
			jobDataMap.put("platformInfo", platformInfo);
			Trigger trigger = null;
			String metricDescp = metric.getDescription();
			int cycle = 0;
			if(appMonitor.contains(metricDescp)){
				cycle = platformInfo.getAppDataFetchCycle();
			}else if(appMetric.contains(metricDescp)){
				cycle = platformInfo.getAppMonitorCycle();
			}else if(hostMonitor.contains(metricDescp)){
				cycle = platformInfo.getHostDataFetchCycle();
			}else if(hostMetric.contains(metricDescp)){
				cycle = platformInfo.getHostMonitorCycle();
			}
			if(cycle > 0){
				trigger = newTrigger()
						.startNow()
						.withSchedule(simpleSchedule().repeatSecondlyForever(cycle))
						.build();
			}else{
				trigger = newTrigger()
						.startNow()
						.build();
			}
			try {
				scheduler.scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
			list.add(job.getKey());
		}
		
		keys.put(obj, list);
	}
	public void stopSchedule(MonitorObject obj){
		List<JobKey> list = keys.get(obj);
		try {
			scheduler.deleteJobs(list);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		for(Entry<String,Object> entry : obj.getAttributes().entrySet()){
			Object value = entry.getValue();
			if(value instanceof MonitorObject){
				stopSchedule((MonitorObject) value);
			}else if(value instanceof Collection){
				@SuppressWarnings("rawtypes")
				Collection coll = (Collection)value;
				for(Object object : coll){
					if(object instanceof MonitorObject){
						stopSchedule((MonitorObject) object);
					}
				}
			}
		}
	}
}
