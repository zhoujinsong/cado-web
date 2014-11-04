package com.ccnt.cado.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import com.ccnt.cado.exception.RestException;

public class RestClient{
	private HttpClient client;
	public RestClient() {
		super();
		client = new HttpClient();
	}
	public String get(String url) throws RestException{
		Logger.info("get:" + url);
		String result = null;
		int stateCode = 0;
		GetMethod method = new GetMethod(url);
					
		try {
			stateCode = client.executeMethod(method);
			switch (stateCode){
			case 200:
				result = inputStream2String(method.getResponseBodyAsStream());
				break;
			default:
				throw new RestException(0,"StateCode is not 200.");
			}
		} catch (HttpException e) {			
			e.printStackTrace();
			throw new RestException(0,"Some http error happended.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RestException(0,"Some IO error happended.");
		} finally{
			method.releaseConnection();
		}
		return result;
	}
	public String post(String url,Map<String,String> pairs) throws RestException{
		String result = null;
		int stateCode = 0;
		PostMethod method = new PostMethod();
		NameValuePair[] data = new NameValuePair[pairs.size()];
		int i = 0;
		for(String key : pairs.keySet()){
			data[i] = new NameValuePair(key, pairs.get(key));
		}
		method.setRequestBody(data);
		try {
			stateCode = client.executeMethod(method);
			switch (stateCode){
			case 200:
				result = inputStream2String(method.getResponseBodyAsStream());
				break;
			default:
				throw new RestException(0,"StateCode is not 200.");
			}
		} catch (HttpException e) {
			e.printStackTrace();
			throw new RestException(0,"Some http error happended.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RestException(0,"Some IO error happended.");
		} finally{
			method.releaseConnection();
		}
		return result;
	}
	@SuppressWarnings("deprecation")
	public String put(String url,String filePath) throws RestException{
		String result = null;
		int stateCode = 0;
		PutMethod method = new PutMethod();
		try {
			method.setRequestBody(new FileInputStream(filePath));
			stateCode = client.executeMethod(method);
			switch (stateCode){
			case 200:
				result = inputStream2String(method.getResponseBodyAsStream());
				break;
			default:
				throw new RestException(0,"StateCode is not 200.");
			}
		} catch (HttpException e) {
			e.printStackTrace();
			throw new RestException(0,"Some http error happended.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RestException(0,"Some IO error happended.");
		} finally{
			method.releaseConnection();
		}
		return result;
	}
	public String delete(String url) throws RestException{
		String result = null;
		int stateCode = 0;
		DeleteMethod method = new DeleteMethod(url);
		try {			
			stateCode = client.executeMethod(method);
			switch (stateCode){
			case 200:
				result = inputStream2String(method.getResponseBodyAsStream());
				break;
			default:
				throw new RestException(0,"StateCode is not 200.");
			}
		} catch (HttpException e) {
			e.printStackTrace();
			throw new RestException(0,"Some http error happended.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RestException(0,"Some IO error happended.");
		} finally{
			method.releaseConnection();
		}
		return result;
	}
	private String inputStream2String(InputStream input) throws IOException{
		String result = null;
		OutputStream output = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		while(input.read(bytes) > 0){
			output.write(bytes);
		}
		result = output.toString();
		return result;
	}
}
