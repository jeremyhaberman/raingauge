package com.jeremyhaberman.raingauge.rest.resource;

public class Observations implements Resource {

	private static final String TAG = Observations.class.getSimpleName();
	public static final String WEEKLY_RAINFALL = "com.jeremyhaberman.raingauge.WEEKLY_RAINFALL";
	public static final String ZIP_CODE = "com.jeremyhaberman.raingauge.ZIP_CODE";
	public static final String TODAYS_FORECAST = "todaysForecast";

	private double mRainfall;

	private Observations(double rainfall) {

		if (rainfall < 0) {
			throw new IllegalArgumentException("Rainfall cannot be less than 0");
		}

		mRainfall = rainfall;
	}

	/**
	 * Creates a new Observations
	 *
	 * @param rain the amount of rainfall
	 * @return new Observations
	 * @throws IllegalArgumentException if rain < 0
	 */
	public static Observations createObservations(double rain) {
		return new Observations(rain);
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

		return true;
	}

	@Override
	public int hashCode() {
		long temp = mRainfall != +0.0d ? Double.doubleToLongBits(mRainfall) : 0L;
		return (int) (temp ^ (temp >>> 32));
	}
}
