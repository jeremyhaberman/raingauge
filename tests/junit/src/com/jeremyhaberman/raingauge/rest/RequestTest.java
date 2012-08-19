package com.jeremyhaberman.raingauge.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class RequestTest extends TestCase {

	private static final URI TEST_URI = URI.create("http://test.com");

	private static final String HEADER_1_KEY = "key1";

	private static final List<String> HEADER_1_VALUE = buildHeaderValue("value1");

	@SmallTest
	public void testRequestWithNullURI() {
		try {
			new Request(null);
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@SmallTest
	public void testRequest() {
		Request request = new Request(TEST_URI);
		assertNotNull(request);
		assertEquals(request.getUri(), TEST_URI);
		Map<String, List<String>> actualHeaders = request.getHeaders();
		assertTrue(actualHeaders.size() == 0);
	}

	@SmallTest
	public void testAddHeader() {
		Request request = new Request(TEST_URI);
		request.addHeader(HEADER_1_KEY, HEADER_1_VALUE);

		Map<String, List<String>> headers = request.getHeaders();

		assertTrue(headers.size() == 1);
		List<String> header1ActualValue = headers.get(HEADER_1_KEY);
		assertHeaderValuesMatch(HEADER_1_VALUE, header1ActualValue);
	}

	private static List<String> buildHeaderValue(String... values) {
		List<String> headerValue = new ArrayList<String>();
		for (String val : values) {
			headerValue.add(val);
		}
		return headerValue;
	}

	public static void assertHeadersMatch(Map<String, List<String>> header1,
										  Map<String, List<String>> header2) {

		if (header1 == null && header2 == null) {
			return;
		}

		// same size
		assertEquals(header1.size(), header2.size());

		// same keys
		Set<String> keys1 = header1.keySet();
		Set<String> keys2 = header2.keySet();
		String[] keys1Sorted = keys1.toArray(new String[0]);
		String[] keys2Sorted = keys2.toArray(new String[0]);
		assertArraysContainSameValues(keys1Sorted, keys2Sorted);

		// same key values
		Iterator<String> keyIterator = keys1.iterator();
		String key = null;
		while (keyIterator.hasNext()) {
			key = keyIterator.next();

			List<String> header1Value = header1.get(key);
			List<String> header2Value = header2.get(key);
			assertHeaderValuesMatch(header1Value, header2Value);
		}
	}

	private static void assertHeaderValuesMatch(List<String> value1, List<String> value2) {

		// assert same size
		assertTrue(value1.size() == value2.size());

		// assert same values
		String[] value1Array = value1.toArray(new String[0]);
		String[] value2Array = value2.toArray(new String[0]);
		assertArraysContainSameValues(value1Array, value2Array);
	}

	private static void assertArraysContainSameValues(String[] array1, String[] array2) {

		Arrays.sort(array1);
		Arrays.sort(array2);
		for (int i = 0; i < array1.length; i++) {
			assertEquals(array1[i], array2[i]);
		}

	}

}

