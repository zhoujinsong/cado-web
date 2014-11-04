package com.ccnt.cado.algorithm.data;
/**
 * 
 * @author LS
 * 虚拟机
 */
public class VM {
	private int vmId;
	private Unit staticMetircs; //虚拟机最大资源
	private Unit usedMetrics; //虚拟机使用资源
	
	public VM(int vmId, Unit staticMetircs, Unit usedMetrics) {
		super();
		this.vmId = vmId;
		this.staticMetircs = staticMetircs;
		this.usedMetrics = usedMetrics;
	}
	public int getVmId() {
		return vmId;
	}
	public void setVmId(int vmId) {
		this.vmId = vmId;
	}
	public Unit getStaticMetircs() {
		return staticMetircs;
	}
	public void setStaticMetircs(Unit staticMetircs) {
		this.staticMetircs = staticMetircs;
	}
	public Unit getUsedMetrics() {
		return usedMetrics;
	}
	public void setUsedMetrics(Unit usedMetrics) {
		this.usedMetrics = usedMetrics;
	}
}
