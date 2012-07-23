package com.jeremyhaberman.raingauge.test.mock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MockBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = MockBroadcastReceiver.class.getSimpleName();

    public boolean alarmed = false;
    private Object mSync = new Object();

    public long elapsedTime;
    public long rtcTime;
	private Intent mIntent;

	@Override
    public void onReceive(Context context, Intent intent) {

		mIntent = intent;
    }

    public Intent getLastIntent() {
		return mIntent;
	}
}
