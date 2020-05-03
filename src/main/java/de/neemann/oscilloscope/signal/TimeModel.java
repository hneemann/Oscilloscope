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
public class TimeModel implements Model {
    private final Frontend frontend1;
    private final Frontend frontend2;
    private final ValueToScreen screen1;
    private final ValueToScreen screen2;
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
    public TimeModel(PeriodicSignal signal1, PeriodicSignal signal2, Oscilloscope osco) {
        if (osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        this.frontend1 = new Frontend(signal1, osco.getCh1());
        this.screen1 = new ValueToScreen(osco.getCh1().getPosPoti(), 8);
        this.frontend2 = new Frontend(signal2, osco.getCh2());
        this.screen2 = new ValueToScreen(osco.getCh2().getPosPoti(), 8);
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
            g.setColor(Color.GREEN);
            switch (mode.getSelected()) {
                case Ch_1:
                    drawTrace(g, t -> screen1.v(frontend1.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime);
                    break;
                case Ch_2:
                    drawTrace(g, t -> screen2.v(frontend2.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime);
                    break;
                case DUAL:
                    drawTrace(g, t -> screen1.v(frontend1.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime);
                    drawTrace(g, t -> screen2.v(frontend2.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime);
                    break;
                case ADD:
                    drawTrace(g, t -> screen1.v(frontend1.v(t) + frontend2.v(t), heigth) + ymin, xmin, xmax, timePerPixel, triggerTime);
            }
        }
    }

    interface Screen {
        int v(double t);
    }

    private void drawTrace(Graphics g, Screen screen, int xmin, int xmax, double timePerPixel, double t) {
        int y0 = screen.v(t);
        for (int x = xmin + 1; x <= xmax; x++) {
            t += timePerPixel;
            int y1 = screen.v(t);
            g.drawLine(x - 1, y0, x, y1);
            y0 = y1;
        }
    }
}
