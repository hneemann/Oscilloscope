package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Coupling;
import de.neemann.oscilloscope.draw.elements.osco.Channel;

/**
 * Used to describe the frontend
 */
public class Frontend implements PeriodicSignal {
    private final PeriodicSignal s;
    private final Coupling coupling;
    private final boolean isInv;
    private final double amplitude;
    private final double var;

    /**
     * Creates a new frontend
     *
     * @param channel the channel parameters
     */
    public Frontend(Channel channel) {
        this.s = channel.getInput().getSignalProvider().getSignal();
        coupling = channel.getCoupling();
        amplitude = channel.getAmplitude();
        var = channel.getVar();
        isInv = channel.isInv();
    }


    @Override
    public double v(double t) {
        if (coupling == Coupling.GND)
            return 0;
        else {
            double v = s.v(t);

            if (coupling == Coupling.AC)
                v -= s.mean();

            if (isInv)
                v = -v;
            return v / amplitude * (1 + var * 2);
        }
    }

    @Override
    public double period() {
        return s.period();
    }

    @Override
    public double mean() {
        if (coupling == Coupling.DC)
            return s.mean();
        return 0;
    }
}
