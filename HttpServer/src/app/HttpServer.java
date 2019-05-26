package app;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import conf.PropertiesWrapper;
import conf.Property;

public class HttpServer {
	private int port = 80;
	private int poolSize = 50;
	private ExecutorService pool = null;
	private static HttpServer server = new HttpServer();
	
	private HttpServer() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//单例模式
	public static HttpServer getInstance() {
		return server;
	}
	//服务器初始化，根据web.xml初始化
	private void init() throws Exception{
		File file = new File("web.xml");
		if (!file.exists()) {
			System.err.println("web.xml is not exist!");
			throw new Exception();
		}
		JAXBContext context = JAXBContext.newInstance(PropertiesWrapper.class);
		Unmarshaller um = context.createUnmarshaller();
		PropertiesWrapper wrapper = (PropertiesWrapper)um.unmarshal(file);
		List<Property> webProperties = wrapper.getProperties();
		if (webProperties != null && webProperties.size() > 0) {
			for (Property property : webProperties) {
				if(property.getName().equals("port")) {
					this.port = Integer.parseInt(property.getValue());
				}
				if (property.getName().equals("poolSize")) {
					this.poolSize = Integer.parseInt(property.getValue());
				}
			}
		}
	}
	//启动服务器
	public void start() {
		pool = Executors.newFixedThreadPool(poolSize);
		System.out.println("服务器启动，监听80端口");
		try(ServerSocket serverSocket = new ServerSocket(port)) {
			while(true) {
				//等待浏览器的请求
				Socket connection = serverSocket.accept();
				//将请求任务提交到线程池
				pool.submit(new RequestTask(connection));
			}
		} catch (IOException e) {
			System.out.println("端口被占用");
		}
	}
	//getters && setters
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		if (port < 0 || port > 65535) {
			this.port = 80;
		}else {
			this.port = port;
		}
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	public static void main(String[] args) {
		HttpServer server = HttpServer.getInstance();
		server.start();
	}
	
}
