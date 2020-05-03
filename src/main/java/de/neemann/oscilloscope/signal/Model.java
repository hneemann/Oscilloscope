package de.neemann.oscilloscope.signal;

import java.awt.*;

/**
 * Abstraction of a oscilloscope mode
 */
public interface Model {
    /**
     * Draws the trace to the screen
     *
     * @param g    the graphics objet to use
     * @param xmin xmin of the screen
     * @param xmax xmax of the screen
     * @param ymin ymin of the screen
     * @param ymax ymax of the screen
     */
    void drawTo(Graphics g, int xmin, int xmax, int ymin, int ymax);
}
