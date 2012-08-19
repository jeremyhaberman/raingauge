package com.jeremyhaberman.raingauge.rest.method;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.mock.MockRestClient;
import com.jeremyhaberman.raingauge.rest.resource.Forecast;
import com.jeremyhaberman.raingauge.util.TestUtil;

public class GetForecastRestMethodTest extends InstrumentationTestCase {

	private String mApiKey;
	private static final int ZIP = 55408;
	private GetForecastRestMethod mMethod;
	private MockRestClient mRestClient;

	protected void setUp() throws Exception {
		super.setUp();

		mMethod =
				GetForecastRestMethod.newInstance(getInstrumentation().getTargetContext(), ZIP);
		mRestClient = MockRestClient.newInstance(getInstrumentation().getContext());
		mMethod.setRestClient(mRestClient);

		mApiKey = getInstrumentation().getTargetContext().getString(R.string.api_key);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testPreconditions() {
		assertNotNull(mMethod);
		assertNotNull(mRestClient);
	}

	@LargeTest
	public void testExecute() {
		RestMethod<Forecast> method =
				GetForecastRestMethod.newInstance(getInstrumentation().getTargetContext(),
						55408);
		RestMethodResult<Forecast> result = method.execute();
		assertEquals(200, result.getStatusCode());
		Forecast forecast = result.getResource();
		assertNotNull(forecast);
	}

	@SmallTest
	public void testGetURI() {

		int zip = 55408;

		GetForecastRestMethod method =
				GetForecastRestMethod.newInstance(getInstrumentation().getTargetContext(), zip);

		String expectedUri =
				"http://i.wxbug.net/REST/Direct/GetForecast.ashx?zip=" + zip + "&nf=1&c=US&l=en&api_key=" +
						mApiKey;

		assertEquals(URI.create(expectedUri).toString(), method.getURI().toString());
	}

	@SmallTest
	public void testGetLogTag() {
		GetForecastRestMethod method =
				GetForecastRestMethod
						.newInstance(getInstrumentation().getTargetContext(), 55408);
		assertEquals("GetForecastRestMethod", method.getLogTag());
	}

	public void testBuildRequest() {
		int zip = 55408;
		GetForecastRestMethod method =
				GetForecastRestMethod.newInstance(getInstrumentation().getTargetContext(), zip);
		method.buildRequest();

		URI expectedURI = URI.create(
				"http://i.wxbug.net/REST/Direct/GetForecast.ashx?zip=" + zip + "&nf=1&c=US&l=en&api_key=" +
										mApiKey);

		assertEquals(expectedURI.toString(), method.getURI().toString());
	}

	@SmallTest
	public void testNewInstance() {
		RestMethod<Forecast> method =
				GetForecastRestMethod.newInstance(getInstrumentation().getTargetContext(),
						55408);
		assertNotNull(method);
	}

	@MediumTest
	public void testParseResponseBodyString() throws Exception {

		String responseBody = TestUtil.getJson(getInstrumentation().getContext(),
				com.jeremyhaberman.raingauge.tests.R.raw.get_forecast_200);

		Forecast actualForecast = mMethod.parseResponseBody(responseBody);

		JSONObject expectedForecastsObject = new JSONObject(responseBody);
		JSONArray forecastArray = expectedForecastsObject.getJSONArray("forecastList");
		JSONObject expectedForecastObject = forecastArray.getJSONObject(0);
		long timestamp = expectedForecastObject.getLong("dateTime");
		String dayForecast = expectedForecastObject.getString("dayPred");
		String nightForecast = expectedForecastObject.getString("nightPred");
		Forecast expectedForecast = Forecast.newForecast(timestamp, dayForecast, nightForecast );

		assertEquals(expectedForecast, actualForecast);
	}
}
