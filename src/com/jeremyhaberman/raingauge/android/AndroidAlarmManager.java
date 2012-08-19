
package com.jeremyhaberman.raingauge.android;

import android.app.PendingIntent;

public interface AndroidAlarmManager {

    void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);

    void set(int type, long triggerAtMillis, PendingIntent operation);
}
