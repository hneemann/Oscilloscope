package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Coupling;
import de.neemann.oscilloscope.draw.elements.osco.Channel;

/**
 * Used to describe the frontend
 */
public class Frontend extends PeriodicSignal {
    private final PeriodicSignal s;
    private final Channel channel;

    /**
     * Creates a new frontend
     *
     * @param s       the periodic signal
     * @param channel the channel parameters
     */
    public Frontend(PeriodicSignal s, Channel channel) {
        this.s = s;
        s.addObserver(this);
        this.channel = channel;
    }

    @Override
    public double v(double t) {
        Coupling coupling = channel.getCoupling();
        if (coupling == Coupling.GND)
            return 0;
        else {
            double v = s.v(t);

            if (coupling == Coupling.AC)
                v -= s.mean();

            if (channel.isInv())
                v = -v;
            return v / channel.getAmplitude() * (1 + channel.getVar() * 2);
        }
    }

    @Override
    public double period() {
        return s.period();
    }

    @Override
    public double mean() {
        if (channel.getCoupling() == Coupling.DC)
            return s.mean();
        return 0;
    }
}
