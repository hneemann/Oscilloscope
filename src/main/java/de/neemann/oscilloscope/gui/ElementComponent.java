package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.draw.graphics.*;
import de.neemann.oscilloscope.signal.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Component to render the GUI elements
 */
public class ElementComponent extends JComponent {
    private final ArrayList<Wire> wires;
    private Container<?> container;
    private Wire pendingWire;
    private BufferedImage buffer;
    private Grid grid;
    private Model model;

    /**
     * Creates a new component to render the GUI elements
     */
    public ElementComponent() {
        wires = new ArrayList<>();
        addMouseWheelListener(mouseWheelEvent -> {
            if (container != null) {
                Element<?> el = container.getElementAt(new Vector(mouseWheelEvent.getX(), mouseWheelEvent.getY()));
                if (el != null) {
                    boolean ctrl = mouseWheelEvent.isControlDown();
                    int d = mouseWheelEvent.getWheelRotation();
                    if (d < 0) el.up(ctrl);
                    else if (d > 0) el.down(ctrl);
                    invalidateGraphic();
                }
            }
        });
        MyMouseListener l = new MyMouseListener();
        addMouseListener(l);
        addMouseMotionListener(l);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                invalidateGraphic();
            }
        });
    }

    /**
     * Creates a screen shot
     *
     * @return the scrennshot or null if operation is not possible.
     */
    public BufferedImage createScreenShot() {
        if (model == null || grid == null)
            return null;

        int width = grid.xmax - grid.xmin + 1;
        int height = grid.ymax - grid.ymin + 1;
        BufferedImage buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Style.SCREEN.getColor());
        g2d.fillRect(0, 0, width, height);
        model.drawTo(g2d, 0, width, 0, height);
        g2d.translate(-grid.xmin, -grid.ymin);
        grid.drawTo(g2d);
        return buffer;
    }

    private BufferedImage createBuffer() {
        BufferedImage buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        GraphicSwing gr = new GraphicSwing(g2d);
        grid = new Grid(gr);
        if (container != null)
            container.draw(grid);

        for (Wire w : wires)
            w.drawTo(g2d);

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

    /**
     * Adds a wire.
     *
     * @param w the wire
     * @return this for chained calls
     */
    public ElementComponent add(Wire w) {
        wires.add(w);
        w.connect();
        invalidateGraphic();
        return this;
    }

    /**
     * Removes a wire.
     *
     * @param w the wire
     * @return this for chained calls
     */
    public ElementComponent remove(Wire w) {
        boolean ok = wires.remove(w);
        if (ok)
            w.disconnect();
        invalidateGraphic();
        return this;
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

        if (pendingWire != null) {
            g.setClip(0, 0, getWidth(), getHeight());
            pendingWire.drawTo((Graphics2D) g);
        }
    }

    /**
     * closes the contained container
     */
    public void close() {
        if (container != null)
            container.close();
    }

    /**
     * Implements the visitor pattern
     *
     * @param visitor the visitor
     */
    public void traverse(Visitor visitor) {
        if (container != null)
            container.traverse(visitor);
    }

    /**
     * Sets the container. Maybe null
     *
     * @param mainContainer the container, maybe null
     */
    public void setContainer(Container<?> mainContainer) {
        this.container = mainContainer;
        wires.clear();
        mainContainer.traverse(element -> {
            if (element instanceof NeedsComponent)
                ((NeedsComponent) element).setComponent(ElementComponent.this);
        });
        model = null;
        invalidateGraphic();
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

    private void invalidateGraphic() {
        buffer = null;
        repaint();
    }

    private class MyMouseListener implements MouseListener, MouseMotionListener {
        private BNCInput pendingInput;
        private BNCOutput pendingOutput;

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (container != null) {
                Element<?> el = container.getElementAt(new Vector(mouseEvent.getX(), mouseEvent.getY()));
                if (el != null) {
                    if (el instanceof BNCOutput) {
                        pendingOutput = (BNCOutput) el;
                        pendingInput = new BNCInput("").setPos(mouseEvent.getX(), mouseEvent.getY());
                        pendingWire = new Wire(pendingOutput, pendingInput);
                        repaint();
                    } else if (el instanceof BNCInput) {
                        BNCInput bncInput = (BNCInput) el;
                        if (pendingWire != null) {
                            pendingWire = null;
                            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                                for (Wire w : wires) {
                                    if (w.getInput() == bncInput)
                                        return;
                                }
                                add(new Wire(pendingOutput, bncInput));
                            }
                        } else {
                            for (Wire w : wires) {
                                if (w.getInput() == bncInput) {
                                    remove(w);
                                    return;
                                }
                            }
                        }
                    } else {
                        el.clicked(mouseEvent.getButton(), mouseEvent.isControlDown());
                        pendingWire = null;
                        invalidateGraphic();
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            if (pendingWire != null) {
                pendingInput.setPos(mouseEvent.getX(), mouseEvent.getY());
                repaint();
            }
        }
    }

    /**
     * implemented by elements that needs access to the {@link ElementComponent}
     */
    public interface NeedsComponent {
        /**
         * Calles if the container is added to this component.
         *
         * @param elementComponent this component
         */
        void setComponent(ElementComponent elementComponent);
    }
}
