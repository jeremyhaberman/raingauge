package com.jeremyhaberman.raingauge.rest.method;

import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.rest.resource.Forecast;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

public class DefaultRestMethodFactoryTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@SmallTest
	public void testGetInstance() {
		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());
		assertNotNull(factory);

		// We should get the same factory back on subsequent calls
		RestMethodFactory factory2 = DefaultRestMethodFactory.getInstance(getContext());
		assertEquals(factory, factory2);
	}

	@SmallTest
	public void testGetRestMethodForObservations() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();
		params.putString(Observations.ZIP_CODE, "55401");

		RestMethod<Observations> method =
				(RestMethod<Observations>) factory
						.getRestMethod(RestMethodFactory.RESOURCE_TYPE_OBSERVATIONS, Method.GET,
								params);

		assertNotNull(method);
	}

	@SmallTest
	public void testGetRestMethodForForecast() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();
		params.putString(Forecast.ZIP_CODE, "55401");

		RestMethod<Forecast> method =
				(RestMethod<Forecast>) factory
						.getRestMethod(RestMethodFactory.RESOURCE_TYPE_FORECAST, Method.GET,
								params);

		assertNotNull(method);
	}

	@SmallTest
	public void testGetRestMethodForObservationsWithInvalidMethod() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();
		params.putString(Observations.ZIP_CODE, "55401");

		try {
			RestMethod<Observations> method =
					(RestMethod<Observations>) factory
							.getRestMethod(RestMethodFactory.RESOURCE_TYPE_OBSERVATIONS,
									Method.POST, params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@SmallTest
	public void testGetRestMethodForForecastWithInvalidMethod() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();
		params.putString(Forecast.ZIP_CODE, "55401");

		try {
			RestMethod<Forecast> method =
					(RestMethod<Forecast>) factory
							.getRestMethod(RestMethodFactory.RESOURCE_TYPE_FORECAST,
									Method.POST, params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@SmallTest
	public void testGetRestMethodForObservationsWithoutZip() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();

		try {
			RestMethod<Observations> method =
					(RestMethod<Observations>) factory
							.getRestMethod(RestMethodFactory.RESOURCE_TYPE_OBSERVATIONS, Method.GET,
									params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@SmallTest
	public void testGetRestMethodForForecastWithoutZip() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();

		try {
			RestMethod<Forecast> method =
					(RestMethod<Forecast>) factory
							.getRestMethod(RestMethodFactory.RESOURCE_TYPE_FORECAST,
									Method.GET,
									params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@SmallTest
	public void testGetRestMethodWithInvalidUri() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Bundle params = new Bundle();
		params.putString(Observations.ZIP_CODE, "55401");

		try {
			RestMethod<Observations> method =
					(RestMethod<Observations>) factory.getRestMethod(4, Method.GET, params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
}

