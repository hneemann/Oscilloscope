package de.neemann.oscilloscope.signal.primitives;

/**
 * The sawtooth signal
 */
public class Sawtooth extends Signal {

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Sawtooth(double ampl, double w, double phase, double offset) {
        super(ampl, w, phase, offset);
    }

    @Override
    public double v(double t) {
        double arg = (t * getOmega() + getPhase()) / (2 * Math.PI);
        double vs = arg - Math.floor(arg);
        return (vs * 2 - 1) * getAmplitude() + getOffset();
    }
}
