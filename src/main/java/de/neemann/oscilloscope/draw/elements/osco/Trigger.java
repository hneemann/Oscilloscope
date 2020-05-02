package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

public class Trigger {
    private Poti trigLevel;
    private Switch<TrigMode> trigMode;
    private  Switch<TrigSource> trigSource;
    private  Switch<String> trigSlope;
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

    public Poti getTrigLevel() {
        return trigLevel;
    }

    public Switch<TrigMode> getTrigMode() {
        return trigMode;
    }

    public Switch<TrigSource> getTrigSource() {
        return trigSource;
    }

    public Switch<String> getTrigSlope() {
        return trigSlope;
    }

    public Input getTrigIn() {
        return trigIn;
    }
}
