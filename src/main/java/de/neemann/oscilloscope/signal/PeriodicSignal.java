package de.neemann.oscilloscope.signal;

/**
 * Used to define a periodic signal.
 */
public abstract class PeriodicSignal extends Signal {

    /**
     * A simple GND signal
     */
    public static final PeriodicSignal GND = new PeriodicSignal() {
        @Override
        public double period() {
            return 1;
        }

        @Override
        public double v(double t) {
            return 0;
        }
    };

    /**
     * @return the period in seconds
     */
    public abstract double period();

    /**
     * @return the mean value. Used to enable AC coupling
     */
    public double mean() {
        return 0;
    }
}
