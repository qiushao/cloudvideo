package client;

import java.io.IOException;
import java.net.Socket;

import parser.Const;
import parser.ParseUtils;

public class PhoneClient extends AbstractClientSocket {


	public PhoneClient(Socket socket, String inStr) {
		super(socket, inStr);
		mRouterID = ParseUtils.get(inStr,"routeid");
		mPhoneID = ParseUtils.get(inStr, "phoneid");
	}
	

	@Override
	public void processMessage(String clientInputStr) throws IOException {
		String type = ParseUtils.getType(clientInputStr);

		// 请求缓存
		if (Const.CAHCE_REQUEST.equals(type)) {
			AbstractClientSocket client = ClientList.getInstance().findClientByRouterID(mRouterID);
			if (client != null) {
				String title = ParseUtils.getTitle(clientInputStr);
				String url = ParseUtils.getUrl(clientInputStr);
				System.out.println("title: " + title);
				System.out.println("url: " + url);
//				String msg = "{\"type\":\"cache_request\",\"title\":" + " \" " + title + "\"" + ",\"url\":" + url + "}\n";
				clientInputStr = clientInputStr + "\n";
				// 发给路由器开始缓存信息
				sendToSomeClient(client, clientInputStr);
				// 返回请求缓存成功
				String responseSucc = "{\"type\":\"cache_response\",\"success\":\"yes\"}\n";
				responseToClient(responseSucc);
			}
			else {
				System.out.println("未绑定路由器!!!!");
				// 请求缓存失败
				String responseFail = "{\"type\":\"cache_response\",\"success\":\"no\"}\n";
				responseToClient(responseFail);
			}

		} 
		// 缓存状态查询
		else if (Const.CACHE_STATE_REQUEST.equals(type)) {
			
//			String url = ParseUtils.getUrl(clientInputStr);
//			String state = ParseUtils.getState(clientInputStr);
			// 通知路由器client查询缓存状态
			AbstractClientSocket client = ClientList.getInstance().findClientByRouterID(mRouterID);
			if (client != null) {
//				String msg = "{\"type\":\"cache_state_request\",\"state\":" + state + ",\"url\":" + url + "}\n";
				// 发给路由器获取缓存状态
				clientInputStr += "\n";
				System.out.println("将要发送给路由的 " + "clientInputStr: " + clientInputStr);
				sendToSomeClient(client, clientInputStr);
			} else {
				System.out.println("手机端请求缓存状态失败！！！！");
				String responseFail = "{\"type\":\"cache_state_response\",\"state\":" + "\"error\"}\n";
				responseToClient(responseFail);
			}
		}
	}


}
