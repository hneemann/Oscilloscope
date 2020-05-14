package de.neemann.oscilloscope.signal;

/**
 * Used to define a periodic signal.
 */
public interface PeriodicSignal {

    /**
     * A simple GND signal
     */
    PeriodicSignal GND = new PeriodicSignal() {
        @Override
        public double period() {
            return 0.01;
        }

        @Override
        public double v(double t) {
            return 0;
        }

        @Override
        public double mean() {
            return 0;
        }
    };

    /**
     * Returns the signals value
     *
     * @param t the current time
     * @return the value
     */
    double v(double t);

    /**
     * @return the period in seconds
     */
    double period();

    /**
     * @return the mean value. Used to enable AC coupling
     */
    double mean();

}
