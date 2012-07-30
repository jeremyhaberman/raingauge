package com.jeremyhaberman.raingauge.model.test;

import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.model.Watering;
import com.jeremyhaberman.raingauge.rest.resource.Observations;
import junit.framework.TestCase;

public class WateringTest extends TestCase {

	@SmallTest
	public void testCreateWatering() {
		long time = System.currentTimeMillis();
		double amount = 0.25;

		Watering watering = Watering.createWatering(time, amount);
		assertNotNull(watering);
		assertEquals(time, watering.getTimeStamp());
		assertEquals(amount, watering.getAmount());
	}

	@SmallTest
	public void testZeroAmount() {
		Watering watering = Watering.createWatering(System.currentTimeMillis(), 0);
		assertEquals(0.0, watering.getAmount());
	}

	@SmallTest
	public void testNegativeAmount() {
		try {
			Watering.createWatering(System.currentTimeMillis(), -1);
			fail("should have thrown illegal argument exception");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testEquals() {
		long timestamp = System.currentTimeMillis();
		double amount = 0.2;

		Watering watering1 = Watering.createWatering(timestamp, amount);
		Watering watering2 = Watering.createWatering(++timestamp, amount);
		assertTrue(watering1.equals(watering1));
		assertFalse(watering1.equals(watering2));
		assertFalse(watering1.equals(null));
		assertFalse(watering1.equals(new String()));
		assertFalse(watering1
				.equals(Observations.createObservations(System.currentTimeMillis(), 0.3)));

		watering1 = Watering.createWatering(timestamp, amount);
		watering2 = Watering.createWatering(timestamp, amount);
		assertEquals(watering1, watering2);
	}

	public void testHashCode() {

		long timestamp = System.currentTimeMillis();

		Watering watering1 = Watering.createWatering(timestamp, 0.5);
		Watering watering2 = Watering.createWatering(timestamp, 0.5);
		Watering watering3 = Watering.createWatering(++timestamp, 0.5);
		Watering watering4 = Watering
				.createWatering(System.currentTimeMillis(), 0.4);

		assertTrue(watering1.hashCode() == watering1.hashCode());
		assertTrue(watering1.hashCode() == watering2.hashCode());
		assertFalse(watering2.hashCode() == watering3.hashCode());
		assertFalse(watering2.hashCode() == watering4.hashCode());
	}
}

