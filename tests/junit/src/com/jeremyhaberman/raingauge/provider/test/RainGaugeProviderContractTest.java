package com.jeremyhaberman.raingauge.provider.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RainGaugeProviderContractTest extends AndroidTestCase {

	@SmallTest
	public void testPrivateConstructors()
			throws NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {

		Class[] classes = new Class[]{RainGaugeProviderContract.class,
				RainGaugeProviderContract.ObservationsTable.class,
				RainGaugeProviderContract.WateringsTable.class,
				RainGaugeProviderContract.ForecastsTable.class};

		for (Class testClass : classes) {
			Constructor constructor = testClass.getDeclaredConstructor();
			try {
				constructor.newInstance();
				fail("Should have thrown IllegalAccessException");
			} catch (IllegalAccessException e) {
				assertTrue(true);
			}

			constructor.setAccessible(true);
			try {
				constructor.newInstance();
				fail("Should have thrown InvocationTargetException");
			} catch (InvocationTargetException e) {
				assertTrue(true);
			}
		}

	}
}
