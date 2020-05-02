package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Coupling;
import de.neemann.oscilloscope.draw.elements.osco.Channel;

public class Frontend implements Signal {
    private final Signal s;
    private final Channel channel;

    public Frontend(Signal s, Channel channel) {
        this.s = s;
        this.channel = channel;
    }

    @Override
    public double v(double t) {
        if (channel.getCoupling() == Coupling.GND)
            return 0;
        else
            return s.v(t) / channel.getMag() * (1 + channel.getVar() * 2);
    }
}
