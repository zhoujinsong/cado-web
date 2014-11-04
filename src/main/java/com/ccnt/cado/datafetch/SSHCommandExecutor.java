package com.ccnt.cado.datafetch;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccnt.cado.util.SSHClient;

public class SSHCommandExecutor implements CommandExecutor{
	public SSHCommandExecutor() {
		super();
	}
	
	public String execute(String command, Map<String, Object> config) {
		
		SSHClient ssh = new SSHClient();
		String response = null;
		Pattern pattern = Pattern.compile("\\{.*\\}");
		Matcher matcher = pattern.matcher(command);
		StringBuffer sb = new StringBuffer(); 
		while(matcher.find()){
			String matchString = matcher.group();
			matcher.appendReplacement(sb, config.get(matchString.substring(1,matchString.length()-1)).toString());
		}
		matcher.appendTail(sb);
		if(ssh.connect((String)config.get("address")) && ssh.auth((String)config.get("username"), (String)config.get("password"))){
			response = ssh.excute(sb.toString());
		}
		ssh.exit();
		return response;
	}

}
