package de.neemann.oscilloscope.signal;

import junit.framework.TestCase;

public class InterpolateCubicTest extends TestCase {


    public void testSimple() {
        InterpolateCubic c = new InterpolateCubic();
        double period = 0.001;
        c.setValues(period, new double[]{1, 0, -1, 0});

        for (int i = 0; i <= 100; i++) {
            double t = period * i / 100;
            double v = c.v(t);
            assertTrue(v <= 1 && v >= -1);
        }

    }
}