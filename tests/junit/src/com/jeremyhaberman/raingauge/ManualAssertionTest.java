
package com.jeremyhaberman.raingauge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test as one that requires manual assertion. For example, a test may
 * simply launch an activity but have no in-code assertions, requiring someone
 * look at the activity screen to verify it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ManualAssertionTest {
}
