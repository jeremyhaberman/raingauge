package com.jeremyhaberman.raingauge.processor;

import com.jeremyhaberman.raingauge.service.WeatherService;

import android.content.Context;

public class WeatherProcessorFactory {

	private static WeatherProcessorFactory mSingleton;
	private Context mContext;

	public ResourceProcessor getProcessor(int resourceType) {
		switch (resourceType) {
		case WeatherService.RESOURCE_TYPE_RAINFALL:
			return new WeatherProcessor(mContext);
		default:
			throw new IllegalArgumentException("Unknown resource type: " + resourceType);
		}

	}

	public static WeatherProcessorFactory getInstance(Context context) {
		if (mSingleton == null) {
			mSingleton = new WeatherProcessorFactory(context.getApplicationContext());
		}
		return mSingleton;
	}

	private WeatherProcessorFactory(Context context) {
		mContext = context;
	};

}
