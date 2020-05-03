package de.neemann.oscilloscope.signal;

/**
 * A signal
 */
public interface Signal {

    /**
     * The signal value
     *
     * @param t the time
     * @return the value
     */
    double v(double t);

}
