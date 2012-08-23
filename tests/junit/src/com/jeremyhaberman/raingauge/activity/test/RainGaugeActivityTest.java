
package com.jeremyhaberman.raingauge.activity.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.activity.RainGaugeActivity;
import com.jeremyhaberman.raingauge.model.Watering;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import com.jeremyhaberman.raingauge.util.Logger;
import com.jeremyhaberman.raingauge.util.TestUtil;

/**
 * RainGaugeActivityTest tests the RainGaugeActivity
 */
public class RainGaugeActivityTest extends ActivityInstrumentationTestCase2<RainGaugeActivity> {

    private static final String TAG = RainGaugeActivityTest.class.getSimpleName();
    
    private static final String POSITIVE_AMOUNT_FORMAT = "%.2f in";
    private static final String NEGATIVE_AMOUNT_FORMAT = "-%.2f in";

    private static final int ZIP = 55417;
    
    private Context mContext;

    public RainGaugeActivityTest() {
        super(RainGaugeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getTargetContext();

        // Make sure all the data is cleared before each test
        TestUtil.clearContentProvider(mContext);

        // Set the ZIP code for the app. Otherwise, RainGaugeActivity will
        // redirect to SetupActivity
        TestUtil.setZip(mContext, ZIP);
    }

    @Override
    protected void tearDown() throws Exception {
        // Clean up all the data from the test
        TestUtil.clearContentProvider(mContext);
        TestUtil.clearZip(mContext);

        super.tearDown();
    }

    public void testPreconditions() throws InterruptedException {

        RainGaugeActivity activity = getActivity();

        TextView rainfallLabel = (TextView) activity.findViewById(R.id.rainfall_label);
        TextView rainfallText = (TextView) activity.findViewById(R.id.rainfall);
        TextView wateringLabel = (TextView) activity.findViewById(R.id.watering_label);
        TextView wateringText = (TextView) activity.findViewById(R.id.watering);
        TextView balanceText = (TextView) activity.findViewById(R.id.balance);
        EditText manualWateringAmountEditText =
                (EditText) activity.findViewById(R.id.watering_amount);
        Button waterButton = (Button) activity.findViewById(R.id.add_manual_watering);
        TextView dayForecast = (TextView) activity.findViewById(R.id.day_forecast);

        assertTrue(rainfallLabel.isShown());
        assertTrue(rainfallText.isShown());
        assertTrue(wateringLabel.isShown());
        assertTrue(wateringText.isShown());
        assertTrue(balanceText.isShown());
        assertTrue(manualWateringAmountEditText.isShown());
        assertTrue(waterButton.isShown());
        assertTrue(dayForecast.isShown());
        assertTrue(rainfallLabel.isShown());
    }

    @MediumTest
    public void testZeroDaysOfRainfall() {
        assertRainfall(new double[] {});
    }

    @MediumTest
    public void testOneDayOfRainfall() {
        assertRainfall(new double[] { 0.72 });
    }

    @MediumTest
    public void testTwoDaysOfRainfallWithOneZero() {
        assertRainfall(new double[] { 0.72, 0.0 });
    }

    @MediumTest
    public void testTwoDaysOfRainfall() {
        assertRainfall(new double[] { 0.72, 0.21 });
    }

    @MediumTest
    public void testSevenDaysOfRainfall() {
        assertRainfall(new double[] {
                0.72, 0.21, 0.0, 1.2, 0.14, 0.72, 0.0
        });
    }

    @MediumTest
    public void testEightDaysOfRainfall() {
        assertRainfall(new double[] {
                0.72, 0.21, 0.0, 1.2, 0.14, 0.72, 0.0, .43
        });
    }

    @MediumTest
    public void testRainfallChangeWhileActivityInForeground() throws InterruptedException {
        addRainfallToContentProvider(new double[] { 0.72 });
        RainGaugeActivity activity = getActivity();
        TextView rainfallText = (TextView) activity.findViewById(R.id.rainfall);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, 0.72), rainfallText.getText().toString());
        addRainfallToContentProvider(new double[] { 0.21 });
        Thread.sleep(1000);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, 0.93), rainfallText.getText().toString());
    }

    @MediumTest
    public void testAddWateringWithoutValue() throws Throwable {
        setActivityInitialTouchMode(false);
        addWatering(new double[] { .25 });
        RainGaugeActivity activity = getActivity();
        TextView watering = (TextView) activity.findViewById(R.id.watering);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, 0.25), watering.getText().toString());

        final Button addWatering = (Button) activity.findViewById(R.id.add_manual_watering);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                addWatering.performClick();
            }
        });
        Thread.sleep(1000);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, 0.25), watering.getText().toString());
    }

    @MediumTest
    public void testWateringInputCharacters() throws Throwable {
        setActivityInitialTouchMode(false);
        RainGaugeActivity activity = getActivity();
        EditText wateringInput = (EditText) activity.findViewById(R.id.watering_amount);

        assertCharacterAllowed("0", "0", wateringInput);
        assertCharacterAllowed("PERIOD", ".", wateringInput);

        assertCharacterNotAllowed("SPACE", wateringInput);
        assertCharacterNotAllowed("-", wateringInput);
        assertCharacterNotAllowed("a", wateringInput);
        assertCharacterNotAllowed(",", wateringInput);
    }

    @MediumTest
    public void testWateringInputLength() throws Throwable {
        setActivityInitialTouchMode(false);
        RainGaugeActivity activity = getActivity();
        EditText wateringInput = (EditText) activity.findViewById(R.id.watering_amount);

        sendKeys("1");
        assertEquals("1", wateringInput.getText().toString());

        sendKeys("2 3 4");
        assertEquals("1234", wateringInput.getText().toString());

        sendKeys("PERIOD");
        assertEquals("1234", wateringInput.getText().toString());

        sendKeys("DEL PERIOD");
        assertEquals("123.", wateringInput.getText().toString());

        sendKeys("DEL DEL PERIOD 4");
        assertEquals("12.4", wateringInput.getText().toString());

        sendKeys("DEL DEL DEL DEL 0 PERIOD 2 3");
        assertEquals("0.23", wateringInput.getText().toString());

        sendKeys("4");
        assertEquals("0.23", wateringInput.getText().toString());
    }

    @MediumTest
    private void assertCharacterAllowed(String keyName, String character, EditText field) {
        sendKeys(keyName);
        assertEquals(character, field.getText().toString());
        sendKeys("DEL");
    }

    @MediumTest
    private void assertCharacterNotAllowed(String keyName, EditText field) {
        sendKeys(keyName);
        assertEquals("", field.getText().toString());
        sendKeys("DEL");
    }

    @MediumTest
    public void testAddWatering() throws Throwable {
        setActivityInitialTouchMode(false);
        addWatering(new double[] { .25 });
        RainGaugeActivity activity = getActivity();
        TextView watering = (TextView) activity.findViewById(R.id.watering);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, 0.25), watering.getText().toString());

        sendKeys("0 PERIOD 2 5");
        final Button addWatering = (Button) activity.findViewById(R.id.add_manual_watering);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                addWatering.performClick();
            }
        });
        Thread.sleep(1000);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, 0.50), watering.getText().toString());
    }

    @MediumTest
    public void testBalanceWithNoRainfallOrWatering() {
        RainGaugeActivity activity = getActivity();
        TextView balance = (TextView) activity.findViewById(R.id.balance);
        assertEquals(String.format(NEGATIVE_AMOUNT_FORMAT, 1.00), balance.getText().toString());
    }

    @MediumTest
    public void testBalanceWithRainfall() throws Throwable {
        addRainfallToContentProvider(new double[] { .25 });
        RainGaugeActivity activity = getActivity();
        TextView balance = (TextView) activity.findViewById(R.id.balance);
        assertEquals(String.format(NEGATIVE_AMOUNT_FORMAT, 0.75), balance.getText().toString());
    }

    @MediumTest
    public void testBalanceWithWatering() throws Throwable {
        addWatering(new double[] { .25 });
        RainGaugeActivity activity = getActivity();
        TextView balance = (TextView) activity.findViewById(R.id.balance);
        assertEquals(String.format(NEGATIVE_AMOUNT_FORMAT, 0.75), balance.getText().toString());
    }

    @MediumTest
    public void testBalanceWithRainfallAndWatering() throws Throwable {
        addRainfallToContentProvider(new double[] { .32 });
        addWatering(new double[] { .25 });
        RainGaugeActivity activity = getActivity();
        TextView balance = (TextView) activity.findViewById(R.id.balance);
        assertEquals(String.format(NEGATIVE_AMOUNT_FORMAT, 0.43), balance.getText().toString());
    }

    @MediumTest
    public void testBalanceAfterAddingWatering() throws Throwable {
        addRainfallToContentProvider(new double[] { .32 });
        addWatering(new double[] { .25 });
        RainGaugeActivity activity = getActivity();
        TextView balance = (TextView) activity.findViewById(R.id.balance);
        assertEquals(String.format(NEGATIVE_AMOUNT_FORMAT, 0.43), balance.getText().toString());

        sendKeys("0 PERIOD 2 5");
        final Button addWatering = (Button) activity.findViewById(R.id.add_manual_watering);
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                addWatering.performClick();
            }
        });
        Thread.sleep(1000);
        assertEquals(String.format(NEGATIVE_AMOUNT_FORMAT, 0.18), balance.getText().toString());
    }

    @MediumTest
    public void testWateringInputNotLostOnOrientationChange() throws Throwable {
        RainGaugeActivity activity = getActivity();
        sendKeys("0 PERIOD 2 5");
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Thread.sleep(500);
        EditText wateringInput = (EditText) activity.findViewById(R.id.watering_amount);
        assertEquals("0.25", wateringInput.getText().toString());
    }

    /**
     * Adds the supplied daily rainfall amounts to the database, starts the
     * activity and asserts that the sum displayed in the activity is correct.
     * <p>
     * The amounts are assumed to be per-day, with the first amount being the
     * oldest.
     * </p>
     * <p>
     * Example: If rainfall is [ 0.12, 0.04 ], the 0.04 amount will be given a
     * timestamp for yesterday, and 0.12 will have a timestamp of two days ago.
     * </p>
     * 
     * @param rainfall the daily rainfall amounts to use
     */
    private void assertRainfall(double[] rainfall) {
        addRainfallToContentProvider(rainfall);
        double total = calculateTotalRainfallLastSevenDays(rainfall);

        // Start the Activity and give it time to initialize
        RainGaugeActivity activity = getActivity();
        getInstrumentation().waitForIdleSync();

        // Wait a moment to allow the data to be loaded
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TextView rainfallText = (TextView) activity.findViewById(R.id.rainfall);
        assertEquals(String.format(POSITIVE_AMOUNT_FORMAT, total), rainfallText.getText()
                .toString());
    }

    /**
     * Calculates the sum of the last seven rainfall amounts supplied
     * 
     * @param rainfall an array of daily rainfall amounts
     * @return the sum of the last seven
     */
    private double calculateTotalRainfallLastSevenDays(double[] rainfall) {
        double total = 0.0;

        if (rainfall.length == 0) {
            return total;
        }

        int index = rainfall.length - 1;
        int dayCount = 1;

        while (dayCount <= 7 && index >= 0) {
            total += rainfall[index];
            dayCount++;
            index--;
        }

        return total;
    }

    /**
     * Inserts the supplied daily rainfall amounts into the ContentProvider. The
     * last amount will have a timestamp representing yesterday, the
     * second-to-last (if more than one) will have a timestamp represneting two
     * days ago, and so on.
     * 
     * @param rainfall the daily rainfall amounts to add, oldest first
     */
    private void addRainfallToContentProvider(double[] rainfall) {

        if (rainfall.length == 0) {
            return;
        }

        int day = rainfall.length + 1;

        for (double amount : rainfall) {

            Calendar c = new GregorianCalendar();
            c.add(Calendar.DATE, -day); // number of days to add

            addRainfall(c.getTimeInMillis(), amount);

            day--;
        }
    }

    /**
     * Adds rainfall to the ContentProvider
     * 
     * @param timestamp
     * @param amount
     */
    private void addRainfall(long timestamp, double amount) {
        Observations observations;
        observations = Observations.createObservations(timestamp, amount);

        Logger.debug(TAG, observations.toString());

        Uri uri = mContext.getContentResolver()
                .insert(RainGaugeProviderContract.ObservationsTable.CONTENT_URI,
                        observations.toContentValues());
        assertNotNull(uri);
    }

    /**
     * Inserts the supplied daily watering amounts into the ContentProvider. The
     * last amount will have a timestamp representing yesterday, the
     * second-to-last (if more than one) will have a timestamp represneting two
     * days ago, and so on.
     * 
     * @param amounts the daily watering amounts to add, oldest first
     */
    private void addWatering(double[] amounts) {

        Watering watering;
        Uri uri;

        if (amounts.length == 0) {
            return;
        }

        int day = amounts.length + 1;

        for (double amount : amounts) {

            Calendar c = new GregorianCalendar();
            c.add(Calendar.DATE, -day); // number of days to add

            watering = Watering.createWatering(c.getTimeInMillis(), amount);

            Logger.debug(TAG, watering.toString());

            uri = mContext.getContentResolver()
                    .insert(RainGaugeProviderContract.WateringsTable.CONTENT_URI,
                            watering.toContentValues());
            assertNotNull(uri);

            day--;
        }
    }
}
