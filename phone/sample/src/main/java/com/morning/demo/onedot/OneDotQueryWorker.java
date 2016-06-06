package com.morning.demo.onedot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import com.morning.demo.onedot.OneDotObject.*;
import com.morning.demo.tools.OKHttpUtils;


class OneDotQueryWorker extends QueryWorker<OneDotObject.OneDotVideo> {
	// 仅在onedot包下可视
	
	interface IWorkFinishListener {
		void workFinish();
	}

	protected static class OneDotTask extends Task<OneDotObject.OneDotVideo> {

		public OneDotTask(OneDotVideo item,
				IWorkerCallback<OneDotObject.OneDotVideo> callback) {
			super(item, callback);
		}

		@Override
		public String toString() {
			return item.toString();
		}
		
	}
	
	private IWorkFinishListener mListener;
	
	public OneDotQueryWorker() {
		super();
	}
	
	public void setWorkFinishListener(IWorkFinishListener listener) {
		mListener = listener;
	}

	@Override
	protected boolean doWork(Task<OneDotVideo> task) {
		final OneDotVideo item = task.item;
		String result = query(item.nextUrl);
		if (!isNullOrEmpty(result)) {
			try {
				OneDotObject temp = JSON.parseObject(result, OneDotObject.class);
				item.nextUrl = temp.getInfos().get(0).nextUrl;
				item.actor = temp.actor;
				item.category = temp.category;
				item.desc = temp.desc;
			} catch (JSONException e){}
		} else {
			item.nextUrl = "";
		}
		return item.nextUrl.contains(".js") || item.nextUrl.isEmpty();
	}
	
	private String query(String url) {
		String result = null;
		result = OKHttpUtils.get(url);
		if (null != result && result.contains("var yyitem=")) {
			result = result.replace("var yyitem=", "");
		}
		return result;
	}

	private boolean isNullOrEmpty(String s) {
		return (null == s) || s.isEmpty();
	}

	@Override
	protected void remainWorkEmpty() {
		if (null != mListener) {
			mListener.workFinish();
		}
	}

}
