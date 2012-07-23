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

	@Override
	public void onReceive(Context context, Intent intent) {

		if (Logger.isEnabled(Logger.DEBUG)) {
			Logger.debug(TAG, "onReceive()", intent);
		}

		int zip = intent.getIntExtra(WeatherService.ZIP_CODE, 0);
		if (zip != 0) {
			WeatherServiceHelper helper = new WeatherServiceHelper(context);
			helper.getTodaysRainfall(zip);
		}

	}
}
