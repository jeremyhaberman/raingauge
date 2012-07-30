package com.jeremyhaberman.raingauge.mock;

import android.content.Context;
import com.jeremyhaberman.raingauge.rest.Request;
import com.jeremyhaberman.raingauge.rest.Response;
import com.jeremyhaberman.raingauge.rest.RestClient;
import com.jeremyhaberman.raingauge.util.TestUtil;

import java.util.List;
import java.util.Map;

public class MockRestClient implements RestClient {

	private int mStatus;
	private Map<String, List<String>> mHeaders;
	private byte[] mBody;
	private Context mContext;

	private MockRestClient(Context testContext) {
		mContext = testContext;
	}

	@Override
	public Response execute(Request request) {
		return new Response(mStatus, mHeaders, mBody);
	}

	public void setHttpResponse(int statusCode, Map<String, List<String>> headers,
								String responseBody) {
		mStatus = statusCode;
		mHeaders = headers;
		mBody = responseBody.getBytes();
	}

	public static MockRestClient newInstance(Context testContext) {
		return new MockRestClient(testContext);
	}

	public void setHttpResponse(int statusCode, Map<String, List<String>> headers,
								int jsonBodyResId) {

		String json = TestUtil.getJson(mContext,
				com.jeremyhaberman.raingauge.tests.R.raw.get_observations_200);
		setHttpResponse(statusCode, headers, json);
	}
}

