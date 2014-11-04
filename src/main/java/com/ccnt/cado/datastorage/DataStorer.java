package com.ccnt.cado.datastorage;

import java.util.List;
import java.util.Map;

import com.ccnt.cado.datafetch.MetricData;
import com.ccnt.cado.datafetch.MonitorObject;
import com.ccnt.cado.web.bean.PlatformInfo;

public interface DataStorer {
	
	//dataFetch
	public List<Map<String,Object>> getMonitorObjects(Map<String,Object> queryConditions);
	public void dropAll();
	public void put(MonitorObject object);
	public void put(MetricData metricData);
	public void remove(MonitorObject object);
	
	//dataPresentation
	public void put(PlatformInfo platformInfo);
	public void removePlatform(int platformId);
	public void updatePlatform(PlatformInfo platformInfo);
	public PlatformInfo getPlatform(int platformId);
	public List<PlatformInfo> getAllPlatforms();
	public List<Map<String,Object>> getMetricData(Map<String,Object> queryConditions);
	public List<Map<String,Object>> getNewestMetricData(Map<String,Object> queryConditions, int count);
}
