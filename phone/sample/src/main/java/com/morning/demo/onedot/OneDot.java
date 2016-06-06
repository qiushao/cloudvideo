package com.morning.demo.onedot;


import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.morning.demo.bean.ResultInfo;
import com.morning.demo.onedot.OneDotObject.*;
import com.morning.demo.onedot.OneDotQueryWorker.*;
import com.morning.demo.onedot.QueryWorker.*;
import com.morning.demo.tools.OKHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OneDot {
    public static final String ONEDOT = "com.konka.cloudsearch.onedot.OneDot";
    private static OneDot sInstance;
    private static final String SEARCH_KEYOWRD_URL = "http://so.rockitv.com/search?"
            + "writer=js&pn=1&encoding=utf-8&src=insite&kw=";
    private static final String SEARCH_HOT_URL = "http://epg.rockitv.com/cate_31_p1.js";
    private static final long SEARCH_WAIT_STEP = 1000;
    private static final long SEARCH_OUT_TIME = 20 * 1000;
    private long start;
    private List<OneDotVideo> results;
    private boolean isQueryFinish;
    private Map<String, String> mVideoList = new HashMap<String, String>();


    public static OneDot getInstance() {
        if (null == sInstance) {
            synchronized (OneDot.class) {
                if (null == sInstance) {
                    sInstance = new OneDot();
                }
            }
        }
        return sInstance;
    }

    private OneDot() {
    }

    public synchronized List<ResultInfo> queryHotSearch(){
        return realSearch(SEARCH_HOT_URL);
    }


    public synchronized List<ResultInfo> queryKeyword(String keyword) {
        final String search = SEARCH_KEYOWRD_URL + Uri.encode(keyword);
        return realSearch(search);
    }



    private List<ResultInfo> realSearch(String url) {
        List<ResultInfo> res = new ArrayList<>();
        results = new ArrayList<>();
        start = SystemClock.uptimeMillis();
        final String temp = query(url);
        List<OneDotVideo> prim = new ArrayList<>();
        if (!TextUtils.isEmpty(temp)) {
            try {
                prim = JSON.parseObject(temp, OneDotObject.class).getInfos();
            } catch (JSONException e) {
            }
        }
        if (prim.isEmpty()) {
            return res;
        }

        final OneDotQueryWorker worker = new OneDotQueryWorker();
        mVideoList.clear();
        for (OneDotVideo v : prim) {
            mVideoList.put(v.img, v.nextUrl);
            worker.submit(new OneDotTask(v, new OneDotCallback()));
        }
        isQueryFinish = false;
        worker.setWorkFinishListener(new IWorkFinishListener() {

            @Override
            public void workFinish() {
                // search finish
                isQueryFinish = true;
                worker.shutdown();
            }
        });
        long use;
        while (!isQueryFinish) {
            try {
                TimeUnit.MILLISECONDS.sleep(SEARCH_WAIT_STEP);
            } catch (InterruptedException e) {
            }
            use = SystemClock.uptimeMillis() - start;
            if (use >= SEARCH_OUT_TIME) {
                isQueryFinish = true;
                worker.shutdown();
            }
        }
        synchronized (results) {
            for (OneDotVideo video : results) {

                res.add(new ResultInfo(video.title,
                        video.img,video.nextUrl,
                        ONEDOT,video.desc,video.actor,
                        video.category));
            }
        }
        return res;
    }

    private String query(String url) {
        String result = OKHttpUtils.get(url);
        if (null != result && result.contains("var yyitem=")) {
            result = result.replace("var yyitem=", "");
        }
        return result;
    }

    private class OneDotCallback implements IWorkerCallback<OneDotVideo> {

        @Override
        public void success(OneDotVideo item) {
            if (null != item.nextUrl && !item.nextUrl.isEmpty()) {
                if (!isQueryFinish) {
                    synchronized (results) {
                        results.add(item);
                    }
                }
            }
        }

    }
}
