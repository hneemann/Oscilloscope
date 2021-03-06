package de.neemann.oscilloscope.experiments;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.diode.Diode;
import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.gui.ElementComponent;
import de.neemann.oscilloscope.gui.Wire;

import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE;
import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE2;

/**
 * The diode experimental setup
 */
public final class DiodeExperiment implements Experiment {

    DiodeExperiment() {
    }

    @Override
    public String toString() {
        return "Diode";
    }

    @Override
    public Container<?> create() {
        return new Container<>()
                .add(new Oscilloscope().setPos(SIZE + SIZE2, SIZE + SIZE2))
                .add(new Generator("gen1").setPos(SIZE + SIZE2, SIZE * 30 + SIZE2))
                .add(new Diode().setPos(SIZE * 32 + SIZE2, SIZE * 30 + SIZE2));
    }

    @Override
    public void setup(ElementComponent component) {
        Oscilloscope oscilloscope = Experiment.getOscilloscope(component);
        Generator gen1 = Experiment.getGenerator(component, "gen1");
        Diode diode = Experiment.get(component, e -> e instanceof Diode);

        oscilloscope.getHorizontal().getPosPoti().set(0.5);
        oscilloscope.getCh1().getPosPoti().set(0.5);
        oscilloscope.getCh1().getCouplingSwitch().set(2);
        oscilloscope.getCh1().getAmplitudeSwitch().set(4);
        oscilloscope.getCh2().getPosPoti().set(0.5);
        oscilloscope.getCh2().getCouplingSwitch().set(2);
        oscilloscope.getCh2().getAmplitudeSwitch().set(3);
        oscilloscope.getCh2().getInvSwitch().set(1);
        oscilloscope.getPowerSwitch().set(1);

        gen1.getPowerSwitch().set(1);
        gen1.getAmplitude().set(0.18);
        gen1.setFrequencySwitch().set(1);

        component.add(new Wire(gen1.getOutput(), diode.getInput()));
        component.add(new Wire(diode.getVoltDiode(), oscilloscope.getCh1().getInput()));
        component.add(new Wire(diode.getVoltRes(), oscilloscope.getCh2().getInput()));
    }

}
