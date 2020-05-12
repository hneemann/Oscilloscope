package de.neemann.oscilloscope.signal.interpolate;

import junit.framework.TestCase;

public class InterpolateLinearTest extends TestCase {

    public void testSimple() {
        InterpolateLinear inter = new InterpolateLinear(0.1, 2, 201, Math::sqrt);
        System.out.println(inter.f(0));
        System.out.println(inter.f(1));
        System.out.println(inter.f(2));
        for (int i = 100; i < 2000; i++) {
            double x = i / 1000.0;
            assertEquals("x=" + x, Math.sqrt(x), inter.f(x), 1e-4);
        }
    }

}