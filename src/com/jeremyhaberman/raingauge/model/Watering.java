
package com.jeremyhaberman.raingauge.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;

public class Watering {

    private long mTimestamp;
    private double mAmount;

    private Watering(long timestamp, double amount) {

        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be less than 0");
        }

        mTimestamp = timestamp;
        mAmount = amount;
    }

    /**
     * Creates a new Watering
     * 
     * @param timestamp when the watering occurred
     * @param amount the amount of water
     * @return new Watering
     * @throws IllegalArgumentException if amount < 0
     */
    public static Watering createWatering(long timestamp, double amount) {
        return new Watering(timestamp, amount);
    }

    public long getTimeStamp() {
        return mTimestamp;
    }

    public double getAmount() {
        return mAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Watering watering = (Watering) o;

        if (Double.compare(watering.mAmount, mAmount) != 0) {
            return false;
        }
        if (mTimestamp != watering.mTimestamp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (mTimestamp ^ (mTimestamp >>> 32));
        temp = mAmount != +0.0d ? Double.doubleToLongBits(mAmount) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RainGaugeProviderContract.WateringsTable.TIMESTAMP, mTimestamp);
        contentValues.put(RainGaugeProviderContract.WateringsTable.AMOUNT, mAmount);
        return contentValues;
    }

    /**
     * Returns a new Watering object from the values in the given cursor.
     * <p>
     * Assumptions:
     * </p>
     * <ol>
     * <li>The cursor is currently on the row for which you want to create a new
     * Watering</li>
     * <li>All Watering columns are present and in the order as-is in the DB</li>
     * </ol>
     * <p>
     * WARNING: It does not close the cursor when finished.
     * </p>
     * 
     * @param cursor
     * @return new Watering object
     * @throws IllegalArgumentException if cursor is null or does not contain
     *             all WateringsTable columns
     */
    public static Watering fromCursor(Cursor cursor) {

        if (cursor == null) {
            throw new IllegalArgumentException("cursor is null");
        }

        if (cursor.getColumnCount() != RainGaugeProviderContract.WateringsTable.ALL_COLUMNS.length) {
            throw new IllegalArgumentException("cursor does not contain all columns");
        }

        long timestamp =
                cursor.getLong(RainGaugeProviderContract.WateringsTable.TIMESTAMP_COLUMN_INDEX);
        double amount =
                cursor.getDouble(RainGaugeProviderContract.WateringsTable.AMOUNT_COLUMN_INDEX);
        return createWatering(timestamp, amount);
    }

}
