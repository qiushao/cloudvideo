package parser;

import com.alibaba.fastjson.JSONObject;

public class ParseUtils {

	/**
	 * 得到请求类型
	 * 
	 * @param json
	 * @return
	 */
	public static String get(String json,String key) {
		String value = "";
		if (json == null || ("").equals(json)) {
			return value;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		value = jsonObject.getString(key);
		return value;
	}

	
	/**
	 * 得到请求类型
	 * 
	 * @param json
	 * @return
	 */
	public static String getType(String json) {
		String type = "";
		if (json == null || ("").equals(json)) {
			return type;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		type = jsonObject.getString("type");
		return type;
	}

	/**
	 * 得到mac地址
	 * 
	 * @param json
	 * @return
	 */
	public static String getMac(String json) {
		String mac = "";
		if (json == null || ("").equals(json)) {
			return mac;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		mac = jsonObject.getString("mac");
		return mac;
	}

	/**
	 * 得到待下载的url地址
	 * 
	 * @param json
	 * @return
	 */
	public static String getUrl(String json) {
		String url = "";
		if (json == null || ("").equals(json)) {
			return url;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		url = jsonObject.getString("url");
		return url;
	}

	/**
	 * 得到phoneid
	 * 
	 * @param json
	 * @return
	 */
	public static String getPhoneId(String json) {
		String phoneId = "";
		if (json == null || ("").equals(json)) {
			return phoneId;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		phoneId = jsonObject.getString("phoneid");
		return phoneId;
	}

	/**
	 * 得到title
	 * 
	 * @param json
	 * @return
	 */
	public static String getTitle(String json) {
		String title = "";
		if (json == null || ("").equals(json)) {
			return title;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		title = jsonObject.getString("phoneid");
		return title;
	}

	/**
	 * 得到Progress
	 * 
	 * @param json
	 * @return
	 */
	public static String getProgress(String json) {
		String progress = "";
		if (json == null || ("").equals(json)) {
			return progress;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		progress = jsonObject.getString("progress");
		return progress;
	}

	/**
	 * 得到state
	 * @param json
	 * @return
	 */
	public static String getState(String json) {
		String state = "";
		if (json == null || ("").equals(json)) {
			return state;
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		state = jsonObject.getString("progress");
		return state;
	}

}
