package com.jeremyhaberman.raingauge.provider.test;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.jeremyhaberman.raingauge.Asserts;
import com.jeremyhaberman.raingauge.provider.RainGaugeProviderContract;

public class RainGaugeProviderContractTest extends TestCase {

	@SmallTest
	public void testPrivateConstructors()
			throws NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
	    
	    Asserts.assertPrivateConstructor(RainGaugeProviderContract.class);
	    Asserts.assertPrivateConstructor(RainGaugeProviderContract.ObservationsTable.class);
	    Asserts.assertPrivateConstructor(RainGaugeProviderContract.WateringsTable.class);
	    Asserts.assertPrivateConstructor(RainGaugeProviderContract.ForecastsTable.class);
	}
}

