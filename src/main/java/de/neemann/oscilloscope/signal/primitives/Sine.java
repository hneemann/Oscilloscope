package de.neemann.oscilloscope.signal.primitives;

/**
 * A sine signal
 */
public final class Sine extends Signal {

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Sine(double ampl, double w, double phase, double offset) {
        super(ampl, w, phase, offset);
    }

    @Override
    public double v(double t) {
        return getAmplitude() * Math.sin(getOmega() * t + getPhase()) + getOffset();
    }

}
