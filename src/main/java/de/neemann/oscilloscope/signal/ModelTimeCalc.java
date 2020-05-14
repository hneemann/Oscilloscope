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
 * Scope model in normal time mode.
 * Humans are not able to see the trace moving across the screen.
 * The trace seems like a stable line. In this case the trans can be calculated
 * and drawn independent of real time.
 */
public class ModelTimeCalc implements Model {
    private final Horizontal horizontal;
    private final Trigger trigger;
    private final Switch<Mode> mode;
    private final long timeOffset;
    private final SignalProvider triggerInProvider;
    private final Channel channel1;
    private final Channel channel2;

    /**
     * Used to simulate the scope in normal mode
     *
     * @param osco the oscilloscope
     */
    public ModelTimeCalc(Oscilloscope osco) {
        if (osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        channel1 = osco.getCh1();
        channel2 = osco.getCh2();
        this.horizontal = osco.getHorizontal();
        this.triggerInProvider = osco.getTrigger().getTrigIn().getSignalProvider();
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
            case EXT:
                trig = trigger.wasTrig(triggerIn, timePerPixel, t0, t0 + triggerIn.period(), triggerIn.mean());
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

        screenBuffer.clear();
        if (show) {
            switch (mode.getSelected()) {
                case Ch_1:
                    drawTrace(screenBuffer, screen1, width, timePerPixel, triggerTime);
                    break;
                case Ch_2:
                    drawTrace(screenBuffer, screen2, width, timePerPixel, triggerTime);
                    break;
                case DUAL:
                    drawTrace(screenBuffer, screen1, width, timePerPixel, triggerTime);
                    drawTrace(screenBuffer, screen2, width, timePerPixel, triggerTime);
                    break;
                case ADD:
                    drawTrace(screenBuffer, screenSum, width, timePerPixel, triggerTime);
            }
        }
    }

    private void drawTrace(ScreenBuffer g, PeriodicSignal screen, int width, double timePerPixel, double t) {
        // to avoid a beat (german Schwebung) don't plot anything if frequency is extremely
        // high compared to screen width. In this case: if a full wave length is less then 2 pixels wide.
        if (screen.period() > timePerPixel * 2) {
            int y0 = (int) screen.v(t);
            for (int x = 0; x < width; x++) {
                t += timePerPixel;
                int y1 = (int) screen.v(t);
                g.drawTrace(x - 1, y0, x, y1);
                y0 = y1;
            }
        }
    }
}
