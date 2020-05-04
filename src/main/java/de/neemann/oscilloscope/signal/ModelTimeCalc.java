package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Mode;
import de.neemann.oscilloscope.draw.elements.Switch;
import de.neemann.oscilloscope.draw.elements.TrigMode;
import de.neemann.oscilloscope.draw.elements.osco.Horizontal;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.elements.osco.Trigger;
import de.neemann.oscilloscope.draw.graphics.Style;

import java.awt.*;

/**
 * Scope model in normal time mode
 */
public class ModelTimeCalc implements Model {
    /**
     * Speed dependent trace color
     */
    private static final Color[] SPEEDCOLOR = new Color[16];

    static {
        int c0 = Style.SCREEN.getColor().getGreen();
        for (int s = 0; s < 16; s++)
            SPEEDCOLOR[s] = new Color(0, 255 - s * (255 - c0) / 15, 0);
    }

    private final Frontend frontend1;
    private final Frontend frontend2;
    private final YValueToScreen screen1;
    private final YValueToScreen screen2;
    private final Horizontal horizontal;
    private final Trigger trigger;
    private final Switch<Mode> mode;
    private final long timeOffset;

    /**
     * Used to simulate the scope in normal mode
     *
     * @param signal1 signal channel 1
     * @param signal2 signal channel 2
     * @param osco    the oscilloscope
     */
    public ModelTimeCalc(PeriodicSignal signal1, PeriodicSignal signal2, Oscilloscope osco) {
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
        double t0 = (System.currentTimeMillis() - timeOffset) / 1000.0;

        if (trigger.getTrigMode() == TrigMode.TV_H || trigger.getTrigMode() == TrigMode.TV_V)
            return;

        Trigger.Trig trig = null;
        switch (trigger.getTrigSource()) {
            case Ch_1:
                trig = trigger.getTriggerTime(frontend1, timePerPixel, t0);
                break;
            case Ch_2:
                trig = trigger.getTriggerTime(frontend2, timePerPixel, t0);
                break;
            case LINE:
                trig = new Trigger.Trig(((long) (t0 / 0.02) + 1) * 0.02, true);
        }

        double triggerTime = t0;
        if (trig != null)
            triggerTime = trig.getT() - horizontal.getPos() * timePerPixel * width;

        boolean show = true;
        if (trig != null)
            show = trig.isFound() || trigger.getTrigMode() == TrigMode.AUTO;

        if (show) {
            switch (mode.getSelected()) {
                case Ch_1:
                    drawTrace(g, t -> screen1.v(frontend1.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime, heigth);
                    break;
                case Ch_2:
                    drawTrace(g, t -> screen2.v(frontend2.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime, heigth);
                    break;
                case DUAL:
                    drawTrace(g, t -> screen1.v(frontend1.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime, heigth);
                    drawTrace(g, t -> screen2.v(frontend2.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime, heigth);
                    break;
                case ADD:
                    drawTrace(g, t -> screen1.v(frontend1.v(t) + frontend2.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime, heigth);
            }
        }
    }

    interface Screen {
        int v(double t);
    }

    private void drawTrace(Graphics g, Screen screen, int xmin, int xmax, double timePerPixel, double t, int height) {
        int y0 = screen.v(t);
        for (int x = xmin + 1; x <= xmax; x++) {
            t += timePerPixel;
            int y1 = screen.v(t);

            // speed on screen, 0 slow, 15 is very fast
            int speed = Math.min(Math.abs(y0 - y1) * 70 / height, 15);
            g.setColor(SPEEDCOLOR[speed]);

            g.drawLine(x - 1, y0, x, y1);
            y0 = y1;
        }
    }
}
