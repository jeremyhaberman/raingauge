
package com.jeremyhaberman.raingauge.notification;

import android.content.Context;

public interface NotificationHelper {

    void scheduleRainfallNotification(Context context, long when, double rainfall);
}
