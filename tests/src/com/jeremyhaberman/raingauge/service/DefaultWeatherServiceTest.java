package com.jeremyhaberman.raingauge.service;

import static com.jeremyhaberman.raingauge.service.WeatherService.EXTRA_PROCESSOR;
import static com.jeremyhaberman.raingauge.service.WeatherService.METHOD_EXTRA;
import static com.jeremyhaberman.raingauge.service.WeatherService.METHOD_GET;
import static com.jeremyhaberman.raingauge.service.WeatherService.RESOURCE_TYPE_EXTRA;
import static com.jeremyhaberman.raingauge.service.WeatherService.RESOURCE_TYPE_RAINFALL;
import static com.jeremyhaberman.raingauge.service.WeatherService.SERVICE_CALLBACK_EXTRA;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.test.IsolatedContext;
import android.test.MoreAsserts;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessorCallback;
import com.jeremyhaberman.raingauge.util.Logger;

public class DefaultWeatherServiceTest extends ServiceTestCase<DefaultWeatherService> {

	public static final String TAG = DefaultWeatherServiceTest.class.getSimpleName();

	public DefaultWeatherServiceTest(String name) {
		super(DefaultWeatherService.class);
		setName(name);
	}
	
	public DefaultWeatherServiceTest() {
		this(TAG);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Logger.clearCache();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@MediumTest
	public void testStartService() throws InterruptedException {
		
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = new MockProcessor();
		MockResultReceiver receiver = new MockResultReceiver(null);
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, METHOD_GET);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, 1);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		Bundle params = processor.getParams();
		
		assertNotNull(params);
		int actualZip = params.getInt(WeatherService.ZIP_CODE);
		assertEquals(zip, actualZip);
	}
	
	@MediumTest
	public void testServiceCallback() throws InterruptedException {
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = new MockProcessor();
		MockResultReceiver receiver = new MockResultReceiver(null);
		
		long expectedRequestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, METHOD_GET);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, expectedRequestId);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		ResourceProcessorCallback callback = processor.getCallback();
		assertNotNull(callback);
		int expectedResultCode = 200;
		callback.send(expectedResultCode, "1");
		
		int actualResultCode = receiver.getResultCode();
		assertEquals(expectedResultCode, actualResultCode);
		Bundle actualResultData = receiver.getResultData();
		
		Intent resultIntent = (Intent) actualResultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(METHOD_GET, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(RESOURCE_TYPE_RAINFALL, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(processor, resultIntent.getParcelableExtra(EXTRA_PROCESSOR));
		assertEquals(expectedRequestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithInvalidMethod() throws InterruptedException {
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = new MockProcessor();
		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = "POST";
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		Bundle params = processor.getParams();
		assertNull(params);
		assertEquals(WeatherService.REQUEST_INVALID, receiver.getResultCode());
		Bundle resultData = receiver.getResultData();
		Intent resultIntent = (Intent) resultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(method, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(RESOURCE_TYPE_RAINFALL, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(processor, resultIntent.getParcelableExtra(EXTRA_PROCESSOR));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullMethod() throws InterruptedException {
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = new MockProcessor();
		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = null;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		Bundle params = processor.getParams();
		assertNull(params);
		assertEquals(WeatherService.REQUEST_INVALID, receiver.getResultCode());
		Bundle resultData = receiver.getResultData();
		Intent resultIntent = (Intent) resultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertNull(resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(RESOURCE_TYPE_RAINFALL, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(processor, resultIntent.getParcelableExtra(EXTRA_PROCESSOR));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullProcessor() throws InterruptedException {
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = null;
		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = METHOD_GET;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		assertEquals(WeatherService.REQUEST_INVALID, receiver.getResultCode());
		Bundle resultData = receiver.getResultData();
		Intent resultIntent = (Intent) resultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(method, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(RESOURCE_TYPE_RAINFALL, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertNull(resultIntent.getParcelableExtra(EXTRA_PROCESSOR));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullParameters() throws InterruptedException {
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = new MockProcessor();
		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = METHOD_GET;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = null;
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		Bundle params = processor.getParams();
		assertNull(params);
		assertEquals(WeatherService.REQUEST_INVALID, receiver.getResultCode());
		Bundle resultData = receiver.getResultData();
		Intent resultIntent = (Intent) resultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(method, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(RESOURCE_TYPE_RAINFALL, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(processor, resultIntent.getParcelableExtra(EXTRA_PROCESSOR));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullCallback() throws InterruptedException {
		Context mockContext = new TestContext(getSystemContext());
		setContext(mockContext);
		
		MockProcessor processor = new MockProcessor();
		MockResultReceiver receiver = null;
		
		String method = METHOD_GET;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, RESOURCE_TYPE_RAINFALL);
		service.putExtra(EXTRA_PROCESSOR, processor);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = null;
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		String[] recentMessages = Logger.getMessageCache();
		assertTrue(recentMessages[0].matches(".*Service\\scallback\\sis\\snull"));
	}
	
	private class TestContext extends IsolatedContext {

		public TestContext(Context targetContext) {
			super(targetContext.getContentResolver(), targetContext);
		}

		@Override
		public Context getApplicationContext() {
			return super.getApplicationContext();
		}

		@Override
		public ComponentName startService(Intent service) {
			return super.startService(service);
		}
	}

	public class MockProcessor implements ResourceProcessor, Parcelable {

		private Bundle mParams;
		private ResourceProcessorCallback mCallback;

		@Override
		public void getResource(ResourceProcessorCallback callback, Bundle params) {
			mockCallback(callback, params);
		}

		private void mockCallback(ResourceProcessorCallback callback, Bundle params) {
			mCallback = callback;
			mParams = params;
		}
		
		public ResourceProcessorCallback getCallback() {
			return mCallback;
		}
		
		public Bundle getParams() {
			return mParams;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
		}

	}
	
	public class MockResultReceiver extends ResultReceiver {

		private int mResultCode;
		private Bundle mResultData;

		public MockResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		public void send(int resultCode, Bundle resultData) {
			mResultCode = resultCode;
			mResultData = resultData;
		}
		
		public int getResultCode() {
			return mResultCode;
		}
		
		public Bundle getResultData() {
			return mResultData;
		}
	}
}
