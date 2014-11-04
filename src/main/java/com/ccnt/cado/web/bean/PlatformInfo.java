package com.ccnt.cado.web.bean;

public class PlatformInfo {
	private int _id;
	//基本信息
	private String platformClass;
	private String platformVer;
	private String platformName;
	private String platformAddr;
	private String platformPort;
	//获取数据参数
	private int hostDataFetchCycle;
	private int appDataFetchCycle;
	private int hostMonitorCycle;
	private int appMonitorCycle;
	//数据处理参数
	private int platformUsageMax;
	private int platformUsageMin;
	private int cpuWeight;
	private int memoryWeight;
	private int ioWeight;
	private int networkWeight;
	public PlatformInfo() {
		super();
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getPlatformClass() {
		return platformClass;
	}
	public void setPlatformClass(String platformClass) {
		this.platformClass = platformClass;
	}
	public String getPlatformVer() {
		return platformVer;
	}
	public void setPlatformVer(String platformVer) {
		this.platformVer = platformVer;
	}
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}	
	public String getPlatformAddr() {
		return platformAddr;
	}
	public void setPlatformAddr(String platformAddr) {
		this.platformAddr = platformAddr;
	}
	public String getPlatformPort() {
		return platformPort;
	}
	public void setPlatformPort(String platformPort) {
		this.platformPort = platformPort;
	}
	public int getHostDataFetchCycle() {
		return hostDataFetchCycle;
	}
	public void setHostDataFetchCycle(int hostDataFetchCycle) {
		this.hostDataFetchCycle = hostDataFetchCycle;
	}
	public int getAppDataFetchCycle() {
		return appDataFetchCycle;
	}
	public void setAppDataFetchCycle(int appDataFetchCycle) {
		this.appDataFetchCycle = appDataFetchCycle;
	}
	public int getHostMonitorCycle() {
		return hostMonitorCycle;
	}
	public void setHostMonitorCycle(int hostMonitorCycle) {
		this.hostMonitorCycle = hostMonitorCycle;
	}
	public int getAppMonitorCycle() {
		return appMonitorCycle;
	}
	public void setAppMonitorCycle(int appMonitorCycle) {
		this.appMonitorCycle = appMonitorCycle;
	}
	public int getPlatformUsageMax() {
		return platformUsageMax;
	}
	public void setPlatformUsageMax(int platformUsageMax) {
		this.platformUsageMax = platformUsageMax;
	}
	public int getPlatformUsageMin() {
		return platformUsageMin;
	}
	public void setPlatformUsageMin(int platformUsageMin) {
		this.platformUsageMin = platformUsageMin;
	}
	public int getCpuWeight() {
		return cpuWeight;
	}
	public void setCpuWeight(int cpuWeight) {
		this.cpuWeight = cpuWeight;
	}
	public int getMemoryWeight() {
		return memoryWeight;
	}
	public void setMemoryWeight(int memoryWeight) {
		this.memoryWeight = memoryWeight;
	}
	public int getIoWeight() {
		return ioWeight;
	}
	public void setIoWeight(int ioWeight) {
		this.ioWeight = ioWeight;
	}
	public int getNetworkWeight() {
		return networkWeight;
	}
	public void setNetworkWeight(int networkWeight) {
		this.networkWeight = networkWeight;
	}
	
	
}
