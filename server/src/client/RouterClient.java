package client;

import java.io.IOException;
import java.net.Socket;

import parser.Const;
import parser.ParseUtils;

public class RouterClient extends AbstractClientSocket {

	
	public RouterClient(Socket socket, String inStr) {
		super(socket, inStr);
		mRouterMac = ParseUtils.get(inStr,"mac");
	}
	
	@Override
	public void processMessage(String clientInputStr) throws IOException {
		String type = ParseUtils.getType(clientInputStr);

		// 请求缓存
		if (Const.LOGIN_REQUEST.equals(type)) {
			// 路由器连接成功
			System.out.println("路由器连接成功...........");
			String responseSucc =  "{\"type\":\"login_response\",\"success\":\"yes\"}\n";
			responseToClient(responseSucc);
		}

		// 缓存状态查询
		if (Const.CACHE_STATE_RESPONSE.equals(type)) {
			
//			String url = ParseUtils.getUrl(clientInputStr);
//			String progress = ParseUtils.getProgress(clientInputStr);
			
			String phoneId = ParseUtils.getPhoneId(clientInputStr);
			System.out.println("将要发送给手机端的ID: " + phoneId);
			AbstractClientSocket client = ClientList.getInstance().findClientByPhoneID(phoneId);
			if (client != null) {
//				String msg = "{\"type\":\"cache_state_response\",\"progress\":" + progress + ",\"url\":" + url + "}\n";
				// 发给手机client缓存状态
				clientInputStr += "\n";
				sendToSomeClient(client, clientInputStr);
			}
			else{
				System.out.println("请求缓存状态失败！！！！");
				String responseFail = "{\"type\":\"cache_state_response\",\"state\":" + "\"error\", \"reseaon\":\"未找到绑定的手机\"}\n";
				responseToClient(responseFail);
			}
		}

	}


}
