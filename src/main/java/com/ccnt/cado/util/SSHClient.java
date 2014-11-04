package com.ccnt.cado.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


import com.trilead.ssh2.Connection;


public class SSHClient{
	private Connection conn;
	
	public SSHClient() {
		super();
	}
	public boolean connect(String hostname){
		try {
			conn = new Connection(hostname);
			conn.connect();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	public boolean auth(String username,String password){
		if(conn != null){
			try {
				return conn.authenticateWithPassword(username, password);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			return false;
		}
	}
	public String excute(String command){
		Logger.info("ssh:"+command);
		String result = null;
		if(conn.isAuthenticationComplete()){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				conn.exec(command, baos);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = baos.toString();
		}
		return result;
	}
	public void exit(){
		conn.close();
	}

}
