package com.jeremyhaberman.raingauge.test;

import android.test.suitebuilder.annotation.SmallTest;
import com.jeremyhaberman.raingauge.Service;
import junit.framework.TestCase;

public class ServiceTest extends TestCase {

	@SmallTest
	public void testValues() {
		Service[] values = Service.values();
		assertEquals(5, values.length);

		assertEquals(Service.PROCESSOR_FACTORY, values[0]);
		assertEquals(Service.ALARM_SERVICE, values[1]);
		assertEquals(Service.REST_METHOD_FACTORY, values[2]);
		assertEquals(Service.NOTIFICATION_MANAGER, values[3]);
		assertEquals(Service.NOTIFICATION_HELPER, values[4]);
	}

	@SmallTest
	public void testValueOf() {
		assertEquals(Service.PROCESSOR_FACTORY, Service.valueOf("PROCESSOR_FACTORY"));
		assertEquals(Service.REST_METHOD_FACTORY, Service.valueOf("REST_METHOD_FACTORY"));
		assertEquals(Service.ALARM_SERVICE, Service.valueOf("ALARM_SERVICE"));
		assertEquals(Service.NOTIFICATION_MANAGER, Service.valueOf("NOTIFICATION_MANAGER"));
		assertEquals(Service.NOTIFICATION_HELPER, Service.valueOf("NOTIFICATION_HELPER"));
	}
}

