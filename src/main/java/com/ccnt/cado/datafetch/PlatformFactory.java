package com.ccnt.cado.datafetch;

import java.util.Map;

public interface PlatformFactory {
	public MonitorObject createPlatform(Map<String,Object> attributes);
	public MonitorObject createHost(Map<String,Object> attributes,Map<String,MonitorObject> fathers);
	public MonitorObject createApplication(Map<String,Object> attributes,Map<String,MonitorObject> fathers);
	public MonitorObject createService(Map<String,Object> attributes,Map<String,MonitorObject> fathers);
	public MonitorObject createInstance(Map<String,Object> attributes,Map<String,MonitorObject> fathers);
	public MonitorObject createDevice(Map<String,Object> attributes,Map<String,MonitorObject> fathers);
}
