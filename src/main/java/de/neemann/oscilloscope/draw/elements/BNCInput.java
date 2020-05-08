package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * A BNC input connector
 */
public class BNCInput extends BNC<BNCInput> {
    private InputSetter setter;

    /**
     * Creates an input
     *
     * @param name the name of the input
     */
    public BNCInput(String name) {
        super(name);
    }

    /**
     * Sets the setter which is used to publish a signal if the setter is called.
     *
     * @param setter the setter
     * @return this for chained calls
     */
    public BNCInput setInputSetter(InputSetter setter) {
        this.setter = setter;
        return this;
    }

    /**
     * Sets the input signal
     *
     * @param signal the signal to set
     */
    public void setInput(PeriodicSignal signal) {
        setter.setSignal(signal);
    }

    /**
     * The input setter interface
     */
    public interface InputSetter {
        /**
         * Calles to set an input
         *
         * @param signal the signal to set
         */
        void setSignal(PeriodicSignal signal);
    }
}
