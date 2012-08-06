package com.jeremyhaberman.raingauge.mock;

import android.content.Context;
import com.jeremyhaberman.raingauge.notification.NotificationHelper;

public class MockNotificationHelper implements NotificationHelper {

	private long mWhen;
	private double mRainfall;

	@Override
	public void scheduleRainfallNotification(Context context, long when, double rainfall) {
		mWhen = when;
		mRainfall = rainfall;
	}

	public long getWhen() {
		return mWhen;
	}

	public double getRainfall() {
		return mRainfall;
	}
}
