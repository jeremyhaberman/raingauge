package com.jeremyhaberman.raingauge.activity.test;

import android.app.Activity;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.activity.SetupActivity;
import com.jeremyhaberman.raingauge.util.TestUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ActivityLeakTest extends InstrumentationTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.setZip(getInstrumentation().getTargetContext(), 55417);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtil.clearZip(getInstrumentation().getTargetContext());
		super.tearDown();
	}

	public void testActivityLeak() throws Exception {
		assertNoLeak(RainGaugeActivity.class);
		assertNoLeak(SetupActivity.class);
	}

	private void assertNoLeak(Class<? extends Activity> activityClass) {
		final int TEST_COUNT = 5;
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(getInstrumentation().getTargetContext(), activityClass);
		ArrayList<WeakReference<Activity>> refs =
				new ArrayList<WeakReference<Activity>>();
		for (int i = 0; i < TEST_COUNT; i++) {
			Activity activity = getInstrumentation().startActivitySync(intent);
			refs.add(new WeakReference<Activity>(activity));
			activity.finish();
			getInstrumentation().waitForIdleSync();
			activity = null;
		}
		Runtime.getRuntime().gc();
		Runtime.getRuntime().runFinalization();
		Runtime.getRuntime().gc();

		int refCount = 0;
		for (WeakReference<Activity> c : refs) {
			if (c.get() != null) {
				refCount++;
			}
		}
		// If applications are leaking activity, every reference is reachable.
		assertTrue(refCount != TEST_COUNT);
	}
}
