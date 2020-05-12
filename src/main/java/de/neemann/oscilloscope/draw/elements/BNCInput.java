package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.SignalProvider;

/**
 * A BNC input connector
 */
public class BNCInput extends BNC<BNCInput> {
    private final SignalProvider signalProvider = new SignalProvider();

    /**
     * Creates an input
     *
     * @param name the name of the input
     */
    public BNCInput(String name) {
        super(name);
    }

    /**
     * @return the signal provider for this connector
     */
    public SignalProvider getSignalProvider() {
        return signalProvider;
    }

    /**
     * Set the signal which is connected to this input
     *
     * @param signal the signal
     */
    public void setSignal(PeriodicSignal signal) {
        signalProvider.setSignal(signal);
    }
}
