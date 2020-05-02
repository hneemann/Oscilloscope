package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.Vector;

/**
 * The screen of the oscilloscope
 */
public class Screen extends Element<Screen> {
    private final Polygon polygon;
    private final int grid;
    private final int gr5;

    /**
     * Creates a new screen
     *
     * @param gr5 a fifth of the grid size
     */
    public Screen(int gr5) {
        grid = gr5 * 5;
        this.gr5 = gr5;
        polygon = new Polygon(true)
                .add(0, 0)
                .add(grid * 10, 0)
                .add(grid * 10, grid * 8)
                .add(0, grid * 8);
    }

    @Override
    void drawToOrigin(Graphic gr) {
        gr.drawPolygon(polygon, Style.SCREEN);

        for (int x = 0; x <= 10; x++)
            gr.drawLine(new Vector(x * grid, 0), new Vector(x * grid, 8 * grid), Style.GRID);
        for (int y = 0; y <= 8; y++)
            gr.drawLine(new Vector(0, y * grid), new Vector(10 * grid, y * grid), Style.GRID);

        for (int x = 0; x <= 50; x++)
            gr.drawLine(new Vector(x * gr5, grid * 4 - gr5), new Vector(x * gr5, grid * 4 + gr5), Style.GRID);
        for (int y = 0; y <= 40; y++)
            gr.drawLine(new Vector(5 * grid - gr5, y * gr5), new Vector(5 * grid + gr5, y * gr5), Style.GRID);
    }
}
