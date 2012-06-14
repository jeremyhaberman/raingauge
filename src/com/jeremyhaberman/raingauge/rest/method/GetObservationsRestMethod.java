package com.jeremyhaberman.raingauge.rest.method;

import java.net.URI;

import org.json.JSONObject;

import android.content.Context;

import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.Request;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

public class GetObservationsRestMethod extends AbstractRestMethod<Observations> {

	private static final String TAG = GetObservationsRestMethod.class.getSimpleName();

	private Context mContext;

	private static final String BASE_URI = "http://i.wxbug.net/REST/Direct/GetObs.ashx";

	private static final String JSON_KEY_RAIN_DAILY = "rainDaily";

	private String mZipCode;

	private URI mUri;

	public GetObservationsRestMethod(Context context, String zipCode) {
		
		if (context == null) {
			throw new IllegalArgumentException("context is null");
		}
		
		if (zipCode == null) {
			throw new IllegalArgumentException("zipCode is null");
		}
		
		mContext = context.getApplicationContext();
		mZipCode = zipCode;
		mUri = buildUri();
	}

	@Override
	protected Request buildRequest() {
		Request request = new Request(Method.GET, mUri, null, null);
		return request;
	}

	private URI buildUri() {
		StringBuilder uriStringBuilder = new StringBuilder(BASE_URI);
		uriStringBuilder.append("?");
		uriStringBuilder.append("zip=").append(mZipCode);
		uriStringBuilder.append("&units=0&ic=1&api_key=qxktcbwgncg9acgwbk45d9jb");
		return URI.create(uriStringBuilder.toString());
	}

	@Override
	protected Observations parseResponseBody(String responseBody) throws Exception {
		JSONObject obj = new JSONObject(responseBody);
		
		double rainDaily = obj.getDouble(JSON_KEY_RAIN_DAILY);
		
		return Observations.createObservations(rainDaily);
	}

	@Override
	protected Context getContext() {
		return mContext;
	}

	@Override
	protected boolean requiresAuthorization() {
		return false;
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
