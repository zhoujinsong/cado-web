package com.ccnt.cado.algorithm.data;

public class MigrateStep {
	//从 vmFrom虚拟机 迁移 deploy单元 到vmTo虚拟机
	private int vmFrom; 
	private int vmTo;
	private int deploy;
	
	public MigrateStep(int vmFrom, int vmTo, int deploy) {
		super();
		this.vmFrom = vmFrom;
		this.vmTo = vmTo;
		this.deploy = deploy;
	}
	public int getVmFrom() {
		return vmFrom;
	}
	public void setVmFrom(int vmFrom) {
		this.vmFrom = vmFrom;
	}
	public int getVmTo() {
		return vmTo;
	}
	public void setVmTo(int vmTo) {
		this.vmTo = vmTo;
	}
	public int getDeploy() {
		return deploy;
	}
	public void setDeploy(int deploy) {
		this.deploy = deploy;
	}	
}	
