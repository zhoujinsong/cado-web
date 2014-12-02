package com.ccnt.cado.algorithm.data;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccnt.cado.datastorage.DataStorer;



/**
 * 
 * @author LS
 * 获取格式化之后的所有虚拟机、部署单元的监控数据
 */
public class DataFetcher {
	private DataStorer dataStorer;
	private Map<Integer,Unit> vmCache;
	public DataFetcher(DataStorer dataStorer){
		super();
		this.dataStorer = dataStorer;
		vmCache = new HashMap<Integer,Unit>();
	}
	
	public DataStorer getDataStorer() {
		return dataStorer;
	}
	public List<VM> getVMsData(int platformId){
		//测试数据
		List<VM> vms = new ArrayList<VM>();
		Map<String,Object> hostQueryConditions = new HashMap<String,Object>();
		hostQueryConditions.put("class", "host");
		hostQueryConditions.put("platform_Id", platformId);
		List<Map<String,Object>> hostInfos = dataStorer.getMonitorObjects(hostQueryConditions);
		for(Map<String,Object> hostInfo : hostInfos){
			Unit staticUnit = null;
			while(!vmCache.containsKey(hostInfo.get("_id"))){
				freshVMCache(platformId);
			}
			staticUnit = vmCache.get(hostInfo.get("_id"));
			double usedCpu = 0, usedMemory = 0, usedIO = 0;
			Map<String,Object> usedQueryConditions = new HashMap<String,Object>();
			usedQueryConditions.put("monitorObjectId", hostInfo.get("_id"));
			List<Map<String,Object>> hostUsedInfos = dataStorer.getNewestMetricData(usedQueryConditions, 1);
			if(hostUsedInfos.size() > 0 ){
				Map<String,Object> hostUsedInfo = hostUsedInfos.get(0);
				usedCpu = (100 - (Integer)hostUsedInfo.get("cpu_id")) / 100.0 * staticUnit.getCpu();
				usedMemory = staticUnit.getMemeory() - (Integer)hostUsedInfo.get("free_memory");
				usedIO = ((Integer)hostUsedInfo.get("io_bi") + (Integer)hostUsedInfo.get("io_bo")) / 1000.0;
			}
			Unit usedUnit = new Unit(usedCpu, usedMemory, usedIO, -1);
			VM vm = new VM((Integer)hostInfo.get("_id"), staticUnit, usedUnit);
			vms.add(vm);
		}
		return vms;
	}
	public List<Deploy> getDeployUnitsData(int platformId){
		List<Deploy> deploys = new ArrayList<Deploy>();
		Map<String,Object> queryConditions = new HashMap<String,Object>();
		queryConditions.put("class", "application");
		queryConditions.put("platform_Id", "platformId");
		List<Map<String,Object>> applicationInfos = dataStorer.getMonitorObjects(queryConditions);
		for(Map<String,Object> applicationInfo : applicationInfos){
			queryConditions.clear();
			queryConditions.put("class", "service");
			queryConditions.put("application_Id", applicationInfo.get("_id"));
			List<Map<String,Object>> serviceInfos = dataStorer.getMonitorObjects(queryConditions);
			for(Map<String,Object> serviceInfo : serviceInfos){
				queryConditions.clear();
				queryConditions.put("class", "instance");
				queryConditions.put("service_Id", serviceInfo.get("_id"));
				List<Map<String,Object>> instanceInfos = dataStorer.getMonitorObjects(queryConditions);
				for(Map<String,Object> instanceInfo : instanceInfos){
					while(!vmCache.containsKey(instanceInfo.get("host_Id"))){
						freshVMCache(platformId);
					}
					double instanceCpu = 0, instanceMemory = 0, instanceIO = 0;
					Map<String,Object> metricQueryConditions = new HashMap<String,Object>();
					metricQueryConditions.put("monitorObjectId", instanceInfo.get("_id"));
					List<Map<String,Object>> metricDatas = dataStorer.getNewestMetricData(metricQueryConditions, 2);
					if(metricDatas.size() > 0){
						instanceCpu = (Double)metricDatas.get(0).get("Process Cpu Usage") * vmCache.get((Integer)instanceInfo.get("host_Id")).getCpu();
						instanceMemory = (Integer)metricDatas.get(0).get("Total Process Residental Memory");
						instanceIO = metricDatas.size() > 1 ? (Integer.parseInt((String)metricDatas.get(1).get("io_read_bytes")) + Integer.parseInt((String)metricDatas.get(1).get("io_write_bytes")) - 
								Integer.parseInt((String)metricDatas.get(0).get("io_read_bytes")) - Integer.parseInt((String)metricDatas.get(0).get("io_write_bytes"))) / (60*1024*1024.0) : 
									(Integer.parseInt((String)metricDatas.get(0).get("io_read_bytes")) + Integer.parseInt((String)metricDatas.get(0).get("io_write_bytes"))) / (60*1024*1024.0);
					}
					Unit metric = new Unit(instanceCpu, instanceMemory, instanceIO, -1);
					Deploy deploy = new Deploy(metric, (Integer)instanceInfo.get("host_Id"), (Integer)instanceInfo.get("_id"));
					deploys.add(deploy);
				}
			}
		}
		return deploys;
	}
	
	public Map<String,Object> getPlatformByInfoId(int platformInfoId){
		Map<String,Object> queryConditions = new HashMap<String,Object>();
		queryConditions.put("platformInfo_Id", platformInfoId);
		List<Map<String,Object>> platform = dataStorer.getMonitorObjects(queryConditions);
		while(platform.size() < 1){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			platform = dataStorer.getMonitorObjects(queryConditions);
		}
		return platform.get(0);
	}
	
	private void freshVMCache(int platformId){
		Map<String,Object> hostQueryConditions = new HashMap<String,Object>();
		hostQueryConditions.put("class", "host");
		hostQueryConditions.put("platform_Id", platformId);
		List<Map<String,Object>> hostInfos = dataStorer.getMonitorObjects(hostQueryConditions);
		for(Map<String,Object> hostInfo : hostInfos){
			double staticCpu = 0, staticMemory = 0, staticIO = 0;
			Map<String,Object> deviceQueryConditions = new HashMap<String,Object>();
			deviceQueryConditions.put("class", "device");
			deviceQueryConditions.put("host_Id", hostInfo.get("_id"));
			List<Map<String,Object>> deviceInfos = dataStorer.getMonitorObjects(deviceQueryConditions);
			if(deviceInfos.size() < 3){
				break;
			}
			for(Map<String,Object> deviceInfo : deviceInfos){
				if(deviceInfo.get("name").equals("cpu")){
					@SuppressWarnings("unchecked")
					List<Map<String,Object>> cores = (List<Map<String, Object>>) deviceInfo.get("cores");
					for(Map<String,Object> core : cores){
						staticCpu += (Double)core.get("frequency");
					}
				}else if(deviceInfo.get("name").equals("memory")){
					staticMemory = (Double)deviceInfo.get("size");
				}else if(deviceInfo.get("name").equals("io")){
					staticIO = (Double)deviceInfo.get("readSpeed");
				}
			}
			Unit staticUnit = new Unit(staticCpu, staticMemory, staticIO, -1);
			vmCache.put((Integer)hostInfo.get("_id"), staticUnit);
		}	
	}
	public List<Unit> getHistoryData(Date from, Date to, Deploy d) {
		return new ArrayList<Unit>();
	}
	public List<Deploy> getDeploysByVM(VM vm) {
		return new ArrayList<Deploy>();
	}
} 
