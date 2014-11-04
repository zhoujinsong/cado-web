package com.ccnt.cado.datafetch;

import java.util.Map;

public interface CommandExecutor {
	public String execute(String command,Map<String,Object> config);
}
