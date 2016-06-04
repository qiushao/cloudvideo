package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import client.AbstractClientSocket;
import client.ClientList;
import client.PhoneClient;
import client.RouterClient;
import parser.Const;
import parser.ParseUtils;

public class SocketServer {

	// 监听的端口号
	public static final int PORT = 11111;

	// 处理客户端连接的线程池
	public ExecutorService clientThreadPool = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		System.out.println("服务器启动....\n");

		SocketServer server = new SocketServer();
		server.init();
	}

	private void init() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("服务器地址：" + serverSocket.getLocalSocketAddress());

			while (true) {
				Socket client = serverSocket.accept();
				System.out.println(">>>>>>有新的连接...");
//				DataInputStream inputStream = new DataInputStream(client.getInputStream());
				 BufferedReader inputStream =new BufferedReader(new InputStreamReader(client.getInputStream()));

				// 请求类型
				String inStr = inputStream.readLine();
				String type = ParseUtils.getType(inStr);
				
				System.out.println("+++++++++++++++++++++++++++++++++++");
				System.out.println("+++客户端IP: " + client.getInetAddress());
				System.out.println("+++客户端的请求：" + inStr);
				System.out.println("+++type: " + type);
				System.out.println("+++++++++++++++++++++++++++++++++++");

				// 按type分发
				AbstractClientSocket clientRunnable = buildClientByType(type, client, inStr);

				if (clientRunnable != null) {
					// 添加进客户端列表
					ClientList.getInstance().add(clientRunnable);
					// 处理这次连接
					clientThreadPool.execute(clientRunnable);
				}
			}
		} catch (IOException e) {
			System.out.println("服务器发生错误： " + e.getMessage());
			clientThreadPool.shutdown();
		}

	}

	private AbstractClientSocket buildClientByType(String type, Socket client, String inStr) {
		// 路由请求
		if (Const.LOGIN_REQUEST.equals(type) || Const.CACHE_STATE_RESPONSE.equals(type)) {
			System.out.println(">>>>>>>>>>路由登录...");
			return new RouterClient(client, inStr);
		}
		// 手机请求
		else if (Const.CAHCE_REQUEST.equals(type)) {
			System.out.println(">>>>>>>>>>手机请求缓存...");
			return new PhoneClient(client, inStr);
		} else if (Const.CACHE_STATE_REQUEST.equals(type)){
			System.out.println(">>>>>>>>>>手机请求缓存的状态...");
			return new PhoneClient(client, inStr);
		} else {
			return null;
		}

	}

}
