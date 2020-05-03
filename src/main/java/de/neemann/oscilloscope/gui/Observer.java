package de.neemann.oscilloscope.gui;

/**
 * A simple observer
 */
public interface Observer {
    /**
     * Is called if the was a change
     */
    void hasChanged();
}
