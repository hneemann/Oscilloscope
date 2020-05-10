package de.neemann.oscilloscope.signal;

/**
 * Abstraction of a oscilloscope screen update algorithm
 */
public interface Model {
    /**
     * Draws the trace to the screen buffer.
     * Is called with a period declared at {@link de.neemann.oscilloscope.draw.elements.osco.Oscilloscope#TIME_DELTA_MS}.
     *
     * @param buffer the screen buffer
     */
    void updateBuffer(ScreenBuffer buffer);
}
