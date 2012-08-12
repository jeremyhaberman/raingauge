package com.jeremyhaberman.raingauge.activity.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * adb shell am instrument -w -e class com.jeremyhaberman.raingauge.activity.test.RainGaugeActivityTest com.jeremyhaberman.raingauge.tests/android.test.InstrumentationTestRunner
 */
public class RainGaugeActivityTest extends ActivityInstrumentationTestCase2<RainGaugeActivity> {

	private static final String TAG = RainGaugeActivityTest.class.getSimpleName();

	private static final int ZIP = 55417;
	private Context mContext;

	public RainGaugeActivityTest() {
		super(RainGaugeActivity.class);
	}

	public RainGaugeActivityTest(Class<RainGaugeActivity> activityClass) {
		super(activityClass);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mContext = getInstrumentation().getTargetContext();

		TestUtil.clearContentProvider(mContext);
		setZip(ZIP);
	}

	@Override
	protected void tearDown() throws Exception {
		TestUtil.clearContentProvider(mContext);
		super.tearDown();
	}

	private void setZip(int zip) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		preferences.edit().putInt(Observations.ZIP_CODE, zip).commit();
	}

	public void testPreconditions() throws InterruptedException {

		RainGaugeActivity activity = getActivity();
		View root = activity.findViewById(R.id.main);
		TextView rainfallLabel = (TextView) activity.findViewById(R.id.rainfall_label);
		TextView rainfallText = (TextView) activity.findViewById(R.id.rainfall);
		TextView wateringText = (TextView) activity.findViewById(R.id.watering);
		TextView balanceText = (TextView) activity.findViewById(R.id.balance);
		EditText manualWateringAmountEditText =
				(EditText) activity.findViewById(R.id.watering_amount);
		TextView forecastText = (TextView) activity.findViewById(R.id.forecast);

		assertNotNull(activity);
		assertNotNull(rainfallText);
		assertNotNull(wateringText);
		assertNotNull(balanceText);
		assertNotNull(manualWateringAmountEditText);
		assertNotNull(forecastText);

		ViewAsserts.assertOnScreen(root, rainfallText);
		ViewAsserts.assertOnScreen(root, wateringText);
		ViewAsserts.assertOnScreen(root, balanceText);
		ViewAsserts.assertOnScreen(root, manualWateringAmountEditText);
		ViewAsserts.assertOnScreen(root, forecastText);
	}

	public void testZeroDaysOfRainfall() {
		assertRainfall(new double[] { });
	}

	public void testOneDayOfRainfall() {
		assertRainfall(new double[] { 0.72 });
	}

	public void testTwoDaysOfRainfallWithOneZero() {
		assertRainfall(new double[] { 0.72, 0.0 });
	}

	public void testTwoDaysOfRainfall() {
		assertRainfall(new double[] { 0.72, 0.21 });
	}

	public void testSevenDaysOfRainfall() {
		assertRainfall(new double[] { 0.72, 0.21, 0.0, 1.2, 0.14, 0.72, 0.0 });
	}

	public void testEightDaysOfRainfall() {
		assertRainfall(new double[] { 0.72, 0.21, 0.0, 1.2, 0.14, 0.72, 0.0, .43 });
	}

	public void testRainfallChangeWhileActivityInForeground() throws InterruptedException {
		addRainfall(new double[] { 0.72 });
		RainGaugeActivity activity = getActivity();
		TextView rainfallText = (TextView) activity.findViewById(R.id.rainfall);
		assertEquals(String.format("%.2f in", 0.72), rainfallText.getText().toString());
		addRainfall(new double[] { 0.21 });
		Thread.sleep(1000);
		assertEquals(String.format("%.2f in", 0.93), rainfallText.getText().toString());
	}

	public void testAddWateringWithoutValue() throws Throwable {
		setActivityInitialTouchMode(false);
		addWatering(new double[] { .25 });
		RainGaugeActivity activity = getActivity();
		TextView watering = (TextView) activity.findViewById(R.id.watering);
		assertEquals(String.format("%.2f in", 0.25), watering.getText().toString());

		final Button addWatering = (Button) activity.findViewById(R.id.add_manual_watering);
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				addWatering.performClick();
			}
		});
		Thread.sleep(1000);
		assertEquals(String.format("%.2f in", 0.25), watering.getText().toString());
	}

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

	private void assertCharacterAllowed(String keyName, String character, EditText field) {
		sendKeys(keyName);
		assertEquals(character, field.getText().toString());
		sendKeys("DEL");
	}

	private void assertCharacterNotAllowed(String keyName, EditText field) {
		sendKeys(keyName);
		assertEquals("", field.getText().toString());
		sendKeys("DEL");
	}

	public void testAddWatering() throws Throwable {
		setActivityInitialTouchMode(false);
		addWatering(new double[] { .25 });
		RainGaugeActivity activity = getActivity();
		TextView watering = (TextView) activity.findViewById(R.id.watering);
		assertEquals(String.format("%.2f in", 0.25), watering.getText().toString());

		sendKeys("0 PERIOD 2 5");
		final Button addWatering = (Button) activity.findViewById(R.id.add_manual_watering);
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				addWatering.performClick();
			}
		});
		Thread.sleep(1000);
		assertEquals(String.format("%.2f in", 0.50), watering.getText().toString());
	}

	public void testBalanceWithNoRainfallOrWatering() {
		RainGaugeActivity activity = getActivity();
		TextView balance = (TextView) activity.findViewById(R.id.balance);
		assertEquals(String.format("-%.2f in", 1.00), balance.getText().toString());
	}

	public void testBalanceWithRainfall() throws Throwable {
		addRainfall(new double[] { .25 });
		RainGaugeActivity activity = getActivity();
		TextView balance = (TextView) activity.findViewById(R.id.balance);
		assertEquals(String.format("-%.2f in", 0.75), balance.getText().toString());
	}

	public void testBalanceWithWatering() throws Throwable {
		addWatering(new double[] { .25 });
		RainGaugeActivity activity = getActivity();
		TextView balance = (TextView) activity.findViewById(R.id.balance);
		assertEquals(String.format("-%.2f in", 0.75), balance.getText().toString());
	}

	public void testBalanceWithRainfallAndWatering() throws Throwable {
		addRainfall(new double[] { .32 });
		addWatering(new double[] { .25 });
		RainGaugeActivity activity = getActivity();
		TextView balance = (TextView) activity.findViewById(R.id.balance);
		assertEquals(String.format("-%.2f in", 0.43), balance.getText().toString());
	}

	public void testBalanceAfterAddingWatering() throws Throwable {
		addRainfall(new double[] { .32 });
		addWatering(new double[] { .25 });
		RainGaugeActivity activity = getActivity();
		TextView balance = (TextView) activity.findViewById(R.id.balance);
		assertEquals(String.format("-%.2f in", 0.43), balance.getText().toString());

		sendKeys("0 PERIOD 2 5");
		final Button addWatering = (Button) activity.findViewById(R.id.add_manual_watering);
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				addWatering.performClick();
			}
		});
		Thread.sleep(1000);
		assertEquals(String.format("-%.2f in", 0.18), balance.getText().toString());
	}

	public void testWateringInputNotLostOnOrientationChange() throws Throwable {
		RainGaugeActivity activity = getActivity();
		sendKeys("0 PERIOD 2 5");
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Thread.sleep(500);
		EditText wateringInput = (EditText) activity.findViewById(R.id.watering_amount);
		assertEquals("0.25", wateringInput.getText().toString());
	}


	private void assertRainfall(double[] rainfall) {
		addRainfall(rainfall);
		double total = getTotalRainfallLastSevenDays(rainfall);
		RainGaugeActivity activity = getActivity();
		getInstrumentation().waitForIdleSync();
		TextView rainfallText = (TextView) activity.findViewById(R.id.rainfall);
		assertEquals(String.format("%.2f in", total), rainfallText.getText().toString());
	}

	private double getTotalRainfallLastSevenDays(double[] rainfall) {
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

	private void addRainfall(double[] amounts) {

		if (amounts.length == 0) {
			return;
		}

		int day = amounts.length + 1;

		for (double amount : amounts) {

			Calendar c = new GregorianCalendar();
			c.add(Calendar.DATE, -day);  // number of days to add

			addRainfall(c.getTimeInMillis(), amount);

			day--;
		}
	}

	private void addRainfall(long timestamp, double amount) {
		Observations observations;
		observations = Observations.createObservations(timestamp, amount);

		Logger.debug(TAG, observations.toString());

		Uri uri = mContext.getContentResolver()
				.insert(RainGaugeProviderContract.ObservationsTable.CONTENT_URI,
						observations.toContentValues());
		assertNotNull(uri);
	}

	private void addWatering(double[] amounts) {

		Watering watering;
		Uri uri;

		if (amounts.length == 0) {
			return;
		}

		int day = amounts.length + 1;

		for (double amount : amounts) {

			Calendar c = new GregorianCalendar();
			c.add(Calendar.DATE, -day);  // number of days to add

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


