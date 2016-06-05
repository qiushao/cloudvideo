package com.morning.demo.activity.download;

/**
 * Created by Morning on 2016-6-4.
 */
public interface IDownloadView {
    String getVideoTitle();
    String getUrl();
    void showLoading();
    void dimissLoading();
    void showEmptyTips();
    void dimissEmptyTips();
}
