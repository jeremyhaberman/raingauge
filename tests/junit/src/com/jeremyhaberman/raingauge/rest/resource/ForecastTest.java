package com.jeremyhaberman.raingauge.rest.resource;

import junit.framework.TestCase;

public class ForecastTest extends TestCase {

	public void testForecast() {
		long timestamp = System.currentTimeMillis();
		String dayForecast = "Partly cloudy";
		String nightForecast = "Mostly cloudy";
		Forecast forecast = Forecast.newForecast(timestamp, dayForecast, nightForecast);
		assertNotNull(forecast);
		assertEquals(timestamp, forecast.getTimestamp());
		assertEquals(dayForecast, forecast.getDayForecast());
		assertEquals(nightForecast, forecast.getNightForecast());
	}

	public void testEquals() {
		long timestamp = System.currentTimeMillis();
		String dayForecast = "Partly cloudy";
		String nightForecast = "Mostly cloudy";

		Forecast forecast1 = Forecast.newForecast(timestamp, dayForecast, nightForecast);
		Forecast forecast2 = Forecast.newForecast(++timestamp, dayForecast, nightForecast);

		assertTrue(forecast1.equals(forecast1));
		assertFalse(forecast1.equals(forecast2));
		assertFalse(forecast1.equals(null));
		assertFalse(forecast1.equals(new String()));
		assertFalse(forecast1.equals(Forecast.newForecast(timestamp, "Clear", nightForecast)));
		assertFalse(forecast1.equals(Forecast.newForecast(timestamp, dayForecast, "Thunderstorms")));
		assertFalse(forecast1.equals(Forecast.newForecast(timestamp, null, nightForecast)));
        assertFalse(forecast1.equals(Forecast.newForecast(timestamp, dayForecast, null)));

		forecast1 = Forecast.newForecast(timestamp, dayForecast, nightForecast);
		forecast2 = Forecast.newForecast(timestamp, dayForecast, nightForecast);
		assertEquals(forecast1, forecast2);
	}

	public void testHashCode() {

		long timestamp = System.currentTimeMillis();
		String dayForecast = "Partly cloudy";
		String nightForecast = "Mostly cloudy";

		Forecast forecast1 = Forecast.newForecast(timestamp, dayForecast, nightForecast);
		Forecast forecast2 = Forecast.newForecast(timestamp, dayForecast, nightForecast);
		Forecast forecast3 = Forecast.newForecast(++timestamp, dayForecast, nightForecast);
		Forecast forecast4 =
				Forecast.newForecast(System.currentTimeMillis(), "Mostly sunny", "Clear");

		assertTrue(forecast1.hashCode() == forecast1.hashCode());
		assertTrue(forecast1.hashCode() == forecast2.hashCode());
		assertFalse(forecast2.hashCode() == forecast3.hashCode());
		assertFalse(forecast2.hashCode() == forecast4.hashCode());
	}
}

