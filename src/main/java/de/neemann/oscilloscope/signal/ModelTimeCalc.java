package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Mode;
import de.neemann.oscilloscope.draw.elements.Switch;
import de.neemann.oscilloscope.draw.elements.TrigMode;
import de.neemann.oscilloscope.draw.elements.osco.Channel;
import de.neemann.oscilloscope.draw.elements.osco.Horizontal;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.elements.osco.Trigger;

/**
 * Scope model in normal time mode
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
        Frontend frontend1 = new Frontend(channel1);
        YValueToScreen screen1 = new YValueToScreen(channel1.getPos(), 8);
        Frontend frontend2 = new Frontend(channel2);
        YValueToScreen screen2 = new YValueToScreen(channel2.getPos(), 8);
        PeriodicSignal triggerIn = triggerInProvider.getSignal();

        int width = screenBuffer.getWidth();
        int heigth = screenBuffer.getHeight();
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
                    drawTrace(screenBuffer, t -> screen1.v(frontend1.v(t), heigth), width, timePerPixel, triggerTime);
                    break;
                case Ch_2:
                    drawTrace(screenBuffer, t -> screen2.v(frontend2.v(t), heigth), width, timePerPixel, triggerTime);
                    break;
                case DUAL:
                    drawTrace(screenBuffer, t -> screen1.v(frontend1.v(t), heigth), width, timePerPixel, triggerTime);
                    drawTrace(screenBuffer, t -> screen2.v(frontend2.v(t), heigth), width, timePerPixel, triggerTime);
                    break;
                case ADD:
                    drawTrace(screenBuffer, t -> screen1.v(frontend1.v(t) + frontend2.v(t), heigth), width, timePerPixel, triggerTime);
            }
        }
    }

    interface Screen {
        int v(double t);
    }

    private void drawTrace(ScreenBuffer g, Screen screen, int width, double timePerPixel, double t) {
        int y0 = screen.v(t);
        for (int x = 0; x < width; x++) {
            t += timePerPixel;
            int y1 = screen.v(t);
            g.drawTrace(x - 1, y0, x, y1);
            y0 = y1;
        }
    }
}
