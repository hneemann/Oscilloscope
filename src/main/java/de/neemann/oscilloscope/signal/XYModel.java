package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.graphics.Style;

import java.awt.*;
import java.awt.image.*;

/**
 * The simulation used for x-y mode
 */
public class XYModel implements Model {
    private static final LookupTable LOOKUP;

    static {
        byte[] data = new byte[256];
        int green = Style.SCREEN.getColor().getGreen();
        for (int i = 0; i <= green; i++)
            data[i] = (byte) i;
        for (int i = green; i < 256; i++)
            data[i] = (byte) Math.max(green, i - 2);
        LOOKUP = new ByteLookupTable(0, data);
    }

    private final Signal xFrontend;
    private final Signal yFrontend;
    private final ToScreen xScreen;
    private final ToScreen yScreen;
    private double lastTime;
    private BufferedImage buffer;
    private Graphics2D g2d;
    private int lastxPos;
    private int lastyPos;
    private double timeDelta = 0.0001;
    private boolean slow = false;

    /**
     * Creates a new model for x-y mode
     *
     * @param x    the x signal
     * @param y    the y signal
     * @param osco the used oscilloscope
     */
    public XYModel(PeriodicSignal x, PeriodicSignal y, Oscilloscope osco) {
        if (!osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        this.xFrontend = new Frontend(x, osco.getCh1());
        this.xScreen = new ValueToScreen(osco.getCh1().getPosPoti(), 10);
        this.yFrontend = new Frontend(y, osco.getCh2());
        this.yScreen = new ValueToScreen(osco.getCh2().getPosPoti(), 8);
        lastTime = System.currentTimeMillis() / 1000.0;
    }

    private void createBuffer(int width, int height) {
        buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Style.SCREEN.getColor());
        g2d.fillRect(0, 0, width, height);
    }

    @Override
    public void drawTo(Graphics g, int xmin, int xmax, int ymin, int ymax) {
        long timeMs = System.currentTimeMillis();
        double time = timeMs / 1000.0;

        int width = xmax - xmin;
        int height = ymax - ymin;

        if (buffer == null)
            createBuffer(width, height);

        LookupOp op = new LookupOp(LOOKUP, null);
        op.filter(buffer, buffer);

        g2d.setColor(Color.GREEN);

        while (lastTime < time) {
            lastTime += timeDelta;
            int xPos = xScreen.v(xFrontend.v(lastTime), width);
            int yPos = yScreen.v(yFrontend.v(lastTime), height);

            int distOnScreen = sqr(xPos - lastxPos) + sqr(yPos - lastyPos);
            if (!slow && distOnScreen > 25)
                timeDelta /= 2;
            if (slow || (distOnScreen < 2 && timeDelta < 0.001))
                timeDelta *= 2;

            if (isOnScreen(xPos, yPos, width, height) || isOnScreen(lastxPos, lastyPos, width, height))
                g2d.drawLine(lastxPos, lastyPos, xPos, yPos);
            lastxPos = xPos;
            lastyPos = yPos;
        }

        long dur = System.currentTimeMillis() - timeMs;
        slow = dur > Oscilloscope.TIME_DELTA_MS;


        g.drawImage(buffer, xmin, ymin, null);
        g.drawString("" + lastxPos + "," + lastyPos + ", " + timeDelta, xmin + 5, ymin + 15);
    }

    private boolean isOnScreen(int x, int y, int width, int height) {
        return x >= 0 && x <= width && y >= 0 && y <= height;
    }

    private int sqr(int i) {
        return i * i;
    }
}
