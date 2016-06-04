package com.example.offlineclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	private SurfaceView mView;
	private MediaPlayer mPlayer;
	private SurfaceHolder surfaceHolder;
	private ListView mListView;
	private JSONArray arr;

	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.v("BLB", "Start The player demo");

		SharedPreferences preferences = getSharedPreferences("bailibin", MODE_WORLD_READABLE); // ∂¡»°SharedPreferences
		String temp = preferences.getString("video", "error");

		Log.v("BLB", "temp = " + temp);

		if (!temp.trim().equals("error")) {
			try {
				Log.v("BLB", "IN temp = " + temp);
				arr = new JSONArray(temp);
			} catch (JSONException e) {
				e.printStackTrace();
				Log.v("BLB", "JSONException");
			}
		}

		if (arr == null) {
			Log.v("BLB", "arr == null");
			arr = new JSONArray();
		}

		mView = (SurfaceView) findViewById(R.id.surface);
		mListView = (ListView) findViewById(R.id.listView1);
		mListView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView t = new TextView(getApplicationContext());
				JSONObject ob = (JSONObject) getItem(position);
				try {
					t.setText(ob.getString("title"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return t;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				try {
					return arr.getJSONObject(position);
				} catch (JSONException e) {
					return null;
				}
			}

			@Override
			public int getCount() {
				return arr.length();
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer.setDisplay(surfaceHolder);
				try {
					mPlayer.setDataSource(getApplicationContext(),
							Uri.parse(arr.getJSONObject(position).getString("url")));
					mPlayer.prepare();
					mPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		surfaceHolder = mView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mPlayer = new MediaPlayer();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
