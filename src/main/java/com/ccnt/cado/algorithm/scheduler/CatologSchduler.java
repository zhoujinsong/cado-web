package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.web.bean.PlatformInfo;

public class CatologSchduler implements AppScheduler{
	public List<ScheduleResult> doSchedule(List<Deploy> units, List<VM> vms) {
		return null;
	}

	public List<Deploy> doSchedule(SystemMonitor monitor, PlatformInfo platformInfo) {
		// TODO Auto-generated method stub
		return null;
	}
}
