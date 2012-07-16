package com.jeremyhaberman.raingauge.processor.test;

import android.test.AndroidTestCase;

import com.jeremyhaberman.raingauge.Service;
import com.jeremyhaberman.raingauge.ServiceManager;
import com.jeremyhaberman.raingauge.processor.DefaultProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ObservationsProcessor;
import com.jeremyhaberman.raingauge.processor.ProcessorFactory;
import com.jeremyhaberman.raingauge.processor.ResourceProcessor;
import com.jeremyhaberman.raingauge.rest.method.DefaultRestMethodFactory;
import com.jeremyhaberman.raingauge.service.WeatherService;
import com.jeremyhaberman.raingauge.util.TestUtil;

public class ObservationsProcessorFactoryTest extends AndroidTestCase {

	private ProcessorFactory mFactory;

	protected void setUp() throws Exception {
		super.setUp();
		mFactory = DefaultProcessorFactory.getInstance(getContext());
		ServiceManager serviceManager = ServiceManager.createServiceManager();
		serviceManager.loadService(Service.PROCESSOR_FACTORY, mFactory);
		serviceManager.loadService(Service.REST_METHOD_FACTORY, DefaultRestMethodFactory.getInstance(getContext()));
		ServiceManager.load(serviceManager);
	}

	protected void tearDown() throws Exception {
		TestUtil.resetServiceManager();
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
