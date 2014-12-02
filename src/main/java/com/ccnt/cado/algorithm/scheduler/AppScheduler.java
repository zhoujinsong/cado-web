package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.MigrateStep;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.web.bean.PlatformInfo;

public interface AppScheduler {
	//迁移
	public List<MigrateStep> Migrate(SystemMonitor monitor, Predictor predictor,
			PlatformInfo platformInfo);
	//合并
	public List<MigrateStep> Merge(SystemMonitor monitor, Predictor predictor,
			PlatformInfo platformInfo);
}
