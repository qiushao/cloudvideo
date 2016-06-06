package com.morning.demo.activity.main;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.morning.demo.bean.ResultInfo;
import com.morning.demo.onedot.OneDot;
import com.morning.demo.onedot.VideoDescriptionObject;
import com.morning.demo.onedot.VideoDescriptionObject.OneDotVideo;
import com.morning.demo.tools.OKHttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Morning on 2016-6-2.
 */
public class MainModel {

    private static final int MAX_THREAD_NUM = 5;
    private ExecutorService mThreadTool;
    private OneDot mOneDot;
    private Handler mHandler;

    public MainModel() {
        this.mOneDot = OneDot.getInstance();
        mThreadTool = Executors.newFixedThreadPool(MAX_THREAD_NUM);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public interface SearchListener{
        void finish(List<ResultInfo> results);
    }

    public void searchKeyword(final String keyword, final SearchListener listener){
        mThreadTool.submit(new Runnable() {
            @Override
            public void run() {
                final List<ResultInfo> results= mOneDot.queryKeyword(keyword);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.finish(results);
                    }
                });
            }
        });
    }

    public void searchHot(final SearchListener listener){
        mThreadTool.submit(new Runnable() {
            @Override
            public void run() {
                final List<ResultInfo> results= mOneDot.queryHotSearch();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.finish(results);
                    }
                });
            }
        });
    }

    public interface DecodeToHTMLListener{
        void success(String title,List<String> html);
    }

    public void getHTML(final ResultInfo resultInfo,final DecodeToHTMLListener listener){
        mThreadTool.submit(new Runnable() {
            @Override
            public void run() {
                String result = OKHttpUtils.get(resultInfo.getPosprofile());
                if (null != result && result.contains("var yyitem=")) {
                    result = result.replace("var yyitem=", "");
                } else {
                    return;
                }
                VideoDescriptionObject object = JSON.parseObject(result, VideoDescriptionObject.class);
                object.parseHtmlDescription();
                final List<String> htmls = new ArrayList<String>();
                for(OneDotVideo video : object.getInfos()){
                    htmls.add(video.url);
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.success(resultInfo.getTitle(),htmls);
                    }
                });
            }
        });
    }


}
