
package com.jeremyhaberman.raingauge.activity.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;

import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.activity.SetupActivity;

/**
 * SetupActivityTest tests the {@link SetupActivity}.
 */
public class SetupActivityTest extends ActivityInstrumentationTestCase2<SetupActivity> {

    private SetupActivity mActivity;
    private EditText mZipCode;
    private Button mGoButton;

    public SetupActivityTest(Class<SetupActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Allows sending keys and click events from the test
        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        mZipCode = (EditText) mActivity.findViewById(R.id.zip_code);
        mGoButton = (Button) mActivity.findViewById(R.id.go);
    }

    @MediumTest
    public void testPreconditions() {
        assertNotNull(mActivity);
        assertNotNull(mZipCode);
        assertNotNull(mGoButton);
    }

    @MediumTest
    public void testViewsAreShown() {
        assertTrue(mZipCode.isShown());
    }

    @MediumTest
    public void testGoButton() {
        assertTrue(mGoButton.isShown());
        assertEquals(mGoButton.getText().toString(), "Go");

        // The button should not be clickable until numbers are added to the ZIP
        // code field
        assertFalse(mGoButton.isEnabled());
    }

    @MediumTest
    public void testGoRequiresZip() throws Throwable {
        assertFalse(mGoButton.isEnabled());

        sendKeys("5");
        assertFalse(mGoButton.isEnabled());

        sendKeys("5 4 0 1");
        assertTrue(mGoButton.isEnabled());
    }

    @MediumTest
    public void testZipOnlyAcceptsNumbers() {
        sendKeys("A");
        assertEquals(0, mZipCode.length());

        sendKeys("1");
        assertEquals(1, mZipCode.length());
    }

    @MediumTest
    public void testZipHasFiveDigitMax() {
        sendKeys("1 2 3 4 5 6");
        assertEquals(5, mZipCode.length());
    }
}
