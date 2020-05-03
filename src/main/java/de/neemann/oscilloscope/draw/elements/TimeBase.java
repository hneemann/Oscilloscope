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
    String toStr(double timeBase) {
        if (timeBase == 0)
            return "X-Y";
        return super.toStr(timeBase);
    }
}
