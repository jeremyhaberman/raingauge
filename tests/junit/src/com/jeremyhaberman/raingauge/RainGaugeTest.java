/**
 * 
 */

package com.jeremyhaberman.raingauge;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class RainGaugeTest extends TestCase {

    @SmallTest
    public void testConstantValues() {
        assertEquals("com.jeremyhaberman.raingauge.ACTION_SHOW_NOTIFICATION",
                RainGauge.ACTION_SHOW_NOTIFICATION);
        assertEquals("com.jeremyhaberman.raingauge.EXTRA_RAINFALL", RainGauge.EXTRA_RAINFALL);
    }

    @SmallTest
    public void testConstructor() throws IllegalArgumentException, NoSuchMethodException,
            InstantiationException, InvocationTargetException, IllegalAccessException {
        
        Asserts.assertPrivateConstructor(RainGauge.class);
    }

}
