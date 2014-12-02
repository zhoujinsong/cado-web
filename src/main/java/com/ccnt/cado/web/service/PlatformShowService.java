package com.ccnt.cado.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ccnt.cado.algorithm.scheduler.AppSchedulerService;
import com.ccnt.cado.datastorage.DataStorer;

@Service("platformShowService")
public class PlatformShowService {
	private DataStorer dataStorer;
	
	@Autowired
	private AppSchedulerService schedulerService;
	
	@Autowired
	public PlatformShowService(@Qualifier("mongoDataStorer")DataStorer dataStorer) {
		super();
		this.dataStorer = dataStorer;
	}

	public List<Map<String,Object>> getPlatformHostsInfo(int platformInfoId){
		Map<String,Object> queryConditions = new HashMap<String,Object>();
		queryConditions.put("class", "platform");
		queryConditions.put("platformInfo_Id", platformInfoId);
		List<Map<String,Object>> platformAttrs = dataStorer.getMonitorObjects(queryConditions);
		queryConditions.clear();
		queryConditions.put("class", "host");
		queryConditions.put("platform_Id", platformAttrs.get(0).get("_id"));
		return dataStorer.getMonitorObjects(queryConditions);
	}
	public List<Map<String,Object>> getHostInstancesInfo(int hostId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> queryConditions = new HashMap<String,Object>();
		queryConditions.put("class", "instance");
		queryConditions.put("host_Id", hostId);
		List<Map<String,Object>> instanceAttrs = dataStorer.getMonitorObjects(queryConditions);
		for(Map<String,Object> instanceAttr : instanceAttrs){
			Map<String,Object> instanceInfo = new HashMap<String,Object>();
			instanceInfo.put("instanceId", instanceAttr.get("instanceId"));
			queryConditions.clear();
			queryConditions.put("_id", instanceAttr.get("service_Id"));
			List<Map<String,Object>> serviceAttrs = dataStorer.getMonitorObjects(queryConditions);
			instanceInfo.put("serviceName", serviceAttrs.get(0).get("name"));
			queryConditions.clear();
			queryConditions.put("_id", serviceAttrs.get(0).get("application_Id"));
			List<Map<String,Object>> applicationAttrs = dataStorer.getMonitorObjects(queryConditions);
			instanceInfo.put("applicationName", applicationAttrs.get(0).get("name"));
			list.add(instanceInfo);
		}
		return list;
	}
	public Map<String,Object> getHostMonitorData(int hostId){
		Map<String,Object> monitorData = new HashMap<String,Object>();
		Map<String,Object> queryConditions = new HashMap<String,Object>();
		queryConditions.put("class", "device");
		queryConditions.put("host_Id", hostId);
		List<Map<String,Object>> deviceInfos = dataStorer.getMonitorObjects(queryConditions);
		for(Map<String,Object> deviceInfo : deviceInfos){
			if(deviceInfo.get("name").equals("memory")){
				monitorData.put("memorySize", deviceInfo.get("size"));
			}else if(deviceInfo.get("name").equals("io")){
				monitorData.put("ioReadSpeed", deviceInfo.get("readSpeed"));
			}
		}
		queryConditions.clear();
		queryConditions.put("monitorObjectId", hostId);
		List<Map<String,Object>> monitorDatas = dataStorer.getNewestMetricData(queryConditions, 1);
		if(monitorDatas.size() > 0){
			monitorData.put("cpuUsagePercent", 100 - (Integer)monitorDatas.get(0).get("cpu_id"));
			monitorData.put("usedMemorySize", (Double)monitorData.get("memorySize") - (Integer)monitorDatas.get(0).get("free_memory"));
			monitorData.put("usedIOSpeed", ((Integer)monitorDatas.get(0).get("io_bi") + (Integer)monitorDatas.get(0).get("io_bo")) / 1000.0);
		}
		return monitorData;
	}
	
	public double getSystemUsage(int platformInfoId){
		return 0.0;
		//return schedulerService.getSysMonitor().getPlatformUsage(platformInfoId);
	}
}
