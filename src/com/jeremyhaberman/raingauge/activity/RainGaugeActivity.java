package com.jeremyhaberman.raingauge.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.WateringsTable;
import com.jeremyhaberman.raingauge.rest.resource.Observations;

public class RainGaugeActivity extends Activity {

	private static final String TAG = RainGaugeActivity.class.getSimpleName();

	private TextView mRainfallText;
	private TextView mWateringText;
	private TextView mBalanceText;
	private EditText mManualWateringAmountEditText;
	private double mBalance;
	private TextView mForecastText;

	private Handler mHandler = new Handler();
	private RainfallAdapter mRainfallAdapter;
	private WateringAdapter mWateringAdapter;

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
						water(Float.parseFloat(mManualWateringAmountEditText.getText().toString()));
						mManualWateringAmountEditText.setText("");
						calculateBalance();
						showBalance();
					}
				});

			}
		});

		mRainfallAdapter = new RainfallAdapter(mHandler, mRainfallText);
		mWateringAdapter = new WateringAdapter(mHandler, mWateringText);
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