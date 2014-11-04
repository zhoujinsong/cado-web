package com.ccnt.cado.algorithm.data;
/**
 * 
 * @author LS
 * ָ指标，考虑cpu、memeory、io和net
 */
public class Unit {
	private double cpu = -1;
	private double memeory = -1;
	private double io = -1;
	private double net = -1;
	public Unit(double cpu, double memeory, double io, double net) {
		super();
		this.cpu = cpu;
		this.memeory = memeory;
		this.io = io;
		this.net = net;
	}
	public double getCpu() {
		return cpu;
	}
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	public double getMemeory() {
		return memeory;
	}
	public void setMemeory(double memeory) {
		this.memeory = memeory;
	}
	public double getIo() {
		return io;
	}
	public void setIo(double io) {
		this.io = io;
	}
	public double getNet() {
		return net;
	}
	public void setNet(double net) {
		this.net = net;
	}
	public void addUnit(Unit u){
		this.cpu += u.cpu;
		this.memeory += u.memeory;
		this.io += u.io;
		this.net += u.net;
	}
	public double multiplyWeigh(Unit u){
		return this.cpu * u.cpu + this.memeory * u.memeory + this.io * u.io+
			   this.net * u.net;
	}
}
