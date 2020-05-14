package de.neemann.oscilloscope.gui;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.draw.graphics.GraphicSwing;
import de.neemann.oscilloscope.draw.graphics.Vector;
import de.neemann.oscilloscope.experiments.Experiment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Component to render the GUI elements.
 * All the switches knobs and so on are drawn to an image to speed up the drawing process.
 * So if a repaint is required this image is transferred to the screen buffer instead
 * of drawing all the gui elements over and over again. This image is updated only if a
 * gui element has changed.
 * The trace is then drawn to the screen buffer after this image is copied. This is
 * done by by implementing the {@link PostImageDraw} interface.
 */
public class ElementComponent extends JComponent {
    private final ArrayList<Wire> wires;
    private final ArrayList<PostImageDraw> postImageDraws;
    private Container<?> container;
    private Wire pendingWire;
    private BufferedImage buffer;

    /**
     * Creates a new component to render the GUI elements
     */
    public ElementComponent() {
        wires = new ArrayList<>();
        postImageDraws = new ArrayList<>();
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
     * @return the screen shot or null if operation is not possible.
     */
    public BufferedImage createScreenShot() {
        Screen screen = Experiment.get(this, e -> e instanceof Screen);
        if (screen != null)
            return screen.createScreenShot();
        else
            return null;
    }

    private BufferedImage createBuffer() {
        BufferedImage buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(getWidth(), getHeight());
        Graphics2D g2d = buffer.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (container != null)
            container.draw(new GraphicSwing(g2d));

        for (Wire w : wires)
            w.drawTo(g2d);

        return buffer;
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
     * Adds a post {@link PostImageDraw} instance to this ElementComponent
     *
     * @param postImageDraw the instance to draw after the image is drawn
     */
    public void add(PostImageDraw postImageDraw) {
        postImageDraws.add(postImageDraw);
    }

    /**
     * Removes a wire.
     *
     * @param w the wire
     */
    public void remove(Wire w) {
        boolean ok = wires.remove(w);
        if (ok)
            w.disconnect();
        invalidateGraphic();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (buffer == null)
            buffer = createBuffer();

        g.drawImage(buffer, 0, 0, null);

        for (PostImageDraw pid : postImageDraws)
            pid.postImageDraw((Graphics2D) g);

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
        invalidateGraphic();
    }

    private void invalidateGraphic() {
        buffer = null;
        repaint();
    }

    private class MyMouseListener implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (container != null) {
                Element<?> el = container.getElementAt(new Vector(mouseEvent.getX(), mouseEvent.getY()));
                if (el != null) {
                    el.clicked(mouseEvent.getButton(), mouseEvent.isControlDown());
                    pendingWire = null;
                    invalidateGraphic();
                } else {
                    if (pendingWire != null) {
                        pendingWire = null;
                        invalidateGraphic();
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (container != null) {
                Element<?> el = container.getElementAt(new Vector(mouseEvent.getX(), mouseEvent.getY()));
                if (el != null) {

                    if (el instanceof BNCOutput) {
                        BNCOutput pendingOutput = (BNCOutput) el;
                        BNCInput pendingInput = new BNCInput("").setPos(mouseEvent.getX(), mouseEvent.getY());
                        pendingWire = new Wire(pendingOutput, pendingInput);
                        repaint();
                    } else if (el instanceof BNCInput) {
                        BNCInput bncInput = (BNCInput) el;
                        Wire foundWire = null;
                        for (Wire w : wires)
                            if (w.getInput() == bncInput)
                                foundWire = w;

                        if (foundWire != null) {
                            remove(foundWire);
                            BNCOutput pendingOutput = foundWire.getOutput();
                            BNCInput pendingInput = new BNCInput("").setPos(mouseEvent.getX(), mouseEvent.getY());
                            pendingWire = new Wire(pendingOutput, pendingInput);
                            repaint();
                        }
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (pendingWire != null) {
                Element<?> el = container.getElementAt(new Vector(mouseEvent.getX(), mouseEvent.getY()));
                if (el != null) {
                    if (el instanceof BNCInput) {
                        BNCInput bncInput = (BNCInput) el;
                        for (Wire w : wires) {
                            if (w.getInput() == bncInput)
                                return;
                        }
                        add(new Wire(pendingWire.getOutput(), bncInput));
                    }
                }
                pendingWire = null;
                repaint();
            }

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            if (pendingWire != null) {
                pendingWire.getInput().setPos(mouseEvent.getX(), mouseEvent.getY());
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
        }
    }

    /**
     * Interface implemented by elements that require a post screen buffer output.
     * Up to now this is only the {@link Screen}
     */
    public interface PostImageDraw {
        /**
         * Called after the base image is drawn to the component.
         *
         * @param graphics2D the graphics object to draw on
         */
        void postImageDraw(Graphics2D graphics2D);
    }

    /**
     * implemented by elements that needs access to the {@link ElementComponent}
     */
    public interface NeedsComponent {
        /**
         * Called if the container is added to this component.
         *
         * @param elementComponent this component
         */
        void setComponent(ElementComponent elementComponent);
    }
}
