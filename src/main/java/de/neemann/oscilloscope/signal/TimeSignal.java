package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.osco.Horizontal;

public class TimeSignal implements Signal {
    private final Signal x;
    private final Horizontal horizontal;
    private double trigTime;

    public TimeSignal(Signal x, Horizontal horizontal) {
        this.x = x;
        this.horizontal = horizontal;
    }

    @Override
    public double v(double t) {
        return x.v(t)/horizontal.getTimeBase()-trigTime;
    }
}
