package com.jeremyhaberman.raingauge.processor;

import android.content.Context;
import com.jeremyhaberman.raingauge.service.WeatherService;

public class DefaultProcessorFactory implements ProcessorFactory {

	private static ProcessorFactory mSingleton;
	private Context mContext;

	@Override
	public ResourceProcessor getProcessor(int resourceType) {
		
		switch (resourceType) {
		case WeatherService.RESOURCE_TYPE_OBSERVATIONS:
			return ObservationsProcessor.createProcessor(mContext);
		default:
			throw new IllegalArgumentException("No processor for resource type: " + resourceType);
		}

	}

	public static ProcessorFactory getInstance(Context context) {
		if (mSingleton == null) {
			mSingleton = new DefaultProcessorFactory(context);
		}
		return mSingleton;
	}

	private DefaultProcessorFactory(Context context) {
		mContext = context;
	};

}
