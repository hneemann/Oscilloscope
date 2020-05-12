package de.neemann.oscilloscope.signal;

import junit.framework.TestCase;

public class InterpolateLinearCubicTest extends TestCase {


    public void testSimple() {
        double period = 0.001;
        InterpolateCubic c = new InterpolateCubic(period, new double[]{1, 0, -1, 0});

        for (int i = 0; i <= 100; i++) {
            double t = period * i / 100;
            double v = c.v(t);
            assertTrue(v <= 1 && v >= -1);
        }

    }
}