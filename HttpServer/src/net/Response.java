package net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLConnection;
import java.util.Date;

public class Response {
	private Request request;
	private BufferedWriter writer;
	private OutputStream out;
	//所有的静态文件放在这个目录下面
	private String base = "src/resource";
	public Response(Request request, OutputStream out) {
		this.request = request;
		this.out = out;
		this.writer = new BufferedWriter(new OutputStreamWriter(out));
	};
	//服务器做出响应
	public void doResponse() throws IOException {
		String method = request.getMethod();
		//根据请求方法做出不同的响应
		switch (method) {
		case "GET":
			doGet();
			break;
		case "POST":
			doPost();
			break;
		case "HEAD":
			doHead();
			break;
		//默认响应
		default:
			defaultResponse();
			break;
		}
	}
	
	public void doGet() throws IOException {
		//get方法请求的是文件，通过拼接字符串获取相对路径
		//比如get localhost/login.html 其url是/login.html,将base路径"src/resource"和url拼接
		//只要这个文件存在并且可读，就能访问到这个文件
		String fileName = base + request.getUrl();
		File file = new File(fileName);
		if(!file.isDirectory() && file.exists() && file.canRead()) {
			//如果访问index页面需要先登录，跳转到登录页面
			if (request.getUrl().equals("/index.html")) {
				redirect("/login.html");
			}else {
				String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
				//发送响应头部，即header
				sendHeader(writer, "Http/1.1 200 OK", contentType, file.length());
				//发送响应数据，即body
				sendFile(file);
			}
		}else {
			//找不到该文件，返回404响应
			File fileOf404 = new File(base + "/404.html");
			sendHeader(writer, "Http/1.1 404 File Not Found", "text/html", fileOf404.length());
			sendFile(fileOf404);
		}
	}
	
	public void doPost() throws IOException {
		//读取body
		//.....
		//可自行实现
		//.....
		if(request.getUrl().equals("/index.html")) {
			File indexFIle = new File(base + "/index.html");
			sendHeader(writer, "Http/1.1 200 OK", "text/html", indexFIle.length());
			sendFile(indexFIle);
		}
	}
	//处理Head请求，只返回头部即可
	public void doHead() throws IOException {
		String fileName = base + request.getUrl();
		System.out.println(fileName);
		File file = new File(fileName);
		if(!file.isDirectory() && file.exists() && file.canRead()) {
			String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
			sendHeader(writer, "Http/1.1 200 OK", contentType, file.length());
		}else {
			File fileOf404 = new File(base + "/404.html");
			sendHeader(writer, "Http/1.1 404 File Not Found", "text/html", fileOf404.length());
		}
	}
	//页面重定向
	public void redirect(String newSite) throws IOException{
		writer.write("Http/1.1 302 FOUND\r\n");
		Date now = new Date();
		writer.write("Date: " + now + "\r\n");
		writer.write("Server: HttpServer 2.0\r\n");
		writer.write("Location: " + newSite + "\r\n");
		writer.write("Content-type: text/html\r\n\r\n");
		writer.flush();
	}
	//服务器错误
	public void defaultResponse() throws IOException {
		File file = new File(base + "/501.html");
		sendHeader(writer, "Http/1.1 501 Not Implement", "text/html", file.length());
		sendFile(file);
	}
	//发送头部信息
	public void sendHeader(Writer writer, String responseCode, String contentType, long length)throws IOException {
		writer.write(responseCode + "\r\n");
		Date now = new Date();
		writer.write("Date: " + now + "\r\n");
		writer.write("Server: HttpServer 2.0\r\n");
		writer.write("Content-length: " + length + "\r\n");
		//注意，由于http头部是按行读取的，并且头部最后要以空行结束，所以是"\r\n\r\n"
		writer.write("Content-type: " + contentType + "\r\n\r\n");
		writer.flush();
	}
	//发送响应数据
	public void sendFile(File file) throws IOException {
		byte[] buffer = new byte[1024];
		@SuppressWarnings("resource")
		FileInputStream fileInputStream = new FileInputStream(file);
		
		while(fileInputStream.available() > 0) {
			int length = fileInputStream.read(buffer);
			out.write(buffer, 0 , length);
		}
		out.flush();
	}
}
