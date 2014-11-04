package com.ccnt.cado.datafetch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorObject {
	private List<Metric> metrics;
	private Map<String,Object> attributes;
	private Map<String,List<MonitorObject>> sons;
	private Map<String,MonitorObject> fathers;
	private MetricData metric;
	public MonitorObject() {
		super();
		this.metrics = new ArrayList<Metric>();
		this.attributes = new HashMap<String,Object>();
		this.sons = new HashMap<String,List<MonitorObject>>();
		this.fathers = new HashMap<String,MonitorObject>();
	}
	public MonitorObject(Map<String,Object> attributes) {
		super();
		this.metrics = new ArrayList<Metric>();
		this.attributes = attributes;
	}
	
	public List<Metric> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public Map<String, List<MonitorObject>> getSons() {
		return sons;
	}
	public void setSons(Map<String, List<MonitorObject>> sons) {
		this.sons = sons;
	}
	public Map<String, MonitorObject> getFathers() {
		return fathers;
	}
	public void setFathers(Map<String, MonitorObject> fathers) {
		this.fathers = fathers;
	}
	public MetricData getMetric() {
		return metric;
	}
	public void setMetric(MetricData metric) {
		this.metric = metric;
	}
}
