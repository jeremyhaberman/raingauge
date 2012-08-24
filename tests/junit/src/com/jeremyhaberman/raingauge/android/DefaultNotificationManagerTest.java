
package com.jeremyhaberman.raingauge.android;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import com.jeremyhaberman.raingauge.ManualAssertionTest;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;

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
    @MediumTest
    @ManualAssertionTest
    public void testNotify() throws InterruptedException {

        mNotificationManager.notify(1234, buildNotification());
    }

    @SuppressWarnings("deprecation")
    private Notification buildNotification() {
        
        Context context = getInstrumentation().getTargetContext();
        
        int icon = R.drawable.status_bar_icon_23;
        String contentTitle = "Title";
        CharSequence contentText = "Text";
        Intent notificationIntent = new Intent(context, RainGaugeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT < 11) {
            Notification notification = new Notification(icon, contentText,
                    System.currentTimeMillis());
            notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
            return notification;
        } else {
            Notification.Builder notificationBuilder = new Notification.Builder(context);
            notificationBuilder.setContentTitle(contentTitle);
            notificationBuilder.setContentText(contentText);
            notificationBuilder.setSmallIcon(icon);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setContentIntent(contentIntent);

            return notificationBuilder.getNotification();
        }
    }
}
