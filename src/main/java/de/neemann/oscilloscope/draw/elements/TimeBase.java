package de.neemann.oscilloscope.draw.elements;

/**
 * The time base values
 */
public class TimeBase extends Magnify {
    /**
     * Creates a new instance
     *
     * @param value the time base value
     */
    public TimeBase(double value) {
        super(value);
    }

    @Override
    void setupValue(double value) {
        if (value == 0)
            setString("X-Y");
        else
            super.setupValue(value);
    }
}
