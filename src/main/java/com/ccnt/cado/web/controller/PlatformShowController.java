package com.ccnt.cado.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ccnt.cado.web.service.PlatformShowService;

@Controller
@RequestMapping("/platformShow.do")
public class PlatformShowController {
	@Autowired
	private PlatformShowService platformShowService;
	
	@RequestMapping(params = "method=listHosts")
	@ResponseBody
	public List<Map<String,Object>> getHostsInfo(int platformInfoId){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> hosts = platformShowService.getPlatformHostsInfo(platformInfoId);
		for(Map<String,Object> host : hosts){
			Map<String,Object> hostInfo = new HashMap<String,Object>();
			hostInfo.put("hostId", host.get("_id"));
			hostInfo.put("address", host.get("address"));
			hostInfo.put("instanceInfo", platformShowService.getHostInstancesInfo((Integer)host.get("_id")));
			list.add(hostInfo);
		}
		return list;
	}
	
	@RequestMapping(params = "method=getHostMonitorData")
	@ResponseBody
	public Map<String,Object> getHostMonitorData(int hostId){
		return platformShowService.getHostMonitorData(hostId);
	}
	
	@RequestMapping(params = "method=loadSystemUsage")
	@ResponseBody
	public Map<String,Object> loadSystemUsage(int platformInfoId){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("usage", platformShowService.getSystemUsage(platformInfoId));
		return result;
	}
}
