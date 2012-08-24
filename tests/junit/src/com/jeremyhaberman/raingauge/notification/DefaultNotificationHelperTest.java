
package com.jeremyhaberman.raingauge.notification;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.android.DefaultNotificationManager;
import com.jeremyhaberman.raingauge.android.NotificationManager;
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

        MockNotificationManager mockNotificationManager = new MockNotificationManager();

        ServiceManager.loadService(mContext, Service.NOTIFICATION_MANAGER,
                mockNotificationManager);

        DefaultNotificationHelper helper = new DefaultNotificationHelper();
        helper.scheduleRainfallNotification(mContext, System.currentTimeMillis() + 5000, 0.20);

        assertNull(mockNotificationManager.getLastNotification());

        Thread.sleep(10000);

        assertNotNull(mockNotificationManager.getLastNotification());
        assertEquals(mockNotificationManager.getLastNotificationId(), 4692);

        // Our MockNotificationManager above doesn't actually send the
        // notification built by the NotificationHelper to the Android system.
        // To ensure that the Notification built by the NotificationHelper is
        // valid, we need to test with the NotificationManager implementation
        // that sends the Notification to the OS. If the Notification is
        // invalid, an IllegalArgumentException will be thrown, so we don't
        // actually need to assert anything.

        NotificationManager realNotificationManager = new DefaultNotificationManager(mContext);
        ServiceManager.loadService(mContext, Service.NOTIFICATION_MANAGER,
                realNotificationManager);
        helper.scheduleRainfallNotification(mContext, System.currentTimeMillis(), 0.20);

    }
}
