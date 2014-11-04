package com.ccnt.cado.datafetch;

import com.ccnt.cado.datastorage.DataStorer;
import com.ccnt.cado.web.bean.PlatformInfo;

public interface CommandResolver {
	public abstract void resolve(String response,MonitorObject object,DataFetchScheduler scheduler,DataStorer dataStorer,PlatformInfo platformInfo);
}
