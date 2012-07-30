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

public class RainfallAdapter extends ContentObserver {

	private static final String TAG = RainfallAdapter.class.getSimpleName();
	private TextView mTextView;
	private double mRainfall;

	public RainfallAdapter(Handler handler, TextView textView) {
		super(handler);

		mTextView = textView;

		setRainfall();
	}

	private void setRainfall() {

		mRainfall = calculateRainfall();
		mTextView.setText(formatRainfall(mRainfall));
		mTextView.invalidate();
	}

	private double calculateRainfall() {
		ContentResolver resolver = mTextView.getContext().getContentResolver();

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -8);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);

		Logger.debug(TAG, String.format("Loading rainfall since %s", TimeUtil
				.format(cal.getTimeInMillis())));

		Cursor rainfallCursor =
				resolver.query(RainGaugeProviderContract.ObservationsTable.CONTENT_URI,
						new String[]{RainGaugeProviderContract.ObservationsTable.RAINFALL},
						RainGaugeProviderContract.ObservationsTable.TIMESTAMP + ">?",
						new String[]{Long.toString(cal.getTimeInMillis())}, null);

		double recentRainfall = 0.0;
		while (rainfallCursor.moveToNext()) {
			recentRainfall += rainfallCursor.getDouble(0);
		}
		rainfallCursor.close();

		return recentRainfall;
	}

	private String formatRainfall(double inches) {
		return String.format("%.2f", inches) + " in";
	}

	@Override
	public void onChange(boolean selfChange) {
		Logger.debug(TAG, "onChange");
		setRainfall();
	}

	@Override
	public boolean deliverSelfNotifications() {
		return true;
	}

	public double getRainfall() {
		return mRainfall;
	}
}

