package com.jeremyhaberman.raingauge.notification;

public interface NotificationHelper {

	void scheduleRainfallNotification(long currentTimeInMillis, double rainfall);
}
