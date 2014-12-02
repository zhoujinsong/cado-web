package com.ccnt.cado.algorithm.scheduler;

import java.util.Date;
import java.util.List;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;

public class MeanPredictor implements Predictor{
	
	/*public Unit predictByHistoryByMax(DataFetcher dataFetcher, Deploy deploy, Date from, Date to, Unit weigh) {
		List<Unit> data = dataFetcher.getHistoryData(from, to, deploy);
		
		double max = Integer.MIN_VALUE;
		Unit result = deploy.getMetrics();
		for(Unit unit : data) {
			double tmp = unit.multiplyWeigh(weigh);
			if(tmp > max) {
				max = tmp;
				result = unit;
			}
		}
		return result;
	}*/
	
	@Override
	public Unit predictDeploy(DataFetcher dataFetcher, Deploy deploy,
			Date from, Date to) {
		List<Unit> data = dataFetcher.getHistoryData(from, to, deploy
				);
		double cpu = 0, memory = 0, io = 0, net = 0, size = data.size();
		if(size == 0) {
			return deploy.getMetrics();
		}
		for(Unit unit : data) {
			cpu += unit.getCpu();
			memory += unit.getMemeory();
			io += unit.getIo();
			net += unit.getNet();
		}
		return new Unit(cpu/size, memory/size, io/size, net/size);
	}

	@Override
	public Unit predictVM(DataFetcher dataFetcher, VM vm, Date from, Date to) {
		List<Deploy> deploys = dataFetcher.getDeploysByVM(vm); 
		double cpu = 0, memory = 0, io = 0, net = 0;
		for(Deploy d : deploys) {
			Unit unit = predictDeploy(dataFetcher, d, from, to);
			cpu += unit.getCpu();
			memory += unit.getMemeory();
			io += unit.getIo();
			net += unit.getNet();
		}
		return new Unit(cpu, memory, io, net);
	}

	@Override
	public double predictVMUsage(DataFetcher dataFetcher, VM vm, Date from,
			Date to) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
