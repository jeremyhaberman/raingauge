package com.jeremyhaberman.raingauge;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Schedules log maintenance on a regular interval
 */
public class WeatherUpdateScheduler extends BroadcastReceiver {

	public static final String TAG = WeatherUpdateScheduler.class.getSimpleName();

	public static final String ACTION_SCHEDULE_WEATHER_UPDATES = "com.jeremyhaberman.raingauge.ACTION_SCHEDULE_LOG_MAINTENANCE";
	private static final String EXTRA_NEXT_RAINFALL_UPDATE_TIME = "com.jeremyhaberman.raingauge.EXTRA_NEXT_RAINFALL_UPDATE_TIME";
	private static final String EXTRA_NEXT_RAIN_FORECAST_UPDATE_TIME = "com.jeremyhaberman.raingauge.EXTRA_NEXT_RAIN_FORECAST_UPDATE_TIME";

	private static final long DAILY_INTERVAL = 5184000l;

	private static boolean mScheduled = false;

	private Calendar mNextRainfulUpdateTime;
	private Calendar mNextRainForecastTime;

	@Override
	public void onReceive(Context context, Intent intent) {

		synchronized (this) {

			if (!mScheduled) {
				if (intent.getAction().equalsIgnoreCase(ACTION_SCHEDULE_WEATHER_UPDATES)) {

					Log.d(TAG, "Starting LogMaintenanceManager and scheduling daily checks");

					Intent weatherUpdateService = new Intent(context, WeatherUpdateService.class);
					PendingIntent updateRainfallPendingIntent = PendingIntent.getService(context,
							0, weatherUpdateService, 0);

					AlarmManager alarmManager = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);

					long nextRainfallUpdateTime = intent.getLongExtra(
							EXTRA_NEXT_RAINFALL_UPDATE_TIME, getNextRainfallUpdateTime(23, 55)
									.getTimeInMillis());

					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(nextRainfallUpdateTime);
					Log.d(TAG, "Next rainfall update scheduled to run at "
							+ cal.getTime().toString());

					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextRainfallUpdateTime,
							DAILY_INTERVAL, updateRainfallPendingIntent);

					mScheduled = true;
				}
			} else {
				Log.d(TAG, "Rainfall updates already scheduled");
			}
		}
	}

	private Calendar getNextRainfallUpdateTime(int hour, int minute) {

		if (mNextRainfulUpdateTime == null) {
			mNextRainfulUpdateTime = getDefaultNextTime(hour, minute);
		}

		return mNextRainfulUpdateTime;
	}

	private Calendar getDefaultNextTime(int hour, int minute) {
		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, hour);
		date.set(Calendar.MINUTE, minute);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		// next day
//		date.add(Calendar.DAY_OF_MONTH, 1);

		return date;
	}
}
