package de.neemann.oscilloscope.signal.interpolate;

/**
 * A simple function
 */
public interface Func {
    /**
     * The function.
     *
     * @param x the argument
     * @return the function value
     */
    double f(double x);
}
