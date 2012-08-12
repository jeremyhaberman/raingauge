package com.jeremyhaberman.raingauge.adapter;

import android.database.ContentObserver;
import android.database.Cursor;
import android.widget.TextView;
import com.jeremyhaberman.raingauge.util.Logger;

public class RainfallAdapter extends ContentObserver {

	private static final String TAG = RainfallAdapter.class.getSimpleName();
	private TextView mTextView;
	private double mRainfall;
	private Cursor mCursor;

	public RainfallAdapter(TextView textView, Cursor cursor) {
		super(null);

		mTextView = textView;
		mCursor = cursor;

		setRainfall();
	}

	private void setRainfall() {

		mRainfall = calculateRainfall();
		mTextView.setText(formatRainfall(mRainfall));
		mTextView.invalidate();
	}

	private double calculateRainfall() {

		mCursor.requery();

		double recentRainfall = 0.0;
		while (mCursor.moveToNext()) {
			recentRainfall += mCursor.getDouble(0);
		}

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

	public void destroy() {
		mTextView = null;
		mCursor = null;
	}
}

