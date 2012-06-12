package com.jeremyhaberman.raingauge.processor;

import android.content.Context;

import com.jeremyhaberman.raingauge.service.WeatherService.ResourceType;

public class WeatherProcessorFactory {

	private static WeatherProcessorFactory mSingleton;
	private Context mContext;

	public ResourceProcessor getProcessor(ResourceType resourceType) {
		
		if (resourceType == null) {
			throw new IllegalArgumentException("resourceType is null");
		}
		
		switch (resourceType) {
		case RAINFALL:
			return new WeatherProcessor(mContext);
		default:
			throw new IllegalArgumentException("No processor for resource type: " + resourceType);
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
