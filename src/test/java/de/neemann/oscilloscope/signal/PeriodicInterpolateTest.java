package de.neemann.oscilloscope.signal;

import junit.framework.TestCase;

public class PeriodicInterpolateTest extends TestCase {

    public void testSimple() {
        int points = 200;
        double[] values = new double[points];
        for (int i = 0; i < points; i++)
            values[i] = Math.sin(2 * Math.PI * i / points);
        PeriodicInterpolate pe = new PeriodicInterpolate(2 * Math.PI, values);

        for (int i = 0; i < points * 10; i++) {
            double t = 2 * Math.PI * i / (points + 13);
            assertEquals("" + i, Math.sin(t), pe.v(t), 2e-4);
        }

        assertEquals(0, pe.mean(), 1e-5);
    }

}