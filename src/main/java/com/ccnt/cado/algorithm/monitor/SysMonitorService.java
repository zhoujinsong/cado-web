package com.ccnt.cado.algorithm.monitor;

import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.SysState;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.scheduler.AppSchedulerService;
import com.ccnt.cado.algorithm.scheduler.SchedulerService;
import com.ccnt.cado.service.CadoService;
import com.ccnt.cado.util.Logger;

public class SysMonitorService implements CadoService,Job{
	
	@Autowired
	private AppSchedulerService schedulerService;
	private JobKey key;
	private SystemMonitor systemMonitor;
	private Map<Integer,Boolean> platformStates;
	
	@Override
	public void start() {
		//启动一个MoitorJob
	}	
	@Override
	public void stop() {
		//关闭MonitorJob
	}
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		//schedulerService.redeployPlatform(0);
		//platformStates.put(0, false);
		
		//对于每一个platform，循环检测是否不满足条件了，不满足调用schedulerService来执行迁移
		double max = 0.8, min = 0.4;
		int platformId = 0;
		SysState state = systemMonitor.computeSysState(max, min, platformId, new Unit(0.25,0.25,0.25,0.25));
		if(state.getScore() < 60) {	
			//SchedulerService执行调度
			if(state.getUsage() < min) {
				//合并，合并的条件比较苛刻，因为需要预留资源来应对以后的资源突增
			}else {
				//迁移
			}
		}
	}
	public void restartPlatformMonitor(int platformId){
		//platformStates.put(platformId, true);
	}

}
