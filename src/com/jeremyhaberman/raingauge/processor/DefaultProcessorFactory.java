package com.jeremyhaberman.raingauge.processor;

import android.content.Context;

import com.jeremyhaberman.raingauge.service.WeatherService.ResourceType;

public class DefaultProcessorFactory implements ProcessorFactory {

	private static ProcessorFactory mSingleton;
	private Context mContext;

	@Override
	public ResourceProcessor getProcessor(ResourceType resourceType) {
		
		if (resourceType == null) {
			throw new IllegalArgumentException("resourceType is null");
		}
		
		switch (resourceType) {
		case OBSERVATIONS:
			return ObservationsProcessor.createProcessor(mContext);
		default:
			throw new IllegalArgumentException("No processor for resource type: " + resourceType);
		}

	}

	public static ProcessorFactory getInstance(Context context) {
		if (mSingleton == null) {
			mSingleton = new DefaultProcessorFactory(context.getApplicationContext());
		}
		return mSingleton;
	}

	private DefaultProcessorFactory(Context context) {
		mContext = context;
	};

}
