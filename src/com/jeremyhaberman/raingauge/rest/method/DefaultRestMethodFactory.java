package com.jeremyhaberman.raingauge.rest.method;

import android.content.Context;
import android.os.Bundle;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.resource.Forecast;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.rest.resource.Resource;
import com.jeremyhaberman.raingauge.util.Logger;

public class DefaultRestMethodFactory implements RestMethodFactory {

	private static final String TAG = DefaultRestMethodFactory.class.getSimpleName();

	private static RestMethodFactory instance;
	private static Object lock = new Object();
	private Context mContext;

	private DefaultRestMethodFactory(Context context) {
		mContext = context;
	}

	public static RestMethodFactory getInstance(Context context) {
		synchronized (lock) {
			if (instance == null) {
				instance = new DefaultRestMethodFactory(context);
			}
		}

		return instance;
	}

	@Override
	public RestMethod<? extends Resource> getRestMethod(int resourceType, Method method,
														Bundle params) {

		switch (resourceType) {
			case RESOURCE_TYPE_OBSERVATIONS:
				if (method == Method.GET) {
					if (params.containsKey(Observations.ZIP_CODE)) {
						return GetObservationsRestMethod.newInstance(mContext,
								params.getInt(Observations.ZIP_CODE));
					} else {
						Logger.error(TAG, "Missing parameter in params for zip code");
						throw new IllegalArgumentException("Missing zip code in params");

					}
				} else {
					throw new IllegalArgumentException(
							"Invalid method (" + method + ") for resource type "
									+ resourceType);
				}
			case RESOURCE_TYPE_FORECAST:
				if (method == Method.GET) {
					if (params.containsKey(Forecast.ZIP_CODE)) {
						return GetForecastRestMethod.newInstance(mContext,
								params.getInt(Forecast.ZIP_CODE));
					} else {
						Logger.error(TAG, "Missing parameter in params for zip code");
						throw new IllegalArgumentException("Missing zip code in params");

					}
				} else {
					throw new IllegalArgumentException(
							"Invalid method (" + method + ") for resource type "
									+ resourceType);
				}
			default:
				throw new IllegalArgumentException("Unknown resource type " + resourceType);
		}
	}
}
