package de.neemann.oscilloscope.signal.primitives;

/**
 * The square signal
 */
public class Square extends Signal {

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Square(double ampl, double w, double phase, double offset) {
        super(ampl, w, phase, offset);
    }

    @Override
    public double v(double t) {
        double arg = (t * getOmega() + getPhase()) / (2 * Math.PI);
        return (arg - Math.floor(arg) < 0.5 ? getAmplitude() : -getAmplitude()) + getOffset();
    }

}
