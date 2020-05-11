package de.neemann.oscilloscope.draw.elements.capacitor;

import de.neemann.oscilloscope.draw.elements.OffOn;
import de.neemann.oscilloscope.draw.elements.Switch;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.InterpolateLinear;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.PeriodicSignalWrapper;
import de.neemann.oscilloscope.signal.Sine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The model of the capacitor
 */
public class CapacitorModel implements Observer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CapacitorModel.class);

    private static final int POINTS = 1000;
    private static final double R = 1000;
    private static final double C = 100e-9;
    private static final double TAU = R * C;

    private final PeriodicSignalWrapper capacitorVoltageSignal;
    private final PeriodicSignalWrapper resistorVoltageSignal;
    private PeriodicSignal input = PeriodicSignal.GND;
    private Switch<OffOn> debugSwitch;

    /**
     * Creates a new diode model
     */
    public CapacitorModel() {
        capacitorVoltageSignal = new PeriodicSignalWrapper();
        resistorVoltageSignal = new PeriodicSignalWrapper();
    }

    /**
     * Sets a debug switch
     *
     * @param debugSwitch the debug switch
     */
    public void setDebugSwitch(Switch<OffOn> debugSwitch) {
        this.debugSwitch = debugSwitch;
        debugSwitch.addObserver(this);
    }

    /**
     * Sets the input signal
     *
     * @param signal the input signal
     */
    public void setInput(PeriodicSignal signal) {
        if (input != null)
            input.removeObserver(this);

        input = signal;
        if (input == null)
            input = PeriodicSignal.GND;
        else
            input.addObserver(this);

        inputSignalHasChanged();
    }

    /**
     * @return the signal describing the capacitors voltage
     */
    public PeriodicSignal getVoltageCapacitor() {
        return capacitorVoltageSignal;
    }

    /**
     * @return the signal describing the resistance voltage
     */
    public PeriodicSignal getVoltageResistor() {
        return resistorVoltageSignal;
    }

    @Override
    public void hasChanged() {
        inputSignalHasChanged();
    }

    private void inputSignalHasChanged() {
        PeriodicSignal.SinParams sinParams = input.getSinParams();
        if (sinParams == null)
            solveDGL();
        else {
            if (debugSwitch == null || debugSwitch.getSelected() == OffOn.Off)
                createSines(sinParams);
            else
                solveDGL();
        }
    }

    private void createSines(PeriodicSignal.SinParams sinParams) {
        LOGGER.info("create sines");
        double w = sinParams.getOmega();
        double uc = sinParams.getAmpl() / Math.sqrt(1 + sqr(w * TAU));
        double phase = -Math.atan(w * TAU);

        double ur = Math.sqrt(sqr(sinParams.getAmpl()) - sqr(uc));

        capacitorVoltageSignal.setSignal(new Sine(uc, w, sinParams.getPhase() + phase, sinParams.getOffset()));
        resistorVoltageSignal.setSignal(new Sine(-ur, w, sinParams.getPhase() + phase + Math.PI / 2, 0));
    }

    private static double sqr(double v) {
        return v * v;
    }

    private void solveDGL() {
        LOGGER.info("recalculate capacitor");
        double[] capacitorVoltage = new double[POINTS];
        double[] resistorVoltage = new double[POINTS];
        double period = input.period();
        double dt = period / POINTS;
        double uc = 0;
        for (int l = 0; l < 3; l++)
            for (int i = 0; i < POINTS; i++) {
                double uGes = input.v(period * i / POINTS);

                capacitorVoltage[i] = uc;
                resistorVoltage[i] = -(uGes - uc);

                double ducdt = (uGes - uc) / TAU;
                uc += ducdt * dt;
            }
        capacitorVoltageSignal.setSignal(new InterpolateLinear(period, capacitorVoltage));
        resistorVoltageSignal.setSignal(new InterpolateLinear(period, resistorVoltage));
    }

}
