package com.morning.demo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Morning on 2016-6-3.
 */
public class RootApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        startAdotService();
    }

    private void startAdotService() {
        Log.d("mumu", "startAdotService");
        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction("com.rockitv.action.START_SDK");
        sendBroadcast(intent);
    }
}
