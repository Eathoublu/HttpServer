package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Request {
	//请求方法
	private String method;
	//请求URL
	private String url;
	//http版本，在这个程序中，默认请求都是基于http1.0以上的
	private double version;
	private BufferedReader reader; 
	
	public Request(InputStream in) {
		this.reader = new BufferedReader(new InputStreamReader(in));
		resolve();
	}
	
	public void resolve() {
		try{
			String line = reader.readLine();
			String[] tokens = line.split("\\s");
			method = tokens[0];
			url = tokens[1];
			version = Double.parseDouble(tokens[2].split("/")[1]);
			System.out.println(method + "," + url + "," + version);
			//读取整个请求头
			line = reader.readLine();
			while(!line.equals("")) {
				line = reader.readLine();
			}
		}catch (IOException e) {
			System.out.println("无法读取请求行");
		}
	}
	//getters
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public double getVersion() {
		return version;
	}
	public BufferedReader getReader() {
		return reader;
	}
}
