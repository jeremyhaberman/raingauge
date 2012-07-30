package com.jeremyhaberman.raingauge.activity.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.activity.SetupActivity;

public class SetupActivityTest extends ActivityInstrumentationTestCase2<SetupActivity> {

	private SetupActivity mActivity;
	private EditText mZipCode;
	private Button mGoButton;
	private View mRootView;

	public SetupActivityTest() {
		super("com.jeremyhaberman.raingauge", SetupActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();
		mRootView = mActivity.findViewById(R.id.setup);
		mZipCode = (EditText) mActivity.findViewById(R.id.zip_code);
		mGoButton = (Button) mActivity.findViewById(R.id.go);
	}

	public void testPreconditions() {
		assertNotNull(mActivity);
		assertNotNull(mZipCode);
		assertNotNull(mGoButton);
	}

	public void testZipInputOnScreen() {
		ViewAsserts.assertOnScreen(mRootView, mZipCode);
	}

	public void testGoButtonOnScreen() {
		ViewAsserts.assertOnScreen(mRootView, mGoButton);
	}

	public void testGoRequiresZip() throws Throwable {
		assertFalse(mGoButton.isEnabled());

		sendKeys("5");
		assertFalse(mGoButton.isEnabled());

		sendKeys("5 4 0 1");
		assertTrue(mGoButton.isEnabled());
	}

	public void testZipOnlyAcceptsNumbers() {
		sendKeys("A");
		assertEquals(0, mZipCode.length());

		sendKeys("1");
		assertEquals(1, mZipCode.length());
	}

	public void testZipHasFiveDigitMax() {
		sendKeys("1 2 3 4 5 6");
		assertEquals(5, mZipCode.length());
	}
}

