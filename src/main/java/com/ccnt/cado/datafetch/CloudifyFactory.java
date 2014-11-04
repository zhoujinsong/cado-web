package com.ccnt.cado.datafetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.web.bean.PlatformInfo;



public class CloudifyFactory implements PlatformFactory{
	private static final class singletonHelper { 
        private static final CloudifyFactory factory = new CloudifyFactory();
    }
	public static CloudifyFactory getFactory(){
		return singletonHelper.factory;
	}
	private CloudifyFactory(){
		super();
		PLATFORM_APPS = new Metric();
		PLATFORM_APPS.setCommand("http://{address}:{port}/service/applications/description");
		PLATFORM_APPS.setCommandResolver(new CommandResolver(){
			
			@SuppressWarnings("unchecked")
			public void resolve(String response,MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					List<MonitorObject> applications = object.getSons().get("applications");
					Map<String,MonitorObject> fathers = new HashMap<String,MonitorObject>();
					fathers.put("platform",object);
					Map<?, ?> map = mapper.readValue(response, Map.class);
					if(map.get("status").equals("success")){
						List<Map<String,Object>> attributesArray = new ArrayList<Map<String,Object>>();
						List<Map<?,?>> appDeses = (List<Map<?, ?>>) map.get("response");
						for(Map<?,?> appDes : appDeses){
							Map<String,Object> attributes = new HashMap<String,Object>();
							attributes.put("name", appDes.get("applicationName"));
							attributesArray.add(attributes);
						}
						for(MonitorObject monitorObject : applications){
							boolean tag = true;
							for(Map<String,Object> attributes : attributesArray){
								if(CloudifyFactory.equals(monitorObject,attributes,fathers)){
									attributesArray.remove(attributes);
									tag = false;
									break;
								}
							}
							if(tag){
								applications.remove(monitorObject);
								scheduler.stopSchedule(monitorObject);
								dataStorer.remove(monitorObject);
							}
						}
						for(Map<String,Object> attribute : attributesArray){
							MonitorObject application = CloudifyFactory.getFactory().createApplication(attribute,fathers);
							applications.add(application);
							dataStorer.put(application);
							scheduler.schedule(application,platformInfo);
							
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		PLATFORM_APPS.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> map = new HashMap<String,Object>();
				Map<String,Object> attributes = obj.getAttributes();
				map.put("address", attributes.get("address"));
				map.put("port", attributes.get("port"));
				map.put("version", attributes.get("version"));
				return map;
			}
		});
		PLATFORM_APPS.setCommandExcutor(REST_EXECUTOR);
		PLATFORM_APPS.setDescription("PLATFORM_APPS");
		
		PLATFORM_HOSTS = new Metric();
		PLATFORM_HOSTS.setCommand("http://{address}:{port}/{version}/templates");
		PLATFORM_HOSTS.setCommandResolver(new CommandResolver(){
			@SuppressWarnings("unchecked")
			
			public void resolve(String response,MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Map<?, ?> map = mapper.readValue(response, Map.class);
					if(map.get("status").equals("Success")){
						List<MonitorObject> hosts = object.getSons().get("hosts");
						List<Map<String,Object>> attributesArray = new ArrayList<Map<String,Object>>();
						Map<String,MonitorObject> fathers = new HashMap<String,MonitorObject>();
						fathers.put("platform",object);
						Map<?,?> tempDeses = (Map<?, ?>) ((Map<?, ?>) map.get("response")).get("templates");
						Set<String> keys = (Set<String>) tempDeses.keySet();
						for(String key : keys){
							Map<?,?> tempDes = (Map<?, ?>) tempDeses.get(key);
							String username = (String) (tempDes.containsKey("username") ? tempDes.get("username") : null);
							String password = (String) (tempDes.containsKey("password") ? tempDes.get("password") : null);
							List<Map<?,?>> nodes = (List<Map<?, ?>>) ((Map<?, ?>) tempDes.get("custom")).get("nodesList");
							for(Map<?,?> node : nodes){
								Map<String,Object> attributes = new HashMap<String,Object>();
								attributes.put("address", node.get("host-list"));
								attributes.put("username", node.containsKey("username") ? node.get("username") : username);
								attributes.put("password", node.containsKey("password") ? node.get("password") : password);
								attributesArray.add(attributes);
							}
						}
						for(MonitorObject monitorObject : hosts){
							boolean tag = true;
							for(Map<String,Object> attributes : attributesArray){
								if(CloudifyFactory.equals(monitorObject,attributes,fathers)){
									attributesArray.remove(attributes);
									tag = false;
									break;
								}
							}
							if(tag){
								hosts.remove(monitorObject);
								scheduler.stopSchedule(monitorObject);
								dataStorer.remove(monitorObject);
							}
						}
						for(Map<String,Object> attribute : attributesArray){
							MonitorObject host = CloudifyFactory.getFactory().createHost(attribute,fathers);
							hosts.add(host);
							dataStorer.put(host);
							scheduler.schedule(host,platformInfo);
							for(MonitorObject device : (List<MonitorObject>)host.getSons().get("devices")){
								scheduler.schedule(device,platformInfo);
							}
						}
						synchronized (object) {
							object.notify();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		PLATFORM_HOSTS.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> map = new HashMap<String,Object>();
				Map<String,Object> attributes = obj.getAttributes();
				map.put("address", attributes.get("address"));
				map.put("port", attributes.get("port"));
				map.put("version", attributes.get("version"));
				return map;
			}
		});
		PLATFORM_HOSTS.setCommandExcutor(REST_EXECUTOR);
		PLATFORM_HOSTS.setDescription("PLATFORM_HOSTS");
		
		CPU_CORES = new Metric();
		CPU_CORES.setCommand("cat /proc/cpuinfo");
		CPU_CORES.setCommandResolver(new CommandResolver() {
			
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				List<Map<String,Object>> cores = new ArrayList<Map<String,Object>>();
				String[] coreInfos = response.split("processor");
				for(int i=1;i<coreInfos.length;i++){
					String coreInfo = coreInfos[i];
					Map<String,Object> core = new HashMap<String,Object>();
					Pattern fPattern = Pattern.compile("^cpu MHz.*$",Pattern.MULTILINE);
					Matcher fMatcher = fPattern.matcher(coreInfo);
					if(fMatcher.find()){
						StringTokenizer frequencyST = new StringTokenizer(fMatcher.group());
						for(int j=0;j<3;j++){
							frequencyST.nextToken();
						}
						core.put("frequency", Double.parseDouble(frequencyST.nextToken()));
					}
					Pattern cPattern = Pattern.compile("^cache size.*$",Pattern.MULTILINE);
					Matcher cMatcher = cPattern.matcher(coreInfo);
					if(cMatcher.find()){
						StringTokenizer cacheST = new StringTokenizer(cMatcher.group());
						for(int j=0;j<3;j++){
							cacheST.nextToken();
						}
						core.put("cache", Double.parseDouble(cacheST.nextToken()));
					}
					cores.add(core);
				}
				object.getAttributes().put("cores",cores);
				dataStorer.put(object);
			}
		});
		CPU_CORES.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				MonitorObject host = obj.getFathers().get("host");
				Map<String,Object> arguments = new HashMap<String,Object>();
				arguments.put("address", host.getAttributes().get("address"));
				arguments.put("username", host.getAttributes().get("username"));
				arguments.put("password", host.getAttributes().get("password"));
				return arguments;
			}
		});
		CPU_CORES.setCommandExcutor(SSH_EXECUTOR);
		CPU_CORES.setDescription("CPU_CORES");
		
		MEMORY_SIZE = new Metric();
		MEMORY_SIZE.setCommand("cat /proc/meminfo");
		MEMORY_SIZE.setCommandResolver(new CommandResolver() {
			
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				StringTokenizer memoryST = new StringTokenizer(response);
				memoryST.nextToken();
				object.getAttributes().put("size",Double.parseDouble(memoryST.nextToken()));
				dataStorer.put(object);
			}
		});
		MEMORY_SIZE.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				MonitorObject host = obj.getFathers().get("host");
				Map<String,Object> arguments = new HashMap<String,Object>();
				arguments.put("address", host.getAttributes().get("address"));
				arguments.put("username", host.getAttributes().get("username"));
				arguments.put("password", host.getAttributes().get("password"));
				return arguments;
			}
		});
		MEMORY_SIZE.setCommandExcutor(SSH_EXECUTOR);
		MEMORY_SIZE.setDescription("MEMORY_SIZE");
		
		IO_READSPEED = new Metric();
		IO_READSPEED.setCommand("echo {password} | sudo -S hdparm -Tt /dev/sda");
		IO_READSPEED.setCommandResolver(new CommandResolver() {
			
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				String[] array = response.split("=");
				StringTokenizer speedST = new StringTokenizer(array[2]);
				object.getAttributes().put("readSpeed", Double.parseDouble(speedST.nextToken()));
				dataStorer.put(object);
			}
		});
		IO_READSPEED.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				MonitorObject host = obj.getFathers().get("host");
				Map<String,Object> arguments = new HashMap<String,Object>();
				arguments.put("address", host.getAttributes().get("address"));
				arguments.put("username", host.getAttributes().get("username"));
				arguments.put("password", host.getAttributes().get("password"));
				return arguments;
			}
		});
		IO_READSPEED.setCommandExcutor(SSH_EXECUTOR);
		IO_READSPEED.setDescription("IO_READSPEED");
		
		NETWORK_DOWNLOADSPEED = new Metric();
		NETWORK_DOWNLOADSPEED.setCommand("wget -O /dev/null http://speedtest.wdc01.softlayer.com/downloads/test10.zip");
		NETWORK_DOWNLOADSPEED.setCommandResolver(new CommandResolver() {
			
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				Double speed = 0.0;
				Pattern pattern = Pattern.compile("\\(.*B/s\\)");
				Matcher matcher = pattern.matcher(response);
				if(matcher.find()){
					String[] strArray = matcher.group().split(" ");
					String speedStr = strArray[0].substring(1,strArray[0].length());
					char unit = strArray[1].charAt(0);
					switch(unit){
					case 'K':
						speed = Double.parseDouble(speedStr) * 1000;
						break;
					case 'M':
						speed = Double.parseDouble(speedStr) * 1000000;
						break;
					case 'G':
						speed = Double.parseDouble(speedStr) * 1000000000;
						break;
					default:
						speed = Double.parseDouble(speedStr);
					}
				}
				object.getAttributes().put("downloadSpeed", speed);
				dataStorer.put(object);
			}
		});
		NETWORK_DOWNLOADSPEED.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				MonitorObject host = obj.getFathers().get("host");
				Map<String,Object> arguments = new HashMap<String,Object>();
				arguments.put("address", host.getAttributes().get("address"));
				arguments.put("username", host.getAttributes().get("username"));
				arguments.put("password", host.getAttributes().get("password"));
				return arguments;
			}
		});
		NETWORK_DOWNLOADSPEED.setCommandExcutor(SSH_EXECUTOR);
		NETWORK_DOWNLOADSPEED.setDescription("NETWORK_DOWNLOADSPEED");
		
		APP_SERVICES = new Metric();
		APP_SERVICES.setCommand("http://{address}:{port}/service/applications/{applicationName}/services/description");
		APP_SERVICES.setCommandResolver(new CommandResolver() {
			
			@SuppressWarnings("unchecked")
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Map<?, ?> map = mapper.readValue(response, Map.class);
					if(map.get("status").equals("success")){
						List<MonitorObject> services = object.getSons().get("services");
						List<Map<String,Object>> attributesArray = new ArrayList<Map<String,Object>>();
						Map<String,MonitorObject> fathers = new HashMap<String,MonitorObject>();
						fathers.put("application", object);
						List<Map<?,?>> appDeses = (List<Map<?, ?>>) map.get("response");
						for(Map<?,?> appDes : appDeses){
							List<Map<?,?>> serviceDeses = (List<Map<?, ?>>) appDes.get("servicesDescription");
							for(Map<?,?> serviceDes : serviceDeses){
								Map<String,Object> attributes = new HashMap<String,Object>();
								attributes.put("name", serviceDes.get("serviceName"));
								attributesArray.add(attributes);
							}
						}
						for(MonitorObject monitorObject : services){
							boolean tag = true;
							for(Map<String,Object> attributes : attributesArray){
								if(CloudifyFactory.equals(monitorObject,attributes,fathers)){
									attributesArray.remove(attributes);
									tag = false;
									break;
								}
							}
							if(tag){
								services.remove(monitorObject);
								scheduler.stopSchedule(monitorObject);
								dataStorer.remove(monitorObject);
							}
						}
						for(Map<String,Object> attribute : attributesArray){
							MonitorObject service = CloudifyFactory.getFactory().createService(attribute,fathers);
							services.add(service);
							dataStorer.put(service);
							scheduler.schedule(service,platformInfo);
							
						}
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		APP_SERVICES.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> arguments = new HashMap<String,Object>();
				MonitorObject platform = obj.getFathers().get("platform");
				arguments.put("address", platform.getAttributes().get("address"));
				arguments.put("port", platform.getAttributes().get("port"));
				arguments.put("applicationName", obj.getAttributes().get("name"));
				return arguments;
			}
		});
		APP_SERVICES.setCommandExcutor(REST_EXECUTOR);
		APP_SERVICES.setDescription("APP_SERVICES");
		
		SERVICE_INSTANCES = new Metric();
		SERVICE_INSTANCES.setCommand("http://{address}:{port}/service/applications/{applicationName}/services/description");
		SERVICE_INSTANCES.setCommandResolver(new CommandResolver() {
			
			@SuppressWarnings("unchecked")
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				ObjectMapper mapper = new ObjectMapper();
				MonitorObject application = object.getFathers().get("application");
				MonitorObject platform = application.getFathers().get("platform");
				List<MonitorObject> hosts = platform.getSons().get("hosts");
				
				try {
					Map<?, ?> map = mapper.readValue(response, Map.class);
					if(map.get("status").equals("success")){
						List<MonitorObject> instances = (List<MonitorObject>) object.getSons().get("instances");
						List<Map<String,Object>> attributesArray = new ArrayList<Map<String,Object>>();
						List<Map<String,MonitorObject>> fathersArray = new ArrayList<Map<String,MonitorObject>>();
						
						List<Map<?,?>> appDeses = (List<Map<?, ?>>) map.get("response");
						for(Map<?,?> appDes : appDeses){
							List<Map<?,?>> serviceDeses = (List<Map<?, ?>>) appDes.get("servicesDescription");
							for(Map<?,?> serviceDes : serviceDeses){
								if(object.getAttributes().get("name").equals(serviceDes.get("serviceName"))){
									List<Map<?,?>> instanceDeses = (List<Map<?, ?>>) serviceDes.get("instancesDescription");
									for(Map<?,?> instanceDes : instanceDeses){
										Map<String,Object> attributes = new HashMap<String,Object>();
										Map<String,MonitorObject> fathers = new HashMap<String,MonitorObject>();
										attributes.put("instanceId", instanceDes.get("instanceId"));
										String address = (String) instanceDes.get("hostAddress");
										MonitorObject host = null;
										synchronized (platform) {
											while(true){
												for(MonitorObject obj : hosts){
													if(obj.getAttributes().get("address").equals(address)){
														host = obj;
														break;
													}
												}
												if(host != null){
													break;
												}else{
													try {
														platform.wait();
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
												}
											}
										}
										fathers.put("host", host);
										fathers.put("service", object);
										attributesArray.add(attributes);
										fathersArray.add(fathers);
									}
									break;
								}
							}
						}
						for(MonitorObject monitorObject : instances){
							boolean tag = true;
							for(int i =0 ; i<attributesArray.size();i++){
								if(CloudifyFactory.equals(monitorObject,attributesArray.get(i),fathersArray.get(i))){
									attributesArray.remove(i);
									fathersArray.remove(i);
									tag = false;
									break;
								}
							}
							if(tag){
								instances.remove(monitorObject);
								scheduler.stopSchedule(monitorObject);
								dataStorer.remove(monitorObject);
							}
						}
						for(int i =0 ; i<attributesArray.size();i++){
							MonitorObject instance = CloudifyFactory.getFactory().createInstance(attributesArray.get(i),fathersArray.get(i));
							instances.add(instance);
							dataStorer.put(instance);
							scheduler.schedule(instance,platformInfo);
							
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		SERVICE_INSTANCES.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> arguments = new HashMap<String,Object>();
				MonitorObject application = obj.getFathers().get("application");
				MonitorObject platform = application.getFathers().get("platform");
				arguments.put("address", platform.getAttributes().get("address"));
				arguments.put("port", platform.getAttributes().get("port"));
				arguments.put("applicationName", application.getAttributes().get("name"));
				arguments.put("serviceName", obj.getAttributes().get("name"));
				return arguments;
			}
		});
		SERVICE_INSTANCES.setCommandExcutor(REST_EXECUTOR);
		SERVICE_INSTANCES.setDescription("SERVICE_INSTANCES");
		
		INSTANCE_METRIC = new Metric();
		INSTANCE_METRIC.setCommand("http://{address}:{port}/{version}/deployments/{appName}/service/{serviceName}/instances/{instanceId}/metrics");
		INSTANCE_METRIC.setCommandResolver(new CommandResolver() {
			
			@SuppressWarnings("unchecked")
			
			public void resolve(String response, MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo) {
				MetricData metricData = (MetricData) object.getMetric();
				ObjectMapper mapper = new ObjectMapper();
				try {
					Map<?, ?> map = mapper.readValue(response, Map.class);
					if(map.get("status").equals("Success")){
						Map<?,?> resp = (Map<?, ?>) map.get("response");
						Map<?,?> metricsData = (Map<?, ?>) resp.get("serviceInstanceMetricsData");
						Map<?,?> metrics = (Map<?, ?>) metricsData.get("metrics");
						Set<String> keys = (Set<String>) metrics.keySet();
						for(String key : keys){
							metricData.getDatas().put(key, metrics.get(key));
						}
						synchronized (metricData) {
							metricData.notify();
						}
						synchronized(object){
							try {
								object.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						metricData.setTime(new Date());
						metricData.setMonitorObject(object);
						dataStorer.put(metricData);
						
					}
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		INSTANCE_METRIC.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> arguments = new HashMap<String,Object>();
				MonitorObject service = obj.getFathers().get("service");
				MonitorObject application = service.getFathers().get("application");
				MonitorObject platform = application.getFathers().get("platform");
				arguments.put("address", platform.getAttributes().get("address"));
				arguments.put("port", platform.getAttributes().get("port"));
				arguments.put("version", platform.getAttributes().get("version"));
				arguments.put("appName", application.getAttributes().get("name"));
				arguments.put("serviceName", service.getAttributes().get("name"));
				arguments.put("instanceId", obj.getAttributes().get("instanceId"));
				return arguments;
			}
		});
		INSTANCE_METRIC.setCommandExcutor(REST_EXECUTOR);
		INSTANCE_METRIC.setDescription("INSTANCE_METRIC");
		
		INSTANCE_METRIC_IO = new Metric();
		INSTANCE_METRIC_IO.setCommand("cat /proc/{pid}/io");
		INSTANCE_METRIC_IO.setCommandResolver(new CommandResolver() {
			
			public void resolve(String response, MonitorObject object,
					DataFetchScheduler scheduler, DataStorer dataStorer,PlatformInfo platformInfo) {
				MetricData metricData = object.getMetric();
				String[] array = response.split("\\s");
				metricData.getDatas().put("io_read_bytes", array[9]);
				metricData.getDatas().put("io_write_bytes", array[11]);
				synchronized(object){
						object.notify();
				}
			}
		});
		INSTANCE_METRIC_IO.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> arguments = new HashMap<String,Object>();
				MetricData metricData = obj.getMetric();
				MonitorObject host =  obj.getFathers().get("host");
				arguments.put("address", host.getAttributes().get("address"));
				arguments.put("username", host.getAttributes().get("username"));
				arguments.put("password", host.getAttributes().get("password"));
				Integer pid = (Integer) metricData.getDatas().get("USM_Actual Process ID");
				synchronized (metricData) {
					while(true){
						if(pid == null){
							try {
								metricData.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							pid = (Integer) metricData.getDatas().get("USM_Actual Process ID");
						}else{
							break;
						}
					}
				}
				arguments.put("pid", pid);
				return arguments;
			}
		});
		INSTANCE_METRIC_IO.setCommandExcutor(SSH_EXECUTOR);
		INSTANCE_METRIC.setDescription("INSTANCE_METRIC");
		
		HOST_METRIC = new Metric();
		HOST_METRIC.setCommand("vmstat 1 5");
		HOST_METRIC.setCommandResolver(new CommandResolver() {
			
			
			public void resolve(String response, MonitorObject object,
					DataFetchScheduler scheduler, DataStorer dataStorer,PlatformInfo platformInfo) {
				MetricData data = object.getMetric();
				String[] array = response.split("\n");
				int free = 0,bi = 0,bo = 0,id = 0;
				for(int i=2;i<array.length;i++){
					StringTokenizer st = new StringTokenizer(array[i]);
					for(int j=0;j<3;j++){
						st.nextToken();
					}
					free += Integer.parseInt(st.nextToken());
					for(int j=0;j<4;j++){
						st.nextToken();
					}
					bi += Integer.parseInt(st.nextToken());
					bo += Integer.parseInt(st.nextToken());
					for(int j=0;j<4;j++){
						st.nextToken();
					}
					id += Integer.parseInt(st.nextToken());
				}
				data.getDatas().put("free_memory", free/5);
				data.getDatas().put("io_bi", bi/5);
				data.getDatas().put("io_bo", bo/5);
				data.getDatas().put("cpu_id", id/5);
				data.setTime(new Date());
				data.setMonitorObject(object);
				dataStorer.put(data);
			}
		});
		HOST_METRIC.setCommandArgumentLoader(new CommandArgumentLoader() {
			
			
			public Map<String, Object> getArguments(MonitorObject obj) {
				Map<String,Object> arguments = new HashMap<String,Object>();
				arguments.put("address", obj.getAttributes().get("address"));
				arguments.put("username", obj.getAttributes().get("username"));
				arguments.put("password", obj.getAttributes().get("password"));
				return arguments;
			}
		});
		HOST_METRIC.setCommandExcutor(SSH_EXECUTOR);
		HOST_METRIC.setDescription("HOST_METRIC");
	}
	
	private final RestCommandExecutor REST_EXECUTOR = new RestCommandExecutor();
	private final SSHCommandExecutor SSH_EXECUTOR = new SSHCommandExecutor();
	private final Metric PLATFORM_APPS;
	private final Metric PLATFORM_HOSTS;
	private final Metric CPU_CORES;
	private final Metric MEMORY_SIZE;
	private final Metric IO_READSPEED;
	private final Metric NETWORK_DOWNLOADSPEED;
	private final Metric APP_SERVICES;
	private final Metric SERVICE_INSTANCES;
	private final Metric INSTANCE_METRIC;
	private final Metric INSTANCE_METRIC_IO;
	private final Metric HOST_METRIC;
	
	
	public MonitorObject createPlatform(Map<String, Object> attributes) {
		MonitorObject platform = new MonitorObject();
		platform.setAttributes(attributes);
		platform.getAttributes().put("class","platform");
		platform.getAttributes().put("name","cloudify");
		platform.getSons().put("applications",new ArrayList<MonitorObject>());
		platform.getSons().put("hosts",new ArrayList<MonitorObject>());
		platform.getMetrics().add(PLATFORM_APPS);
		platform.getMetrics().add(PLATFORM_HOSTS);
		return platform;
	}
	
	public MonitorObject createHost(Map<String, Object> attributes,Map<String,MonitorObject> fathers) {
		MonitorObject host = new MonitorObject();
		host.setAttributes(attributes);
		host.setFathers(fathers);
		host.getAttributes().put("class", "host");
		List<MonitorObject> devices = new ArrayList<MonitorObject>();
		Map<String,Object> attri = new HashMap<String,Object>();
		Map<String,MonitorObject> fath = new HashMap<String,MonitorObject>();
		fath.put("host", host);
		attri.put("name", "cpu");
		devices.add(CloudifyFactory.getFactory().createDevice(attri,fath));
		attri = new HashMap<String,Object>();
		attri.put("name", "memory");
		devices.add(CloudifyFactory.getFactory().createDevice(attri,fath));
		attri = new HashMap<String,Object>();
		attri.put("name", "io");
		devices.add(CloudifyFactory.getFactory().createDevice(attri,fath));
		attri = new HashMap<String,Object>();
		attri.put("name", "network");
		devices.add(CloudifyFactory.getFactory().createDevice(attri,fath));
		host.getSons().put("devices", devices);
		host.setMetric(new MetricData());
		host.getMetrics().add(HOST_METRIC);
		return host;
	}
	
	public MonitorObject createApplication(Map<String, Object> attributes,Map<String,MonitorObject> fathers) {
		MonitorObject application = new MonitorObject();
		application.setAttributes(attributes);
		application.setFathers(fathers);
		application.getAttributes().put("class","application");
		application.getSons().put("services",new ArrayList<MonitorObject>());
		application.getMetrics().add(APP_SERVICES);
		return application;
	}
	
	public MonitorObject createService(Map<String, Object> attributes,Map<String,MonitorObject> fathers) {
		MonitorObject service = new MonitorObject();
		service.setAttributes(attributes);
		service.setFathers(fathers);
		service.getAttributes().put("class","service");
		service.getSons().put("instances",new ArrayList<MonitorObject>());
		service.getMetrics().add(SERVICE_INSTANCES);
		return service;
	}
	
	public MonitorObject createInstance(Map<String, Object> attributes,Map<String,MonitorObject> fathers) {
		MonitorObject instance = new MonitorObject();
		instance.setAttributes(attributes);
		instance.setFathers(fathers);
		instance.getAttributes().put("class", "instance");
		instance.setMetric(new MetricData());
		instance.getMetrics().add(INSTANCE_METRIC);
		instance.getMetrics().add(INSTANCE_METRIC_IO);
		return instance;
	}
	
	public MonitorObject createDevice(Map<String, Object> attributes,Map<String,MonitorObject> fathers) {
		MonitorObject device = new MonitorObject();
		device.setAttributes(attributes);
		device.setFathers(fathers);
		device.getAttributes().put("class", "device");
		String name = (String) attributes.get("name");
		if("cpu".equals(name)){
			device.getMetrics().add(CPU_CORES);
		}else if("memory".equals(name)){
			device.getMetrics().add(MEMORY_SIZE);
		}else if("io".equals(name)){
			device.getMetrics().add(IO_READSPEED);
		}else if("network".equals(name)){
			device.getMetrics().add(NETWORK_DOWNLOADSPEED);
		}
		return device;
	}
	
	private static boolean equals(MonitorObject monitorObject,Map<String,Object> attributes,Map<String,MonitorObject> fathers){
		String className = (String) monitorObject.getAttributes().get("class");
		if("application".equals(className)){
			return attributes.get("name").equals(monitorObject.getAttributes().get("name"))&&
					fathers.get("platform").equals(monitorObject.getFathers().get("platform"));
		}else if("host".equals(className)){
			return attributes.get("address").equals(monitorObject.getAttributes().get("address"))&&
					fathers.get("platform").equals(monitorObject.getFathers().get("platform"));
		}else if("service".equals(className)){
			return attributes.get("name").equals(monitorObject.getAttributes().get("name")) &&
					fathers.get("application").equals(monitorObject.getFathers().get("application"));
		}else if("instance".equals(className)){
			return attributes.get("instanceId").equals(monitorObject.getAttributes().get("instanceId")) &&
					fathers.get("service").equals(monitorObject.getFathers().get("service"));
		}else if("device".equals(className)){
			return attributes.get("name").equals(monitorObject.getAttributes().get("name")) &&
					fathers.get("host").equals(monitorObject.getFathers().get("host"));
		}else{
			return false;
		}

	}
	
	
	
	
	
	
	
}
