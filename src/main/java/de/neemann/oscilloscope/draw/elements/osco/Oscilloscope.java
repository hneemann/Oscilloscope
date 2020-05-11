package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.signal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The oscilloscope
 */
public class Oscilloscope extends Container<Oscilloscope> implements ElementComponent.NeedsComponent {
    private static boolean debug = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(Oscilloscope.class);
    /**
     * The screen update period
     */
    public static final int TIME_DELTA_MS = 20;
    private static final ArrayList<TimeBase> TIMES = createTimes();
    private static final ArrayList<Magnify> MAGNIFY = createMagnify();

    /**
     * Sets debug mode
     */
    public static void setDebug() {
        debug = true;
    }

    private final Trigger trigger;
    private final Horizontal horizontal;
    private final Channel ch1;
    private final Channel ch2;

    private final Switch<Mode> mode;
    private final PowerSwitch power;
    private final Screen screen;

    private PeriodicSignal signal1 = PeriodicSignal.GND;
    private PeriodicSignal signal2 = PeriodicSignal.GND;
    private PeriodicSignal triggerIn = PeriodicSignal.GND;
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
                .add(trigger.setLevel(new Poti("Level", 20).setPos(SIZE, SIZE * 8)))
                .add(trigger.setMode(new Switch<TrigMode>("Mode").add(TrigMode.values()).setPos(SIZE * 3, SIZE)))
                .add(trigger.setSource(new Switch<TrigSource>("Source").add(TrigSource.values()).setPos(SIZE * 7, SIZE)))
                .add(trigger.setSlope(new Switch<String>("Slope").add("+").add("-").setPos(SIZE * 4, SIZE * 7)))
                .add(trigger.setIn(new BNCInput("Trig. In").setInputSetter(this::setTriggerIn).setPos(SIZE * 8, SIZE * 8)));

        horizontal = new Horizontal();
        Container<?> horizontalContainer = new Container<>("Horizontal", SIZE * 16, SIZE * 9)
                .add(horizontal.setPos(new Poti("POS", 20).setPos(SIZE, SIZE * 2)))
                .add(horizontal.setVar(new Poti("VAR", 20).setPos(SIZE * 4, SIZE * 2)))
                .add(horizontal.setMag(new Switch<OffOn>("MAG10").add(OffOn.values()).setPos(SIZE * 2, SIZE * 7)))
                .add(horizontal.setTimeBase(new SelectorKnobUnit<TimeBase>("TIME/DIV", 60, "s").addAll(TIMES).setPos(SIZE * 11, SIZE * 5 + SIZE2)));

        ch1 = new Channel();
        ch2 = new Channel();
        mode = new Switch<Mode>("Mode").add(Mode.values());
        Container<?> verticalContainer = new Container<>("Vertical", SIZE * 28, SIZE * 12)
                .add(ch1.setAmplitude(new SelectorKnob<Magnify>("VOLTS/DIV", 40).addAll(MAGNIFY).setPos(SIZE * 3, SIZE * 5)))
                .add(ch1.setPos(new Poti("POS", 20).setPos(SIZE * 9, SIZE * 2)))
                .add(ch1.setVar(new Poti("VAR", 20).setPos(SIZE * 9, SIZE * 6)))
                .add(ch2.setAmplitude(new SelectorKnob<Magnify>("VOLTS/DIV", 40).addAll(MAGNIFY).setPos(SIZE * 24, SIZE * 5)))
                .add(ch2.setPos(new Poti("POS", 20).setPos(SIZE * 18, SIZE * 2)))
                .add(ch2.setVar(new Poti("VAR", 20).setPos(SIZE * 18, SIZE * 6)))
                .add(mode.setPos(SIZE * 13, SIZE * 3))
                .add(ch1.setCoupling(new Switch<Coupling>("").add(Coupling.values()).set(1).setPos(SIZE * 9, SIZE * 9)))
                .add(ch2.setCoupling(new Switch<Coupling>("").add(Coupling.values()).set(1).setPos(SIZE * 17, SIZE * 9)))
                .add(ch2.setInv(new Switch<OffOn>("Ch 2 INV").add(OffOn.values()).setPos(SIZE * 13, SIZE * 9)))
                .add(ch1.setInput(new BNCInput("Ch 1 / X").setInputSetter(this::setSignalCh1).setPos(SIZE * 5, SIZE * 11)))
                .add(ch2.setInput(new BNCInput("Ch 2 / Y").setInputSetter(this::setSignalCh2).setPos(SIZE * 22, SIZE * 11)))
                .add(ch1.setMag5(new Switch<OffOn>("MAG5").add(OffOn.values()).setPos(SIZE, SIZE * 10)))
                .add(ch2.setMag5(new Switch<OffOn>("MAG5").add(OffOn.values()).setPos(SIZE * 26, SIZE * 10)));

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
            if (power.is(OffOn.On)) {
                createNewModel();

                timer = executor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (debug)
                            System.out.println("*");
                        if (model != null) {
                            long t = System.currentTimeMillis();
                            model.updateBuffer(screen.getScreenBuffer());
                            t = System.currentTimeMillis() - t;
                            if (t > TIME_DELTA_MS)
                                LOGGER.info("slow " + t);
                        }
                        elementComponent.repaint();
                    }
                }, TIME_DELTA_MS, TIME_DELTA_MS, TimeUnit.MILLISECONDS);
                LOGGER.info("timer started");
            } else {
                model = null;
                stopTimer();
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
     * Sets the signal for channel 1
     *
     * @param s the signal
     */
    public void setSignalCh1(PeriodicSignal s) {
        signal1 = s;
        createNewModel();
    }

    /**
     * Sets the signal for channel 2
     *
     * @param s the signal
     */
    public void setSignalCh2(PeriodicSignal s) {
        signal2 = s;
        createNewModel();
    }


    /**
     * Sets the trigger input
     *
     * @param s the signal
     */
    public void setTriggerIn(PeriodicSignal s) {
        triggerIn = s;
        createNewModel();
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

    /**
     * @return the channel 1 input signal
     */
    public PeriodicSignal getSignal1() {
        return signal1;
    }

    /**
     * @return the channel 2 input signal
     */
    public PeriodicSignal getSignal2() {
        return signal2;
    }

    /**
     * @return the trigger input signal
     */
    public PeriodicSignal getTriggerIn() {
        return triggerIn;
    }

    @Override
    public void setComponent(ElementComponent elementComponent) {
        this.elementComponent = elementComponent;
    }
}
