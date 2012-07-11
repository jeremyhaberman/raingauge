package com.jeremyhaberman.raingauge.processor.test;

import android.database.Cursor;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.processor.ObservationsProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessorCallback;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.service.WeatherService;
import com.jeremyhaberman.raingauge.test.mock.MockRestMethodFactory;

public class ObservationsProcessorTest extends AndroidTestCase {

	private ObservationsProcessor mProcessor;

	protected void setUp() throws Exception {
		super.setUp();
		
		ServiceManager serviceManager = ServiceManager.createServiceManager();
		serviceManager.loadService(Service.REST_METHOD_FACTORY, new MockRestMethodFactory());
		
		mProcessor = ObservationsProcessor.createProcessor(getContext());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@MediumTest
	public void testWeatherProcessor() {
		assertNotNull(mProcessor);
	}

	@MediumTest
	public void testGetResource() throws InterruptedException {
		
		Cursor cursor = getContext().getContentResolver().query(ObservationsTable.CONTENT_URI, null, null, null, null);
		int countBefore = cursor.getCount();
		
		TestCallback callback = new TestCallback();
		int zip = 55401;
		Bundle params = createParams(zip);
		
		mProcessor.getResource(callback, params);
		
		Thread.sleep(500);
		
		assertEquals(ResourceProcessor.SUCCESS, callback.getLocalResultCode());
		assertEquals(200, callback.getRemoteResultCode());
		
		cursor.requery();
		int countAfter = cursor.getCount();
		assertTrue(countAfter == countBefore + 1);
	}
	
	private Bundle createParams(int zip) {
		Bundle requestParams = new Bundle();
		requestParams.putInt(WeatherService.ZIP_CODE, zip);
		return requestParams;
	}

	class TestCallback implements ResourceProcessorCallback {

		private int mRemoteResultCode;
		private int mLocalResultCode;
		
		@Override
		public void send(int remoteResultCode, int localResultCode) {
			mRemoteResultCode = remoteResultCode;
			mLocalResultCode = localResultCode;
		}
		
		public int getRemoteResultCode() {
			return mRemoteResultCode;
		}
		
		public int getLocalResultCode() {
			return mLocalResultCode;
		}
		
	}

}
