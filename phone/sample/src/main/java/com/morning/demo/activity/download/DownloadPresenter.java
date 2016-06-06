package com.morning.demo.activity.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.morning.demo.activity.download.DownloadModel.RequestListener;
import com.morning.demo.adapter.DownloadListAdapter;
import com.morning.demo.bean.DownloadingVideo;
import com.morning.demo.service.DownloadService;
import com.morning.demo.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Morning on 2016-6-4.
 */
public class DownloadPresenter {

    private Context mContext;
    private IDownloadView mDownloadView;
    private RecyclerView mDownloadList;
    private DownloadListAdapter mDownloadListAdapter;

    private DownloadModel mModel;
    private List<DownloadingVideo> mVideos;


    public DownloadPresenter(Context context) {
        this.mContext = context;
        this.mDownloadView = (IDownloadView) context;
        mVideos = new ArrayList<>();
        mModel = new DownloadModel();
    }

    public void setDownloadList(RecyclerView downloadList) {
        mDownloadList = downloadList;
        mDownloadListAdapter = new DownloadListAdapter();
        mDownloadList.setAdapter(mDownloadListAdapter);
        mDownloadList.setLayoutManager(new LinearLayoutManager(mContext));
        mDownloadListAdapter.setItemsOnClickListener(new DownloadListAdapter.OnItemClickListener() {
            @Override
            public void onClick(DownloadingVideo video) {
                showDelete(video);
            }
        });
    }

    private void showDelete(final DownloadingVideo video) {
        final SweetAlertDialog delete = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        delete.setTitleText("客官，真的要删除吗？")
                .setContentText(video.getTitle())
                .setConfirmText("是的")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        delete(delete,video);
                    }
                }).show();
    }

    private void delete(final SweetAlertDialog delete, DownloadingVideo video){
        mDownloadListAdapter.deleteDate(video);
        delete.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        delete.setTitleText("已删除")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        delete.dismiss();
                    }
                });
    }

    public void handleCacheVideo(String url) {
        String realUrl = url;
        if (Utils.isNeedDecode(url)) {
            realUrl = Utils.decode(url);
        }
        Intent serviceIntent = new Intent(mContext, DownloadService.class);
        serviceIntent.putExtra("title", mDownloadView.getVideoTitle());
        serviceIntent.putExtra("url", realUrl);
        serviceIntent.setFlags(Service.START_REDELIVER_INTENT);
        mContext.startService(serviceIntent);
        Log.d("mumu", "m3u8: " + realUrl);
    }

    public void handleVideosStateRequest() {
        mDownloadView.showLoading();
        mModel.requestVideosState(new RequestListener() {
            @Override
            public void success(List<DownloadingVideo> videos) {
                showDownloadList(videos);
            }
        });
    }

    private void showDownloadList(List<DownloadingVideo> videos) {
        if (null != mDownloadView.getUrl() && !"".equals(mDownloadView.getUrl())) {
            videos.add(new DownloadingVideo("url", mDownloadView.getVideoTitle(), 0, "downloading", ""));
        } else if (videos.isEmpty()) {
            mDownloadView.showEmptyTips();
        }
        mDownloadListAdapter.swapData(videos);
        mDownloadView.dimissLoading();
    }
}
