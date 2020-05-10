package de.neemann.oscilloscope.signal;

/**
 * Base class of interpolation algorithms.
 */
public abstract class Interpolate extends PeriodicSignal {

    /**
     * Sets the values used for interpolation
     *
     * @param period the period
     * @param values the values
     */
    public abstract void setValues(double period, double[] values);
}
