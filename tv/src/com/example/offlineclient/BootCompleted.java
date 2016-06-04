package com.example.offlineclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleted extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.v("BLB", "onReceive and action = " + action);
		if (action.equals("com.konka.zhaotong")) {
			Intent intent1 = new Intent(context, MainActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
		} else {
			Log.v("BLB", "get");
			Intent intent1 = new Intent(context, ReceiverService.class);
			context.startService(intent1);
		}
	}
}
