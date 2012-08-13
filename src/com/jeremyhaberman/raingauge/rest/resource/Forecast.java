package com.jeremyhaberman.raingauge.rest.resource;

import android.content.ContentValues;
import android.database.Cursor;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;

public class Forecast implements Resource {

	public static final String ZIP_CODE = "com.jeremyhaberman.raingauge.ZIP_CODE";

	public static final String KEY_DAY_FORECAST = "dayForecast";
	public static final String KEY_NIGHT_FORECAST = "nightForecast";

	private long mTimestamp;
	private String mDayForecast;
	private String mNightForecast;

	private Forecast(long timestamp, String day, String night) {
		mTimestamp = timestamp;
		mDayForecast = day;
		mNightForecast = night;
	}

	public static Forecast newForecast(long timestamp, String day, String night) {
		return new Forecast(timestamp, day, night);
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public String getDayForecast() {
		return mDayForecast;
	}

	public String getNightForecast() {
		return mNightForecast;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(RainGaugeProviderContract.ForecastsTable.TIMESTAMP, mTimestamp);
		contentValues.put(RainGaugeProviderContract.ForecastsTable.DAY_FORECAST, mDayForecast);
		contentValues.put(RainGaugeProviderContract.ForecastsTable.NIGHT_FORECAST, mNightForecast);
		return contentValues;
	}

	/**
	 * Returns a new Observations object from the values in the given cursor.
	 * <p>Assumptions:</p>
	 * <ol>
	 * <li>The cursor is currently on the row for which you want to create a new Observations</li>
	 * <li>All Observations columns are present and in the order as-is in the DB</li>
	 * </ol>
	 * <p>WARNING: It does not close the cursor when finished.</p>
	 *
	 * @param cursor
	 * @return new Observations object
	 * @throws IllegalArgumentException if cursor is null or does not contain all ObservationsTable
	 *                                  columns
	 */
	public static Forecast fromCursor(Cursor cursor) {

		if (cursor == null) {
			throw new IllegalArgumentException("cursor is null");
		}

		if (cursor.getColumnCount() !=
				RainGaugeProviderContract.ForecastsTable.ALL_COLUMNS.length) {
			throw new IllegalArgumentException("cursor does not contain all columns");
		}


		long timestamp =
						cursor.getLong(RainGaugeProviderContract.ForecastsTable.TIMESTAMP_COLUMN_INDEX);
		String dayForecast =
				cursor.getString(RainGaugeProviderContract.ForecastsTable.DAY_FORECAST_COLUMN_INDEX);
		String nightForecast =
				cursor.getString(RainGaugeProviderContract.ForecastsTable.NIGHT_FORECAST_COLUMN_INDEX);



		return newForecast(timestamp, dayForecast, nightForecast);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Forecast forecast = (Forecast) o;

		if (mTimestamp != forecast.mTimestamp) {
			return false;
		}
		if (mDayForecast != null ? !mDayForecast.equals(forecast.mDayForecast) :
				forecast.mDayForecast != null) {
			return false;
		}
		if (mNightForecast != null ? !mNightForecast.equals(forecast.mNightForecast) :
				forecast.mNightForecast != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (mTimestamp ^ (mTimestamp >>> 32));
		result = 31 * result + (mDayForecast != null ? mDayForecast.hashCode() : 0);
		result = 31 * result + (mNightForecast != null ? mNightForecast.hashCode() : 0);
		return result;
	}
}
