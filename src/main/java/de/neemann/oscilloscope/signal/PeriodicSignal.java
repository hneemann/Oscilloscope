package de.neemann.oscilloscope.signal;

/**
 * Used to define a periodic signal.
 */
public interface PeriodicSignal extends Signal {
    /**
     * @return the period in seconds
     */
    double period();

    /**
     * @return the mean value. Used to enable AC coupling
     */
    default double mean() {
        return 0;
    }
}
