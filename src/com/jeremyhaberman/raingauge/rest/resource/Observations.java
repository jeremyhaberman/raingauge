package com.jeremyhaberman.raingauge.rest.resource;

import android.content.ContentValues;
import android.database.Cursor;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract.ObservationsTable;
import org.json.JSONException;
import org.json.JSONObject;

public class Observations implements Resource {

	private static final String TAG = Observations.class.getSimpleName();

	public static final String WEEKLY_RAINFALL = "com.jeremyhaberman.raingauge.WEEKLY_RAINFALL";
	public static final String ZIP_CODE = "com.jeremyhaberman.raingauge.ZIP_CODE";
	public static final String TODAYS_FORECAST = "todaysForecast";

	private long mTimestamp;
	private double mRainfall;

	private Observations(long timestamp, double rainfall) {

		if (rainfall < 0) {
			throw new IllegalArgumentException("Rainfall cannot be less than 0");
		}

		mTimestamp = timestamp;
		mRainfall = rainfall;
	}

	/**
	 * Creates a new Observations
	 *
	 * @param rain the amount of rainfall
	 * @return new Observations
	 * @throws IllegalArgumentException if rain < 0
	 */
	public static Observations createObservations(long timestamp, double rain) {
		return new Observations(timestamp, rain);
	}

	public long getTimeStamp() {
		return mTimestamp;
	}

	public double getRainfall() {
		return mRainfall;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Observations that = (Observations) o;

		if (Double.compare(that.mRainfall, mRainfall) != 0) {
			return false;
		}
		if (mTimestamp != that.mTimestamp) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (int) (mTimestamp ^ (mTimestamp >>> 32));
		temp = mRainfall != +0.0d ? Double.doubleToLongBits(mRainfall) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("timestamp", mTimestamp);
			obj.put("rainfall", mRainfall);
			return obj.toString();
		} catch (JSONException e) {
			return super.toString();
		}
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ObservationsTable.TIMESTAMP, mTimestamp);
		contentValues.put(ObservationsTable.RAINFALL, mRainfall);
		return contentValues;
	}

	/**
	 * Returns a new Observations object from the values in the given cursor.
	 * <p>Assumptions:</p>
	 * <ol>
	 *     <li>The cursor is currently on the row for which you want to create a new Observations</li>
	 *     <li>All Observations columns are present and in the order as-is in the DB</li>
	 * </ol>
	 * <p>WARNING: It does not close the cursor when finished.</p>
	 *
	 * @param cursor
	 * @return new Observations object
	 * @throws IllegalArgumentException if cursor is null or does not contain all ObservationsTable
	 *                                  columns
	 */
	public static Observations fromCursor(Cursor cursor) {

		if (cursor == null) {
			throw new IllegalArgumentException("cursor is null");
		}

		if (cursor.getColumnCount() != ObservationsTable.ALL_COLUMNS.length) {
			throw new IllegalArgumentException("cursor does not contain all columns");
		}

		long timestamp = cursor.getLong(ObservationsTable.TIMESTAMP_COLUMN_INDEX);
		double rainfall = cursor.getDouble(ObservationsTable.RAINFALL_COLUMN_INDEX);
		return createObservations(timestamp, rainfall);
	}
}
