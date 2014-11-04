package com.ccnt.cado.algorithm.scheduler;

import java.util.List;

import com.ccnt.cado.algorithm.data.Deploy;
import com.ccnt.cado.algorithm.data.SysState;
import com.ccnt.cado.algorithm.data.Unit;
import com.ccnt.cado.algorithm.data.VM;
import com.ccnt.cado.algorithm.monitor.SystemMonitor;
import com.ccnt.cado.util.Logger;
import com.ccnt.cado.web.bean.PlatformInfo;

public class AHPScheduler implements AppScheduler{
	private Unit ahpWeigh;
	public List<ScheduleResult> doSchedule(List<Deploy> units, List<VM> vms) {
		
		return null;
	}
	public List<Deploy> doSchedule(SystemMonitor monitor,PlatformInfo platformInfo) {
		//Logger.info("AHP调度器执行调度，阈值max="+platformInfo.getPlatformUsageMax()+", min="+platformInfo.getPlatformUsageMin());
		int platformId = (Integer)monitor.getFetcher().getPlatformByInfoId(platformInfo.get_id()).get("_id");
		Unit weigh = new Unit(platformInfo.getCpuWeight(), platformInfo.getMemoryWeight(), platformInfo.getIoWeight(), platformInfo.getNetworkWeight());
		List<Deploy> deployList = monitor.getFetcher().getDeployUnitsData(platformId);
		List<VM> vmList = monitor.getFetcher().getVMsData(platformId);
		SysState state = monitor.computeSysState(platformInfo.getPlatformUsageMax(), platformInfo.getPlatformUsageMin(),platformId,weigh);
		//Logger.info("计算系统当前状态      评分："+state.getScore() +" 使用率："+state.getUsage() +"  方差："+state.getVariance());
		if(state.getUsage() > platformInfo.getPlatformUsageMax()){
			
			//系统状态超过阈值，迁移
			//Logger.info("-----------------------------------------------------");
			//Logger.info("系统状态超过阈值，做迁移....");
			double tmp = state.getUsage();
			while(tmp > platformInfo.getPlatformUsageMax()){
				VM from = monitor.getTop(vmList, ahpWeigh);
				//Logger.info("占用率最高的虚拟机："+from.getVmId());
				Deploy d = monitor.computeMostConsume(from, deployList, ahpWeigh);
				//Logger.info("虚拟机"+from.getVmId()+"上最消耗资源的应用："+d.getUnitId());
				//找压力最小的一台部署
				int minVmId = Integer.MAX_VALUE;
				double minPressure = Double.MAX_VALUE;
				for(VM vm : vmList){
					double currentPressure = deployPressure(d, vm);
					if(currentPressure != -1){
						if(currentPressure < minPressure && vm.getVmId() != d.getVmId()){
							minVmId = vm.getVmId();
							minPressure = currentPressure;
						}
					}
					
				}
				VM to = getVmById(minVmId, vmList);
				if(minPressure == Double.MAX_VALUE){
					//Logger.info("无法合并！");
					break;
				}
				migrate(d, to,from);
				SysState ss = monitor.computeCurrentState(vmList, platformInfo.getPlatformUsageMax(), platformInfo.getPlatformUsageMin(),weigh);
				//Logger.info("迁移后系统  评分" + ss.getScore() +"  使用率："+ss.getUsage() +" 方差："+ss.getVariance());
				break;
			}
		}else if(state.getUsage() < platformInfo.getPlatformUsageMin()){
			//系统状态过低，合并
			double tmp = state.getUsage();
			while(tmp < platformInfo.getPlatformUsageMin()){
				VM vm1 = monitor.getButtom(vmList, ahpWeigh);
				vmList.remove(vm1);
				VM vm2 = monitor.getButtom(vmList, ahpWeigh);
				merge(vm2, vm1, deployList);
				//tmp = monitor.computeSysState(vmList);
			}
		}
		return deployList;
	}
	public VM getVmById(int id, List<VM> vms){
		VM result = null;
		for(VM vm : vms){
			if(vm.getVmId() == id){
				result = vm;
			}
		}
		return result;
	}
	public AHPScheduler(Unit ahpWeigh){
		//Logger.info("[INFO]初始化AHP调度器....权值：" +
				//"cpu="+ahpWeigh.getCpu()+", memory="+ahpWeigh.getMemeory()+
				//" ,net="+ ahpWeigh.getNet()+", io="+ahpWeigh.getIo());
		this.ahpWeigh = ahpWeigh;
	}
	
	//将虚拟机vm2的所有应用迁移到vm1上
	public VM merge(VM vm1, VM vm2, List<Deploy> deployes){
		for(Deploy d : deployes){
			if(d.getVmId() == vm2.getVmId()){
				if(canMigrate(d, vm1)){
					this.migrate(d, vm1,vm2);
					//Logger.info("将应用"+d.getUnitId() +"从"+vm2.getVmId()+"合并到"+vm1.getVmId());
				}else{
					//Logger.error("目标主机没有可用空间，迁移失败");
				}
			}
		}
		return vm1;
	}
	private boolean canMigrate(Deploy from, VM to){
		Unit fromUnit = from.getMetrics();
		Unit used = to.getUsedMetrics();
		
		if((fromUnit.getCpu() + used.getCpu()) > to.getStaticMetircs().getCpu()
				|| (fromUnit.getMemeory() + used.getMemeory()) > to.getStaticMetircs().getMemeory()
				||(fromUnit.getIo() + used.getIo()) > to.getStaticMetircs().getIo()
				||(fromUnit.getNet() + used.getNet()) > to.getStaticMetircs().getNet()){
			return false;
		}else{
			return true;
		}
	}
	//将应用迁移到另一台虚拟机
	public void migrate(Deploy from, VM to, VM fromVm){
		if(!canMigrate(from, to)){
			//Logger.info("可用空间不足，迁移失败，请重新开启虚拟机。。。");
			return;
		}
		//Logger.info("[INFO]从虚拟机"+fromVm.getVmId()+"迁移应用"+from.getUnitId()+"到虚拟机"+to.getVmId());
		Unit used = to.getUsedMetrics();
		Unit deploy = from.getMetrics();
		used.addUnit(deploy);
		to.setUsedMetrics(used);
		
		Unit fromUsed = fromVm.getUsedMetrics();
		Unit tmp = new Unit(fromUsed.getCpu() - deploy.getCpu(), 
				fromUsed.getMemeory() - deploy.getMemeory(), 
				fromUsed.getIo() - deploy.getIo(), 
				fromUsed.getNet() - deploy.getNet());
		fromVm.setUsedMetrics(tmp);	
		
		from.setVmId(to.getVmId());
	}
	//计算虚拟机部署应用的压力值
	public double deployPressure(Deploy d, VM vm){
		if(!canMigrate(d, vm)){
			//Logger.info("主机"+vm.getVmId()+"空间不足，无法部署"+d.getUnitId());
			return -1;
		}
		Unit used = vm.getUsedMetrics();
		Unit max = vm.getStaticMetircs();
		Unit tmp = new Unit(max.getCpu() - used.getCpu(),max.getMemeory() - used.getMemeory(),max.getIo() - used.getIo(), max.getNet() - used.getNet());
		return tmp.multiplyWeigh(ahpWeigh);
	}
}
