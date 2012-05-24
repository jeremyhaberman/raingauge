package com.jeremyhaberman.raingauge;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kinvey.KCSClient;
import com.kinvey.KinveySettings;

public class RainGaugeActivity extends Activity {
	private static final String TAG = null;
	private TextView mRainfallText;
	private TextView mWateringText;
	private TextView mBalanceText;
	private EditText mManualWateringAmoutEditText;
	private float mRainfall;
	private float mWatering;
	private float mBalance;
	private KCSClient mKinvey;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// KinveySettings settings = new KinveySettings("kid1692",
		// "c1c676def2f94ba59c0af08c4ddec92b");
		// mKinvey = KCSClient.getInstance(this.getApplicationContext(),
		// settings);

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
			startActivityForResult(new Intent(this, SetupActivity.class), SetupActivity.CONFIGURE_ZIP);
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

	private float calculateBalance() {

		Calendar date = new GregorianCalendar();

		int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "Day of week: " + dayOfWeek);

		mBalance = -(dayOfWeek * 0.14f) + mRainfall + mWatering;
		return mBalance;
	}

	private float loadRainfall() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mRainfall = preferences.getFloat(Weather.WEEKLY_RAINFALL, 0.0f);
		mRainfallText.setText("Rainfall this week: " + formatRainfall(mRainfall));
		return mRainfall;
	}

	private float loadWatering() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mWatering = preferences.getFloat(Weather.WEEKLY_WATERING, 0.0f);
		mWateringText.setText("Watering this week: " + formatRainfall(mWatering));
		return mWatering;
	}

	private float water(float amount) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		float watering = preferences.getFloat(Weather.WEEKLY_WATERING, 0.0f);
		watering += amount;
		preferences.edit().putFloat(Weather.WEEKLY_WATERING, watering).commit();
		mWatering = watering;
		return watering;
	}

	private String formatRainfall(float inches) {
		return String.format("%.2f", inches) + " in";
	}

}