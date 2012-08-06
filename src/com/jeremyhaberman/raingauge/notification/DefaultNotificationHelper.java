package com.jeremyhaberman.raingauge.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import com.jeremyhaberman.raingauge.android.NotificationManager;

public class DefaultNotificationHelper extends BroadcastReceiver implements NotificationHelper {

	private static final int RAINFALL_NOTIFICATION_ID = 4692;
	private static final int SHOW_NOTIFICATION_REQUEST_CODE = 1;
	private static final String ACTION_SHOW_NOTIFICATION =
			"com.jeremyhaberman.raingauge.notification.ACTION_SHOW_NOTIFICATION";
	private static final String EXTRA_RAINFALL =
			"com.jeremyhaberman.raingauge.notification.EXTRA_RAINFALL";

	public DefaultNotificationHelper() {

	}

	@Override
	public void scheduleRainfallNotification(Context context, long when, double rainfall) {

		Intent notificationIntent = new Intent(ACTION_SHOW_NOTIFICATION);
		notificationIntent.putExtra(EXTRA_RAINFALL, rainfall);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				SHOW_NOTIFICATION_REQUEST_CODE, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AndroidAlarmManager alarmManager =
				(AndroidAlarmManager) ServiceManager.getService(context, Service.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, when, pendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(ACTION_SHOW_NOTIFICATION)) {
			double rainfall = intent.getDoubleExtra(EXTRA_RAINFALL, -1.0);
			if (rainfall >= 0.0) {
				Notification notification = buildNotification(context, rainfall);
				notify(context, RAINFALL_NOTIFICATION_ID, notification);
			}
		}
	}


	private Notification buildNotification(Context context, double rainfall) {
		int icon = R.drawable.status_bar_icon_23;
		CharSequence contentText = "Yesterday's Rainfall: " + rainfall + " in";
		Intent notificationIntent = new Intent(context, RainGaugeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Notification.Builder notificationBuilder = new Notification.Builder(context);
		notificationBuilder.setContentTitle(context.getString(R.string.app_name));
		notificationBuilder.setContentText(contentText);
		notificationBuilder.setSmallIcon(icon);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setContentIntent(contentIntent);

		return notificationBuilder.build();
	}

	private void notify(Context context, int id, Notification notification) {

		NotificationManager notificationManager =
				(NotificationManager) ServiceManager
						.getService(context, Service.NOTIFICATION_MANAGER);

		notificationManager.notify(id, notification);
	}
}
