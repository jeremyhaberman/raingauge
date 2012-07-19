package com.jeremyhaberman.raingauge.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.jeremyhaberman.raingauge.service.WeatherService.METHOD_EXTRA;
import static com.jeremyhaberman.raingauge.service.WeatherService.METHOD_GET;
import static com.jeremyhaberman.raingauge.service.WeatherService.RESOURCE_TYPE_EXTRA;
import static com.jeremyhaberman.raingauge.service.WeatherService.SERVICE_CALLBACK_EXTRA;

/**
 * Weather API
 */
public final class WeatherServiceHelper {

	@SuppressWarnings("unused")
	private static final String TAG = WeatherServiceHelper.class.getSimpleName();

	public static String ACTION_REQUEST_RESULT = "REQUEST_RESULT";
	public static String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
	public static String EXTRA_RESULT_CODE = "EXTRA_RESULT_CODE";

	private List<Long> mPendingRequests = new ArrayList<Long>();
	private final Object mPendingRequestsLock = new Object();
	private Context mAppContext;
	private ServiceResultReceiver mServiceCallback;

	private Class<? extends WeatherService> mWeatherServiceClass;

	public WeatherServiceHelper(Context context) {
		init(context, DefaultWeatherService.class);
	}

	public WeatherServiceHelper(Context context, Class<? extends WeatherService> weatherService) {
		init(context, weatherService);
	}

	private void init(Context context, Class<? extends WeatherService> weatherService) {
		mAppContext = context.getApplicationContext();
		mServiceCallback = new ServiceResultReceiver();
		mWeatherServiceClass = weatherService;
	}

	/**
	 * Initiates a request to get today's rainfall
	 *
	 * @param zip ZIP code
	 * @return request ID
	 */
	public long getTodaysRainfall(int zip) {

		long requestId = generateRequestID();
		synchronized (mPendingRequestsLock) {
			mPendingRequests.add(requestId);
		}

		Intent intent = getTodaysObservationsIntent(zip);
		intent.putExtra(SERVICE_CALLBACK_EXTRA, mServiceCallback);
		intent.putExtra(EXTRA_REQUEST_ID, requestId);

		mAppContext.startService(intent);

		return requestId;
	}

	public Intent getTodaysObservationsIntent(int zip) {
		Intent intent = new Intent(mAppContext, mWeatherServiceClass);
		intent.putExtra(METHOD_EXTRA, METHOD_GET);
		intent.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);

		Bundle requestParams = new Bundle();
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		intent.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);

		return intent;
	}

	protected Class<? extends WeatherService> getWeatherServiceClass() {
		return mWeatherServiceClass;
	}

	private long generateRequestID() {
		long requestId = UUID.randomUUID().getLeastSignificantBits();
		return requestId;
	}

	/**
	 * Determines whether a request is pending
	 *
	 * @param requestId the ID of a previous request
	 * @return <code>true</code> if the request is pending, <code>false</code>
	 *         otherwise
	 */
	public boolean isRequestPending(long requestId) {
		synchronized (mPendingRequestsLock) {
			return mPendingRequests.contains(requestId);
		}
	}

	/**
	 * Handles the result code and data during a callback from
	 * CatPicturesService
	 */
	final class ServiceResultReceiver extends ResultReceiver {

		public ServiceResultReceiver() {
			// null passed in to run callback on an arbitrary thread
			super(null);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			Intent origIntent = (Intent) resultData
					.getParcelable(WeatherService.ORIGINAL_INTENT_EXTRA);

			if (origIntent != null) {
				long requestId = origIntent.getLongExtra(EXTRA_REQUEST_ID, 0);
				synchronized (mPendingRequestsLock) {
					mPendingRequests.remove(requestId);
				}

				Intent resultBroadcast = new Intent(ACTION_REQUEST_RESULT);
				resultBroadcast.putExtra(EXTRA_REQUEST_ID, requestId);
				resultBroadcast.putExtra(EXTRA_RESULT_CODE, resultCode);

				mAppContext.sendBroadcast(resultBroadcast);
			}
		}
	}
}
