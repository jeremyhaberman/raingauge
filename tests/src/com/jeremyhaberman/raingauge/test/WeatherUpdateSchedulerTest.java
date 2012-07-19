package com.jeremyhaberman.raingauge.test;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.MediumTest;
import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.WeatherUpdateScheduler;
import com.jeremyhaberman.raingauge.android.AndroidAlarmManager;
import com.jeremyhaberman.raingauge.test.mock.MockAlarmManager;

public class WeatherUpdateSchedulerTest extends InstrumentationTestCase {

	@MediumTest
	public void testOnReceive() {

		AndroidAlarmManager alarmManager = new MockAlarmManager(getInstrumentation().getContext());
		ServiceManager.loadService(getInstrumentation().getContext(), Service.ALARM_SERVICE, alarmManager);

		Context mockContext = new MyMockContext(getInstrumentation().getTargetContext());

		Intent intent = new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_WEATHER_UPDATES);

		WeatherUpdateScheduler weatherUpdateScheduler = new WeatherUpdateScheduler();
		weatherUpdateScheduler.onReceive(mockContext, intent);
	}

	public class MyMockContext extends MockContext {

		private Context mTestContext;
		private MyMockApplication mMockApplication;

		MyMockContext(Context testContext) {
			mTestContext = testContext;
			mMockApplication = new MyMockApplication(testContext);
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
