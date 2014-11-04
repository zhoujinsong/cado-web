package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.web.bean.PlatformInfo;

public interface AppScheduler {
	public List<Deploy> doSchedule(SystemMonitor monitor,PlatformInfo platformInfo);
}
