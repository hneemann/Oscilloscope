package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;

import java.awt.*;

/**
 * The simulation used for x-y mode
 */
public class ModelXY implements Model {
    private static final int MAX_LOOP = 10000;

    private final PeriodicSignal xFrontend;
    private final PeriodicSignal yFrontend;
    private final ToScreen xScreen;
    private final ToScreen yScreen;
    private final long timeOffset;
    private double lastTime;
    private ScreenBuffer buffer;
    private int lastxPos;
    private int lastyPos;

    /**
     * Creates a new model for x-y mode
     *
     * @param x    the x signal
     * @param y    the y signal
     * @param osco the used oscilloscope
     */
    public ModelXY(PeriodicSignal x, PeriodicSignal y, Oscilloscope osco) {
        if (!osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        this.xFrontend = new Frontend(x, osco.getCh1());
        this.xScreen = new YValueToScreen(osco.getCh1().getPosPoti(), 10);
        this.yFrontend = new Frontend(y, osco.getCh2());
        this.yScreen = new YValueToScreen(osco.getCh2().getPosPoti(), 8);
        timeOffset = System.currentTimeMillis();
        lastTime = getTimeInMillis() / 1000.0;
    }

    private long getTimeInMillis() {
        return System.currentTimeMillis() - timeOffset;
    }

    @Override
    public void drawTo(Graphics g, int xmin, int xmax, int ymin, int ymax) {
        double time = getTimeInMillis() / 1000.0;

        int width = xmax - xmin;
        int height = ymax - ymin;

        if (buffer == null)
            buffer = new ScreenBuffer(width, height);

        buffer.darken();

        double period = Math.max(xFrontend.period(), yFrontend.period());

        double timeDelta = period / 1000;
        if (timeDelta > 0.0001)
            timeDelta = 0.0001;
        int n = (int) ((time - lastTime) / timeDelta);
        if (n > MAX_LOOP)
            timeDelta = (time - lastTime) / MAX_LOOP;

        while (lastTime < time) {
            lastTime += timeDelta;
            int xPos = xScreen.v(xFrontend.v(lastTime), width);
            int yPos = yScreen.v(yFrontend.v(lastTime), height);
            buffer.drawTrace(lastxPos, lastyPos, xPos, yPos);
            lastxPos = xPos;
            lastyPos = yPos;
        }

        g.drawImage(buffer.getBuffer(), xmin, ymin, null);
    }
}
