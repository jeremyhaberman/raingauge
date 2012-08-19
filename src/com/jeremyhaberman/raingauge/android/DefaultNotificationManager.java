
package com.jeremyhaberman.raingauge.android;

import android.app.Notification;
import android.content.Context;

public class DefaultNotificationManager implements NotificationManager {

    private Context mContext;

    public DefaultNotificationManager(Context context) {
        mContext = context;
    }

    @Override
    public void notify(int id, Notification notification) {

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) mContext
                        .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notification);
    }
}
