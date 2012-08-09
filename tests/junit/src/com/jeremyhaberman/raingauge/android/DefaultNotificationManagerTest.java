package com.jeremyhaberman.raingauge.android;

import android.app.Notification;
import android.test.InstrumentationTestCase;
import com.jeremyhaberman.raingauge.R;

public class DefaultNotificationManagerTest extends InstrumentationTestCase {

	private DefaultNotificationManager mNotificationManager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mNotificationManager = new DefaultNotificationManager(getInstrumentation().getTargetContext());
	}

	public void testPreconditions() {
		assertNotNull(mNotificationManager);
	}

	public void testNotify() throws InterruptedException {

		Notification.Builder notificationBuilder = new Notification.Builder(getInstrumentation().getTargetContext());
		notificationBuilder.setSmallIcon(R.drawable.icon);
		notificationBuilder.setContentTitle("Title");
		notificationBuilder.setContentText("Text");
		notificationBuilder.setAutoCancel(true);

		mNotificationManager.notify(1234, notificationBuilder.getNotification());
	}

}
