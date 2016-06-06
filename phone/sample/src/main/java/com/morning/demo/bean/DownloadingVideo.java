package com.morning.demo.bean;

/**
 * Created by Morning on 2016-6-4.
 */
public class DownloadingVideo {

    String url;
    String title;
    int progress;
    String state;
    String reason;

    public DownloadingVideo(){}

    public DownloadingVideo(String url, String title, int progress, String state, String reason) {
        this.url = url;
        this.title = title;
        this.progress = progress;
        this.state = state;
        this.reason = reason;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
