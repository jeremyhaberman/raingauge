package com.jeremyhaberman.raingauge.processor.test;

import android.test.AndroidTestCase;

import com.jeremyhaberman.raingauge.processor.DefaultProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ObservationsProcessor;
import com.jeremyhaberman.raingauge.processor.ProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.service.WeatherService;

public class ObservationsProcessorFactoryTest extends AndroidTestCase {

	private ProcessorFactory mFactory;

	protected void setUp() throws Exception {
		super.setUp();
		mFactory = DefaultProcessorFactory.getInstance(getContext());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetProcessor() {
		ResourceProcessor processor = mFactory.getProcessor(WeatherService.ResourceType.OBSERVATIONS);
		assertTrue(processor instanceof ObservationsProcessor);
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
