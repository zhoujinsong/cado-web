package com.ccnt.cado.datafetch;

import java.util.Map;

public interface CommandArgumentLoader {
	public Map<String,Object> getArguments(MonitorObject obj);
}
