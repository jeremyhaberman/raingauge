package com.jeremyhaberman.raingauge.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessorCallback;
import com.jeremyhaberman.raingauge.util.Logger;

public class DefaultWeatherService extends IntentService implements WeatherService {

	private static final String TAG = DefaultWeatherService.class.getSimpleName();

	public DefaultWeatherService() {
		super("DefaultWeatherService");
	}

	@Override
	protected void onHandleIntent(Intent requestIntent) {

		// Get request data from Intent
		int resourceType = requestIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1);
		String method = requestIntent.getStringExtra(METHOD_EXTRA);
		Bundle parameters = requestIntent.getBundleExtra(EXTRA_REQUEST_PARAMETERS);
		ResultReceiver serviceHelperCallback = requestIntent
				.getParcelableExtra(SERVICE_CALLBACK_EXTRA);
		ResourceProcessor processor = (ResourceProcessor) requestIntent.getParcelableExtra(EXTRA_PROCESSOR);
		
		if (serviceHelperCallback == null) {
			Logger.error(TAG, "Service callback is null");
		}
		
		if (processor == null || method == null || parameters == null) {
			if (serviceHelperCallback != null) {
				serviceHelperCallback.send(REQUEST_INVALID, bundleOriginalIntent(requestIntent));
			}
			return;
		}

		ResourceProcessorCallback processorCallback = createProcessorCallback(requestIntent,
				serviceHelperCallback);

		
		if (method.equalsIgnoreCase(METHOD_GET)) {
			processor.getResource(processorCallback, parameters);
		} else if (serviceHelperCallback != null) {
			serviceHelperCallback.send(REQUEST_INVALID, bundleOriginalIntent(requestIntent));
		}
	}

	private ResourceProcessorCallback createProcessorCallback(final Intent originalIntent,
			final ResultReceiver serviceHelperCallback) {

		ResourceProcessorCallback callback = new ResourceProcessorCallback() {

			@Override
			public void send(int resultCode, String resourceId) {
				if (serviceHelperCallback != null) {
					serviceHelperCallback.send(resultCode, bundleOriginalIntent(originalIntent));
				}
			}
		};
		return callback;
	}

	private Bundle bundleOriginalIntent(Intent originalIntent) {

		Bundle originalRequest = new Bundle();
		originalRequest.putParcelable(ORIGINAL_INTENT_EXTRA, originalIntent);
		return originalRequest;
	}

}
