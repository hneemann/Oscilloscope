package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Mode;
import de.neemann.oscilloscope.draw.elements.Switch;
import de.neemann.oscilloscope.draw.elements.TrigMode;
import de.neemann.oscilloscope.draw.elements.osco.Channel;
import de.neemann.oscilloscope.draw.elements.osco.Horizontal;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.elements.osco.Trigger;
import de.neemann.oscilloscope.signal.primitives.Sum;

/**
 * Scope model in real time mode.
 * Humans can see the trace moving across the screen.
 * Drawing has to consider real time.
 */
public class ModelTimeRT implements Model {
    private final Horizontal horizontal;
    private final Trigger trigger;
    private final Switch<Mode> mode;
    private final long timeOffset;
    private final SignalProvider triggerInProvider;
    private final Channel channel1;
    private final Channel channel2;
    private boolean isRunning = false;
    private double tStart;
    private double tLast;

    /**
     * Used to simulate the scope in normal mode
     *
     * @param osco the oscilloscope
     */
    public ModelTimeRT(Oscilloscope osco) {
        if (osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        channel1 = osco.getCh1();
        channel2 = osco.getCh2();
        this.triggerInProvider = osco.getTrigger().getTrigIn().getSignalProvider();
        this.horizontal = osco.getHorizontal();
        this.trigger = osco.getTrigger();
        this.mode = osco.getMode();
        timeOffset = System.currentTimeMillis();
    }

    @Override
    public void updateBuffer(ScreenBuffer screenBuffer) {
        PeriodicSignal frontend1 = channel1.getSignal();
        PeriodicSignal frontend2 = channel2.getSignal();
        PeriodicSignal triggerIn = triggerInProvider.getSignal();

        int width = screenBuffer.getWidth();
        int height = screenBuffer.getHeight();
        ValueToScreen screen1 = new ValueToScreen(frontend1, channel1.getPos(), 8, height);
        ValueToScreen screen2 = new ValueToScreen(frontend2, channel2.getPos(), 8, height);
        ValueToScreen screenSum = new ValueToScreen(new Sum(frontend1, frontend2), channel1.getPos(), 8, height);

        double timePerPixel = horizontal.getTimePerDiv() * 10 / width;
        double tNow = (System.currentTimeMillis() - timeOffset) / 1000.0;

        screenBuffer.darken();

        if (trigger.getTrigMode() == TrigMode.TV_H || trigger.getTrigMode() == TrigMode.TV_V) {
            tLast = tNow;
            return;
        }

        if (!isRunning) {
            Trigger.Trig trig = null;
            switch (trigger.getTrigSource()) {
                case Ch_1:
                    trig = trigger.wasTrig(frontend1, timePerPixel, tLast, tNow);
                    break;
                case Ch_2:
                    trig = trigger.wasTrig(frontend2, timePerPixel, tLast, tNow);
                    break;
                case EXT:
                    trig = trigger.wasTrig(triggerIn, timePerPixel, tLast, tNow, triggerIn.mean());
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
            } else if (trigger.getTrigMode() == TrigMode.AUTO) {
                tStart = tNow;
                tLast = tNow;
                isRunning = true;
            }
        }

        if (isRunning) {
            screenBuffer.darken();
            switch (mode.getSelected()) {
                case Ch_1:
                    drawTrace(screenBuffer, screen1, tStart, tLast, tNow, timePerPixel);
                    break;
                case Ch_2:
                    drawTrace(screenBuffer, screen2, tStart, tLast, tNow, timePerPixel);
                    break;
                case DUAL:
                    drawTrace(screenBuffer, screen1, tStart, tLast, tNow, timePerPixel);
                    drawTrace(screenBuffer, screen2, tStart, tLast, tNow, timePerPixel);
                    break;
                case ADD:
                    drawTrace(screenBuffer, screenSum, tStart, tLast, tNow, timePerPixel);
            }
        }
        tLast = tNow;

        if ((tNow - tStart) / timePerPixel > width)
            isRunning = false;
    }

    private void drawTrace(ScreenBuffer buffer, PeriodicSignal screen, double t0, double t1, double t2, double timePerPixel) {
        int y0 = (int) screen.v(t1);
        int x1 = (int) ((t1 - t0) / timePerPixel);
        int x0 = x1;

        double deltaTime = timePerPixel;
        double pixels = (t2 - t1) / deltaTime;
        if (pixels < 20)
            deltaTime = (t2 - t1) / 20;

        while (t1 < t2) {
            int ym = (int) screen.v(t1 + deltaTime / 2);
            t1 += deltaTime;
            x1 = (int) ((t1 - t0) / timePerPixel);
            int y1 = (int) screen.v(t1);
            if (ym == y1 || ym == y0)
                buffer.drawTrace(x0, y0, x1, y1);
            else
                buffer.drawBrightTrace(x0, y0, x1, y1);
            y0 = y1;
            x0 = x1;
        }
    }
}
