package com.jeremyhaberman.raingauge;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.RainfallTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;

public class RainGaugeActivity extends Activity {
	private static final String TAG = null;
	private TextView mRainfallText;
	private TextView mWateringText;
	private TextView mBalanceText;
	private EditText mManualWateringAmoutEditText;
	private double mRainfall;
	private double mWatering;
	private double mBalance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mRainfallText = (TextView) findViewById(R.id.rainfall);
		mWateringText = (TextView) findViewById(R.id.watering);
		mBalanceText = (TextView) findViewById(R.id.balance);

		mManualWateringAmoutEditText = (EditText) findViewById(R.id.watering_amount);
		Button addManualWatering = (Button) findViewById(R.id.add_manual_watering);
		addManualWatering.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				runOnUiThread(new Runnable() {

					public void run() {
						water(Float.parseFloat(mManualWateringAmoutEditText.getText().toString()));
						mManualWateringAmoutEditText.setText("");
						loadWatering();
						calculateBalance();
						showBalance();
					}
				});

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!hasZip()) {
			startActivityForResult(new Intent(this, SetupActivity.class),
					SetupActivity.CONFIGURE_ZIP);
		} else {
			loadRainfall();
			loadWatering();
			showBalance();
			mBalance = calculateBalance();
			showBalance();
		}
	}

	private boolean hasZip() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int zip = prefs.getInt(Weather.ZIP_CODE, 0);
		return zip != 0;
	}

	private void showBalance() {
		String balanceText = "Balance: ";
		if (mBalance > 0.0) {
			balanceText += "+";
		}
		balanceText += formatRainfall(mBalance);
		mBalanceText.setText(balanceText);
	}

	private double calculateBalance() {

		mBalance = -1.0 + mRainfall + mWatering;
		return mBalance;
	}

	private double loadRainfall() {

		ContentResolver resolver = getContentResolver();
		Cursor rainfallCursor = resolver.query(RainfallTable.CONTENT_URI,
				new String[] { RainfallTable.RAINFALL }, null, null, RainfallTable.TIMESTAMP
						+ " DESC LIMIT 7");

		double recentRainfall = 0.0;
		while (rainfallCursor.moveToNext()) {
			recentRainfall += rainfallCursor.getDouble(0);
		}

		mRainfall = recentRainfall;
		mRainfallText.setText("Rainfall: " + formatRainfall(mRainfall));
		return mRainfall;
	}

	private double loadWatering() {

		ContentResolver resolver = getContentResolver();

		// SELECT * FROM statistics WHERE date BETWEEN datetime('now', ' -6
		// days') AND datetime('now', 'localtime')

		Calendar date = new GregorianCalendar();
		int day = date.get(Calendar.DATE);
		date.set(Calendar.DATE, day - 6);

		Cursor wateringCursor = resolver.query(WateringsTable.CONTENT_URI,
				new String[] { WateringsTable.AMOUNT }, WateringsTable.TIMESTAMP + ">?",
				new String[] { Long.toString(date.getTimeInMillis()) }, null);

		double recentWatering = 0.0;
		while (wateringCursor.moveToNext()) {
			recentWatering += wateringCursor.getDouble(0);
		}

		mWatering = recentWatering;
		mWateringText.setText("Watering: " + formatRainfall(mWatering));
		return mWatering;
	}

	private void water(float amount) {

		ContentValues values = new ContentValues();
		values.put(WateringsTable.TIMESTAMP, System.currentTimeMillis());
		values.put(WateringsTable.AMOUNT, amount);
		Uri uri = getContentResolver().insert(WateringsTable.CONTENT_ID_URI_BASE, values);

		Log.d(TAG, "inserted watering: " + uri.toString());
	}

	private String formatRainfall(double inches) {
		return String.format("%.2f", inches) + " in";
	}

}