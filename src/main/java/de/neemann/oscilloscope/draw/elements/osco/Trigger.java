package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * Abstraction of the trigger unit.
 */
public class Trigger {
    private Potentiometer trigLevel;
    private Switch<TrigMode> trigMode;
    private Switch<TrigSource> trigSource;
    private Switch<String> trigSlope;
    private BNCInput trigIn;

    /**
     * Sets the level poti
     *
     * @param trigLevel the level poti
     * @return the given value for chained calls
     */
    public Potentiometer setLevel(Potentiometer trigLevel) {
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
    public BNCInput setIn(BNCInput trigIn) {
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
    public BNCInput getTrigIn() {
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
    public Trig getTriggerTime(PeriodicSignal frontend, double timePerPixel, double t0) {
        return wasTrig(frontend, timePerPixel, t0, t0 + frontend.period());
    }

    /**
     * Tests if there was a trigger in between t0 and t1
     *
     * @param frontend     the frontend containing the signal
     * @param timePerPixel the time used for a single pixel on the screen
     * @param t0           the time to start the search
     * @param t1           the time to stop the search
     * @return the trigger event
     */
    public Trig wasTrig(PeriodicSignal frontend, double timePerPixel, double t0, double t1) {
        double level = (trigLevel.get() - 0.5) * 16;
        return wasTrig(frontend, timePerPixel, t0, t1, level);
    }

    /**
     * Tests if there was a trigger in between t0 and t1
     *
     * @param signal       the signal
     * @param timePerPixel the time used for a single pixel on the screen
     * @param t0           the time to start the search
     * @param t1           the time to stop the search
     * @param level        the trigger level
     * @return the trigger event
     */
    public Trig wasTrig(PeriodicSignal signal, double timePerPixel, double t0, double t1, double level) {
        boolean up = trigSlope.is("+");
        double t = t0;
        boolean ol0 = signal.v(t) > level;
        while (t < t1) {
            t += timePerPixel;
            boolean ol1 = signal.v(t) > level;
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
     * @return the level poti
     */
    public Potentiometer getLevelPoti() {
        return trigLevel;
    }

    /**
     * @return the mode switch
     */
    public Switch<TrigMode> getTrigModeSwitch() {
        return trigMode;
    }

    /**
     * @return the source switch
     */
    public Switch<TrigSource> getTrigSourceSwitch() {
        return trigSource;
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
