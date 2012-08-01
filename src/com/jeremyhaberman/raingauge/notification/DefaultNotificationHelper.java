package com.jeremyhaberman.raingauge.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.android.NotificationManager;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DefaultNotificationHelper implements NotificationHelper {

	private Context mContext;

	private static final int RAINFALL_NOTIFICATION_ID = 4692;

	private DefaultNotificationHelper(Context context) {
		mContext = context;
	}

	public static DefaultNotificationHelper newNotificationHelper(Context context) {
		return new DefaultNotificationHelper(context);
	}

	@Override
	public void scheduleRainfallNotification(long currentTimeInMillis, double rainfall) {
		Notification notification = buildNotification(rainfall);

		notify(RAINFALL_NOTIFICATION_ID, notification);
	}

	private Notification buildNotification(double rainfall) {
		int icon = R.drawable.status_bar_icon_23;
		CharSequence contentText = "Yesterday's Rainfall: " + rainfall + " in";
		long when = getNextNotificationTime();
		Notification notification = new Notification(icon, contentText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		CharSequence title = mContext.getString(R.string.app_name);
		Intent notificationIntent = new Intent(mContext, RainGaugeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(mContext, title, contentText, contentIntent);
		return notification;

	}

	private void notify(int id, Notification notification) {

		NotificationManager notificationManager =
				(NotificationManager) ServiceManager
						.getService(mContext, Service.NOTIFICATION_MANAGER);

		notificationManager.notify(id, notification);
	}

	private long getNextNotificationTime() {

		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, 5);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		date.add(Calendar.DAY_OF_MONTH, 1);

		return date.getTimeInMillis();
	}


}
