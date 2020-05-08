package de.neemann.oscilloscope.signal;

/**
 * Used to define a periodic signal.
 */
public interface PeriodicSignal extends Signal {

    /**
     * A simple GND signal
     */
    PeriodicSignal GND = new PeriodicSignal() {
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
    double period();

    /**
     * @return the mean value. Used to enable AC coupling
     */
    default double mean() {
        return 0;
    }
}
