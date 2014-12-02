package com.ccnt.cado.algorithm.scheduler;

import java.util.Date;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;
/**
 * 基于神经网络的预测算法
 * @author Lisa
 *
 */
public class ECNNPredictor implements Predictor{

	@Override
	public Unit predictDeploy(DataFetcher dataFetcher, Deploy deploy,
			Date from, Date to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Unit predictVM(DataFetcher dataFetcher, VM vm, Date from, Date to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double predictVMUsage(DataFetcher dataFetcher, VM vm, Date from,
			Date to) {
		// TODO Auto-generated method stub
		return 0;
	}

}
