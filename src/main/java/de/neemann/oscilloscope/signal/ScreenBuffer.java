package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.graphics.Style;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

import static de.neemann.oscilloscope.signal.ModelTimeCalc.MIN_TRACE_BRIGHT;

/**
 * The scope screen buffer
 */
public class ScreenBuffer {
    private static final LookupTable LOOKUP;

    static {
        byte[] data = new byte[256];
        int green = Style.SCREEN.getColor().getGreen();
        for (int i = 0; i <= green; i++)
            data[i] = (byte) i;
        for (int i = green; i < 256; i++)
            data[i] = (byte) Math.max(green, i - 8);
        LOOKUP = new ByteLookupTable(0, data);
    }

    /**
     * Speed dependent trace color
     */
    private static final int SPEEDCOLORS = 256;
    static final Color[] SPEEDCOLOR = new Color[SPEEDCOLORS];

    static {
        int c0 = Style.SCREEN.getColor().getGreen() + MIN_TRACE_BRIGHT;
        for (int doss = 0; doss < SPEEDCOLORS; doss++) {
            double dos = Math.sqrt(doss);
            SPEEDCOLOR[doss] = new Color(0, (int) (255 - dos * (255 - c0) / Math.sqrt(SPEEDCOLORS - 1)), 0);
        }
    }


    private final BufferedImage buffer;
    private final Graphics2D g2d;
    private final int width;
    private final int height;

    /**
     * Creates a new instance
     *
     * @param width  the width
     * @param height the height
     */
    public ScreenBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Style.SCREEN.getColor());
        g2d.fillRect(0, 0, width, height);
    }

    /**
     * Makes the complete screen darker
     */
    public void darken() {
        LookupOp op = new LookupOp(LOOKUP, null);
        op.filter(buffer, buffer);
    }

    /**
     * @return the image buffer
     */
    public BufferedImage getBuffer() {
        return buffer;
    }

    /**
     * Draws a trace, color depends on distance
     *
     * @param x0 x0
     * @param y0 y0
     * @param x1 x1
     * @param y1 y1
     */
    public void drawTrace(int x0, int y0, int x1, int y1) {
        if (isOnScreen(x0, y0) || isOnScreen(x1, y1)) {
            int distOnScreenSqr = sqr(x0 - x1) + sqr(y0 - y1);
            g2d.setColor(SPEEDCOLOR[Math.min(distOnScreenSqr, SPEEDCOLORS - 1)]);
            g2d.drawLine(x0, y0, x1, y1);
        }
    }

    /**
     * Draws a bright trace
     *
     * @param x0 x0
     * @param y0 y0
     * @param x1 x1
     * @param y1 y1
     */
    public void drawBrightTrace(int x0, int y0, int x1, int y1) {
        if (isOnScreen(x0, y0) || isOnScreen(x1, y1)) {
            g2d.setColor(Color.GREEN);
            g2d.drawLine(x0, y0, x1, y1);
        }
    }

    private int sqr(int i) {
        return i * i;
    }

    private boolean isOnScreen(int x, int y) {
        return x >= 0 && x <= width && y >= 0 && y <= height;
    }

}
