
package com.jeremyhaberman.raingauge.rest.resource;

import junit.framework.TestCase;

public class ObservationsTest extends TestCase {

    public void testObservations() {
        Observations observations = Observations
                .createObservations(System.currentTimeMillis(), 0.5);
        assertNotNull(observations);
    }

    public void testNegativeRainfall() {
        try {
            Observations.createObservations(System.currentTimeMillis(), -1);
            fail("should have thrown illegal argument exception");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    public void testGetRainfall() {
        double expected = 0.5;
        Observations observations = Observations.createObservations(System.currentTimeMillis(),
                expected);

        assertEquals(expected, observations.getRainfall());
    }

    public void testEquals() {
        long timestamp = System.currentTimeMillis();
        double rainfall = 0.2;
        Observations observations1 = Observations.createObservations(timestamp, rainfall);
        Observations observations2 = Observations.createObservations(++timestamp, rainfall);
        assertTrue(observations1.equals(observations1));
        assertFalse(observations1.equals(observations2));
        assertFalse(observations1.equals(null));
        assertFalse(observations1.equals(new String()));
        assertFalse(observations1.equals(Observations.createObservations(
                System.currentTimeMillis(), 0.3)));

        observations1 = Observations.createObservations(timestamp, rainfall);
        observations2 = Observations.createObservations(timestamp, rainfall);
        assertEquals(observations1, observations2);
    }

    public void testHashCode() {

        long timestamp = System.currentTimeMillis();

        Observations observations1 = Observations.createObservations(timestamp, 0.5);
        Observations observations2 = Observations.createObservations(timestamp, 0.5);
        Observations observations3 = Observations.createObservations(++timestamp, 0.5);
        Observations observations4 = Observations.createObservations(System.currentTimeMillis(),
                0.4);

        assertTrue(observations1.hashCode() == observations1.hashCode());
        assertTrue(observations1.hashCode() == observations2.hashCode());
        assertFalse(observations2.hashCode() == observations3.hashCode());
        assertFalse(observations2.hashCode() == observations4.hashCode());
    }
}
