package com.jeremyhaberman.raingauge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.service.WeatherService;
import com.jeremyhaberman.raingauge.util.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Schedules log maintenance on a regular interval
 */
public class WeatherUpdateScheduler extends BroadcastReceiver {

	public static final String TAG = WeatherUpdateScheduler.class.getSimpleName();

	public static final String ACTION_SCHEDULE_WEATHER_UPDATES =
			"com.jeremyhaberman.raingauge.ACTION_SCHEDULE_WEATHER_UPDATES";
	private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	public static final String EXTRA_ZIP_CODE =
			"com.jeremyhaberman.raingauge.WeatherUpdateScheduler.EXTRA_ZIP_CODE";
	public static final String EXTRA_NEXT_RAINFALL_UPDATE_TIME =
			"com.jeremyhaberman.raingauge.EXTRA_NEXT_RAINFALL_UPDATE_TIME";
	private static final String EXTRA_NEXT_RAIN_FORECAST_UPDATE_TIME =
			"com.jeremyhaberman.raingauge.EXTRA_NEXT_RAIN_FORECAST_UPDATE_TIME";

	private static final long DAILY_INTERVAL = 86400000;

	private boolean mRainfallUpdateScheduled = false;

	private Calendar mNextRainfulUpdateTime;
	private Calendar mNextRainForecastTime;

	private Calendar mNextForecastUpdateTime;


	@Override
	public void onReceive(Context context, Intent intent) {

		synchronized (this) {

			if (Logger.isEnabled(Logger.DEBUG)) {
				Logger.debug(TAG, "onReceive()", intent);
			}

			if (intent.getAction().equalsIgnoreCase(ACTION_SCHEDULE_WEATHER_UPDATES) ||
					intent.getAction().equalsIgnoreCase(ACTION_BOOT_COMPLETED)) {

				if (!mRainfallUpdateScheduled) {
					scheduleRainfallUpdates(context, intent);
				} else {
					if (Logger.isEnabled(Logger.DEBUG)) {
						Logger.debug(TAG, "Rainfall update already scheduled");
					}
				}
			} else {
				if (Logger.isEnabled(Logger.WARN)) {
					Logger.warn(TAG, "Unknown action: " + intent.getAction());
				}
			}
		}
	}

	private void scheduleRainfallUpdates(Context context, Intent intent) {

		if (Logger.isEnabled(Logger.DEBUG)) {
			Logger.debug(TAG, "Scheduling rainfall updates");
		}

		int zip = getZip(context, intent);
		long nextRainfallUpdateTime = intent.getLongExtra(EXTRA_NEXT_RAINFALL_UPDATE_TIME,
				getNextRainfallUpdateTime(23, 55).getTimeInMillis());

		mRainfallUpdateScheduled = scheduleRainfallUpdates(context, zip, nextRainfallUpdateTime);
	}

	public boolean isRainfallUpdateScheduled() {
		return mRainfallUpdateScheduled;
	}

	private boolean scheduleRainfallUpdates(Context context, int zip, long timeInMillis) {

		if (zip == 0) {
			if (Logger.isEnabled(Logger.WARN)) {
				Logger.warn(TAG, String.format("zip is 0; rainfall update not scheduled"));
			}
			return false;
		}

		Intent updateRainfallIntent = new Intent(WeatherUpdater.ACTION_UPDATE_RAINFALL);
		updateRainfallIntent.putExtra(WeatherService.ZIP_CODE, zip);

		Logger.debug(TAG, "updateRainfallIntent", updateRainfallIntent);

		PendingIntent updateRainfallPendingIntent = PendingIntent.getBroadcast(context, 0,
				updateRainfallIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		AndroidAlarmManager alarmManager =
				(AndroidAlarmManager) ServiceManager.getService(context, Service.ALARM_SERVICE);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, DAILY_INTERVAL,
				updateRainfallPendingIntent);

		if (Logger.isEnabled(Logger.DEBUG)) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timeInMillis);
			Logger.debug(TAG,
					"Next rainfall update scheduled to run at " + cal.getTime().toString());
		}

		return true;
	}

	private int getZip(Context context, Intent intent) {
		if (intent.hasExtra(EXTRA_ZIP_CODE)) {
			return intent.getIntExtra(EXTRA_ZIP_CODE, 0);
		} else {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			return preferences.getInt(Observations.ZIP_CODE, 0);
		}
	}

//	private void scheduleForecastUpdate(Context context) {
//		Intent weatherUpdateService = new Intent(context, WeatherUpdateService.class);
//		weatherUpdateService.setAction(WeatherUpdateService.ACTION_GET_FORECAST);
//		PendingIntent updateRainfallPendingIntent = PendingIntent.getService(context, 0,
//				weatherUpdateService, 0);
//
//		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//		long nextForecastUpdateTime = getNextForecastUpdateTime(5, 0).getTimeInMillis();
//
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(nextForecastUpdateTime);
//		if (Logger.isEnabled(Logger.DEBUG)) {
//			Logger.debug(TAG,
//					"Next forecast update scheduled to run at " + cal.getTime().toString());
//		}
//
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextForecastUpdateTime, DAILY_INTERVAL,
//				updateRainfallPendingIntent);
//
//	}

	private Calendar getNextRainfallUpdateTime(int hour, int minute) {

		if (mNextRainfulUpdateTime == null) {
			mNextRainfulUpdateTime = getDefaultNextTime(hour, minute);
		}

		return mNextRainfulUpdateTime;
	}

	private Calendar getNextForecastUpdateTime(int hour, int minute) {

		if (mNextForecastUpdateTime == null) {
			mNextForecastUpdateTime = getDefaultNextTime(hour, minute);
		}

		return mNextForecastUpdateTime;
	}

	private Calendar getDefaultNextTime(int hour, int minute) {
		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		// next day
		// date.add(Calendar.DAY_OF_MONTH, 1);

		return date;
	}
}
