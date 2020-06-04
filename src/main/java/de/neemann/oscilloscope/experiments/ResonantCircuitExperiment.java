package de.neemann.oscilloscope.experiments;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.draw.elements.resonantCircuit.ResonantCircuit;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.gui.Wire;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The capacitor experimental setup
 */
public final class ResonantCircuitExperiment implements Experiment {

    ResonantCircuitExperiment() {
    }

    @Override
    public String toString() {
        return "Resonant Circuit";
    }

    @Override
    public Container<?> create() {
        return new Container<>()
                .add(new Oscilloscope().setPos(SIZE + SIZE2, SIZE + SIZE2))
                .add(new Generator("gen1").setPos(SIZE + SIZE2, SIZE * 30 + SIZE2))
                .add(new ResonantCircuit().setPos(SIZE * 32 + SIZE2, SIZE * 30 + SIZE2));
    }

    @Override
    public void setup(ElementComponent component) {
        Oscilloscope oscilloscope = Experiment.getOscilloscope(component);
        Generator gen1 = Experiment.getGenerator(component, "gen1");
        ResonantCircuit capacitor = Experiment.get(component, e -> e instanceof ResonantCircuit);


        oscilloscope.getCh1().getPosPoti().set(0.5);
        oscilloscope.getCh1().getCouplingSwitch().set(2);
        oscilloscope.getCh1().getAmplitudeSwitch().set(3);
        oscilloscope.getCh2().getPosPoti().set(0.5);
        oscilloscope.getCh2().getCouplingSwitch().set(2);
        oscilloscope.getCh2().getAmplitudeSwitch().set(3);
        oscilloscope.getMode().set(2);
        oscilloscope.getTrigger().getTrigSourceSwitch().set(3);
        oscilloscope.getTrigger().getTrigModeSwitch().set(1);
        oscilloscope.getTrigger().getLevelPoti().set(0.5);
        oscilloscope.getPowerSwitch().set(1);
        oscilloscope.getHorizontal().getTimeBaseKnob().set(12);

        gen1.getPowerSwitch().set(1);
        gen1.getAmplitude().set(0.2);
        gen1.setFrequencySwitch().set(2);
        gen1.setFrequencyFinePoti().set(Math.log(1.591) / Math.log(10));

        component.add(new Wire(gen1.getOutput(), capacitor.getInput()));
        component.add(new Wire(gen1.getOutput(), oscilloscope.getCh1().getInput()));
        component.add(new Wire(capacitor.getVoltRes(), oscilloscope.getCh2().getInput()));
        component.add(new Wire(gen1.getTrigOutput(), oscilloscope.getTrigger().getTrigIn()));
    }

}
