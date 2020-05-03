package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.Element;
import de.neemann.oscilloscope.draw.graphics.*;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.signal.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Component to render the GUI elements
 */
public class ElementComponent extends JComponent {
    private final Container<?> container;
    private BufferedImage buffer;
    private Grid grid;
    private Model model;

    /**
     * Creates a new component to render the GUI elements
     *
     * @param container the container
     */
    public ElementComponent(Container<?> container) {
        this.container = container;
        addMouseWheelListener(mouseWheelEvent -> {
            Element<?> el = container.getElementAt(new Vector(mouseWheelEvent.getX(), mouseWheelEvent.getY()));
            if (el != null) {
                int d = mouseWheelEvent.getWheelRotation();
                if (d < 0) el.up();
                else if (d > 0) el.down();
                buffer = null;
                repaint();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                buffer = null;
            }
        });
    }

    private BufferedImage createBuffer() {
        BufferedImage buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        GraphicSwing gr = new GraphicSwing(g2d);
        grid = new Grid(gr);
        container.draw(grid);
        return buffer;
    }

    /**
     * Sets the mode to use to generate the trace
     *
     * @param model the model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (buffer == null)
            buffer = createBuffer();

        g.drawImage(buffer, 0, 0, null);

        g.setColor(Color.GREEN);
        if (model != null) {
            g.setClip(grid.xmin, grid.ymin, grid.xmax - grid.xmin, grid.ymax - grid.ymin);
            model.drawTo(g, grid.xmin, grid.xmax, grid.ymin, grid.ymax);
        }

        grid.drawTo(g);
    }

    private static final class GridLine {
        private final VectorInterface p1;
        private final VectorInterface p2;

        private GridLine(VectorInterface p1, VectorInterface p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private static final class Grid extends Graphic {
        private final GraphicSwing parent;
        private final ArrayList<GridLine> grid;
        private boolean first = true;
        private int xmin;
        private int xmax;
        private int ymin;
        private int ymax;

        private Grid(GraphicSwing parent) {
            this.parent = parent;
            this.grid = new ArrayList<>();
        }

        @Override
        public void drawLine(VectorInterface p1, VectorInterface p2, Style style) {
            if (style == Style.GRID) {
                grid.add(new GridLine(p1, p2));
                check(p1);
                check(p2);
            } else
                parent.drawLine(p1, p2, style);
        }

        private void check(VectorInterface p) {
            int x = p.getX();
            int y = p.getY();
            if (first) {
                xmin = x;
                xmax = x;
                ymin = y;
                ymax = y;
                first = false;
            } else {
                if (x < xmin) xmin = x;
                if (x > xmax) xmax = x;
                if (y < ymin) ymin = y;
                if (y > ymax) ymax = y;
            }
        }

        @Override
        public void drawPolygon(Polygon p, Style style) {
            parent.drawPolygon(p, style);
        }

        @Override
        public void drawCircle(VectorInterface p1, VectorInterface p2, Style style) {
            parent.drawCircle(p1, p2, style);
        }

        @Override
        public void drawText(VectorInterface p1, VectorInterface p2, VectorInterface p3, String text, Orientation orientation, Style style) {
            parent.drawText(p1, p2, p3, text, orientation, style);
        }

        private void drawTo(Graphics g) {
            g.setColor(Style.GRID.getColor());
            for (GridLine gl : grid)
                g.drawLine(gl.p1.getX(), gl.p1.getY(), gl.p2.getX(), gl.p2.getY());
        }
    }
}
