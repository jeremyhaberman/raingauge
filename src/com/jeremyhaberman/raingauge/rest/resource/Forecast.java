package com.jeremyhaberman.raingauge.rest.resource;


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
