package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.BNCInput;
import de.neemann.oscilloscope.draw.elements.BNCOutput;
import de.neemann.oscilloscope.draw.graphics.Vector;
import de.neemann.oscilloscope.signal.PeriodicSignal;

import java.awt.*;
import java.awt.geom.QuadCurve2D;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;

/**
 * A wire to connect BNC connectors
 */
public class Wire {
    private final BNCOutput out;
    private final BNCInput in;

    /**
     * Creates a new instance
     *
     * @param out connector a
     * @param in  connector b
     */
    public Wire(BNCOutput out, BNCInput in) {
        this.out = out;
        this.in = in;
    }

    /**
     * Draws the wire
     *
     * @param g2d the {@link Graphics2D} object to draw to
     */
    public void drawTo(Graphics2D g2d) {
        Vector p0 = out.getScreenPos(new Vector(0, 0));
        Vector p1 = in.getScreenPos(new Vector(0, 0));

        Vector c = new Vector((p0.x + p1.x) / 2, Math.max(p0.y, p1.y) + SIZE * 8);

        g2d.setColor(new Color(0, 0, 0, 192));
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new QuadCurve2D.Float(p0.x, p0.y, c.x, c.y, p1.x, p1.y));
    }

    /**
     * disconnects this wire
     */
    public void connect() {
        in.setInput(out.getOutput());
    }

    /**
     * Connects this wire
     */
    public void disconnect() {
        in.setInput(PeriodicSignal.GND);
    }

    /**
     * @return the input connector
     */
    public BNCInput getInput() {
        return in;
    }
}
