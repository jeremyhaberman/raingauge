
package com.jeremyhaberman.raingauge.android;

import android.app.Notification;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.jeremyhaberman.raingauge.ManualAssertionTest;
import com.jeremyhaberman.raingauge.R;

/**
 * Test for the {@link DefaultNotificationManager}.
 */
public class DefaultNotificationManagerTest extends InstrumentationTestCase {

    private DefaultNotificationManager mNotificationManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mNotificationManager = new DefaultNotificationManager(getInstrumentation()
                .getTargetContext());
    }

    public void testPreconditions() {
        assertNotNull(mNotificationManager);
    }

    /**
     * This test creates and sends a notification, which should appear in the
     * status bar. Verifying success requires manually looking at the device. We
     * could try to do this in code, but the application package (real app, not
     * test app), would need permission to inject events outside itself, which
     * the app shouldn't have.
     * 
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @MediumTest
    @ManualAssertionTest
    public void testNotify() throws InterruptedException {

        Notification.Builder notificationBuilder = new Notification.Builder(getInstrumentation()
                .getTargetContext());
        notificationBuilder.setSmallIcon(R.drawable.icon);
        notificationBuilder.setContentTitle("Title");
        notificationBuilder.setContentText("Text");
        notificationBuilder.setAutoCancel(true);

        mNotificationManager.notify(1234, notificationBuilder.getNotification());
    }

}
