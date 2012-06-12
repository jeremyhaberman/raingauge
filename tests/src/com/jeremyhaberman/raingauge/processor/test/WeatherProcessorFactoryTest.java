package com.jeremyhaberman.raingauge.processor.test;

import android.test.AndroidTestCase;

import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.processor.WeatherProcessor;
import com.jeremyhaberman.raingauge.processor.WeatherProcessorFactory;
import com.jeremyhaberman.raingauge.service.WeatherService;

public class WeatherProcessorFactoryTest extends AndroidTestCase {

	private WeatherProcessorFactory mFactory;

	protected void setUp() throws Exception {
		super.setUp();
		mFactory = WeatherProcessorFactory.getInstance(getContext());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetProcessor() {
		ResourceProcessor processor = mFactory.getProcessor(WeatherService.ResourceType.RAINFALL);
		assertTrue(processor instanceof WeatherProcessor);
	}

	public void testGetProcessorWithInvalidType() {
		try {
			mFactory.getProcessor(null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	public void testGetInstance() {
		assertNotNull(mFactory);
	}

}
