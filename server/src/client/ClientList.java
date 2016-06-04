package client;

import java.util.ArrayList;
import java.util.List;

public class ClientList {

	private static List<AbstractClientSocket> clientList = new ArrayList<AbstractClientSocket>();
	
	// 饿汗式
	private static ClientList instance = null;
	static {
		instance = new ClientList();
	}
	
	private ClientList() {
		
	}
	public static ClientList getInstance() {
		return instance;
	}
	
	/**
	 * 添加
	 * @param abstractClientSocket
	 */
	public void add(AbstractClientSocket client) {
		synchronized (clientList) {
			clientList.add(client);
		}
	}
	
	/**
	 * 移除
	 * @param client
	 */
	public void remove(AbstractClientSocket client) {
		synchronized (clientList) {
			clientList.remove(client);
		}
	}
	/**
	 *  根据mac地址判断是否为同一个client
	 * @param mac
	 * @return
	 */
	public boolean isEqualRouterID(String mac, AbstractClientSocket client) {
		if(mac == null || ("").equals(mac))
			return false;
		return mac.equals(client.getmRouterMac());
	}
	
	/**
	 *  根据phoneID地址判断是否为同一个client
	 * @param mac
	 * @return
	 */
	public boolean isEqualPhoneID(String phoneID, AbstractClientSocket client) {
		if(phoneID == null || ("").equals(phoneID))
			return false;
		return phoneID.equals(client.getPhoneID());
	}
	
	/**
	 * 根据mac地址在clientList列表中得到client
	 * @param mac
	 * @return
	 */
	public AbstractClientSocket findClientByRouterID(String mac) {
		synchronized (clientList) {
			for (AbstractClientSocket client : clientList) {
				// 找到手机所绑定的路由器client
				if (isEqualRouterID(mac, client)) {
					return client;
				}
			}
			return null;
		}
	}
	
	
	/**
	 * 根据phoneID在clientList列表中得到client
	 * @param mac
	 * @return
	 */
	public AbstractClientSocket findClientByPhoneID(String phoneID) {
		synchronized (clientList) {
			for (AbstractClientSocket client : clientList) {
				// 找到手机所绑定的路由器client
				if (isEqualPhoneID(phoneID, client)) {
					return client;
				}
			}
			return null;
		}
	}
}
