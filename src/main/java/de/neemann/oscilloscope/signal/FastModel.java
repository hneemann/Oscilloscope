package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.osco.Channel;
import de.neemann.oscilloscope.draw.elements.osco.Horizontal;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.graphics.Style;

import java.awt.*;
import java.util.LinkedList;

public class FastModel implements Model {
    private static final int GREEN = Style.SCREEN.getColor().getGreen();

    private static final double GLOW = 1;
    private final Signal xFrontend;
    private final Signal yFrontend;
    private final ToScreen xScreen;
    private final ToScreen yScreen;
    private final Oscilloscope osco;
    private final LinkedList<TracePoint> trace;
    private double lastTime;
    private TracePoint lastOffScreen;

    public FastModel(Signal x, Signal y, Oscilloscope osco) {
        if (osco.getHorizontal().getTimeBase() == 0) {
            this.xFrontend = new Frontend(x, osco.getCh1());
            this.xScreen = new ValueToScreen(osco.getCh1());
            this.yFrontend = new Frontend(y, osco.getCh2());
            this.yScreen = new ValueToScreen(osco.getCh2());
        } else {
            this.xFrontend = new TimeSignal(x, osco.getHorizontal());
            this.xScreen = new TimeToScreen(osco.getHorizontal());
            this.yFrontend = new Frontend(y, osco.getCh1());
            this.yScreen = new ValueToScreen(osco.getCh1());
        }
        this.osco = osco;
        lastTime = System.currentTimeMillis() / 1000.0;
        trace = new LinkedList<>();
    }

    @Override
    public void drawTo(Graphics g, int xmin, int xmax, int ymin, int ymax) {
        double time = System.currentTimeMillis() / 1000.0;

        int xPos = 0;
        int yPos = 0;
        while (lastTime < time) {
            lastTime += 0.0001;
            double xDiv = xFrontend.v(lastTime);
            double yDiv = yFrontend.v(lastTime);
            xPos = toScreen(xScreen.v(xDiv), xmin, xmax, 10);
            yPos = toScreen(yScreen.v(yDiv), ymin, ymax, 8);
            addToTrace(new TracePoint(xPos, yPos, lastTime, xPos >= xmin && xPos <= xmax && yPos >= ymin && yPos <= ymax));
        }

        while (trace.size() > 2 && trace.getFirst().time < time - GLOW)
            trace.removeFirst();

        TracePoint l = null;
        for (TracePoint t : trace) {
            if (l != null) {
                if (l.onScreen || t.onScreen) {
                    double br = (GLOW - (time - t.time)) / GLOW;
                    if (br<0) br=0;
                    int c = (int) (GREEN + br * (255 - GREEN));
                    g.setColor(new Color(0,c,0));
                    g.drawLine(l.xPos, l.yPos, t.xPos, t.yPos);
                }
            }
            l = t;
        }


        lastTime = time;
        g.setColor(Color.GREEN);
        g.drawString("" + xPos + "," + yPos+", "+trace.size(), xmin + 5, ymin + 15);
    }

    private void addToTrace(TracePoint tp) {
        if (tp.onScreen) {
            if (lastOffScreen != null) {
                trace.add(lastOffScreen);
                lastOffScreen = null;
            }
            trace.add(tp);
        } else {
            if (lastOffScreen == null)
                trace.add(tp);
            lastOffScreen = tp;
        }
    }

    private int toScreen(double div, int min, int max, int divs) {
        return (int) (min + div * (max - min) / divs);
    }

    private interface ToScreen {
        double v(double v);
    }

    private static final class ValueToScreen implements ToScreen {
        private final Channel channel;

        public ValueToScreen(Channel channel) {
            this.channel = channel;
        }

        @Override
        public double v(double v) {
            return v + 5 + (channel.getPos() - 0.5) * 20;
        }
    }

    private class TimeToScreen implements ToScreen {
        public TimeToScreen(Horizontal horizontal) {
        }

        @Override
        public double v(double v) {
            return 0;
        }
    }

    private static final class TracePoint {
        private final int xPos;
        private final int yPos;
        private final double time;
        private final boolean onScreen;

        public TracePoint(int xPos, int yPos, double time, boolean onScreen) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.time = time;
            this.onScreen = onScreen;
        }
    }

}
