package com.jeremyhaberman.raingauge.rest.method;

import android.net.Uri;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.rest.Method;
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

		Uri uri = RainGaugeProviderContract.ObservationsTable.CONTENT_URI;

		Bundle params = new Bundle();
		params.putString(Observations.ZIP_CODE, "55401");

		RestMethod<Observations> method =
				(RestMethod<Observations>) factory.getRestMethod(uri, Method.GET, params);

		assertNotNull(method);
	}

	@SmallTest
	public void testGetRestMethodForObservationsWithInvalidMethod() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Uri uri = RainGaugeProviderContract.ObservationsTable.CONTENT_URI;

		Bundle params = new Bundle();
		params.putString(Observations.ZIP_CODE, "55401");

		try {
			RestMethod<Observations> method =
					(RestMethod<Observations>) factory.getRestMethod(uri, Method.POST, params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@SmallTest
	public void testGetRestMethodForObservationsWithoutZip() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Uri uri = RainGaugeProviderContract.ObservationsTable.CONTENT_URI;

		Bundle params = new Bundle();

		try {
			RestMethod<Observations> method =
					(RestMethod<Observations>) factory.getRestMethod(uri, Method.GET, params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}

	@SmallTest
	public void testGetRestMethodWithInvalidUri() {

		RestMethodFactory factory = DefaultRestMethodFactory.getInstance(getContext());

		Uri uri = Uri.parse("invalid");

		Bundle params = new Bundle();
		params.putString(Observations.ZIP_CODE, "55401");

		try {
			RestMethod<Observations> method =
					(RestMethod<Observations>) factory.getRestMethod(uri, Method.GET, params);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
		}
	}
}

