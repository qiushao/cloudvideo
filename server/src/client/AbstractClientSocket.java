package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;


public abstract class AbstractClientSocket extends Socket implements Runnable {

	private Socket mSocket;
//	private DataInputStream mInputStream;
//	private DataOutputStream mOutputStream;
	private BufferedReader mInputStream;
	private PrintStream mOutputStream;
	// 初始连接的请求参数传入
	private String mInitStr;

	// 一般来说，
	// 路由器的请求带mac地址
	// 手机的请求带phoneID
	protected String mPhoneMac = "";
	protected String mRouterID = "";
	protected String mRouterMac = "";
	protected String mPhoneID = "";
	
	public String getmRouterMac() {
		return mRouterMac;
	}

	public void setmRouterMac(String mRouterMac) {
		this.mRouterMac = mRouterMac;
	}

	
	
	public String getmPhoneMac() {
		return mPhoneMac;
	}

	public void setmPhoneMac(String mPhoneMac) {
		this.mPhoneMac = mPhoneMac;
	}

	public String getmRouterID() {
		return mRouterID;
	}

	public void setmRouterID(String mRouterID) {
		this.mRouterID = mRouterID;
	}

	public String getPhoneID() {
		return mPhoneID;
	}

	public void setPhoneID(String phoneID) {
		this.mPhoneID = phoneID;
	}


	public AbstractClientSocket(Socket socket, String inStr) {
		this.mSocket = socket;
		this.mInitStr = inStr;
	}

	/**
	 * 关闭连接
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		mInputStream.close();
		mOutputStream.close();
		mSocket.close();
	}

	@Override
	public void run() {
		// 读取客户端数据
		try {
//			mInputStream = new DataInputStream(mSocket.getInputStream());
//			mOutputStream = new DataOutputStream(mSocket.getOutputStream());
			mInputStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mOutputStream = new PrintStream(mSocket.getOutputStream());


			while (true) {
				// 处理客户端数据
				processMessage(mInitStr);
				this.mInitStr = "";
				// 这里要注意和客户端输出流的写方法对应，否则抛出EOFException
				String clientInputStr = mInputStream.readLine();
				if(clientInputStr == null) {
					this.close();
					ClientList.getInstance().remove(this);
				} else {
					// 处理客户端数据
					System.out.println("客户端请求的内容： " + clientInputStr);
					processMessage(clientInputStr);
				}
			}

		} catch (IOException e) {
			System.out.println("-----------------------------------");
			System.out.println("客户端退出： " + e.getMessage());
			System.out.println("-----------------------------------");

			ClientList.getInstance().remove(this);
		} finally {
			try {
				this.close();
				ClientList.getInstance().remove(this);
			} catch (IOException e) {
				System.out.println("已关闭连接： " + e.getMessage());
			}
		}
	}

	/**
	 * 给客户端发消息
	 * 
	 * @throws IOException
	 */
	public void responseToClient(String str) throws IOException {
		mOutputStream.print(str);
		mOutputStream.flush();
	}

	public void sendToSomeClient(AbstractClientSocket client, String msg) {
		client.mOutputStream.print(msg);
		client.mOutputStream.flush();
	}

	abstract public void processMessage(String clientInputStr) throws IOException;
}
