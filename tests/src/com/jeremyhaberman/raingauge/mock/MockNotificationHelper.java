package com.jeremyhaberman.raingauge.mock;

import com.jeremyhaberman.raingauge.notification.NotificationHelper;

public class MockNotificationHelper implements NotificationHelper {

	private long mCurrentTimeInMillis;
	private double mRainfall;

	@Override
	public void scheduleRainfallNotification(long currentTimeInMillis, double rainfall) {
		mCurrentTimeInMillis = currentTimeInMillis;
		mRainfall = rainfall;
	}

	public long getCurrentTimeInMillis() {
		return mCurrentTimeInMillis;
	}

	public double getRainfall() {
		return mRainfall;
	}
}
