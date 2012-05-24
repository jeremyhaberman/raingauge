package com.jeremyhaberman.raingauge;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * This is a simple framework for a test of an Application. See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more
 * information on how to write and extend Application tests.
 * <p/>
 * To run this test, you can type: adb shell am instrument -w \ -e class
 * com.jeremyhaberman.raingauge.RainGaugeActivityTest \
 * com.jeremyhaberman.raingauge.tests/android.test.InstrumentationTestRunner
 */
public class WeatherTest extends AndroidTestCase {

	private static final String TAG = WeatherTest.class.getSimpleName();
	private Weather mWeather;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mWeather = new Weather();
	}

	public void testPreconditions() {

		assertNotNull(mWeather);
	}

	public void testGetTodaysRainfall() throws Exception {

		double rainfall = mWeather.getTodaysRainfall(56572);

		Log.d(TAG, "rainfall: " + rainfall);

		assertTrue(rainfall > 0);
	}
}
