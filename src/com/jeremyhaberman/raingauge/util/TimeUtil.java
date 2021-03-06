package com.jeremyhaberman.raingauge.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {

	private TimeUtil() {} // prevent instantiation

	public static String format(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		c.add(Calendar.DATE, 1);  // number of days to add
		return "(" + sdf.format(c.getTime()) + ")";
	}
}

