package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Coupling;
import de.neemann.oscilloscope.draw.elements.osco.Channel;

public class Frontend implements PeriodicSignal {
    private final PeriodicSignal s;
    private final Channel channel;

    public Frontend(PeriodicSignal s, Channel channel) {
        this.s = s;
        this.channel = channel;
    }

    @Override
    public double v(double t) {
        if (channel.getCoupling() == Coupling.GND)
            return 0;
        else {
            double v = s.v(t);
            if (channel.isInv())
                v = -v;
            return v / channel.getAmplitude() * (1 + channel.getVar() * 2);
        }
    }

    @Override
    public double period() {
        return s.period();
    }
}
