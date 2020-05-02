package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The oscilloscope
 */
public class Oscilloscope extends Container<Oscilloscope> {

    private static final ArrayList<TimeBase> times = createTimes();
    private static final ArrayList<Magnify> magnify = createMagnify();

    private final Trigger trigger;
    private final Horizontal horizontal;
    private final Channel ch1;
    private final Channel ch2;

    private final Switch<Mode> mode;
    private final Switch<OffOn> power;

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
        t.add(new TimeBase(5));
        t.add(new TimeBase(2));
        t.add(new TimeBase(1));
        t.add(new TimeBase(0.5));
        t.add(new TimeBase(0.2));
        t.add(new TimeBase(0.1));
        t.add(new TimeBase(0.05));
        t.add(new TimeBase(0.02));
        t.add(new TimeBase(0.01));
        t.add(new TimeBase(0.005));
        return t;
    }

    public Oscilloscope() {
        super(SIZE * 58, SIZE * 26);

        trigger = new Trigger();
        Container<?> triggerContainer = new Container<>("Trigger", SIZE * 9, SIZE * 9)
                .add(trigger.setLevel(new Poti("Level", 20).setPos(SIZE, SIZE * 2)))
                .add(trigger.setMode(new Switch<TrigMode>("Mode").add(TrigMode.values()).setPos(SIZE * 5, SIZE)))
                .add(trigger.setSource(new Switch<TrigSource>("Source").add(TrigSource.values()).setPos(SIZE * 8, SIZE)))
                .add(trigger.setSlope(new Switch<String>("Slope").add("+").add("-").setPos(SIZE, SIZE * 7)))
                .add(trigger.setIn(new Input("Trig. In").setPos(SIZE * 6, SIZE * 8)));

        horizontal = new Horizontal();
        Container<?> horizontalContainer = new Container<>("Horizontal", SIZE * 16, SIZE * 9)
                .add(horizontal.setPos(new Poti("POS", 20).setPos(SIZE, SIZE * 2)))
                .add(horizontal.setVar(new Poti("VAR", 20).setPos(SIZE * 4, SIZE * 2)))
                .add(horizontal.setMag(new Switch<OffOn>("MAG10").add(OffOn.values()).setPos(SIZE * 2, SIZE * 7)))
                .add(horizontal.setTimeBase(new SelectorKnob<TimeBase>("TIME/DIV", 80).addAll(times).setPos(SIZE * 11, SIZE * 5 + SIZE2)));

        ch1 = new Channel();
        ch2 = new Channel();
        mode = new Switch<Mode>("Mode").add(Mode.values());
        Container<?> verticalContainer = new Container<>("Vertical", SIZE * 28, SIZE * 12)
                .add(ch1.setMag(new SelectorKnob<Magnify>("VOLTS/DIV", 40).addAll(magnify).setPos(SIZE * 3, SIZE * 5)))
                .add(ch1.setPos(new Poti("POS", 20).setPos(SIZE * 9, SIZE * 2)))
                .add(ch1.setVar(new Poti("VAR", 20).setPos(SIZE * 9, SIZE * 6)))
                .add(ch2.setMag(new SelectorKnob<Magnify>("VOLTS/DIV", 40).addAll(magnify).setPos(SIZE * 24, SIZE * 5)))
                .add(ch2.setPos(new Poti("POS", 20).setPos(SIZE * 18, SIZE * 2)))
                .add(ch2.setVar(new Poti("VAR", 20).setPos(SIZE * 18, SIZE * 6)))
                .add(mode.setPos(SIZE * 13, SIZE * 3))
                .add(ch1.setCoupling(new Switch<Coupling>("").add(Coupling.values()).set(1).setPos(SIZE * 8, SIZE * 9)))
                .add(ch2.setCoupling(new Switch<Coupling>("").add(Coupling.values()).set(1).setPos(SIZE * 18, SIZE * 9)))
                .add(ch2.setInv(new Switch<OffOn>("Ch 2 INV").add(OffOn.values()).setPos(SIZE * 13, SIZE * 9)))
                .add(ch1.setInput(new Input("Ch 1 / X").setPos(SIZE * 3, SIZE * 11)))
                .add(ch2.setInput(new Input("Ch 2 / Y").setPos(SIZE * 24, SIZE * 11)));

        add(triggerContainer.setPos(SIZE * 48, SIZE));
        add(horizontalContainer.setPos(SIZE * 29, SIZE));
        add(verticalContainer.setPos(SIZE * 29, SIZE * 13)).setPos(SIZE, SIZE);
        add(new Container<>(SIZE * 25, SIZE * 20).add(new Screen(SIZE2)).setPos(SIZE, SIZE));

        power = new Switch<OffOn>("Power").add(OffOn.values());
        add(power.setPos(SIZE * 2, SIZE * 24));
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public Horizontal getHorizontal() {
        return horizontal;
    }

    public Channel getCh1() {
        return ch1;
    }

    public Channel getCh2() {
        return ch2;
    }

    public Switch<Mode> getMode() {
        return mode;
    }

    public Switch<OffOn> getPower() {
        return power;
    }
}
