package com.jeremyhaberman.raingauge.processor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.method.RestMethod;
import com.jeremyhaberman.raingauge.rest.method.RestMethodFactory;
import com.jeremyhaberman.raingauge.rest.method.RestMethodResult;
import com.jeremyhaberman.raingauge.rest.resource.Forecast;
import com.jeremyhaberman.raingauge.util.Logger;

import java.io.IOException;

public class ForecastProcessor implements ResourceProcessor {

	protected static final String TAG = ForecastProcessor.class.getSimpleName();

	private Context mContext;

	private RestMethodFactory mRestMethodFactory;

	private ForecastProcessor(Context context) {
		mContext = context;

		mRestMethodFactory = (RestMethodFactory) ServiceManager
				.getService(context, Service.REST_METHOD_FACTORY);
	}

	public static ForecastProcessor createProcessor(Context context) {
		return new ForecastProcessor(context);
	}

	@Override
	public void getResource(ResourceProcessorCallback callback, Bundle params) {

		RestMethod<Forecast> method = (RestMethod<Forecast>) mRestMethodFactory
				.getRestMethod(RestMethodFactory.RESOURCE_TYPE_FORECAST, Method.GET,
						params);

		RestMethodResult<Forecast> result = method.execute();

		try {
			persist(result.getResource());
			callback.send(result.getStatusCode(), ResourceProcessor.SUCCESS);
		} catch (IOException e) {
			callback.send(result.getStatusCode(), ResourceProcessor.IO_ERROR);
		}
	}

	private void persist(Forecast forecast) throws IOException {

		if (forecast != null) {
			SharedPreferences.Editor editor =
					PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			editor.putString(Forecast.KEY_DAY_FORECAST, forecast.getDayForecast());
			editor.putString(Forecast.KEY_NIGHT_FORECAST, forecast.getNightForecast());
			editor.commit();
		} else {
			if (Logger.isEnabled(Logger.WARN)) {
				Logger.warn(TAG, "Cannot save forecast; forecast = null");
			}
		}
	}
}