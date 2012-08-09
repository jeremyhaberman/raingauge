package com.jeremyhaberman.raingauge.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.test.IsolatedContext;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.mock.MockProcessor;
import com.jeremyhaberman.raingauge.mock.MockProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessorCallback;
import com.jeremyhaberman.raingauge.util.Logger;

import static com.jeremyhaberman.raingauge.service.WeatherService.METHOD_EXTRA;
import static com.jeremyhaberman.raingauge.service.WeatherService.METHOD_GET;
import static com.jeremyhaberman.raingauge.service.WeatherService.RESOURCE_TYPE_EXTRA;
import static com.jeremyhaberman.raingauge.service.WeatherService.SERVICE_CALLBACK_EXTRA;

public class DefaultWeatherServiceTest extends ServiceTestCase<DefaultWeatherService> {

	public static final String TAG = DefaultWeatherServiceTest.class.getSimpleName();
	private TestContext mMockContext;
	private MockProcessorFactory mProcessorFactory;

	public DefaultWeatherServiceTest(String name) {
		super(DefaultWeatherService.class);
		setName(name);
	}
	
	public DefaultWeatherServiceTest() {
		this(TAG);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mMockContext = new TestContext(getSystemContext());
		Logger.clearCache();
		mProcessorFactory = new MockProcessorFactory();
		ServiceManager.loadService(mMockContext, Service.PROCESSOR_FACTORY, mProcessorFactory);
	}

	protected void tearDown() throws Exception {
		ServiceManager.reset(getSystemContext());
		super.tearDown();
	}

	@MediumTest
	public void testStartService() throws InterruptedException {

		setContext(mMockContext);

		MockResultReceiver receiver = new MockResultReceiver(null);
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, METHOD_GET);
		service.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, 1);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);

		MockProcessor processor = mProcessorFactory.getProcessor();
		Bundle params = processor.getParams();
		
		assertNotNull(params);
		int actualZip = params.getInt(WeatherService.ZIP_CODE);
		assertEquals(zip, actualZip);
	}
	
	@MediumTest
	public void testServiceCallback() throws InterruptedException {

		setContext(mMockContext);

		MockResultReceiver receiver = new MockResultReceiver(null);
		
		long expectedRequestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, METHOD_GET);
		service.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, expectedRequestId);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		ResourceProcessorCallback callback = mProcessorFactory.getProcessor().getCallback();
		assertNotNull(callback);
		int expectedResultCode = 200;
		callback.send(expectedResultCode, ResourceProcessor.SUCCESS);
		
		int actualResultCode = receiver.getResultCode();
		assertEquals(expectedResultCode, actualResultCode);
		Bundle actualResultData = receiver.getResultData();
		
		Intent resultIntent = (Intent) actualResultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(METHOD_GET, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(WeatherService.RESOURCE_TYPE_OBSERVATIONS, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(expectedRequestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithInvalidMethod() throws InterruptedException {
		setContext(mMockContext);

		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = "POST";
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = new Bundle();
		int zip = 55401;
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		Bundle params = mProcessorFactory.getProcessor().getParams();
		assertNull(params);
		assertEquals(WeatherService.REQUEST_INVALID, receiver.getResultCode());
		Bundle resultData = receiver.getResultData();
		Intent resultIntent = (Intent) resultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(method, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(WeatherService.RESOURCE_TYPE_OBSERVATIONS, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullMethod() throws InterruptedException {

		setContext(mMockContext);

		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = null;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);
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
		
		assertNull(resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(WeatherService.RESOURCE_TYPE_OBSERVATIONS, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullParameters() throws InterruptedException {

		setContext(mMockContext);

		MockResultReceiver receiver = new MockResultReceiver(null);
		
		String method = METHOD_GET;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = null;
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);

		assertEquals(WeatherService.REQUEST_INVALID, receiver.getResultCode());
		Bundle resultData = receiver.getResultData();
		Intent resultIntent = (Intent) resultData.get(WeatherService.ORIGINAL_INTENT_EXTRA);
		
		assertEquals(method, resultIntent.getStringExtra(METHOD_EXTRA));
		assertEquals(WeatherService.RESOURCE_TYPE_OBSERVATIONS, resultIntent.getIntExtra(RESOURCE_TYPE_EXTRA, -1));
		assertEquals(requestId, resultIntent.getLongExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, -1));
	}
	
	@MediumTest
	public void testStartServiceWithNullCallback() throws InterruptedException {

		setContext(mMockContext);

		MockResultReceiver receiver = null;
		
		String method = METHOD_GET;
		long requestId = 1;
		
		Intent service = new Intent(getSystemContext(), DefaultWeatherService.class);
		service.putExtra(METHOD_EXTRA, method);
		service.putExtra(RESOURCE_TYPE_EXTRA, WeatherService.RESOURCE_TYPE_OBSERVATIONS);
		service.putExtra(SERVICE_CALLBACK_EXTRA, receiver);
		service.putExtra(WeatherServiceHelper.EXTRA_REQUEST_ID, requestId);
		
		Bundle requestParams = null;
		service.putExtra(WeatherService.EXTRA_REQUEST_PARAMETERS, requestParams);
		
		super.startService(service);
		
		Thread.sleep(500);
		
		String[] recentMessages = Logger.getMessageCache();
		assertTrue(recentMessages[1].matches(".*Service\\scallback\\sis\\snull"));
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
