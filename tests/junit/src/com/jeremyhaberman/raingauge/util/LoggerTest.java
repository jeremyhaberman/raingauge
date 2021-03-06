package com.jeremyhaberman.raingauge.util;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import junit.framework.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LoggerTest extends AndroidTestCase {

	private static final String TEST_TAG = "TestTag";
	private static final String TEST_MESSAGE = "Test message";
	private static final String TEST_EXCEPTION_DETAIL_MESSAGE = "Exception detail";
	private static final String TEST_ACTION = "com.jeremyhaberman.raingauge.TEST_ACTION";
	private static final String TEST_EXTRA_NAME = "testExtraName";
	private static final String TEST_EXTRA_VALUE = "testExtraValue";
	private static final Exception TEXT_EXCEPTION = new Exception(TEST_EXCEPTION_DETAIL_MESSAGE);

	@SmallTest
	public void testPrivateConstructor()
			throws NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
		Constructor<?> constructor = Logger.class.getDeclaredConstructor();
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

	@SmallTest
	public void testIsEnabled() {

		Logger.setLevel(Logger.DEBUG);
		assertTrue(Logger.isEnabled(Logger.DEBUG));
		assertTrue(Logger.isEnabled(Logger.INFO));
		assertTrue(Logger.isEnabled(Logger.WARN));
		assertTrue(Logger.isEnabled(Logger.ERROR));

		Logger.setLevel(Logger.INFO);
		assertFalse(Logger.isEnabled(Logger.DEBUG));
		assertTrue(Logger.isEnabled(Logger.INFO));
		assertTrue(Logger.isEnabled(Logger.WARN));
		assertTrue(Logger.isEnabled(Logger.ERROR));

		Logger.setLevel(Logger.WARN);
		assertFalse(Logger.isEnabled(Logger.DEBUG));
		assertFalse(Logger.isEnabled(Logger.INFO));
		assertTrue(Logger.isEnabled(Logger.WARN));
		assertTrue(Logger.isEnabled(Logger.ERROR));

		Logger.setLevel(Logger.ERROR);
		assertFalse(Logger.isEnabled(Logger.DEBUG));
		assertFalse(Logger.isEnabled(Logger.INFO));
		assertFalse(Logger.isEnabled(Logger.WARN));
		assertTrue(Logger.isEnabled(Logger.ERROR));
	}

	@SmallTest
	public void testSetLevel() {
		Logger.setLevel(Logger.WARN);
		assertTrue(Logger.getLevel() == Logger.WARN);
		Logger.setLevel(0);
		assertTrue(Logger.getLevel() == Logger.WARN);
	}

	@MediumTest
	public void testInfo() {
		Logger.setLevel(Logger.INFO);
		Logger.clearCache();

		Logger.info(TEST_TAG, TEST_MESSAGE);
		String[] output = Logger.getMessageCache();

		String line = output[0];

		Assert.assertTrue(line.matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
	}

	@MediumTest
	public void testWarn() {
		Logger.setLevel(Logger.WARN);
		Logger.clearCache();

		Logger.warn(TEST_TAG, TEST_MESSAGE);
		String[] output = Logger.getMessageCache();

		String line = output[0];

		Assert.assertTrue(line.matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
	}

	@MediumTest
	public void testWarnWithThrowable() {
		Logger.setLevel(Logger.WARN);
		Logger.clearCache();

		Logger.warn(TEST_TAG, TEST_MESSAGE, TEXT_EXCEPTION);
		String[] output = Logger.getMessageCache();

		String line = output[0];

		Assert.assertTrue(line.matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
	}

	@MediumTest
	public void testErrorWithThrowable() {
		Logger.setLevel(Logger.ERROR);
		Logger.clearCache();

		Logger.error(TEST_TAG, TEST_MESSAGE, TEXT_EXCEPTION);
		String[] output = Logger.getMessageCache();

		String line = output[0];

		Assert.assertTrue(line.matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
	}

	@MediumTest
	public void testDebugWithThrowable() {
		Logger.setLevel(Logger.DEBUG);
		Logger.clearCache();
		Logger.debug(TEST_TAG, TEST_MESSAGE, new Exception(TEST_EXCEPTION_DETAIL_MESSAGE));
		String[] output = Logger.getMessageCache();

		String line = output[0];

		Assert.assertTrue(line.matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
	}

	@SmallTest
	public void testDebugWithIntentWithExtras() {
		Logger.setLevel(Logger.DEBUG);
		Logger.clearCache();

		Intent intent = new Intent(TEST_ACTION);
		intent.putExtra(TEST_EXTRA_NAME, TEST_EXTRA_VALUE);

		Logger.debug(TEST_TAG, TEST_MESSAGE, intent);
		String[] output = Logger.getMessageCache();

		for (int i = 0; i < output.length; i++) {
			System.out.println(i + ": " + output[i]);
		}

		Assert.assertTrue(output[3].matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
		Assert.assertTrue(output[2]
				.matches("\\[" + TEST_TAG + "\\]              Intent action=" + TEST_ACTION));
		Assert.assertTrue(output[1].matches("\\[" + TEST_TAG + "\\]                extras:"));
		Assert.assertTrue(output[0].matches(
				"\\[" + TEST_TAG + "\\]                  " + TEST_EXTRA_NAME + "=" +
						TEST_EXTRA_VALUE));
	}

	@SmallTest
	public void testDebugWithIntentWithNoExtras() {
		Logger.setLevel(Logger.DEBUG);
		Logger.clearCache();

		Intent intent = new Intent(TEST_ACTION);

		Logger.debug(TEST_TAG, TEST_MESSAGE, intent);
		String[] output = Logger.getMessageCache();

		for (int i = 0; i < output.length; i++) {
			System.out.println(i + ": " + output[i]);
		}

		Assert.assertTrue(output[1].matches("\\[" + TEST_TAG + "\\]              " + TEST_MESSAGE));
		Assert.assertTrue(output[0].matches(
				"\\[" + TEST_TAG + "\\]              Intent action=" + TEST_ACTION));
	}
}

