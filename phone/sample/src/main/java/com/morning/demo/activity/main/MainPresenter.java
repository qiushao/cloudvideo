package com.morning.demo.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.morning.demo.adapter.SearchResultsListAdapter;
import com.morning.demo.adapter.SearchResultsListAdapter.OnItemClickListener;
import com.morning.demo.bean.ResultInfo;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Morning on 2016-6-2.
 */
public class MainPresenter {

    private MainModel mModel;
    private RecyclerView mSearchResultsList;
    private SearchResultsListAdapter mSearchResultsAdapter;

    private Context mContext;
    private IMainView mMainView;

    public MainPresenter(Context context) {
        this.mContext = context;
        this.mModel = new MainModel();
        this.mMainView = (IMainView) context;
    }

    public void setSearchResultsList(RecyclerView searchResultsList) {
        mSearchResultsList = searchResultsList;
        mSearchResultsAdapter = new SearchResultsListAdapter();
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new GridLayoutManager(mContext, 3));
        mSearchResultsAdapter.setItemsOnClickListener(new OnItemClickListener() {
            @Override
            public void onClick(ResultInfo resultInfo) {
                SweetAlertDialog alert = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                alert.setTitleText("Loading...").show();
                getDetial(resultInfo, alert);
            }
        });
    }

    private void getDetial(ResultInfo resultInfo, final SweetAlertDialog alert) {
        mModel.getHTML(resultInfo, new MainModel.DecodeToHTMLListener() {
            @Override
            public void success(final String title, final List<String> htmls) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (htmls.size() == 1) {
                            changeAlertMovie(title,htmls.get(0), alert);
                        } else {
                            changeAlertDrama(title,htmls, alert);
                        }
                    }
                },300);
            }
        });
    }

    private void changeAlertMovie(final String title, final String html, final SweetAlertDialog alert) {
        alert.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        alert.setTitleText("客观，是否要下载该电影呢?")
                .setContentText(title)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setAction("com.rockitv.sdk.action.PLAY_VIDEO_START");
                        intent.putExtra("title",title);
                        intent.putExtra("url", html);
                        mContext.startActivity(intent);
                        alert.dismiss();
                    }
                });
    }

    private void changeAlertDrama(final String title, final List<String> htmls, final SweetAlertDialog alert) {
        alert.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        alert.setTitleText("客观，您要看哪集?")
                .setContentText(title)
                .setConfirmText("选集去！")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setAction("com.rockitv.sdk.action.PLAY_VIDEO_START");
                        intent.putExtra("title",title);
                        intent.putExtra("url", htmls.get(0));
                        mContext.startActivity(intent);
                        //电视剧选集页面
                        alert.dismiss();
                    }
                });
    }

    public void resetResult(List<ResultInfo> results) {
        if (results.isEmpty()) {
            mMainView.showEmptyTips();
        } else {
            mMainView.dimissEmptyTips();
        }
        Log.d("mumu", "display Count " + results.size());
        mSearchResultsAdapter.swapData(results);
        mMainView.dimissLoading();
    }

    public void handleSearch(String keyword) {
        mMainView.showLoading();
        if("".equals(keyword) || null == keyword){
            mModel.searchHot(new MainModel.SearchListener() {
                @Override
                public void finish(List<ResultInfo> results) {
                    resetResult(results);
                }
            });
        }else {
            mModel.searchKeyword(keyword, new MainModel.SearchListener() {
                @Override
                public void finish(List<ResultInfo> results) {
                    resetResult(results);
                }
            });
        }
    }
}
