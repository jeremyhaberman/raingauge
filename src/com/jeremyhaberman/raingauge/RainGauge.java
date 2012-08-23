package com.jeremyhaberman.raingauge;

import java.lang.reflect.InvocationTargetException;

public class RainGauge {

	/**
	 * Broadcast action: show a notification in the status bar
	 * <p>
	 * Input: rainfall ({@link #EXTRA_RAINFALL})
	 * </p>
	 */
	public static final String ACTION_SHOW_NOTIFICATION = "com.jeremyhaberman.raingauge.ACTION_SHOW_NOTIFICATION";

	public static final String EXTRA_RAINFALL = "com.jeremyhaberman.raingauge.EXTRA_RAINFALL";
	
	private RainGauge() throws InvocationTargetException {
        throw new InvocationTargetException(new InstantiationException("Instantiation forbidden"));
    }

}
