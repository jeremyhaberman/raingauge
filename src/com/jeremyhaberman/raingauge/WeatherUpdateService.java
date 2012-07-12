package com.jeremyhaberman.raingauge;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WeatherUpdateService extends IntentService {

	private static final String TAG = null;

	private static final int WATER_NOTIFICATION_ID = 42;

	public static final String ACTION_GET_FORECAST = "com.jeremyhaberman.raingauge.ACTION_GET_FORECAST";
	public static final String ACTION_GET_RAINFALL = "com.jeremyhaberman.raingauge.ACTION_GET_RAINFALL";

	public WeatherUpdateService() {
		this("WeatherUpdateService");
	}

	public WeatherUpdateService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

//		Observations weather = new Observations();

		int zip = getCurrentZip();

		if (zip > 0) {

			if (intent.getAction().equalsIgnoreCase(ACTION_GET_RAINFALL)) {

				double rainfall = 0.0;
				try {
//					rainfall = weather.getTodaysRainfall(zip);
				} catch (Exception e) {
					Log.e(TAG, "Error getting rainfall", e);
				}

				handleDailyRainfall(rainfall);
			} else if (intent.getAction().equalsIgnoreCase(ACTION_GET_FORECAST)) {
				
				String forecast = null;
				try {
//					forecast = weather.getTodaysForecast(zip);
				} catch (Exception e) {
					Log.e(TAG, "Error getting forecast", e);
				}
				
				if (forecast != null) {
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
					prefs.edit().putString(Observations.TODAYS_FORECAST, forecast).commit();
				}
				
			}
		}

	}

	private void handleDailyRainfall(double rainfall) {

		ContentValues values = new ContentValues();
		values.put(ObservationsTable.TIMESTAMP, System.currentTimeMillis());
		values.put(ObservationsTable.RAINFALL, rainfall);
		Uri uri = getContentResolver().insert(
				ObservationsTable.CONTENT_URI, values);

		Log.d(TAG, "inserted rainfall: " + uri.toString());

		// if (rainfall < 0.1) {
		scheduleNotification(rainfall);
		// }
		updateWeeklyTotal(rainfall);
	}

	private void updateWeeklyTotal(double rainfall) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		float weeklyTotal = preferences.getFloat(Observations.WEEKLY_RAINFALL, 0.0f);
		weeklyTotal += rainfall;
		preferences.edit().putFloat(Observations.WEEKLY_RAINFALL, weeklyTotal).commit();
	}

	private void scheduleNotification(double rainfall) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = buildNotification(rainfall);

		notificationManager.notify(WATER_NOTIFICATION_ID, notification);
	}

	private Notification buildNotification(double rainfall) {
		int icon = R.drawable.status_bar_icon_23;
		CharSequence contentText = "Yesterday's Rainfall: " + rainfall + " in";
		long when = getNextNotificationTime();
		Notification notification = new Notification(icon, contentText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		CharSequence title = getString(R.string.app_name);
		Intent notificationIntent = new Intent(this, RainGaugeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(this, title, contentText, contentIntent);
		return notification;

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

	private int getCurrentZip() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getInt(Observations.ZIP_CODE, 0);
	}

}
