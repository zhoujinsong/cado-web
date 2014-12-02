package com.ccnt.cado.algorithm.scheduler;

import java.util.Date;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;

public interface Predictor {
	public Unit predictDeploy(DataFetcher dataFetcher, Deploy deploy, Date from, Date to);
	public Unit predictVM(DataFetcher dataFetcher, VM vm, Date from, Date to);
	public double predictVMUsage(DataFetcher dataFetcher, VM vm, Date from, Date to);
}
