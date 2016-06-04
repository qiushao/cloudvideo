package com.example.offlineclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class ReceiverService extends Service {

	private boolean STOP = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.v("BLB", "Start the receiver service");
		new Thread() {
			public void run() {
				try {
					Socket socket = new Socket("192.168.199.1", 32222);
					Log.v("BLB", "Connected the server");
					OutputStream os = socket.getOutputStream();
					JSONObject reqJson = new JSONObject();
					reqJson.put("type", "cached_list");
					os.write((reqJson.toString() + "\n").getBytes("utf-8"));
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while (!STOP) {
						String content;
						while ((content = br.readLine()) != null) {
							JSONObject receJson = new JSONObject(content);
							JSONArray videoArry = receJson.getJSONArray("videos");
							for (int i = 0; i < videoArry.length(); i++) {
								Log.v("BLB", "Get the video");
								JSONObject video = videoArry.getJSONObject(i);
								String videoName = video.getString("title");
								String url = video.getString("url");
								Log.v("BLB", "Get the video videoName = " + videoName);
								Log.v("BLB", "Get the video url = " + url);

								JSONObject realJson = new JSONObject();
								JSONObject sendJson = new JSONObject();
								sendJson.put("package_name", "com.example.palydemo");
								sendJson.put("push_type", "popup");
								sendJson.put("title", videoName);
								sendJson.put("summary", videoName + "下载完成，可以播放");
								sendJson.put("content", "亲爱的用户" + videoName + "我们已经在后台帮您下载了，可以直接点击播放");
								JSONArray temparr = new JSONArray();

								JSONObject tempJson1 = new JSONObject();
								tempJson1.put("name", "进入播放");
								tempJson1.put("type", 1);
								tempJson1.put("value", "com.konka.blb");

								JSONObject tempJson2 = new JSONObject();
								tempJson2.put("name", "t播放");
								tempJson2.put("type", 2);
								tempJson2.put("value", "com.konka.zhaotong");

								temparr.put(tempJson1);
								temparr.put(tempJson2);

								sendJson.put("button", temparr);

								realJson.put("data", sendJson);

								Log.v("BLB", "send it " + realJson.toString());
								Intent sendIntent = new Intent();
								sendIntent.setAction("com.konka.message.PUSH_LOCAL_MESSAGE");
								sendIntent.putExtra("data", realJson.toString());
								startService(sendIntent);
							}

						}
					}
				} catch (UnknownHostException e) {
					Log.v("BLB", "UnknownHostException");
					e.printStackTrace();
				} catch (IOException e) {
					Log.v("BLB", "IOException", e);
					e.printStackTrace();
				} catch (JSONException e) {
					Log.v("BLB", "JSONException");
					e.printStackTrace();
				}
			};
		};

		new Thread() {
			@SuppressLint("WorldReadableFiles")
			public void run() {
				String s = HttpUtils.get("http://192.168.199.1/cloudvideo/videos.json");
				if (s != null) {
					if (!s.trim().equals("")) {
						try {
							JSONObject receJson = new JSONObject(s);
							JSONArray videoArry = receJson.getJSONArray("videos");
							@SuppressWarnings("deprecation")
							SharedPreferences preferences = getSharedPreferences("bailibin",
									Context.MODE_WORLD_READABLE);
							SharedPreferences.Editor ed = preferences.edit();
							ed.putString("video", videoArry.toString());
							ed.commit();
							Log.v("BLB", "Write shared =" + videoArry.toString());
							Log.v("BLB", "read from shared" + preferences.getString("video", "error"));
							for (int i = 0; i < videoArry.length(); i++) {
								Log.v("BLB", "Get the video");
								JSONObject video = videoArry.getJSONObject(i);
								String videoName = video.getString("title");
								String url = video.getString("url");
								Log.v("BLB", "Get the video videoName = " + videoName);
								Log.v("BLB", "Get the video url = " + url);

								JSONObject realJson = new JSONObject();
								JSONObject sendJson = new JSONObject();
								sendJson.put("package_name", "com.example.offlineclient");
								sendJson.put("push_type", "popup");
								sendJson.put("title", videoName);
								sendJson.put("summary", videoName + "下载完成，可以播放");
								sendJson.put("content", "亲爱的用户" + videoName + "我们已经在后台帮您下载了，可以直接点击播放");
								JSONArray temparr = new JSONArray();

								JSONObject tempJson1 = new JSONObject();
								tempJson1.put("name", "进入播放");
								tempJson1.put("type", 1);
								tempJson1.put("value", "com.konka.blb");

								JSONObject tempJson2 = new JSONObject();
								tempJson2.put("name", "t播放");
								tempJson2.put("type", 2);
								tempJson2.put("value", "com.konka.zhaotong");
								temparr.put(tempJson1);

								temparr.put(tempJson2);
								sendJson.put("button", temparr);

								realJson.put("data", sendJson);

								Log.v("BLB", "send it " + realJson.toString());
								Intent sendIntent = new Intent();
								sendIntent.setAction("com.konka.message.PUSH_LOCAL_MESSAGE");
								sendIntent.putExtra("data", realJson.toString());
								startService(sendIntent);
							}

						} catch (JSONException e) {
							Log.v("BLB", "JSONException");
							e.printStackTrace();
						}
					}
				}
			};
		}.start();

		super.onCreate();
	}
}
