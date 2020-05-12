package de.neemann.oscilloscope.signal.interpolate;

import junit.framework.TestCase;

public class InterpolateCubicTest extends TestCase {

    public void testSimple() {
        InterpolateCubic inter = new InterpolateCubic(0.1, 2, 201, Math::sqrt, (x) -> 1 / (2 * Math.sqrt(x)));
        System.out.println(inter.f(0));
        System.out.println(inter.f(1));
        System.out.println(inter.f(2));
        for (int i = 100; i < 2000; i++) {
            double x = i / 1000.0;
            assertEquals("x=" + x, Math.sqrt(x), inter.f(x), 1e-7);
        }
    }

}