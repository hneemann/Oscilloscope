package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.signal.Frontend;

public class Trigger {
    private Poti trigLevel;
    private Switch<TrigMode> trigMode;
    private Switch<TrigSource> trigSource;
    private Switch<String> trigSlope;
    private Input trigIn;

    public Poti setLevel(Poti trigLevel) {
        this.trigLevel = trigLevel;
        return trigLevel;
    }

    public Switch<TrigMode> setMode(Switch<TrigMode> trigMode) {
        this.trigMode = trigMode;
        return trigMode;
    }

    public Switch<TrigSource> setSource(Switch<TrigSource> trigSource) {
        this.trigSource = trigSource;
        return trigSource;
    }

    public Switch<String> setSlope(Switch<String> trigSlope) {
        this.trigSlope = trigSlope;
        return trigSlope;
    }

    public Input setIn(Input trigIn) {
        this.trigIn = trigIn;
        return trigIn;
    }

    public TrigMode getTrigMode() {
        return trigMode.getSelected();
    }

    public TrigSource getTrigSource() {
        return trigSource.getSelected();
    }

    public Input getTrigIn() {
        return trigIn;
    }

    public Trig getTriggerTime(Frontend frontend, double timePerPixel, double t0) {
        double level = (0.5 - trigLevel.get()) * 16;
        boolean up = trigSlope.is("+");
        double t = t0;
        double tEnd = t + frontend.period();
        boolean ol0 = frontend.v(t) > level;
        while (t < tEnd) {
            t += timePerPixel;
            boolean ol1 = frontend.v(t) > level;
            if (ol0 ^ ol1) {
                if (ol0 == up) {
                    return new Trig(t, true);
                }
            }
            ol0 = ol1;
        }
        return new Trig(t0, false);
    }

    public static final class Trig {
        private final double t;
        private final boolean found;

        public Trig(double t, boolean found) {
            this.t = t;
            this.found = found;
        }

        public double getT() {
            return t;
        }

        public boolean isFound() {
            return found;
        }
    }
}
