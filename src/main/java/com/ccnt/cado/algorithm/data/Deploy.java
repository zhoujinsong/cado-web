package com.ccnt.cado.algorithm.data;
/**
 * 
 * @author LS
 *	部署单位，用于调度计算
 */
public class Deploy {
	private Unit metrics;
	private int vmId;
	private int id;
	
	public Deploy(Unit metrics, int vmId, int unitId) {
		super();
		this.metrics = metrics;
		this.vmId = vmId;
		this.id = unitId;
	}
	public int getUnitId() {
		return id;
	}
	public void setUnitId(int unitId) {
		this.id = unitId;
	}

	public Unit getMetrics() {
		return metrics;
	}
	public void setMetrics(Unit metrics) {
		this.metrics = metrics;
	}
	public int getVmId() {
		return vmId;
	}
	public void setVmId(int vmId) {
		this.vmId = vmId;
	}
}
