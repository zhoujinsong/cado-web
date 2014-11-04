package com.ccnt.cado.datafetch;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccnt.cado.exception.RestException;
import com.ccnt.cado.util.RestClient;

public class RestCommandExecutor implements CommandExecutor{
	
	public RestCommandExecutor() {
		super();
		
	}

	
	public String execute(String command,Map<String,Object> config) {
		RestClient rest = new RestClient();
		Pattern pattern = Pattern.compile("\\{.*?\\}");
		Matcher matcher = pattern.matcher(command);
		StringBuffer sb = new StringBuffer(); 
		while(matcher.find()){
			String matchString = matcher.group();
			matcher.appendReplacement(sb, config.get(matchString.substring(1,matchString.length()-1)).toString());
		}
		matcher.appendTail(sb);
		String response = null;
		try {
			response = rest.get(sb.toString());
		} catch (RestException e) {
			e.printStackTrace();
		}
		
		return response;
	}

}
