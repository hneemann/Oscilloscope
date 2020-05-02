package de.neemann.oscilloscope.draw.elements;

public class TimeBase extends Magnify {
    public TimeBase(double value) {
        super(value);
    }

    @Override
    String toStr(double timeBase) {
        if (timeBase==0)
            return "X-Y";
        return super.toStr(timeBase);
    }
}
