package com.ccnt.cado.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ccnt.cado.algorithm.scheduler.SchedulerService;
import com.ccnt.cado.datafetch.DataFetchService;
import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.web.bean.PlatformInfo;

@Service("platformManagementService")
public class PlatformManagementService {
	private DataStorer dataStorer;
	@Autowired
	private DataFetchService dataFetchService;
	@Autowired
	private SchedulerService schedulerService;
	@Autowired
	public PlatformManagementService(@Qualifier("mongoDataStorer") DataStorer dataStorer) {
		super();
		this.dataStorer = dataStorer;
	}
	public void createPlatform(PlatformInfo platformInfo){
		dataStorer.put(platformInfo);
		dataFetchService.fetchPlatform(platformInfo);
		schedulerService.schedulerPlatform(platformInfo);
	}
	public void deletePlatforms(int[] platformIds){
		for(int platformId : platformIds){
			dataStorer.removePlatform(platformId);
			dataFetchService.stopFetchPlatform(platformId);
			schedulerService.stopSchedulerPlatform(platformId);
		}
	}
	public void updatePlatform(PlatformInfo platformInfo){
		dataStorer.updatePlatform(platformInfo);
	}
	public PlatformInfo getPlatform(int platformId){
		return dataStorer.getPlatform(platformId);
	}
	public List<PlatformInfo> getAllPlatforms(){
		return dataStorer.getAllPlatforms();
	}
	
}
