/**
 * 
 */
package com.jeremyhaberman.raingauge;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

/**
 * 
 */
public class Asserts extends TestCase {

    /**
     * @param class1
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws InstantiationException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
    public static void assertPrivateConstructor(Class<?> c) throws NoSuchMethodException, IllegalArgumentException, InstantiationException, InvocationTargetException, IllegalAccessException {
        Constructor<?> constructor = c.getDeclaredConstructor();
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

}
