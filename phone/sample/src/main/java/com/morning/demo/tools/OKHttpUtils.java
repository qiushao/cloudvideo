package com.morning.demo.tools;

import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by shaoqiu on 2015-8-28.
 */
public class OKHttpUtils {
    private static OkHttpClient client;

    static {
        client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.SECONDS);
    }

    public static String get(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.d("mumu", "exception: ", e);
        }
        return null;
    }
}
