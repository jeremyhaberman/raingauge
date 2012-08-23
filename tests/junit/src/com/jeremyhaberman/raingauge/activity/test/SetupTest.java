
package com.jeremyhaberman.raingauge.activity.test;

import android.app.Instrumentation;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.TextView;

import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.activity.SetupActivity;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.util.TestUtil;

/**
 * SetupTest tests the first-time setup of the app.
 */
public class SetupTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.clearZip(getInstrumentation().getTargetContext());
    }

    @Override
    protected void tearDown() throws Exception {
        TestUtil.clearZip(getInstrumentation().getTargetContext());
        super.tearDown();
    }

    @MediumTest
    public void testSetup() throws Throwable {

        // We're not going to launch the SetupActivity directly. Rather, we'll
        // be starting the RainGaugeActivity, which should start the
        // SetupActivity since a ZIP code has not been configured. This monitor
        // allows us to "watch" for the SetupActivity being launched
        Instrumentation.ActivityMonitor monitor =
                new Instrumentation.ActivityMonitor(SetupActivity.class.getName(), null, false);
        getInstrumentation().addMonitor(monitor);

        // Launch the RainGaugeActivity
        RainGaugeActivity rainGaugeActivity =
                launchActivity("com.jeremyhaberman.raingauge", RainGaugeActivity.class, null);

        // The RainGaugeActivity should have launched the SetupActivity. We wait
        // for that to happen, allowing 5 seconds before bailing
        SetupActivity setupActivity = (SetupActivity) monitor.waitForActivityWithTimeout(5000);

        // Give a little more time for the SetupActivity to initialize
        Thread.sleep(1000);

        // Enter a ZIP code
        final Button go = (Button) setupActivity.findViewById(R.id.go);
        sendKeys("5 5 4 0 1");
        
        // We need to perform clicks on the UI thread
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                go.performClick();
            }
        });

        // Confirm that the SetupActivity stored the ZIP code
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getInstrumentation().getTargetContext());
        assertEquals(55401, prefs.getInt(Observations.ZIP_CODE, -1));

        // Give it a moment to go back to the RainGaugeActivity
        Thread.sleep(2000);
        
        // Test the something from the RainGaugeActivity is being shown
        TextView headerOnRainGaugeActivity = (TextView) rainGaugeActivity.findViewById(R.id.header);
        assertTrue(headerOnRainGaugeActivity.isShown());
    }
}
