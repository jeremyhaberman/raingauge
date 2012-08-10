package com.jeremyhaberman.raingauge.rest.method;

import android.content.Context;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.rest.Request;
import com.jeremyhaberman.raingauge.rest.resource.Forecast;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

public class GetForecastRestMethod extends AbstractRestMethod<Forecast> {

	private static final String TAG = GetForecastRestMethod.class.getSimpleName();

	private Context mContext;

	private static final String BASE_URI = "http://i.wxbug.net/REST/Direct/GetForecast.ashx";

	private static final String JSON_KEY_FORECAST_LIST = "forecastList";
	private static final String JSON_KEY_DATE_TIME = "dateTime";
	private static final String JSON_KEY_DAY_FORECAST = "dayPred";
	private static final String JSON_KEY_NIGHT_FORECAST = "nightPred";

	private int mZipCode;

	private URI mUri;

	// WeatherBug API key
	private String mApiKey;

	private GetForecastRestMethod(Context context, int zipCode) {
		
		if (context == null) {
			throw new IllegalArgumentException("context is null");
		}
		
		mContext = context.getApplicationContext();
		mApiKey = context.getString(R.string.api_key);
		mZipCode = zipCode;
		mUri = buildUri();
	}

	public static GetForecastRestMethod newInstance(Context context, int zipCode) {
		return new GetForecastRestMethod(context, zipCode);
	}

	@Override
	protected Request buildRequest() {
		Request request = new Request(mUri);
		return request;
	}

	private URI buildUri() {

		StringBuilder uriStringBuilder = new StringBuilder(BASE_URI);
		uriStringBuilder.append("?");
		uriStringBuilder.append("zip=").append(mZipCode);
		uriStringBuilder.append("&nf=1&c=US&l=en&api_key=" + mApiKey);
		return URI.create(uriStringBuilder.toString());
	}

	@Override
	protected Forecast parseResponseBody(String responseBody) throws Exception {
		JSONObject obj = new JSONObject(responseBody);
		JSONArray forecastList = obj.getJSONArray(JSON_KEY_FORECAST_LIST);
		JSONObject forecastObj = forecastList.getJSONObject(0);

		long dateTime = forecastObj.getLong(JSON_KEY_DATE_TIME);
		String dayForecast = forecastObj.getString(JSON_KEY_DAY_FORECAST);
		String nightForecast = forecastObj.getString(JSON_KEY_NIGHT_FORECAST);
		
		return Forecast.newForecast(dateTime, dayForecast, nightForecast);
	}

	@Override
	protected String getLogTag() {
		return TAG;
	}

	@Override
	protected URI getURI() {
		return mUri;
	}

}
