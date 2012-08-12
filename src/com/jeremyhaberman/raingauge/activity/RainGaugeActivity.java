package com.jeremyhaberman.raingauge.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jeremyhaberman.raingauge.R;
import com.jeremyhaberman.raingauge.WeatherUpdateScheduler;
import com.jeremyhaberman.raingauge.adapter.RainfallAdapter;
import com.jeremyhaberman.raingauge.adapter.WateringAdapter;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RainGaugeActivity extends Activity {

	private static final String TAG = RainGaugeActivity.class.getSimpleName();

	private TextView mRainfallText;
	private TextView mWateringText;
	private TextView mBalanceText;
	private EditText mManualWateringAmountEditText;
	private double mBalance;
	private TextView mForecastText;

	private RainfallAdapter mRainfallAdapter;
	private WateringAdapter mWateringAdapter;

	private TextWatcher mWaterTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			calculateBalance();
			showBalance();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mRainfallText = (TextView) findViewById(R.id.rainfall);
		mWateringText = (TextView) findViewById(R.id.watering);
		mBalanceText = (TextView) findViewById(R.id.balance);
		mForecastText = (TextView) findViewById(R.id.forecast);

		mManualWateringAmountEditText = (EditText) findViewById(R.id.watering_amount);
		Button addManualWatering = (Button) findViewById(R.id.add_manual_watering);
		addManualWatering.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				runOnUiThread(new Runnable() {

					public void run() {
						String enteredAmount = mManualWateringAmountEditText.getText().toString();
						if (validWateringInput(enteredAmount)) {
							water(Float.parseFloat(enteredAmount));
							mManualWateringAmountEditText.setText("");
							calculateBalance();
							showBalance();
						}
					}
				});

			}
		});

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -8);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);

		String[] projection = new String[] { RainGaugeProviderContract.ObservationsTable.RAINFALL };
		Cursor rainfallCursor = getContentResolver().query(RainGaugeProviderContract.ObservationsTable.CONTENT_URI,
				projection, RainGaugeProviderContract.ObservationsTable.TIMESTAMP + ">?",
				new String[] { Long.toString(cal.getTimeInMillis()) }, null);
		startManagingCursor(rainfallCursor);
		mRainfallAdapter = new RainfallAdapter(mRainfallText, rainfallCursor);

		Cursor wateringCursor = getContentResolver().query(
				RainGaugeProviderContract.WateringsTable.CONTENT_URI,
				new String[] { RainGaugeProviderContract.WateringsTable.AMOUNT },
				RainGaugeProviderContract.WateringsTable.TIMESTAMP + ">?",
				new String[] { Long.toString(cal.getTimeInMillis()) }, null);
		startManagingCursor(wateringCursor);
		mWateringAdapter = new WateringAdapter(mWateringText, wateringCursor);
	}

	private boolean validWateringInput(String value) {

		try {
			Float.parseFloat(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!hasZip()) {
			startActivityForResult(new Intent(this, SetupActivity.class),
					SetupActivity.CONFIGURE_ZIP);
		} else {
			getContentResolver().registerContentObserver(ObservationsTable.CONTENT_URI, true,
					mRainfallAdapter);
			getContentResolver().registerContentObserver(WateringsTable.CONTENT_URI, true,
					mWateringAdapter);

			mRainfallText.addTextChangedListener(mWaterTextWatcher);
			mWateringText.addTextChangedListener(mWaterTextWatcher);

			mBalance = calculateBalance();
			showBalance();
			showForecast();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		getContentResolver().unregisterContentObserver(mRainfallAdapter);
		getContentResolver().unregisterContentObserver(mWateringAdapter);
		mRainfallText.removeTextChangedListener(mWaterTextWatcher);
		mWateringText.removeTextChangedListener(mWaterTextWatcher);
	}

	@Override
	protected void onDestroy() {
		mRainfallAdapter.destroy();
		mWateringAdapter.destroy();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SetupActivity.CONFIGURE_ZIP && resultCode == Activity.RESULT_OK) {
			sendBroadcast(new Intent(WeatherUpdateScheduler.ACTION_SCHEDULE_WEATHER_UPDATES));
		}
	}

	private void showForecast() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String forecast = prefs.getString(Observations.TODAYS_FORECAST, "unknown");
		mForecastText.setText(forecast);
	}

	private boolean hasZip() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int zip = prefs.getInt(Observations.ZIP_CODE, 0);
		return zip != 0;
	}

	private void showBalance() {
		String balanceText = "";
		if (mBalance > 0.0) {
			balanceText += "+";
		}
		balanceText += formatRainfall(mBalance);
		mBalanceText.setText(balanceText);
	}

	private double calculateBalance() {

		mBalance = -1.0 + mRainfallAdapter.getRainfall() + mWateringAdapter.getWatering();
		return mBalance;
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