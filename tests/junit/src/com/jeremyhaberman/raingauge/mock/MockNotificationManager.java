package com.jeremyhaberman.raingauge.mock;

import android.app.Notification;
import com.jeremyhaberman.raingauge.android.NotificationManager;

public class MockNotificationManager implements NotificationManager {

	private int mNotificationId;
	private Notification mNotification;

	@Override
	public void notify(int notificationId, Notification notification) {
		mNotificationId = notificationId;
		mNotification = notification;
	}

	public int getLastNotificationId() {
		return mNotificationId;
	}

	public Notification getLastNotification() {
		return mNotification;
	}
}
