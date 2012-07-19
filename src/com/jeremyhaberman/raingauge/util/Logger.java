package com.jeremyhaberman.raingauge.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Utility class for logging
 *
 * @author jeremy
 */
public class Logger {

	public static final int DEBUG = Log.DEBUG;
	public static final int INFO = Log.INFO;
	public static final int WARN = Log.WARN;
	public static final int ERROR = Log.ERROR;

	private static int mCurrentLevel = DEBUG;

	private static final String APP_LOG_TAG = "RainGauge";

	private static final int MESSAGE_CACHE_SIZE = 50;

	private static String[] sMessageCache = new String[MESSAGE_CACHE_SIZE];

	// Prevent instantiation
	private Logger() throws InvocationTargetException {
		throw new InvocationTargetException(new InstantiationException("Instantiation forbidden"));
	}

	/**
	 * Sets the current log level.  Must be DEBUG, INFO, WARN or ERROR; otherwise, the level will
	 * not be changed.
	 *
	 * @param level
	 */
	public static void setLevel(int level) {

		if (level == DEBUG || level == INFO || level == WARN || level == ERROR) {
			mCurrentLevel = level;
		}
	}

	public static int getLevel() {
		return mCurrentLevel;
	}

	public static boolean isEnabled(int level) {
		return level >= mCurrentLevel;
	}

	public static void debug(String tag, String message) {
		Log.d(APP_LOG_TAG, cache(formatMessage(tag, message)));
	}

	private static String cache(String message) {
		for (int i = sMessageCache.length - 1; i > 0; i--) {
			sMessageCache[i] = sMessageCache[i - 1];
		}
		sMessageCache[0] = message;
		return message;
	}

	public static void debug(String tag, String message, Throwable throwable) {
		Log.d(APP_LOG_TAG, cache(formatMessage(tag, message)));
	}

	public static void debug(String tag, String message, Intent intent) {
		debug(tag, message);
		debug(tag, "Intent action=" + intent.getAction());

		// log extras

		Bundle extras = intent.getExtras();

		if (extras != null) {
			Set<String> keys = extras.keySet();

			debug(tag, "  extras:");

			for (String key : keys) {
				debug(tag, "    " + key + "=" + extras.get(key));
			}
		}
	}

	public static void info(String tag, String message) {
		Log.i(APP_LOG_TAG, cache(formatMessage(tag, message)));
	}

	public static void error(String tag, String error) {
		Log.e(APP_LOG_TAG, cache(formatMessage(tag, error)));
	}

	public static void error(String tag, String error, Throwable throwable) {
		Log.e(APP_LOG_TAG, cache(formatMessage(tag, error)), throwable);
	}

	public static void warn(String tag, String message) {
		Log.w(APP_LOG_TAG, cache(formatMessage(tag, message)));
	}

	public static void warn(String tag, String message, Throwable throwable) {
		Log.w(APP_LOG_TAG, cache(formatMessage(tag, message)), throwable);
	}

	/**
	 * Formats a log message
	 *
	 * @param tag     message prefix, typically the requesting class name
	 * @param message message to write
	 * @return formatted string of the log message
	 */
	private static String formatMessage(String tag, String message) {
		StringBuilder builder = new StringBuilder();

		if (tag.length() > 20) {
			tag = tag.substring(0, 20);
		}

		tag = "[" + tag + "]";

		String prefix = String.format("%-22s ", tag);

		builder.append(prefix).append(message);
		return builder.toString();
	}

	public static String[] getMessageCache() {
		return sMessageCache;
	}

	public static void clearCache() {
		sMessageCache = new String[MESSAGE_CACHE_SIZE];
	}

}
