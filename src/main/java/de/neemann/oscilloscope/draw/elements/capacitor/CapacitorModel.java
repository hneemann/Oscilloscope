package de.neemann.oscilloscope.draw.elements.capacitor;

import de.neemann.oscilloscope.draw.elements.OnOffSwitch;
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
public class CapacitorModel implements Observer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CapacitorModel.class);

    private static final int POINTS = 1000;
    private static final double R = 1000;
    private static final double C = 100e-9;
    private static final double TAU = R * C;

    private final SignalProvider capacitorVoltageSignal;
    private final SignalProvider resistorVoltageSignal;
    private final SignalProvider input;
    private OnOffSwitch debugSwitch;

    /**
     * Creates a new diode model
     *
     * @param input the provider for the input
     */
    public CapacitorModel(SignalProvider input) {
        this.input = input;
        input.addObserver(this);
        capacitorVoltageSignal = new SignalProvider();
        resistorVoltageSignal = new SignalProvider();
    }

    /**
     * Sets a debug switch
     *
     * @param debugSwitch the debug switch
     */
    public void setDebugSwitch(OnOffSwitch debugSwitch) {
        this.debugSwitch = debugSwitch;
        debugSwitch.addObserver(this);
    }

    /**
     * @return the signal describing the capacitors voltage
     */
    public SignalProvider getVoltageCapacitor() {
        return capacitorVoltageSignal;
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
        if (in instanceof Sine)
            if (debugSwitch == null || debugSwitch.isOff())
                createSines((Sine) in);
            else
                solveDGL(in);
        else {
            solveDGL(in);
        }
    }

    private void createSines(Sine sine) {
        LOGGER.info("create sines");
        double w = sine.getOmega();
        double uc = sine.getAmplitude() / Math.sqrt(1 + sqr(w * TAU));
        double phase = -Math.atan(w * TAU);

        double ur = Math.sqrt(sqr(sine.getAmplitude()) - sqr(uc));

        capacitorVoltageSignal.setSignal(new Sine(uc, w, sine.getPhase() + phase, sine.getOffset()));
        resistorVoltageSignal.setSignal(new Sine(-ur, w, sine.getPhase() + phase + Math.PI / 2, 0));
    }

    private static double sqr(double v) {
        return v * v;
    }

    /**
     * a simple gauss solver
     *
     * @param input the input signal
     */
    private void solveDGL(PeriodicSignal input) {
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
