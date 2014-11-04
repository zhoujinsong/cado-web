package com.ccnt.cado.algorithm.data;

/**
 * 存储系统状态
 * @author Lisa
 *
 */
public class SysState {
	private double usage = -1; 
	private double variance = -1; 
	private double score = -1; 
	
	public boolean betterThan(SysState anotherState){

		return false;
	}
	public SysState(double usage, double variance, double score) {
		super();
		this.usage = usage;
		this.variance = variance;
		this.score = score;
	}
	

	public SysState() {
		super();
	}


	public double getUsage() {
		return usage;
	}

	public void setUsage(double usage) {
		this.usage = usage;
	}

	public double getVariance() {
		return variance;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
