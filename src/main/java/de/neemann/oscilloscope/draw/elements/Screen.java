package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.Vector;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.signal.ScreenBuffer;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The screen of the oscilloscope
 */
public class Screen extends Element<Screen> implements ElementComponent.NeedsComponent, ElementComponent.PostImageDraw {
    private final Polygon polygon;
    private final int grid;
    private final int gr5;
    private final ScreenBuffer screenBuffer;
    private Vector pos;

    /**
     * Creates a new screen
     *
     * @param gr5 a fifth of the grid size
     */
    public Screen(int gr5) {
        grid = gr5 * 5;
        this.gr5 = gr5;
        int width = grid * 10;
        int height = grid * 8;
        polygon = new Polygon(true)
                .add(0, 0)
                .add(width, 0)
                .add(width, height)
                .add(0, height);

        screenBuffer = new ScreenBuffer(width, height);
    }

    /**
     * @return the used screen buffer
     */
    public ScreenBuffer getScreenBuffer() {
        return screenBuffer;
    }

    /**
     * Creates a screen shot
     *
     * @return the screen shot
     */
    public BufferedImage createScreenShot() {
        int width = screenBuffer.getWidth();
        int height = screenBuffer.getHeight();
        BufferedImage buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        screenBuffer.drawBufferTo(g2d, 0, 0);
        grawGrid(g2d, 0, 0);
        return buffer;
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        gr.drawPolygon(polygon, Style.SCREEN);
    }

    @Override
    public void postImageDraw(Graphics2D gr) {
        screenBuffer.drawBufferTo(gr, pos.x, pos.y);

        grawGrid(gr, pos.x, pos.y);
    }

    /**
     * Draws the grid to the given {@link Graphics2D} object.
     *
     * @param gr   the {@link Graphics2D} instance to use
     * @param posx x pos
     * @param posy ypos
     */
    public void grawGrid(Graphics2D gr, int posx, int posy) {
        gr.setColor(Style.GRID.getColor());
        for (int x = 0; x <= 10; x++)
            gr.drawLine(posx + x * grid, posy, posx + x * grid, posy + 8 * grid);
        for (int y = 0; y <= 8; y++)
            gr.drawLine(posx, posy + y * grid, posx + 10 * grid, posy + y * grid);

        for (int x = 0; x <= 50; x++)
            gr.drawLine(posx + x * gr5, posy + grid * 4 - gr5, posx + x * gr5, posy + grid * 4 + gr5);
        for (int y = 0; y <= 40; y++)
            gr.drawLine(posx + 5 * grid - gr5, posy + y * gr5, posx + 5 * grid + gr5, posy + y * gr5);
    }

    @Override
    public void setComponent(ElementComponent elementComponent) {
        elementComponent.add(this);
        pos = getScreenPos(new Vector(0, 0));
    }

}
