package com.jeremyhaberman.raingauge;

import com.jeremyhaberman.raingauge.util.Logger;

import android.app.Application;
import android.content.Intent;

public class RainGauge extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		Logger.setAppTag("RainGauge");
		Logger.setLevel(Logger.DEBUG);

		sendBroadcast(new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_WEATHER_UPDATES));
	}

}
