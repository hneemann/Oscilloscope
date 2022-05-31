package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.gui.SaveException;
import de.neemann.oscilloscope.signal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE;
import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE2;

/**
 * The oscilloscope
 */
public class Oscilloscope extends Container<Oscilloscope> implements ElementComponent.NeedsComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(Oscilloscope.class);
    /**
     * The screen update period
     */
    public static final int TIME_DELTA_MS = 20;
    private static final ArrayList<TimeBase> TIMES = createTimes();
    private static final ArrayList<Magnify> MAGNIFY = createMagnify();
    private static boolean debug;
    private final Trigger trigger;
    private final Horizontal horizontal;
    private final Channel ch1;
    private final Channel ch2;

    private final Switch<Mode> mode;
    private final PowerSwitch power;
    private final Screen screen;

    private boolean isInXY;
    private boolean useRT;
    private Model model;
    private ElementComponent elementComponent;
    private final ScheduledThreadPoolExecutor executor;
    private ScheduledFuture<?> timer;


    private static ArrayList<TimeBase> createTimes() {
        ArrayList<TimeBase> t = new ArrayList<>();
        t.add(new TimeBase(0));
        t.add(new TimeBase(0.5));
        t.add(new TimeBase(0.2));
        t.add(new TimeBase(0.1));
        t.add(new TimeBase(0.05));
        t.add(new TimeBase(0.02));
        t.add(new TimeBase(0.01));
        t.add(new TimeBase(0.005));
        t.add(new TimeBase(0.002));
        t.add(new TimeBase(0.001));
        t.add(new TimeBase(0.0005));
        t.add(new TimeBase(0.0002));
        t.add(new TimeBase(0.0001));
        t.add(new TimeBase(0.00005));
        t.add(new TimeBase(0.00002));
        t.add(new TimeBase(0.00001));
        t.add(new TimeBase(0.000005));
        t.add(new TimeBase(0.000002));
        t.add(new TimeBase(0.000001));
        t.add(new TimeBase(0.0000005));
        t.add(new TimeBase(0.0000002));
        return t;
    }

    private static ArrayList<Magnify> createMagnify() {
        ArrayList<Magnify> t = new ArrayList<>();
        t.add(new Magnify(5));
        t.add(new Magnify(2));
        t.add(new Magnify(1));
        t.add(new Magnify(0.5));
        t.add(new Magnify(0.2));
        t.add(new Magnify(0.1));
        t.add(new Magnify(0.05));
        t.add(new Magnify(0.02));
        t.add(new Magnify(0.01));
        t.add(new Magnify(0.005));
        return t;
    }

    /**
     * Creates a new oscilloscope.
     */
    public Oscilloscope() {
        super(SIZE * 58, SIZE * 26);

        trigger = new Trigger();
        Container<?> triggerContainer = new Container<>("Trigger", SIZE * 9, SIZE * 9)
                .add(trigger.setLevel(new Potentiometer("Level", SIZE).setPos(SIZE, SIZE * 8).setCenterZero()))
                .add(trigger.setMode(new Switch<TrigMode>("Mode").add(TrigMode.values()).setPos(SIZE * 3, SIZE)))
                .add(trigger.setSource(new Switch<TrigSource>("Source").add(TrigSource.values()).setPos(SIZE * 7, SIZE)))
                .add(trigger.setSlope(new Switch<Slope>("Slope").add(Slope.values()).setPos(SIZE * 4, SIZE * 7)))
                .add(trigger.setIn(new BNCInput("Trig. In").setPos(SIZE * 8, SIZE * 8)));

        horizontal = new Horizontal();
        Container<?> horizontalContainer = new Container<>("Horizontal", SIZE * 16, SIZE * 9)
                .add(horizontal.setPos(new Potentiometer("POS", SIZE).setPos(SIZE, SIZE * 2).setCenterZero()))
                .add(horizontal.setVar(new Potentiometer("VAR", SIZE).setPos(SIZE * 4, SIZE * 2)))
                .add(horizontal.setMag(new OnOffSwitch("MAG10")).setPos(SIZE * 2, SIZE * 7))
                .add(horizontal.setTimeBase(new SelectorKnobUnit<TimeBase>("TIME/DIV", SIZE * 3, "s").addAll(TIMES).setPos(SIZE * 11, SIZE * 5 + SIZE2)));

        ch1 = new Channel();
        ch2 = new Channel();
        mode = new Switch<Mode>("Mode").add(Mode.values());
        Container<?> verticalContainer = new Container<>("Vertical", SIZE * 28, SIZE * 12)
                .add(ch1.setAmplitude(new SelectorKnob<Magnify>("VOLTS/DIV", SIZE * 2).addAll(MAGNIFY).setPos(SIZE * 3, SIZE * 5)))
                .add(ch1.setPos(new Potentiometer("POS", SIZE).setCenterZero().setPos(SIZE * 9, SIZE * 2)))
                .add(ch1.setVar(new Potentiometer("VAR", SIZE).setPos(SIZE * 9, SIZE * 6)))
                .add(ch2.setAmplitude(new SelectorKnob<Magnify>("VOLTS/DIV", SIZE * 2).addAll(MAGNIFY).setPos(SIZE * 24, SIZE * 5)))
                .add(ch2.setPos(new Potentiometer("POS", SIZE).setCenterZero().setPos(SIZE * 18, SIZE * 2)))
                .add(ch2.setVar(new Potentiometer("VAR", SIZE).setPos(SIZE * 18, SIZE * 6)))
                .add(mode.setPos(SIZE * 13, SIZE * 3))
                .add(ch1.setCoupling(new Switch<Coupling>("").add(Coupling.values()).set(1).setPos(SIZE * 9, SIZE * 9)))
                .add(ch2.setCoupling(new Switch<Coupling>("").add(Coupling.values()).set(1).setPos(SIZE * 17, SIZE * 9)))
                .add(ch2.setInv(new OnOffSwitch("Ch 2 INV")).setPos(SIZE * 13, SIZE * 9))
                .add(ch1.setInput(new BNCInput("Ch 1 / X").setPos(SIZE * 5, SIZE * 11)))
                .add(ch2.setInput(new BNCInput("Ch 2 / Y").setPos(SIZE * 22, SIZE * 11)))
                .add(ch1.setMag5(new OnOffSwitch("MAG5")).setPos(SIZE, SIZE * 10))
                .add(ch2.setMag5(new OnOffSwitch("MAG5")).setPos(SIZE * 26, SIZE * 10));

        setBackground(Color.WHITE);
        add(triggerContainer.setPos(SIZE * 48, SIZE));
        add(horizontalContainer.setPos(SIZE * 29, SIZE));
        add(verticalContainer.setPos(SIZE * 29, SIZE * 13)).setPos(SIZE, SIZE);
        screen = new Screen(SIZE2);
        add(new Container<>(SIZE * 25, SIZE * 20).add(screen).setPos(SIZE, SIZE));

        power = new PowerSwitch();
        add(power.setPos(SIZE * 2, SIZE * 24));

        executor = new ScheduledThreadPoolExecutor(1);

        power.addObserver(() -> {
            if (power.isOn()) {
                createNewModel();

                timer = executor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (model != null) {
                            if (debug)
                                System.out.print("(");
                            long t = System.currentTimeMillis();
                            try {
                                model.updateBuffer(screen.getScreenBuffer());
                            } catch (Throwable e) {
                                SaveException.save(e);
                                e.printStackTrace();
                            }
                            t = System.currentTimeMillis() - t;
                            if (t > TIME_DELTA_MS)
                                LOGGER.info("slow: " + t + "ms");
                            if (debug)
                                System.out.println(")");
                        }
                        elementComponent.repaint();
                    }
                }, TIME_DELTA_MS, TIME_DELTA_MS, TimeUnit.MILLISECONDS);
                LOGGER.info("timer started");
            } else {
                model = null;
                stopTimer();
                screen.getScreenBuffer().clear();
            }

        });

        isInXY = horizontal.isXY();
        useRT = horizontal.requiresRT();
        horizontal.getTimeBaseKnob().addObserver(() -> {
            boolean xy = horizontal.isXY();
            boolean rt = horizontal.requiresRT();
            if (xy != isInXY || rt != useRT) {
                isInXY = xy;
                useRT = rt;
                createNewModel();
            }
        });
    }

    /**
     * Enables debug mode
     */
    public static void toggleDebug() {
        debug = !debug;
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel(false);
            timer = null;
            LOGGER.info("timer stopped");
        }
    }

    private void createNewModel() {
        if (isInXY)
            model = new ModelXY(this);
        else if (useRT)
            model = new ModelTimeRT(Oscilloscope.this);
        else
            model = new ModelTimeCalc(Oscilloscope.this);
    }

    /**
     * @return the trigger unit
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * @return the horizontal unit
     */
    public Horizontal getHorizontal() {
        return horizontal;
    }

    /**
     * @return channel 1
     */
    public Channel getCh1() {
        return ch1;
    }

    /**
     * @return channel 2
     */
    public Channel getCh2() {
        return ch2;
    }

    /**
     * @return the mode switch
     */
    public Switch<Mode> getMode() {
        return mode;
    }

    /**
     * Closes the oscilloscope. Stops all timers.
     */
    @Override
    public void close() {
        super.close();
        stopTimer();
        executor.shutdown();
    }

    /**
     * @return the power switch
     */
    public PowerSwitch getPowerSwitch() {
        return power;
    }

    @Override
    public void setComponent(ElementComponent elementComponent) {
        this.elementComponent = elementComponent;
    }
}
