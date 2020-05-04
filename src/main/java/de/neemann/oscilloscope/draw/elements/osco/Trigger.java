package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.signal.Frontend;

/**
 * Abstraction of the trigger unit.
 */
public class Trigger {
    private Poti trigLevel;
    private Switch<TrigMode> trigMode;
    private Switch<TrigSource> trigSource;
    private Switch<String> trigSlope;
    private Input trigIn;

    /**
     * Sets the level poti
     *
     * @param trigLevel the level poti
     * @return the given value for chained calls
     */
    public Poti setLevel(Poti trigLevel) {
        this.trigLevel = trigLevel;
        return trigLevel;
    }

    /**
     * Sets the mode switch.
     *
     * @param trigMode the mode switch
     * @return the given value for chained calls
     */
    public Switch<TrigMode> setMode(Switch<TrigMode> trigMode) {
        this.trigMode = trigMode;
        return trigMode;
    }

    /**
     * Sets the source switch.
     *
     * @param trigSource the source switch
     * @return the given value for chained calls
     */
    public Switch<TrigSource> setSource(Switch<TrigSource> trigSource) {
        this.trigSource = trigSource;
        return trigSource;
    }

    /**
     * Sets the trigger slope
     *
     * @param trigSlope the slope
     * @return the given value for chained calls
     */
    public Switch<String> setSlope(Switch<String> trigSlope) {
        this.trigSlope = trigSlope;
        return trigSlope;
    }

    /**
     * Sets the trigger input
     *
     * @param trigIn the trigger input
     * @return the given value for chained calls
     */
    public Input setIn(Input trigIn) {
        this.trigIn = trigIn;
        return trigIn;
    }

    /**
     * @return the trigger mode
     */
    public TrigMode getTrigMode() {
        return trigMode.getSelected();
    }

    /**
     * @return the trigger source
     */
    public TrigSource getTrigSource() {
        return trigSource.getSelected();
    }

    /**
     * @return the trigger input
     */
    public Input getTrigIn() {
        return trigIn;
    }

    /**
     * Calculates the trigger event of a signal
     *
     * @param frontend     the frontend containing the signal
     * @param timePerPixel the time used for a single pixel on the screen
     * @param t0           the time to start the search
     * @return the trigger event
     */
    public Trig getTriggerTime(Frontend frontend, double timePerPixel, double t0) {
        double level = (trigLevel.get()-0.5) * 16;
        boolean up = trigSlope.is("+");
        double t = t0;
        double tEnd = t + frontend.period();
        boolean ol0 = frontend.v(t) > level;
        while (t < tEnd) {
            t += timePerPixel;
            boolean ol1 = frontend.v(t) > level;
            if (ol0 ^ ol1) {
                if (ol1 == up) {
                    return new Trig(t, true);
                }
            }
            ol0 = ol1;
        }
        return new Trig(t0, false);
    }

    /**
     * Description of a trigger event
     */
    public static final class Trig {
        private final double t;
        private final boolean found;

        /**
         * Creates a new instance
         *
         * @param t     the trigger time
         * @param found true if there was a trigger event
         */
        public Trig(double t, boolean found) {
            this.t = t;
            this.found = found;
        }

        /**
         * @return the trigger time
         */
        public double getT() {
            return t;
        }

        /**
         * @return true is a trigger event was found
         */
        public boolean isFound() {
            return found;
        }
    }
}
