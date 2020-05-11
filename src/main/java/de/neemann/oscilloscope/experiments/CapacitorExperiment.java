package de.neemann.oscilloscope.experiments;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.capacitor.Capacitor;
import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.gui.Wire;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The capacitor experimental setup
 */
public final class CapacitorExperiment implements Experiment {

    CapacitorExperiment() {
    }

    @Override
    public String toString() {
        return "Capacitor";
    }

    @Override
    public Container<?> create() {
        return new Container<>()
                .add(new Oscilloscope().setPos(SIZE + SIZE2, SIZE + SIZE2))
                .add(new Generator("gen1").setPos(SIZE + SIZE2, SIZE * 31 + SIZE2))
                .add(new Capacitor().setPos(SIZE * 32 + SIZE2, SIZE * 31 + SIZE2));
    }

    @Override
    public void setup(ElementComponent component) {
        Oscilloscope oscilloscope = Experiment.getOscilloscope(component);
        Generator gen1 = Experiment.getGenerator(component, "gen1");
        Capacitor capacitor = Experiment.get(component, e -> e instanceof Capacitor);


        oscilloscope.getCh1().getPosPoti().set(0.5);
        oscilloscope.getCh1().getCouplingSwitch().set(2);
        oscilloscope.getCh1().getAmplitudeSwitch().set(3);
        oscilloscope.getCh2().getPosPoti().set(0.5);
        oscilloscope.getCh2().getCouplingSwitch().set(2);
        oscilloscope.getCh2().getAmplitudeSwitch().set(6);
        oscilloscope.getCh2().getInvSwitch().set(1);
        oscilloscope.getMode().set(2);
        oscilloscope.getTrigger().getTrigModeSwitch().set(1);
        oscilloscope.getTrigger().getTrigSourceSwitch().set(3);
        oscilloscope.getTrigger().getLevelPoti().set(0.5);
        oscilloscope.getPowerSwitch().set(1);
        oscilloscope.getHorizontal().getTimeBaseKnob().set(8);

        gen1.getPowerSwitch().set(1);
        gen1.getAmplitude().set(0.18);
        gen1.setFrequencySwitch().set(2);

        component.add(new Wire(gen1.getOutput(), capacitor.getInput()));
        component.add(new Wire(capacitor.getVoltCapacitor(), oscilloscope.getCh1().getInput()));
        component.add(new Wire(capacitor.getVoltRes(), oscilloscope.getCh2().getInput()));
        component.add(new Wire(gen1.getTrigOutput(), oscilloscope.getTrigger().getTrigIn()));
    }

}
