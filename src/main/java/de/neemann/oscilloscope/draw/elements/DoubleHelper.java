package de.neemann.oscilloscope.draw.elements;

public final class DoubleHelper {

    private static final double EPS = 1e-8;

    private DoubleHelper() {
    }

    public static boolean different(double a, double b) {
        return Math.abs(a - b) > EPS;
    }
}
