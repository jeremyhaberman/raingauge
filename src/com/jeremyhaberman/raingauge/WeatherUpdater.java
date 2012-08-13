package com.jeremyhaberman.raingauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.jeremyhaberman.raingauge.service.WeatherService;
import com.jeremyhaberman.raingauge.service.WeatherServiceHelper;
import com.jeremyhaberman.raingauge.util.Logger;

public class WeatherUpdater extends BroadcastReceiver {

	private static final String TAG = WeatherUpdater.class.getSimpleName();

	public static final String ACTION_UPDATE_RAINFALL =
			"com.jeremyhaberman.raingauge.WeatherUpdater.ACTION_UPDATE_RAINFALL";

	public static final String ACTION_UPDATE_FORECAST =
			"com.jeremyhaberman.raingauge.WeatherUpdater.ACTION_UPDATE_FORECAST";

	@Override
	public void onReceive(Context context, Intent intent) {

		synchronized (this) {

			if (Logger.isEnabled(Logger.DEBUG)) {
				Logger.debug(TAG, "onReceive()", intent);
			}

			int zip = intent.getIntExtra(WeatherService.ZIP_CODE, 0);

			if (zip != 0) {
				WeatherServiceHelper helper = new WeatherServiceHelper(context);

				if (intent.getAction().equals(ACTION_UPDATE_RAINFALL)) {
					helper.getTodaysRainfall(zip);
				} else if (intent.getAction().equals(ACTION_UPDATE_FORECAST)) {
					helper.getTodaysForecast(zip);
				} else {
					if (Logger.isEnabled(Logger.WARN)) {
						Logger.warn(TAG, "Unknown action: " + intent.getAction());
					}
				}
			}
		}

	}
}
