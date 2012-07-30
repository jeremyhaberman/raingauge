package com.jeremyhaberman.raingauge.rest;

import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.TestCase;

/**
 * This exists to satisfy Emma
 */
public class MethodTest extends TestCase {
	
	@SmallTest
	public void testValueOf() {
		assertEquals(Method.GET, Method.valueOf("GET"));
		assertEquals(Method.POST, Method.valueOf("POST"));
	}
}

