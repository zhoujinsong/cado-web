package com.ccnt.cado.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logger {
	private static final Log log = LogFactory.getLog(Logger.class);

	public static void debug(String logInfo){
		if(log.isDebugEnabled()){
			log.debug(logInfo);
		}
	}
	public static void info(String logInfo){
		if(log.isInfoEnabled()){
			log.info(logInfo);
		}
	}
	public static void warn(String logInfo){
		if(log.isWarnEnabled()){
			log.warn(logInfo);
		}
	}
	public static void error(String logInfo){
		if(log.isErrorEnabled()){
			log.error(logInfo);
		}
	}
}
