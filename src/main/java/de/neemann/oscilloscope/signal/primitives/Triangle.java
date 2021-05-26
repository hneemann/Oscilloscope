package de.neemann.oscilloscope.signal.primitives;

/**
 * The triangle signal
 */
public class Triangle extends Signal {

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Triangle(double ampl, double w, double phase, double offset) {
        super(ampl, w, phase, offset);
    }

    @Override
    public double v(double t) {
        double arg = (t * getOmega() + getPhase()) / (2 * Math.PI);
        double vt = arg - Math.floor(arg);
        return (vt < 0.5 ? getAmplitude() * (4 * vt - 1) : getAmplitude() * (4 * (1 - vt) - 1)) + getOffset();
    }

}
