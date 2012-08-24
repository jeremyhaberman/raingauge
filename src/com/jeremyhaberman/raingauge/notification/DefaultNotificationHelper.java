
package com.jeremyhaberman.raingauge.notification;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.RainGauge;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import com.jeremyhaberman.raingauge.android.NotificationManager;

public class DefaultNotificationHelper extends BroadcastReceiver implements NotificationHelper {

    private static final int RAINFALL_NOTIFICATION_ID = 4692;

    private static final int SHOW_NOTIFICATION_REQUEST_CODE = 1;

    public DefaultNotificationHelper() {
    }

    @Override
    public void scheduleRainfallNotification(Context context, long when, double rainfall) {

        Intent notificationIntent = new Intent(RainGauge.ACTION_SHOW_NOTIFICATION);
        notificationIntent.putExtra(RainGauge.EXTRA_RAINFALL, rainfall);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                SHOW_NOTIFICATION_REQUEST_CODE, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AndroidAlarmManager alarmManager = (AndroidAlarmManager) ServiceManager.getService(context,
                Service.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, when, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(RainGauge.ACTION_SHOW_NOTIFICATION)) {
            double rainfall = intent.getDoubleExtra(RainGauge.EXTRA_RAINFALL, -1.0);
            if (rainfall >= 0.0) {
                Notification notification = buildNotification(context, rainfall);
                notify(context, RAINFALL_NOTIFICATION_ID, notification);
            }
        }
    }

    @TargetApi(11)
    @SuppressWarnings("deprecation")
    private Notification buildNotification(Context context, double rainfall) {
        int icon = R.drawable.status_bar_icon_23;
        String contentTitle = context.getString(R.string.app_name);
        CharSequence contentText = "Yesterday's Rainfall: " + rainfall + " in";
        Intent notificationIntent = new Intent(context, RainGaugeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT < 11) {
            Notification notification =  new Notification(icon, contentText, System.currentTimeMillis());
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

    private void notify(Context context, int id, Notification notification) {

        NotificationManager notificationManager = (NotificationManager) ServiceManager.getService(
                context, Service.NOTIFICATION_MANAGER);

        notificationManager.notify(id, notification);
    }
}
