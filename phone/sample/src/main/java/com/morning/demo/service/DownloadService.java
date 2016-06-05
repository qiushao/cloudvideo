package com.morning.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.morning.demo.tools.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService extends Service {

    private static final int MAX_THREAD_NUM = 5;
    private ExecutorService mThreadTool;

    private String mTitle;
    private String mUrl;

    private Socket mSocket;
    private BufferedReader mInput;
    private PrintWriter mOutput;

    public DownloadService() {
        mThreadTool = Executors.newSingleThreadExecutor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTitle = intent.getStringExtra("title");
        mUrl = intent.getStringExtra("url");
        mThreadTool.submit(new Runnable() {
            @Override
            public void run() {
                postData();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    private void postData() {
        initSocket();

        String request = buildString() + "\n";
        mOutput.print(request);
        mOutput.flush();
        listenFeedBack();
        Log.d("mumu", "postData title is " + mTitle + " url is " + mUrl);
    }

    private void initSocket() {
        try {
            mSocket = new Socket("10.117.3.22", 11111);
            if (mSocket.isConnected()) {
                mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOutput = new PrintWriter(mSocket.getOutputStream(), true);
            } else {
                Log.d("mumu", "socket is not connect!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenFeedBack() {
        try {
            String res = mInput.readLine();
            JSONObject response = JSON.parseObject(res);
            Log.d("mumu", "get feedback " + response.getString("success"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                mSocket.close();
                mSocket = null;
                mOutput.close();
                mInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildString() {
        JSONObject request = new JSONObject();
        request.put("type", "cache_request");
        request.put("title", mTitle);
        request.put("url", mUrl);
        request.put("phoneid", Utils.getPhoneId());
        request.put("routeid", Utils.getRouteId());
        return request.toJSONString();
    }
}
