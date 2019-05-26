package app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.Request;
import net.Response;

public class RequestTask implements Runnable {
	private Socket connection;

	public RequestTask(Socket connection) {
		this.connection = connection;
	}
	@Override
	public void run() {
		try {
			InputStream in = connection.getInputStream();
			OutputStream out = connection.getOutputStream();
			//构造request对象进行解析
			Request request = new Request(in);
			//通过请求构造response对象
			Response response = new Response(request,out);
			//做出响应
			response.doResponse();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
