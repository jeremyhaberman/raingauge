package com.jeremyhaberman.notification;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.mock.MockNotificationManager;
import com.jeremyhaberman.raingauge.notification.DefaultNotificationHelper;

public class DefaultNotificationHelperTest extends InstrumentationTestCase {

	private Context mContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mContext = getInstrumentation().getTargetContext();
		ServiceManager.reset(mContext);
	}

	@Override
	protected void tearDown() throws Exception {
		ServiceManager.reset(mContext);
		super.tearDown();
	}

	@MediumTest
	public void testScheduleRainfallNotification() throws InterruptedException {

		MockNotificationManager notificationManager = new MockNotificationManager();

		ServiceManager.loadService(mContext, Service.NOTIFICATION_MANAGER,
				notificationManager);

		DefaultNotificationHelper helper = new DefaultNotificationHelper();
		helper.scheduleRainfallNotification(mContext, System.currentTimeMillis() + 5000, 0.20);

		assertNull(notificationManager.getLastNotification());

		Thread.sleep(10000);

		assertNotNull(notificationManager.getLastNotification());
		assertEquals(notificationManager.getLastNotificationId(), 4692);
	}
}
