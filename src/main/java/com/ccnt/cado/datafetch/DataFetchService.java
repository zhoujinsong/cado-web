package com.ccnt.cado.datafetch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.service.CadoService;
import com.ccnt.cado.web.bean.PlatformInfo;

@Service("dataFetchService")
public class DataFetchService implements CadoService{
	private DataStorer dataStorer;
	private DataFetchScheduler dataFetchScheduler;
	private Map<Integer,MonitorObject> platformCache;

	@Autowired
	public DataFetchService(@Qualifier("mongoDataStorer") DataStorer dataStorer) {
		super();
		this.dataStorer = dataStorer;
		this.dataFetchScheduler = new DataFetchScheduler(dataStorer);
		platformCache = new HashMap<Integer,MonitorObject>();
	}

	@PostConstruct 
	public void start() {
		dataFetchScheduler.start();
		dataStorer.dropAll();
		List<PlatformInfo> platformInfos = dataStorer.getAllPlatforms();
		for(PlatformInfo platformInfo : platformInfos){
			fetchPlatform(platformInfo);
		}
	}
	
	@PreDestroy
	public void stop() {
		dataFetchScheduler.stop();
	}
	
	public void fetchPlatform(PlatformInfo platformInfo){
		Map<String,Object> attributes = new HashMap<String,Object>();
		attributes.put("platformInfo_Id",platformInfo.get_id());
		attributes.put("platformClass", platformInfo.getPlatformClass());
		attributes.put("version", platformInfo.getPlatformVer());
		attributes.put("class", "platform");
		attributes.put("name", platformInfo.getPlatformName());
		attributes.put("address", platformInfo.getPlatformAddr());
		attributes.put("port", platformInfo.getPlatformPort());
		MonitorObject platform = CloudifyFactory.getFactory().createPlatform(attributes);
		dataStorer.put(platform);
		dataFetchScheduler.schedule(platform,platformInfo);
		platformCache.put(platformInfo.get_id(), platform);
	}
	
	public void stopFetchPlatform(int platformInfoId){
		MonitorObject platform = platformCache.get(platformInfoId);
		dataStorer.remove(platform);
		dataFetchScheduler.stopSchedule(platform);
		platformCache.remove(platformInfoId);
	}
	
	public void updateFetchingPlatform(PlatformInfo platformInfo){
		stopFetchPlatform(platformInfo.get_id());
		fetchPlatform(platformInfo);
	}
}
