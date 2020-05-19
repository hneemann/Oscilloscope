package de.neemann.oscilloscope.draw.elements;

/**
 * Helper to deal with doubles
 */
public final class DoubleHelper {

    private static final double EPS = 1e-8;

    private DoubleHelper() {
    }

    /**
     * Returns true if a and b are different.
     * This means that the difference is larger than 1e-8.
     *
     * @param a number a
     * @param b number b
     * @return true id a and b are different
     */
    public static boolean different(double a, double b) {
        return Math.abs(a - b) > EPS;
    }

    /**
     * Returns true if a and b are equal.
     * This means that the difference is smaller or equal to 1e-8.
     *
     * @param a number a
     * @param b number b
     * @return true id a and b are equal
     */
    public static boolean equal(double a, double b) {
        return !different(a, b);
    }

    /**
     * Returns the square of x
     *
     * @param x x
     * @return x*x
     */
    public static double sqr(double x) {
        return x * x;
    }

}
