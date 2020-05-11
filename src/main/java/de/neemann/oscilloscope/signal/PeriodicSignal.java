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

    /**
     * Used to get the sine parameters.
     * Returns null if signal ist not a sine signal.
     *
     * @return the sine parameters
     */
    public SinParams getSinParams() {
        return null;
    }

    /**
     * A sine signal description
     */
    public static class SinParams {
        private final double ampl;
        private final double omega;
        private final double phase;
        private final double offset;

        /**
         * Describes a sine function.
         * the function is ampl*sin(omega*t+phase)+offset.
         *
         * @param ampl   the amplitude
         * @param offset the offset
         * @param omega  omega
         * @param phase  the phase
         */
        public SinParams(double ampl, double offset, double omega, double phase) {
            this.ampl = ampl;
            this.offset = offset;
            this.omega = omega;
            this.phase = phase;
        }

        /**
         * @return the amplitude
         */
        public double getAmpl() {
            return ampl;
        }

        /**
         * @return the angular frequency
         */
        public double getOmega() {
            return omega;
        }

        /**
         * @return the phase
         */
        public double getPhase() {
            return phase;
        }

        /**
         * @return the offset
         */
        public double getOffset() {
            return offset;
        }
    }
}
