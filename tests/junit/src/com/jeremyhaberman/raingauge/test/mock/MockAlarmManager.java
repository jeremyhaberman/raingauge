package com.jeremyhaberman.raingauge.test.mock;

import android.app.PendingIntent;

import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;

public class MockAlarmManager implements AndroidAlarmManager {

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

		Alarm(int type, long triggerAtMillis) {
			this.type = type;
			this.triggerAtMillis = triggerAtMillis;
			this.intervalMillis = -1;
		}
	}

	public MockAlarmManager() {
	}

	@Override
	public void setRepeating(int type, long triggerAtMillis, long intervalMillis,
							 PendingIntent operation) {

		mLastAlarm = new Alarm(type, triggerAtMillis, intervalMillis);
	}

	@Override
	public void set(int type, long triggerAtMillis, PendingIntent operation) {
		mLastAlarm = new Alarm(type, triggerAtMillis);
	}

	public Alarm getLastAlarm() {
		return mLastAlarm;
	}
}
