package com.jeremyhaberman.raingauge;

import android.app.Application;
import android.content.Intent;

public class RainGauge extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		sendBroadcast(new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_WEATHER_UPDATES));
	}

}
