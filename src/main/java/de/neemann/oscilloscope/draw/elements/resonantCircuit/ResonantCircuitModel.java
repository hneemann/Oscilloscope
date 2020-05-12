package de.neemann.oscilloscope.draw.elements.resonantCircuit;

import de.neemann.oscilloscope.draw.elements.OffOn;
import de.neemann.oscilloscope.draw.elements.Switch;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.InterpolateLinear;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.SignalProvider;
import de.neemann.oscilloscope.signal.primitives.Sine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The model of the capacitor
 */
public class ResonantCircuitModel implements Observer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResonantCircuitModel.class);

    private static final int POINTS = 1000;
    private static final double C = 100e-9;
    private static final double L = 0.1;
    private static final double RL = 100;

    private final SignalProvider resistorVoltageSignal;
    private final SignalProvider input;
    private double resistor;
    private Switch<OffOn> debugSwitch;

    /**
     * Creates a new diode model
     *
     * @param input the input signal
     */
    public ResonantCircuitModel(SignalProvider input) {
        this.input = input;
        input.addObserver(this);
        resistorVoltageSignal = new SignalProvider();
    }

    /**
     * Sets the debug switch
     *
     * @param debugSwitch the debug switch
     */
    public void setDebugSwitch(Switch<OffOn> debugSwitch) {
        this.debugSwitch = debugSwitch;
        debugSwitch.addObserver(this);
    }

    /**
     * @return the signal describing the resistance voltage
     */
    public SignalProvider getVoltageResistor() {
        return resistorVoltageSignal;
    }

    @Override
    public void hasChanged() {
        inputSignalHasChanged();
    }

    private void inputSignalHasChanged() {
        PeriodicSignal in = input.getSignal();
        if (in instanceof Sine) {
            if (debugSwitch == null || debugSwitch.getSelected() == OffOn.Off)
                resistorVoltageSignal.setSignal(createSines((Sine) in));
            else
                resistorVoltageSignal.setSignal(solveDGL(in));
        } else {
            resistorVoltageSignal.setSignal(solveDGL(in));
        }
    }

    private PeriodicSignal solveDGL(PeriodicSignal input) {
        double t = Math.sqrt(L * C) * 2 * Math.PI;
        LOGGER.info("recalculate resonant circuit, f0=" + 1 / t + "Hz");


        double period = input.period();
        int points = (int) (period / t * 1000);
        if (points < POINTS)
            points = POINTS;
        else if (points > 10000)
            points = 10000;

        double[] resistorVoltage = new double[points];
        double dt = period / points;
        double didt = 0;
        double i = 0;
        double lastUGes = 0;
        for (int l = 0; l < 8; l++) {
            for (int j = 0; j < points; j++) {
                double uGes = input.v(period * j / points);

                resistorVoltage[j] = -i * resistor;

                double d2idt2 = -(resistor + RL) / L * didt - i / L / C - (uGes - lastUGes) / dt / L;
                i += didt * dt;
                didt += d2idt2 * dt;

                lastUGes = uGes;
            }
        }
        return new InterpolateLinear(period, resistorVoltage);
    }

    /**
     * Sets the used resistor
     *
     * @param res the resistor
     */
    public void setResistor(int res) {
        this.resistor = res;
        inputSignalHasChanged();
    }

    private PeriodicSignal createSines(Sine sine) {
        LOGGER.info("create sine");
        double w = sine.getOmega();
        double a = sine.getAmpl() * (resistor + RL) / Math.sqrt(sqr(resistor + RL) + sqr(w * L - 1 / (w * C)));
        double ampl = a * resistor / (resistor + RL);

        double phi = Math.atan((w * L - 1 / (w * C)) / (resistor + RL));

        double phase = sine.getPhase() - phi;

        return new Sine(ampl, w, phase, 0);
    }

    private static double sqr(double v) {
        return v * v;
    }

}
