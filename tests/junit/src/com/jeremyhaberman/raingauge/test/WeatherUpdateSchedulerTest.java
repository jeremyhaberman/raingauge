package com.jeremyhaberman.raingauge.test;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.WeatherUpdateScheduler;
import com.jeremyhaberman.raingauge.WeatherUpdater;
import com.jeremyhaberman.raingauge.android.DefaultAndroidAlarmManager;
import com.jeremyhaberman.raingauge.service.WeatherService;
import com.jeremyhaberman.raingauge.test.mock.MockAlarmManager;
import com.jeremyhaberman.raingauge.test.mock.MockBroadcastReceiver;
import com.jeremyhaberman.raingauge.util.TestUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WeatherUpdateSchedulerTest extends InstrumentationTestCase {

	private Context mTestContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		TestUtil.setZip(getInstrumentation().getTargetContext(), 55417);
		mTestContext = getInstrumentation().getContext();
		ServiceManager.reset(getInstrumentation().getTargetContext());
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtil.clearZip(getInstrumentation().getTargetContext());
		ServiceManager.reset(getInstrumentation().getTargetContext());
		super.tearDown();
	}

	@MediumTest
	public void testOnReceiveActionScheduleRainfallUpdates() {

		MockAlarmManager alarmManager = new MockAlarmManager(getInstrumentation().getContext());
		ServiceManager.loadService(getInstrumentation().getContext(), Service.ALARM_SERVICE,
				alarmManager);

		Context mockContext = new MyMockContext(getInstrumentation().getTargetContext());

		Intent intent = new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_RAINFALL_UPDATES);

		WeatherUpdateScheduler weatherUpdateScheduler = new WeatherUpdateScheduler();
		weatherUpdateScheduler.onReceive(mockContext, intent);

		MockAlarmManager.Alarm alarm = alarmManager.getLastAlarm();
		assertEquals(0, alarm.type);
		assertEquals(getExpectedRainfallUpdateTriggerAt(), alarm.triggerAtMillis);
		assertEquals(getExpectedRainfallUpdateInterval(), alarm.intervalMillis);
	}

	@MediumTest
	public void testOnReceiveActionScheduleForecastUpdates() {

		MockAlarmManager alarmManager = new MockAlarmManager(getInstrumentation().getContext());
		ServiceManager.loadService(getInstrumentation().getContext(), Service.ALARM_SERVICE,
				alarmManager);

		Context mockContext = new MyMockContext(getInstrumentation().getTargetContext());

		Intent intent = new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_FORECAST_UPDATES);

		WeatherUpdateScheduler weatherUpdateScheduler = new WeatherUpdateScheduler();
		weatherUpdateScheduler.onReceive(mockContext, intent);

		MockAlarmManager.Alarm alarm = alarmManager.getLastAlarm();
		assertEquals(0, alarm.type);
		assertEquals(getExpectedForecastUpdateTriggerAt(), alarm.triggerAtMillis);
		assertEquals(getExpectedForecastUpdateInterval(), alarm.intervalMillis);
	}

	@MediumTest
	public void testOnReceiveUpdateRainfallIntent() throws InterruptedException {

		Context context = getInstrumentation().getTargetContext();

		MockBroadcastReceiver mockBroadcastReceiver = new MockBroadcastReceiver();
		IntentFilter filter = new IntentFilter(WeatherUpdater.ACTION_UPDATE_RAINFALL);
		context.registerReceiver(mockBroadcastReceiver, filter);

		ServiceManager.loadService(context, Service.ALARM_SERVICE,
				new DefaultAndroidAlarmManager(context));

		int expectedZip = 55417;

		Intent intent = new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_RAINFALL_UPDATES);
		intent.putExtra(WeatherUpdateScheduler.EXTRA_ZIP_CODE, expectedZip);
		intent.putExtra(WeatherUpdateScheduler.EXTRA_NEXT_RAINFALL_UPDATE_TIME,
				System.currentTimeMillis() + 2000);

		WeatherUpdateScheduler weatherUpdateScheduler = new WeatherUpdateScheduler();
		weatherUpdateScheduler.onReceive(context, intent);

		Thread.sleep(5000);

		Intent updateRainfallIntent = mockBroadcastReceiver.getLastIntent();
		assertNotNull(updateRainfallIntent);
		assertEquals(WeatherUpdater.ACTION_UPDATE_RAINFALL, updateRainfallIntent.getAction());
		assertEquals(expectedZip, updateRainfallIntent.getIntExtra(WeatherService.ZIP_CODE, 0));
	}

	@MediumTest
	public void testOnReceiveUpdateForecastIntent() throws InterruptedException {

		Context context = getInstrumentation().getTargetContext();

		MockBroadcastReceiver mockBroadcastReceiver = new MockBroadcastReceiver();
		IntentFilter filter = new IntentFilter(WeatherUpdater.ACTION_UPDATE_FORECAST);
		context.registerReceiver(mockBroadcastReceiver, filter);

		ServiceManager.loadService(context, Service.ALARM_SERVICE,
				new DefaultAndroidAlarmManager(context));

		int expectedZip = 55417;

		Intent intent = new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_FORECAST_UPDATES);
		intent.putExtra(WeatherUpdateScheduler.EXTRA_ZIP_CODE, expectedZip);
		intent.putExtra(WeatherUpdateScheduler.EXTRA_NEXT_FORECAST_UPDATE_TIME,
				System.currentTimeMillis() + 2000);

		WeatherUpdateScheduler weatherUpdateScheduler = new WeatherUpdateScheduler();
		weatherUpdateScheduler.onReceive(context, intent);

		Thread.sleep(5000);

		Intent updateForecastIntent = mockBroadcastReceiver.getLastIntent();
		assertNotNull(updateForecastIntent);
		assertEquals(WeatherUpdater.ACTION_UPDATE_FORECAST, updateForecastIntent.getAction());
		assertEquals(expectedZip, updateForecastIntent.getIntExtra(WeatherService.ZIP_CODE, 0));
	}

	private long getExpectedRainfallUpdateInterval() {
		return 1000 * 60 * 60 * 24;
	}

	private long getExpectedForecastUpdateInterval() {
		return 1000 * 60 * 60 * 4;
	}

	private long getExpectedRainfallUpdateTriggerAt() {
		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 55);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTimeInMillis();
	}

	private long getExpectedForecastUpdateTriggerAt() {
		Calendar date = new GregorianCalendar();

		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTimeInMillis();
	}

	public class MyMockContext extends MockContext {

		private Context mTestContext;
		private MyMockApplication mMockApplication;

		MyMockContext(Context testContext) {
			mTestContext = testContext;
			mMockApplication = new MyMockApplication(testContext);
		}

		@Override
		public SharedPreferences getSharedPreferences(String name, int mode) {
			return mTestContext.getSharedPreferences(name, mode);
		}

		@Override
		public Context getApplicationContext() {
			return mMockApplication.getApplicationContext();
		}

		@Override
		public String getPackageName() {
			return mTestContext.getPackageName();
		}

		@Override
		public ContentResolver getContentResolver() {
			return mTestContext.getContentResolver();
		}

		@Override
		public Object getSystemService(String name) {

			if (name.equalsIgnoreCase(Context.ALARM_SERVICE)) {
				return mTestContext.getSystemService(Context.ALARM_SERVICE);
			}

			return null;
		}
	}

	private class MyMockApplication extends MockApplication {

		private Context mContext;

		MyMockApplication(Context context) {
			mContext = context;
		}

		@Override
		public Context getApplicationContext() {
			return mContext;
		}
	}
}
