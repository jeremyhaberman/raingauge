package com.jeremyhaberman.raingauge.test.mock;

import android.app.PendingIntent;
import android.content.Context;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;

public class MockAlarmManager implements AndroidAlarmManager {

	private Context mContext;
	private static final String TAG = MockAlarmManager.class.getSimpleName();
	private Alarm mLastAlarm;

	public class Alarm {
		public final int type;
		public final long triggerAtMillis;
		public final long intervalMillis;

		Alarm(int type, long triggerAtMillis, long intervalMillis) {
			this.type = type;
			this.triggerAtMillis = triggerAtMillis;
			this.intervalMillis = intervalMillis;
		}
	}

	public MockAlarmManager(Context context) {
		mContext = context;
	}

	@Override
	public void setRepeating(int type, long triggerAtMillis, long intervalMillis,
							 PendingIntent operation) {

		mLastAlarm = new Alarm(type, triggerAtMillis, intervalMillis);
	}

	public Alarm getLastAlarm() {
		return mLastAlarm;
	}
}
