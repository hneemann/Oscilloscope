package de.neemann.oscilloscope.signal;

/**
 * A signal that is a wrapper for an other signal
 */
public class PeriodicSignalWrapper extends PeriodicSignal {
    private PeriodicSignal delegate = PeriodicSignal.GND;

    /**
     * Sets the signal
     *
     * @param signal the signal
     */
    public void setSignal(PeriodicSignal signal) {
        if (delegate != null)
            delegate.removeObserver(this);
        delegate = signal;
        delegate.addObserver(this);
        hasChanged();
    }

    @Override
    public double period() {
        return delegate.period();
    }

    @Override
    public double v(double t) {
        return delegate.v(t);
    }

    @Override
    public double mean() {
        return delegate.mean();
    }

    @Override
    public SinParams getSinParams() {
        return delegate.getSinParams();
    }
}
