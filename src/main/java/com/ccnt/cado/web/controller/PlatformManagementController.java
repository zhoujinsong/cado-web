package com.ccnt.cado.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ccnt.cado.util.BeanCaster;
import com.ccnt.cado.web.bean.PlatformInfo;
import com.ccnt.cado.web.service.PlatformManagementService;




@Controller
@RequestMapping("/platformManagement.do")
public class PlatformManagementController {
	@Autowired
	private PlatformManagementService service;

	@RequestMapping(params = "method=createPlatform")
	@ResponseBody
	public Map<String,Object> createPlatform(@ModelAttribute("platformInfo") PlatformInfo platformInfo){
		Map<String,Object> result = new HashMap<String,Object>();
		service.createPlatform(platformInfo);
		result.put("success", true);
		return result;
	}
	@RequestMapping(params = "method=deletePlatforms")
	@ResponseBody
	public Map<String,Object> deletePlatforms(int[] platformIds){
		Map<String,Object> result = new HashMap<String,Object>();
		service.deletePlatforms(platformIds);
		result.put("success", true);
		return result;
		
	}
	@RequestMapping(params = "method=updatePlatform")
	@ResponseBody
	public Map<String,Object> updatePlatform(@ModelAttribute("platformInfo") PlatformInfo platformInfo){
		Map<String,Object> result = new HashMap<String,Object>();
		service.updatePlatform(platformInfo);
		result.put("success", true);
		return result;
	}
	@RequestMapping(params = "method=getPlatform")
	@ResponseBody
	public Map<String,Object> getPlatform(int platformId){
		return BeanCaster.castBean2Map(service.getPlatform(platformId));
	}
	@RequestMapping(params = "method=listPlatforms")
	@ResponseBody
	public List<Map<String,Object>> listPlatforms(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<PlatformInfo> platformInfos = service.getAllPlatforms();
		for(PlatformInfo platformInfo : platformInfos){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("_id", platformInfo.get_id());
			map.put("platformClass", platformInfo.getPlatformClass());
			map.put("platformName", platformInfo.getPlatformName());
			list.add(map);
		}
		return list;
	}
}
