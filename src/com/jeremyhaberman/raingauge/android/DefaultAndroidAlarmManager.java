
package com.jeremyhaberman.raingauge.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

public class DefaultAndroidAlarmManager implements AndroidAlarmManager {

    private Context mContext;

    public DefaultAndroidAlarmManager(Context context) {
        mContext = context;
    }

    @Override
    public void setRepeating(int type, long triggerAtMillis, long intervalMillis,
            PendingIntent operation) {

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(type, triggerAtMillis, intervalMillis, operation);
    }

    @Override
    public void set(int type, long triggerAtMillis, PendingIntent operation) {

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(type, triggerAtMillis, operation);
    }
}
