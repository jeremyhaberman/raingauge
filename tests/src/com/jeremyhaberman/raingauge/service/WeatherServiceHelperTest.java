package com.jeremyhaberman.raingauge.service;

import static com.jeremyhaberman.raingauge.service.WeatherService.SERVICE_CALLBACK_EXTRA;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.test.AndroidTestCase;
import android.test.IsolatedContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.jeremyhaberman.raingauge.rest.Method;
import com.jeremyhaberman.raingauge.service.WeatherService.ResourceType;
import com.jeremyhaberman.raingauge.service.WeatherServiceHelper.ServiceResultReceiver;
import com.jeremyhaberman.raingauge.test.mock.MockWeatherService;

/**
 * This is a simple framework for a test of an Application. See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more
 * information on how to write and extend Application tests.
 * <p/>
 * To run this test, you can type: adb shell am instrument -w \ -e class
 * com.jeremyhaberman.raingauge.RainGaugeActivityTest \
 * com.jeremyhaberman.raingauge.tests/android.test.InstrumentationTestRunner
 */
public class WeatherServiceHelperTest extends AndroidTestCase {

	private static final String TAG = WeatherServiceHelperTest.class.getSimpleName();
	private WeatherServiceHelper mWeather;
	private TestContext mTestContext;
	
	private static final int TEST_ZIP = 55401;

	private Class<? extends WeatherService> mWeatherServiceClass = MockWeatherService.class;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mTestContext = new TestContext(getContext().getContentResolver(), getContext());
		mWeather = new WeatherServiceHelper(mTestContext, mWeatherServiceClass);
	}

	public void testPreconditions() {

		assertNotNull(mWeather);
	}
	
	@SmallTest
	public void testWeatherServiceHelperContext() {
		WeatherServiceHelper helper = new WeatherServiceHelper(getContext());
		assertNotNull(helper);
		assertTrue(helper.getWeatherServiceClass() == DefaultWeatherService.class);
	}

	@SmallTest
	public void testGetTodaysRainfall() throws Exception {
		
		long requestId = mWeather.getTodaysRainfall(TEST_ZIP);

		Intent startServiceIntent = mTestContext.getStartServiceIntent();

		assertNotNull(startServiceIntent);
		ComponentName component = startServiceIntent.getComponent();
		String componentClassName = component.getClassName();
		assertEquals(mWeatherServiceClass.getName(), componentClassName);
		
		Bundle extras = startServiceIntent.getExtras();
		
		long intentRequestId = extras.getLong(WeatherServiceHelper.EXTRA_REQUEST_ID);
		assertEquals(requestId, intentRequestId);
		
		String method = extras.getString(WeatherService.METHOD_EXTRA);
		assertEquals(Method.GET.toString(), method);
		
		ResourceType resourceType = (ResourceType) extras.getSerializable(WeatherService.RESOURCE_TYPE_EXTRA);
		assertEquals(WeatherService.ResourceType.OBSERVATIONS, resourceType);
		
		ResultReceiver resultReceiver = (ResultReceiver) extras.getParcelable(SERVICE_CALLBACK_EXTRA);
		assertNotNull(resultReceiver);
		
		Bundle requestParams = extras.getBundle(WeatherService.EXTRA_REQUEST_PARAMETERS);
		int intentZip = requestParams.getInt(WeatherService.ZIP_CODE);
		assertEquals(TEST_ZIP, intentZip);
	}
	
	@SmallTest
	public void testServiceResultReceiverWithValidOriginalIntent() throws InterruptedException {
		
		long requestId = mWeather.getTodaysRainfall(TEST_ZIP);
		
		assertTrue(mWeather.isRequestPending(requestId));
		
		int resultCode = 1;
		Intent originalIntent = mTestContext.getStartServiceIntent();
		Bundle resultData = new Bundle();
		resultData.putParcelable(WeatherService.ORIGINAL_INTENT_EXTRA, originalIntent);
		
		ServiceResultReceiver receiver = mWeather.new ServiceResultReceiver();
		receiver.onReceiveResult(resultCode, resultData);
		
		assertFalse(mWeather.isRequestPending(requestId));
		
		Intent broadcast = mTestContext.getBroadcast();
		assertNotNull(broadcast);
		
		assertEquals(resultCode, broadcast.getIntExtra(WeatherServiceHelper.EXTRA_RESULT_CODE, -1));
		assertEquals(requestId, broadcast.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));	
	}
	
	@SmallTest
	public void testServiceResultReceiverWithInvalidOriginalIntent() throws InterruptedException {
		
		mWeather.getTodaysRainfall(TEST_ZIP);
		
		int resultCode = 1;
		Bundle invalidResultData = new Bundle();
		
		ServiceResultReceiver receiver = mWeather.new ServiceResultReceiver();
		receiver.onReceiveResult(resultCode, invalidResultData);
		
		Intent broadcast = mTestContext.getBroadcast();
		assertNull(broadcast);		
	}
	
	@SmallTest
	public void testIsRequestPendingInvalidRequestId() {
		
		assertFalse(mWeather.isRequestPending(1234));
	}

	private class TestContext extends IsolatedContext {

		private Intent mStartServiceIntent;
		private Intent mBroadcast;

		public TestContext(ContentResolver resolver, Context targetContext) {
			super(resolver, targetContext);
		}

		@Override
		public Context getApplicationContext() {
			return this;
		}

		@Override
		public ComponentName startService(Intent service) {
			mStartServiceIntent = service;
			return super.startService(service);
		}

		public Intent getStartServiceIntent() {
			return mStartServiceIntent;
		}

		@Override
		public void sendBroadcast(Intent intent) {
			mBroadcast = intent;
		}
		
		public Intent getBroadcast() {
			return mBroadcast;
		}
	}
}
