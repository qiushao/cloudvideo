package com.morning.demo.activity.download;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.morning.demo.R;
import com.rockitv.android.BasePlayerActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class DownloadActivity extends BasePlayerActivity implements IDownloadView{

    private RecyclerView mDownloadList;
    private LinearLayout mTip;
    private ImageView mTipImg;
    private TextView mTipText;

    private DownloadPresenter mPresenter;
    private SweetAlertDialog mLoading;

    private String mTitle;
    private String mUrl;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_download);
        mDownloadList = (RecyclerView) findViewById(R.id.download_list);
        mTip = (LinearLayout) findViewById(R.id.download_tip);
        mTipImg = (ImageView) findViewById(R.id.download_tip_img);
        mTipText = (TextView) findViewById(R.id.download_tip_text);
        mLoading = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

        mPresenter = new DownloadPresenter(this);
        setData();
        mPresenter.setDownloadList(mDownloadList);
    }

    private void setData(){
        Intent intent = getIntent();
        mUrl = getVideoUrl(intent);
        mTitle = intent.getStringExtra("title");
    }

    @Override
    public String getVideoTitle() {
        return mTitle;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public void showLoading() {
        mLoading.setTitleText("Loading...").show();
    }

    @Override
    public void dimissLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoading.dismiss();
            }
        },300);
    }

    @Override
    public void showEmptyTips() {
        mTipImg.setBackgroundResource(R.drawable.no_result);
        mTipText.setText("sorry, I had not found anything...");
        mTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void dimissEmptyTips() {

    }


    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        showVideosState();

        Log.d("mumu","pageUrl " + mUrl);
        if (mUrl != null && mUrl.trim().length() > 0) {
            startParse(mUrl, DEFINITION_HD);
        }
	}

    private void showVideosState(){
        mPresenter.handleVideosStateRequest();
    }



    /** 收到播放视频的广播 可能来自微信或者网页 */
    @Override
    protected void onVideoPageChange(String pageUrl) {
    }

    /** 连接解析成功回调 */
	@Override
	protected void onVideoParseOk(String originalUrl, String url, String current,
			String resolution) {
        mPresenter.handleCacheVideo(url);
	}

    /** 连接解析失败回调 */
    @Override
    protected void onVideoParseFail(String url) {
    }

}
