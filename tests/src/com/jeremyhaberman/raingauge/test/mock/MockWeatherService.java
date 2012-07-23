package com.jeremyhaberman.raingauge.test.mock;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.jeremyhaberman.raingauge.service.WeatherService;

public class MockWeatherService extends IntentService implements WeatherService {

	private static final String TAG = MockWeatherService.class.getSimpleName();

	public MockWeatherService() {
		this("MockWeatherService");
	}

	public MockWeatherService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, intent.getAction());
	}
}
