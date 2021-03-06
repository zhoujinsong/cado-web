package com.ccnt.cado.algorithm.monitor;
import java.util.List;

import com.ccnt.cado.algorithm.data.DataFetcher;
import com.ccnt.cado.algorithm.data.SysState;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;

public class SystemMonitor {
	private DataFetcher fetcher; //从数据库获取数据
	
	public SystemMonitor(DataFetcher fetcher){
		this.fetcher = fetcher;
	}

	public DataFetcher getDataFetcher(){
		return this.fetcher;
	}
	/**
	 * 计算系统当前状态
	 * @param max
	 * @param min
	 * @param platformId
	 * @param weigh
	 * @return
	 */
	public SysState computeSysState(double max, double min, int platformId,Unit weigh){	
		weigh = fomatWeigh(weigh);
		List<VM> vms = fetcher.getVMsData(platformId);
		return computeCurrentState(vms, max, min,weigh);
	}
	/**
	 * 计算评分，资源消耗率越接近max和min的中间值，分数越高
	 * @param x
	 * @param max
	 * @param min
	 * @return 评分值
	 */
	private double computeScore(double x, double max, double min) {
		if(max <= min){
			throw new IllegalArgumentException("最大阈值必须大于最小阈值！");
		}
		double best = (max - min) / 2 + min;
		double score = 0;
		if(x <= max && x >= min){
			score = 100 - Math.abs((x - best)) * 40 / (max - best);
		}else if(x > max){
			score = 60 - 1.5 * 60 * (x - max)/(1 - max);
		}else{
			score = 60 - 1.5 * 60 * (min - x)/min; 
		}		
		if(score < 0 ){
			score = 0;
		}
		return score;
	}
	/**
	 * 计算方差
	 * @param usages
	 * @param average
	 * @return
	 */
	private double computeVariance(double[] usages, double average){
		double result = 0;
		for(int i = 0; i < usages.length; i++){
			result += (usages[i] - average) * (usages[i] - average);
		}
		return result;
	}
	public SysState computeCurrentState(List<VM> vms, double max, double min,Unit weigh){
		weigh = fomatWeigh(weigh);
		SysState state = new SysState();
		Unit used, vmstatic;
		double usage = 0, score = 0;
		double[] usages = new double[vms.size()];
		
		for(int i = 0; i < vms.size(); i++) {
			VM vm = vms.get(i);
			used = vm.getUsedMetrics();
			vmstatic = vm.getStaticMetircs();
			
			usages[i] = weigh.getCpu() * used.getCpu() / vmstatic.getCpu() +
			weigh.getMemeory() * used.getMemeory() / vmstatic.getMemeory() +
			weigh.getIo() * used.getIo() / vmstatic.getIo() + 
			weigh.getNet() * used.getNet() / vmstatic.getNet();
			
			usage += usages[i];
			score += this.computeScore(usages[i], max, min);
		}
		
		state.setScore(score / vms.size());
		state.setUsage(usage / vms.size());
		state.setVariance(this.computeVariance(usages, usage / vms.size()));
		
		return state;
	}
	
	/**
	 * 将用户传入的评分（权重）参数归一化
	 * @param weigh
	 * @return
	 */
	private Unit fomatWeigh(Unit weigh) {
		double sum = weigh.getCpu() + weigh.getMemeory() + weigh.getIo() + weigh.getNet();
		weigh.setCpu(weigh.getCpu() / sum);
		weigh.setMemeory(weigh.getMemeory() / sum);
		weigh.setIo(weigh.getIo() / sum);
		weigh.setNet(weigh.getNet() / sum);
		return weigh;
	}
	
	/*
	  //计算消耗率最大的虚拟机
	public VM getTop(List<VM> vms, Unit weigh){
		double max = Double.MIN_VALUE;
		VM result = null;
		for(VM vm : vms){
			double tmp = computePercent(vm).multiplyWeigh(weigh);
			if(tmp > max){
				result  = vm;
			}
		}
		return result;
	}
	//计算消耗率最低的虚拟机
	public VM getButtom(List<VM> vms, Unit weigh){
		double min = Double.MAX_VALUE;
		VM result = null;
		for(VM vm : vms){
			double tmp = computePercent(vm).multiplyWeigh(weigh);
			if(tmp < min){
				result  = vm;
			}
		}
		return result;
	}
	//计算消耗率最低的虚拟机
	public List<VM> getTopN(double percent,List<VM> vms,Unit weigh){
		double tmp = 0;
		for(VM vm : vms){
			Unit used = vm.getUsedMetrics();
			Unit max = vm.getStaticMetircs();
			tmp = weigh.getCpu()*used.getCpu() / max.getCpu() +
					weigh.getMemeory() * used.getMemeory() / max.getMemeory() +
					weigh.getIo() * used.getIo() / max.getIo() +
					weigh.getNet() * used.getNet() / max.getNet();
			if(tmp >= percent){
				vms.add(vm);
			}
		}
		return vms;
	}
	//计算使用率超过percent的虚拟机
	public List<VM> getBottomN(double percent,List<VM> vms,Unit weigh){
		double tmp = 0;
		for(VM vm : vms){
			Unit used = vm.getUsedMetrics();
			Unit max = vm.getStaticMetircs();
			tmp = weigh.getCpu()*used.getCpu() / max.getCpu() +
					weigh.getMemeory() * used.getMemeory() / max.getMemeory() +
					weigh.getIo() * used.getIo() / max.getIo() +
					weigh.getNet() * used.getNet() / max.getNet();
			if(tmp <= percent){
				vms.add(vm);
			}
		}
		return vms;
	}
	public Unit computePercent(VM vm){
		Unit used = vm.getUsedMetrics();
		Unit max = vm.getStaticMetircs();
		
		return new Unit(used.getCpu() / max.getCpu(), 
				used.getMemeory()/max.getMemeory(), 
				used.getIo()/ max.getIo(), 
				used.getNet() / max.getNet());
	}
	//获取一台虚拟机上消耗资源最多的部署单元
	public Deploy computeMostConsume(VM vm, List<Deploy> list, Unit weigh){
		Deploy result = null;
		double max = Double.MIN_VALUE;
		for(Deploy d : list){
			if(d.getVmId() == vm.getVmId()){
				double tmp = d.getMetrics().multiplyWeigh(weigh);
				if(tmp > max){
					max = tmp;
					result = d;
				}
			}
		}
		return result;
	}
	
	public double getPlatformUsage(int platformInfoId){
		Map<String,Object> platform = fetcher.getPlatformByInfoId(platformInfoId);
		List<VM> vms = fetcher.getVMsData((Integer)platform.get("_id"));
		PlatformInfo platformInfo = fetcher.getDataStorer().getPlatform(platformInfoId);
		int weigh = platformInfo.getCpuWeight() + platformInfo.getMemoryWeight() + platformInfo.getIoWeight() + platformInfo.getNetworkWeight();
		double usage = 0;
		for(int i = 0; i < vms.size(); i++) {
			VM vm = vms.get(i);
			Unit used = vm.getUsedMetrics();
			Unit vmstatic = vm.getStaticMetircs();
			
			usage += platformInfo.getCpuWeight() * used.getCpu() / vmstatic.getCpu() / weigh+
			platformInfo.getMemoryWeight() * used.getMemeory() / vmstatic.getMemeory() / weigh+
			platformInfo.getIoWeight() * used.getIo() / vmstatic.getIo() /weigh + 
			platformInfo.getNetworkWeight() * used.getNet() / vmstatic.getNet() /weigh;
		}	
		return usage / vms.size();
	}*/
}
