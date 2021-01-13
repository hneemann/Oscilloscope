package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * Base class of all available basic signals
 */
public abstract class Signal implements PeriodicSignal {
    private final double ampl;
    private final double omega;
    private final double phase;
    private final double offs;
    private final double period;

    /**
     * Creates a new instance
     *
     * @param ampl  the amplitude
     * @param omega the circular frequency
     * @param phase the phase
     * @param offs  the offset
     */
    public Signal(double ampl, double omega, double phase, double offs) {
        this.ampl = ampl;
        this.omega = omega;
        this.phase = phase;
        this.period = 2 * Math.PI / omega;
        this.offs = offs;
    }

    @Override
    public double period() {
        return period;
    }

    @Override
    public double mean() {
        return offs;
    }

    /**
     * @return the amplitude
     */
    final public double getAmplitude() {
        return ampl;
    }

    /**
     * @return the circular frequency
     */
    final public double getOmega() {
        return omega;
    }

    /**
     * @return the phase
     */
    final public double getPhase() {
        return phase;
    }

    /**
     * @return the signals offset
     */
    final public double getOffset() {
        return offs;
    }
}
