package com.jeremyhaberman.raingauge.processor.test;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.mock.MockNotificationHelper;
import com.jeremyhaberman.raingauge.processor.ObservationsProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.ResourceProcessorCallback;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.service.WeatherService;
import com.jeremyhaberman.raingauge.test.mock.MockRestMethodFactory;

import java.util.Calendar;

public class ObservationsProcessorTest extends InstrumentationTestCase {

	private ObservationsProcessor mProcessor;
	private Context mTargetContext;
	private MyMockContext mMockContext;

	protected void setUp() throws Exception {
		super.setUp();

		mTargetContext = getInstrumentation().getTargetContext();

		mMockContext = new MyMockContext(mTargetContext);

		ServiceManager.loadService(mTargetContext, Service.REST_METHOD_FACTORY,
				new MockRestMethodFactory());

		mProcessor = ObservationsProcessor.createProcessor(mMockContext);
	}

	protected void tearDown() throws Exception {
		ServiceManager.reset(mTargetContext);
		super.tearDown();
	}

	@MediumTest
	public void testWeatherProcessor() {
		assertNotNull(mProcessor);
	}

	@SuppressWarnings("deprecation")
	@MediumTest
	public void testGetResource() throws InterruptedException {

		Cursor cursor = mMockContext.getContentResolver()
				.query(ObservationsTable.CONTENT_URI, null, null, null, null);
		int countBefore = cursor.getCount();

		TestCallback callback = new TestCallback();
		int zip = 55401;
		Bundle params = createParams(zip);

		mProcessor.getResource(callback, params);

		Thread.sleep(1000);

		assertEquals(ResourceProcessor.SUCCESS, callback.getLocalResultCode());
		assertEquals(200, callback.getRemoteResultCode());

		cursor.requery();
		int countAfter = cursor.getCount();
		assertTrue(
				String.format("countAfter (%d) != countBefore (%d) + 1", countAfter, countBefore),
				countAfter == countBefore + 1);
	}

	public void testScheduleNotification() throws InterruptedException {

		MockNotificationHelper mockNotificationHelper = new MockNotificationHelper();
		ServiceManager
				.loadService(mMockContext, Service.NOTIFICATION_HELPER,
						mockNotificationHelper);

		TestCallback callback = new TestCallback();
		int zip = 55401;
		Bundle params = createParams(zip);

		mProcessor.getResource(callback, params);

		Thread.sleep(1000);

		assertEquals(0.75, mockNotificationHelper.getRainfall());

		long actualWhen = mockNotificationHelper.getWhen();

		Calendar expectedCalendar = Calendar.getInstance();
		expectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
		expectedCalendar.set(Calendar.MINUTE, 0);
		expectedCalendar.set(Calendar.SECOND, 0);
		expectedCalendar.set(Calendar.MILLISECOND, 0);
		expectedCalendar.add(Calendar.DAY_OF_MONTH, 1);
		long expectedWhen = expectedCalendar.getTimeInMillis();

		long diff = Math.abs(expectedWhen - actualWhen);

		assertTrue(diff < 3000);
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

	public class MyMockContext extends RenamingDelegatingContext {

		private static final String MOCK_FILE_PREFIX = "test.";

		public MyMockContext(Context context) {
			super(context, MOCK_FILE_PREFIX);
		}

		@Override
		public Resources getResources() {
			return super.getResources();
		}


	}

}
