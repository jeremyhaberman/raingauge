package com.jeremyhaberman.raingauge.activity.test;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import android.test.ViewAsserts;
import android.widget.Button;
import android.widget.TextView;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.activity.SetupActivity;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.util.TestUtil;

public class SetupTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		TestUtil.clearZip(getInstrumentation().getTargetContext());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSetup() throws Throwable {

		Instrumentation.ActivityMonitor monitor =
				new Instrumentation.ActivityMonitor(SetupActivity.class.getName(), null, false);
		getInstrumentation().addMonitor(monitor);

		RainGaugeActivity rainGaugeActivity =
				launchActivity("com.jeremyhaberman.raingauge", RainGaugeActivity.class, null);

		SetupActivity setupActivity = (SetupActivity) monitor.waitForActivityWithTimeout(5000);

		Thread.sleep(1000);

		final Button go = (Button) setupActivity.findViewById(R.id.go);
		sendKeys("5 5 4 0 1");
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				go.performClick();
			}
		});

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				getInstrumentation().getTargetContext());
		assertEquals(55401, prefs.getInt(Observations.ZIP_CODE, -1));

		TextView headerOnRainGaugeActivity = (TextView) rainGaugeActivity.findViewById(R.id.header);
		ViewAsserts.assertOnScreen(rainGaugeActivity.getWindow().getDecorView(),
				headerOnRainGaugeActivity);
	}
}

