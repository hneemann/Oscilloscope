package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Potentiometer;
import de.neemann.oscilloscope.draw.elements.osco.Channel;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;

/**
 * The simulation used for x-y mode
 */
public class ModelXY implements Model {
    private static final int MAX_LOOP = 10000;

    private final long timeOffset;
    private final Channel channel1;
    private final Channel channel2;
    private final Potentiometer xPotentiometer;
    private double lastTime;
    private int lastxPos;
    private int lastyPos;

    /**
     * Creates a new model for x-y mode
     *
     * @param osco the used oscilloscope
     */
    public ModelXY(Oscilloscope osco) {
        if (!osco.getHorizontal().isXY())
            throw new RuntimeException("wrong model");

        channel1 = osco.getCh1();
        channel2 = osco.getCh2();

        xPotentiometer = osco.getHorizontal().getPosPoti();

        timeOffset = System.currentTimeMillis();
        lastTime = getTimeInMillis() / 1000.0;
    }

    private long getTimeInMillis() {
        return System.currentTimeMillis() - timeOffset;
    }

    @Override
    public void updateBuffer(ScreenBuffer screenBuffer) {
        PeriodicSignal xFrontend = channel1.getSignal();
        PeriodicSignal yFrontend = channel2.getSignal();

        int width = screenBuffer.getWidth();
        int height = screenBuffer.getHeight();
        ValueToScreen xScreen = new ValueToScreen(xFrontend, xPotentiometer.get(), 10, width);
        ValueToScreen yScreen = new ValueToScreen(yFrontend, channel2.getPos(), 8, height);

        double time = getTimeInMillis() / 1000.0;

        double period = Math.max(xFrontend.period(), yFrontend.period());

        double timeDelta = period / 1000;
        if (timeDelta > 0.0001)
            timeDelta = 0.0001;
        int n = (int) ((time - lastTime) / timeDelta);
        if (n > MAX_LOOP)
            timeDelta = (time - lastTime) / MAX_LOOP;

        screenBuffer.darken();
        while (lastTime < time) {
            lastTime += timeDelta;
            int xPos = (int) xScreen.v(lastTime);
            int yPos = (int) yScreen.v(lastTime);
            screenBuffer.drawTrace(lastxPos, lastyPos, xPos, yPos);
            lastxPos = xPos;
            lastyPos = yPos;
        }
    }
}
