package com.ccnt.cado.datafetch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MetricData {
	private Date time;
	private Map<String,Object> datas;
	private MonitorObject monitorObject;
	
	public MetricData() {
		super();
		this.datas = new HashMap<String,Object>();
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Map<String, Object> getDatas() {
		return datas;
	}
	public void setDatas(Map<String, Object> datas) {
		this.datas = datas;
	}
	public MonitorObject getMonitorObject() {
		return monitorObject;
	}
	public void setMonitorObject(MonitorObject monitorObject) {
		this.monitorObject = monitorObject;
	}
	
}
