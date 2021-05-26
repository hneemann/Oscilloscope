package de.neemann.oscilloscope.experiments;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.gui.Wire;

import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE;
import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE2;

/**
 * A simple setup with two generators available
 */
public final class General implements Experiment {

    /**
     * The name of the general exercise.
     */
    public static final String NAME = "General";

    General() {
    }

    @Override
    public String toString() {
        return NAME;
    }

    @Override
    public Container<?> create() {
        Generator gen1 = new Generator("gen1").setPos(SIZE + SIZE2, SIZE * 30 + SIZE2);
        return new Container<>()
                .add(new Oscilloscope().setPos(SIZE + SIZE2, SIZE + SIZE2))
                .add(gen1)
                .add(new Generator("gen2", gen1).setPos(SIZE * 32 + SIZE2, SIZE * 30 + SIZE2));
    }

    @Override
    public void setup(ElementComponent component) {
        Oscilloscope oscilloscope = Experiment.getOscilloscope(component);
        Generator gen1 = Experiment.getGenerator(component, "gen1");
        Generator gen2 = Experiment.getGenerator(component, "gen2");

        oscilloscope.getHorizontal().getPosPoti().set(0.5);
        oscilloscope.getCh1().getPosPoti().set(0.5);
        oscilloscope.getCh1().getCouplingSwitch().set(2);
        oscilloscope.getCh1().getAmplitudeSwitch().set(2);
        oscilloscope.getCh2().getPosPoti().set(0.5);
        oscilloscope.getCh2().getCouplingSwitch().set(2);
        oscilloscope.getCh2().getAmplitudeSwitch().set(2);
        oscilloscope.getPowerSwitch().set(1);

        gen1.getPowerSwitch().set(1);
        gen1.getAmplitude().set(0.18);
        gen1.setFrequencySwitch().set(1);
        gen2.getPowerSwitch().set(1);
        gen2.getAmplitude().set(0.18);
        gen2.setFrequencySwitch().set(1);
        gen2.setFrequencyFinePoti().set(Math.log(3) / Math.log(10));

        component.add(new Wire(gen1.getOutput(), oscilloscope.getCh1().getInput()));
        component.add(new Wire(gen2.getOutput(), oscilloscope.getCh2().getInput()));
    }

}
