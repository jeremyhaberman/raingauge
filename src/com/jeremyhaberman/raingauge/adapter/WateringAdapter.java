package com.jeremyhaberman.raingauge.adapter;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.widget.TextView;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;
import com.jeremyhaberman.raingauge.util.Logger;
import com.jeremyhaberman.raingauge.util.TimeUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class WateringAdapter extends ContentObserver {

	private static final String TAG = WateringAdapter.class.getSimpleName();
	private TextView mTextView;
	private double mWatering;

	public WateringAdapter(Handler handler, TextView textView) {
		super(handler);

		mTextView = textView;

		setWatering();
	}

	private void setWatering() {

		mWatering = calculateWatering();
		mTextView.setText(formatWatering(mWatering));
		mTextView.invalidate();
	}

	private double calculateWatering() {
		ContentResolver resolver = mTextView.getContext().getContentResolver();

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -8);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);

		Logger.debug(TAG, String.format("Loading waterings since %s", TimeUtil
				.format(cal.getTimeInMillis())));

		Cursor wateringCursor = resolver.query(RainGaugeProviderContract.WateringsTable.CONTENT_URI,
				new String[]{RainGaugeProviderContract.WateringsTable.AMOUNT},
				RainGaugeProviderContract.WateringsTable.TIMESTAMP + ">?",
				new String[]{Long.toString(cal.getTimeInMillis())}, null);

		double recentWatering = 0.0;
		while (wateringCursor.moveToNext()) {
			recentWatering += wateringCursor.getDouble(0);
		}

		wateringCursor.close();

		return recentWatering;
	}

	private String formatWatering(double inches) {
		return String.format("%.2f", inches) + " in";
	}

	@Override
	public void onChange(boolean selfChange) {
		Logger.debug(TAG, "onChange");
		setWatering();
	}

	@Override
	public boolean deliverSelfNotifications() {
		return true;
	}

	public double getWatering() {
		return mWatering;
	}
}
