
package com.jeremyhaberman.raingauge.adapter;

import android.database.ContentObserver;
import android.database.Cursor;
import android.widget.TextView;
import com.jeremyhaberman.raingauge.util.Logger;

public class WateringAdapter extends ContentObserver {

    private static final String TAG = WateringAdapter.class.getSimpleName();
    private TextView mTextView;
    private double mWatering;
    private Cursor mCursor;

    public WateringAdapter(TextView textView, Cursor cursor) {
        super(null);

        mTextView = textView;
        mCursor = cursor;

        setWatering();
    }

    private void setWatering() {

        mWatering = calculateWatering();
        mTextView.setText(formatWatering(mWatering));
        mTextView.invalidate();
    }

    @SuppressWarnings("deprecation")
    private double calculateWatering() {

        mCursor.requery();
        double recentWatering = 0.0;
        while (mCursor.moveToNext()) {
            recentWatering += mCursor.getDouble(0);
        }

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

    public void destroy() {
        mTextView = null;
        mCursor = null;
    }
}
