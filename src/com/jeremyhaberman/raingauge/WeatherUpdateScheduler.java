package com.jeremyhaberman.raingauge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.service.WeatherServiceHelper;
import com.jeremyhaberman.raingauge.util.Logger;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Schedules log maintenance on a regular interval
 */
public class WeatherUpdateScheduler extends BroadcastReceiver {

	public static final String TAG = WeatherUpdateScheduler.class.getSimpleName();

	public static final String ACTION_SCHEDULE_WEATHER_UPDATES = "com.jeremyhaberman.raingauge.ACTION_SCHEDULE_WEATHER_UPDATES";
	private static final String EXTRA_NEXT_RAINFALL_UPDATE_TIME = "com.jeremyhaberman.raingauge.EXTRA_NEXT_RAINFALL_UPDATE_TIME";
	private static final String EXTRA_NEXT_RAIN_FORECAST_UPDATE_TIME = "com.jeremyhaberman.raingauge.EXTRA_NEXT_RAIN_FORECAST_UPDATE_TIME";

	private static final long DAILY_INTERVAL = 5184000l;

	private boolean mScheduled = false;

	private Calendar mNextRainfulUpdateTime;
	private Calendar mNextRainForecastTime;

	private Calendar mNextForecastUpdateTime;

	@Override
	public void onReceive(Context context, Intent intent) {

		synchronized (this) {

			if (!mScheduled) {
				if (intent.getAction().equalsIgnoreCase(ACTION_SCHEDULE_WEATHER_UPDATES)) {

					if (Logger.isEnabled(Logger.DEBUG)) {
						Logger.debug(TAG, "Scheduling rainfall and forecast updates");
					}

					scheduleRainfallUpdate(context, intent);
					scheduleForecastUpdate(context);

					mScheduled = true;
				}
			} else {
				if (Logger.isEnabled(Logger.DEBUG)) {
					Logger.debug(TAG, "Rainfall and forecast updates already scheduled");
				}
			}
		}
	}

	private void scheduleForecastUpdate(Context context) {
		Intent weatherUpdateService = new Intent(context, WeatherUpdateService.class);
		weatherUpdateService.setAction(WeatherUpdateService.ACTION_GET_FORECAST);
		PendingIntent updateRainfallPendingIntent = PendingIntent.getService(context, 0,
				weatherUpdateService, 0);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		long nextForecastUpdateTime = getNextForecastUpdateTime(5, 0).getTimeInMillis();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(nextForecastUpdateTime);
		if (Logger.isEnabled(Logger.DEBUG)) {
			Logger.debug(TAG, "Next forecast update scheduled to run at " + cal.getTime().toString());
		}

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextForecastUpdateTime, DAILY_INTERVAL,
				updateRainfallPendingIntent);

	}

	private void scheduleRainfallUpdate(Context context, Intent intent) {

		WeatherServiceHelper helper = new WeatherServiceHelper(context);
		Intent weatherUpdateServiceIntent = helper.getTodaysObservationsIntent(intent.getIntExtra(Observations.ZIP_CODE, 0));

		PendingIntent updateRainfallPendingIntent = PendingIntent.getService(context, 0,
				weatherUpdateServiceIntent, 0);

		AndroidAlarmManager alarmManager =
				(AndroidAlarmManager) ServiceManager.getService(context, Service.ALARM_SERVICE);

		long nextRainfallUpdateTime = intent.getLongExtra(EXTRA_NEXT_RAINFALL_UPDATE_TIME,
				getNextRainfallUpdateTime(8, 39).getTimeInMillis());

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(nextRainfallUpdateTime);

		if (Logger.isEnabled(Logger.DEBUG)) {
			Logger.debug(TAG, "Next rainfall update scheduled to run at " + cal.getTime().toString());
		}

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextRainfallUpdateTime, DAILY_INTERVAL,
				updateRainfallPendingIntent);
	}

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
