package com.ccnt.cado.algorithm.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.MigrateStep;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.exception.CannotMergeException;
import com.ccnt.cado.algorithm.exception.CannotMigrateException;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.web.bean.PlatformInfo;

public class AHPScheduler implements AppScheduler{
	
	@Override
	public List<MigrateStep> Migrate(SystemMonitor monitor,
			Predictor predictor, PlatformInfo platformInfo) {
		List<VM> vms = monitor.getDataFetcher().getVMsData(platformInfo.get_id());
		
		Date from = new Date();
		from.setYear(1989);
		Date to = new Date();
		double maxUsage = Integer.MIN_VALUE;
		double minUsage = Integer.MAX_VALUE;
		VM fromVM = vms.get(0); //选出预估使用率最大的虚拟机来做迁移
		VM toVM = vms.get(0); //选出预估使用率最小的虚拟器来做目的机器
		
		for(VM vm : vms) {
			double usage = predictor.predictVMUsage(monitor.getDataFetcher(), vm, from, to);
			if(usage > maxUsage) {
				fromVM = vm;
				maxUsage = usage;
			}
			if(usage < minUsage) {
				toVM = vm;
				minUsage = usage;
			}
		}
		//from虚拟机预测以后的资源消耗率
		fromVM.setUsedMetrics(predictor.predictVM(monitor.getDataFetcher(), fromVM, from, to));
		Unit predictVMUsed = fromVM.getUsedMetrics();
		
		double minPercentage = Integer.MAX_VALUE;
		Deploy minDeploy = null;
		VM bestToVM = null;
		
		//对from虚拟机的每一个应用，模拟其迁移到其他主机上压力值，取压力最小的部署
		for(Deploy deploy : monitor.getDataFetcher().getDeploysByVM(fromVM)) {
			Unit predictDeploy = predictor.predictDeploy(monitor.getDataFetcher(), deploy, from, to);
			Double minusUsage = (predictVMUsed.getCpu() - predictDeploy.getCpu())/ fromVM.getStaticMetircs().getCpu() + 
					(predictVMUsed.getMemeory() - predictDeploy.getMemeory())/ fromVM.getStaticMetircs().getMemeory() + 
					(predictVMUsed.getIo() - predictDeploy.getIo()) / fromVM.getStaticMetircs().getIo() +
					(predictVMUsed.getNet() - predictDeploy.getNet()) / fromVM.getStaticMetircs().getNet();
			for(VM vm : vms) {
				if(vm != fromVM) {
					Unit vmPredict = predictor.predictVM(monitor.getDataFetcher(), vm, from, to);
					double addUsage = (vmPredict.getCpu() + predictDeploy.getCpu()) / vm.getStaticMetircs().getCpu() + 
							(vmPredict.getMemeory() + predictDeploy.getMemeory()) / vm.getStaticMetircs().getMemeory() + 
							(vmPredict.getIo() + predictDeploy.getIo()) / vm.getStaticMetircs().getIo() + 
							(vmPredict.getNet() + predictDeploy.getNet()) / vm.getStaticMetircs().getNet();
					if(addUsage < 4 && Math.abs(addUsage - minUsage) < minPercentage) {
						minDeploy = deploy;
						bestToVM = vm;
						minPercentage = Math.abs(addUsage - minUsage);
					}
				}
			}
		}
		if(minDeploy == null || bestToVM == null) {
			throw new CannotMigrateException("没有合适的迁移虚拟机");
		}
		
		List<MigrateStep> res = new ArrayList<MigrateStep>();
		res.add(new MigrateStep(fromVM.getVmId(), bestToVM.getVmId(), minDeploy.getUnitId()));
		
		return res;
	}

	@Override
	public List<MigrateStep> Merge(SystemMonitor monitor, Predictor predictor,
			PlatformInfo platformInfo) {
		List<VM> vms = this.getBottom2(monitor, predictor, platformInfo);
		if(vms.size() == 0) {
			throw new CannotMergeException();
		}
		VM from = vms.get(0);
		VM to = vms.get(1);
		
		List<MigrateStep> steps = new ArrayList<MigrateStep>();
		for(Deploy d : monitor.getDataFetcher().getDeploysByVM(from)) {
			steps.add(new MigrateStep(from.getVmId(), to.getVmId(), d.getUnitId()));
		}
		return steps;
	}
	
	private List<VM> getBottom2 (SystemMonitor monitor, Predictor predictor,
			PlatformInfo platformInfo) {
		List<VM> vms = monitor.getDataFetcher().getVMsData(platformInfo.get_id());
		if(vms.size() < 2) {
			return new ArrayList<VM>();
		}
		//需要设置开始时间
		Date from = new Date();
		from.setYear(1989);
		Date to = new Date();
		for(VM vm : vms) {
			vm.setUsedMetrics(predictor.predictVM(monitor.getDataFetcher(), vm, from, to));
		}
		Collections.sort(vms, new Comparator<VM>() {
			@Override
			public int compare(VM o1, VM o2) {
				double usage1 = o1.getUsedMetrics().getCpu() / o1.getStaticMetircs().getCpu()
						+ o1.getUsedMetrics().getMemeory() / o1.getStaticMetircs().getMemeory()
						+ o1.getUsedMetrics().getIo() / o1.getStaticMetircs().getIo()
						+ o1.getUsedMetrics().getNet() / o1.getStaticMetircs().getNet();
				
				double usage2 = o2.getUsedMetrics().getCpu() / o2.getStaticMetircs().getCpu()
						+ o2.getUsedMetrics().getMemeory() / o2.getStaticMetircs().getMemeory()
						+ o2.getUsedMetrics().getIo() / o2.getStaticMetircs().getIo()
						+ o2.getUsedMetrics().getNet() / o2.getStaticMetircs().getNet();
				return usage1 > usage2 ? 1 : 0;
			}
		});
		ArrayList<VM> res = new ArrayList<VM>();
		res.add(vms.get(0));
		res.add(vms.get(1));		
		return res;		
	}
}
