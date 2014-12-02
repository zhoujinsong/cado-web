package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.monitor.SysMonitorService;
import com.ccnt.cado.service.CadoService;

public class AppSchedulerService implements CadoService {
	private List<AppScheduler> schedulers;
	private SysMonitorService monitorService;

	@Override
	public void start() {
		// TODO Auto-generated method stub
		//初始化schedulers
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}
	
	public void redeployPlatform(int platformId){
		//monitorService.restartPlatformMonitor(platformId);
	}

}
