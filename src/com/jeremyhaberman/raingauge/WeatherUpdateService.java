package com.jeremyhaberman.raingauge;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class WeatherUpdateService extends IntentService {

	private static final String TAG = null;
	
	private static final int WATER_NOTIFICATION_ID = 42;

	public WeatherUpdateService() {
		this("WeatherUpdateService");
	}

	public WeatherUpdateService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Weather weather = new Weather();
		
		int zip = getCurrentZip();
		
		double rainfall = 0.0;
		try {
			rainfall = weather.getTodaysRainfall(zip);
		} catch (Exception e) {
			Log.e(TAG, "Error getting rainfall", e);
		}
		
		handleDailyRainfall(rainfall);
		
	}

	private void handleDailyRainfall(double rainfall) {
//		if (rainfall < 0.1) {
			showNotification(rainfall);
//		}
		updateWeeklyTotal(rainfall);
	}

	private void updateWeeklyTotal(double rainfall) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		float weeklyTotal = preferences.getFloat(Weather.WEEKLY_RAINFALL, 0.0f);
		weeklyTotal += rainfall;
		preferences.edit().putFloat(Weather.WEEKLY_RAINFALL, weeklyTotal).commit();
	}

	private void showNotification(double rainfall) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = buildNotification(rainfall);
		
		notificationManager.notify(WATER_NOTIFICATION_ID, notification);
	}

	private Notification buildNotification(double rainfall) {
		int icon = R.drawable.status_bar_icon;
		CharSequence contentText = "Yesterday's Rainfall: " + rainfall + " in";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, contentText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		CharSequence title = getString(R.string.app_name);
		Intent notificationIntent = new Intent(this, RainGaugeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(this, title, contentText, contentIntent);
		return notification;

	}

	private int getCurrentZip() {
		return 55417;
	}

}
