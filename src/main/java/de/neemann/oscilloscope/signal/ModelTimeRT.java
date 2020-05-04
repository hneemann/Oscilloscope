package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Mode;
import de.neemann.oscilloscope.draw.elements.Switch;
import de.neemann.oscilloscope.draw.elements.TrigMode;
import de.neemann.oscilloscope.draw.elements.osco.Horizontal;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.elements.osco.Trigger;

import java.awt.*;

/**
 * Scope model in normal time mode
 */
public class ModelTimeRT implements Model {
    private final Frontend frontend1;
    private final Frontend frontend2;
    private final YValueToScreen screen1;
    private final YValueToScreen screen2;
    private final Horizontal horizontal;
    private final Trigger trigger;
    private final Switch<Mode> mode;
    private final long timeOffset;
    private ScreenBuffer buffer;
    private boolean isRunning = false;
    private double tStart;
    private double tLast;

    /**
     * Used to simulate the scope in normal mode
     *
     * @param signal1 signal channel 1
     * @param signal2 signal channel 2
     * @param osco    the oscilloscope
     */
    public ModelTimeRT(PeriodicSignal signal1, PeriodicSignal signal2, Oscilloscope osco) {
        if (osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        this.frontend1 = new Frontend(signal1, osco.getCh1());
        this.screen1 = new YValueToScreen(osco.getCh1().getPosPoti(), 8);
        this.frontend2 = new Frontend(signal2, osco.getCh2());
        this.screen2 = new YValueToScreen(osco.getCh2().getPosPoti(), 8);
        this.horizontal = osco.getHorizontal();
        this.trigger = osco.getTrigger();
        this.mode = osco.getMode();
        timeOffset = System.currentTimeMillis();
    }

    @Override
    public void drawTo(Graphics g, int xmin, int xmax, int ymin, int ymax) {
        int width = xmax - xmin;
        int heigth = ymax - ymin;
        double timePerPixel = horizontal.getTimePerDiv() * 10 / width;
        double tNow = (System.currentTimeMillis() - timeOffset) / 1000.0;

        if (buffer == null)
            buffer = new ScreenBuffer(width, heigth);

        buffer.darken();

        if (trigger.getTrigMode() == TrigMode.TV_H || trigger.getTrigMode() == TrigMode.TV_V) {
            tLast = tNow;
            return;
        }

        if (!isRunning) {
            if (trigger.getTrigMode() == TrigMode.AUTO) {
                tStart = tNow;
                tLast = tNow;
                isRunning = true;
            } else {
                Trigger.Trig trig = null;
                switch (trigger.getTrigSource()) {
                    case Ch_1:
                        trig = trigger.wasTrig(frontend1, timePerPixel, tLast, tNow);
                        break;
                    case Ch_2:
                        trig = trigger.wasTrig(frontend2, timePerPixel, tLast, tNow);
                        break;
                    case LINE:
                        double nextTrig = ((long) (tLast / 0.02) + 1) * 0.02;
                        trig = new Trigger.Trig(nextTrig, nextTrig < tNow);
                        break;
                }
                if (trig != null && trig.isFound()) {
                    tStart = trig.getT();
                    tLast = tStart;
                    isRunning = true;
                }
            }
        }

        if (isRunning) {
            switch (mode.getSelected()) {
                case Ch_1:
                    drawTrace(t -> screen1.v(frontend1.v(t), heigth), tStart, tLast, tNow, timePerPixel);
                    break;
                case Ch_2:
                    drawTrace(t -> screen2.v(frontend2.v(t), heigth), tStart, tLast, tNow, timePerPixel);
                    break;
                case DUAL:
                    drawTrace(t -> screen1.v(frontend1.v(t), heigth), tStart, tLast, tNow, timePerPixel);
                    drawTrace(t -> screen2.v(frontend2.v(t), heigth), tStart, tLast, tNow, timePerPixel);
                    break;
                case ADD:
                    drawTrace(t -> screen1.v(frontend1.v(t) + frontend2.v(t), heigth), tStart, tLast, tNow, timePerPixel);
            }
        }
        tLast = tNow;

        if ((tNow - tStart) / timePerPixel > width)
            isRunning = false;

        g.drawImage(buffer.getBuffer(), xmin, ymin, null);
    }

    interface Screen {
        int v(double t);
    }

    private void drawTrace(Screen screen, double t0, double t1, double t2, double timePerPixel) {
        int y0 = screen.v(t1);
        int x = (int) ((t1 - t0) / timePerPixel);
        while (t1 < t2) {
            int ym = screen.v(t1 + timePerPixel / 2);
            t1 += timePerPixel;
            int y1 = screen.v(t1);
            if (ym==y1 || ym==y0)
                buffer.drawTrace(x - 1, y0, x, y1);
            else
                buffer.drawBrightTrace(x - 1, y0, x, y1);
            y0 = y1;
            x++;
        }
    }
}