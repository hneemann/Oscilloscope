package de.neemann.oscilloscope.signal;

import java.awt.*;

public interface Model {
    void drawTo(Graphics g, int xmin, int xmax, int ymin, int ymax);
}
