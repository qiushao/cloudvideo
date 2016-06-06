package com.morning.demo.activity.download;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.morning.demo.bean.DownloadingVideo;
import com.morning.demo.tools.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Morning on 2016-6-4.
 */
public class DownloadModel {

    private ExecutorService mThreadPool;

    private Socket mSocket;
    private BufferedReader mInput;
    private PrintWriter mOutput;

    public DownloadModel(){
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    public interface RequestListener{
        void success(List<DownloadingVideo> videos);
    }

    public void requestVideosState(final RequestListener listener){
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Log.d("mumu","requestVideosState");
                initSocket();
                String request = buildRequest() + "\n";
                mOutput.print(request);
                mOutput.flush();
                listenFeedBack(listener);
            }
        });
    }

    public void initSocket(){
        try {
            mSocket = new Socket("10.117.3.22", 11111);
            if (mSocket.isConnected()) {
                mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOutput = new PrintWriter(mSocket.getOutputStream(), true);
            } else {
                Log.d("mumu", "socket is not connect!");
            }
            Log.d("mumu","socket initial");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildRequest(){
        JSONObject request = new JSONObject();
        request.put("type","cache_state_request");
        request.put("phoneid", Utils.getPhoneId());
        request.put("routeid",Utils.getRouteId());
        return request.toJSONString();
    }

    private void listenFeedBack(final RequestListener listener){
        try {
            String res = mInput.readLine();
            Log.d("mumu","response " + res);
            JSONObject response = JSON.parseObject(res);
            final List<DownloadingVideo> videos = JSON.parseArray(
                    response.getString("data"),DownloadingVideo.class);
            Log.d("mumu","size: " + videos.size());
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.success(videos);
                }
            });
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
}
