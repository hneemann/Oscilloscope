package de.neemann.oscilloscope.draw.elements.resonantCircuit;

import de.neemann.oscilloscope.draw.elements.OnOffSwitch;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.PeriodicInterpolate;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.SignalProvider;
import de.neemann.oscilloscope.signal.primitives.Sine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.neemann.oscilloscope.draw.elements.DoubleHelper.different;
import static de.neemann.oscilloscope.draw.elements.DoubleHelper.sqr;

/**
 * The model of the resonant circuit
 */
public class ResonantCircuitModel implements Observer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResonantCircuitModel.class);

    private static final int MIN_POINTS = 1000;
    private static final int MAX_POINTS = 100000;

    // parasitic resistance of the inductor
    private static final double RL = 50;

    private final SignalProvider resistorVoltageSignal;
    private final SignalProvider capacitorVoltageSignal;
    private final SignalProvider input;
    private double resistor = 50;
    private double capacitor = 100e-9;
    private double inductor = 0.01;
    private OnOffSwitch debugSwitch;

    /**
     * Creates a new resonant circuit model
     *
     * @param input the input signal
     */
    public ResonantCircuitModel(SignalProvider input) {
        this.input = input;
        input.addObserver(this);
        resistorVoltageSignal = new SignalProvider();
        capacitorVoltageSignal = new SignalProvider();
    }

    /**
     * Sets the debug switch
     *
     * @param debugSwitch the debug switch
     * @return the switch
     */
    public OnOffSwitch setDebugSwitch(OnOffSwitch debugSwitch) {
        this.debugSwitch = debugSwitch;
        debugSwitch.addObserver(this);
        return debugSwitch;
    }

    /**
     * Sets the used resistor
     *
     * @param res the resistor
     */
    public void setResistor(int res) {
        if (different(res, resistor)) {
            this.resistor = res;
            inputSignalHasChanged();
        }
    }

    /**
     * Sets the used capacitor
     *
     * @param cap the capacitor in nF
     */
    public void setCapacitor(int cap) {
        double c = cap * 1e-9;
        if (different(c, capacitor)) {
            this.capacitor = c;
            inputSignalHasChanged();
        }
    }

    /**
     * Sets the used inductor
     *
     * @param ind the inductor in mH
     */
    public void setInductor(int ind) {
        double i = ind * 1e-3;
        if (different(i, inductor)) {
            this.inductor = i;
            inputSignalHasChanged();
        }
    }

    /**
     * @return the signal describing the resistance voltage
     */
    public SignalProvider getVoltageResistor() {
        return resistorVoltageSignal;
    }

    /**
     * @return the signal describing the capacitor voltage
     */
    public SignalProvider getVoltageCapacitor() {
        return capacitorVoltageSignal;
    }

    @Override
    public void hasChanged() {
        inputSignalHasChanged();
    }

    private void inputSignalHasChanged() {
        PeriodicSignal in = input.getSignal();
        if (in instanceof Sine) {
            if (debugSwitch == null || debugSwitch.isOff())
                createSines((Sine) in);
            else
                solveDifferentialEquation(in);
        } else {
            solveDifferentialEquation(in);
        }
    }

    /**
     * Simple algorithm to solve the differential equation
     *
     * @param input the input signal
     */
    private void solveDifferentialEquation(PeriodicSignal input) {
        double t = Math.sqrt(inductor * capacitor) * 2 * Math.PI;
        LOGGER.info("solve resonant circuit equation, f0=" + 1 / t + "Hz");

        double period = input.period();
        int points = (int) (period / t * 1000);
        if (points < MIN_POINTS)
            points = MIN_POINTS;
        else if (points > MAX_POINTS)
            points = MAX_POINTS;

        double[] resistorVoltage = new double[points];
        double[] capacitorVoltage = new double[points];
        double dt = period / points;
        double i = 0;
        double uc = 0;
        for (int l = 0; l < 8; l++) {
            double iStart = i;
            double iMax = 0;
            for (int j = 0; j < points; j++) {
                double uGes = input.v(period * j / points);

                resistorVoltage[j] = -i * resistor;
                capacitorVoltage[j] = uc;

                double didt = (uGes - uc - (resistor + RL) * i) / inductor;
                double ducdt = i / capacitor;

                if (iMax < i)
                    iMax = i;

                i += didt * dt;
                uc += ducdt * dt;
            }
            if (Math.abs(iStart - i) / iMax < 1e-4)
                break;
        }
        resistorVoltageSignal.setSignal(new PeriodicInterpolate(period, resistorVoltage));
        capacitorVoltageSignal.setSignal(new PeriodicInterpolate(period, capacitorVoltage));
    }


    /**
     * Creates the well known solution of the differential equation in case of a sine input signal.
     *
     * @param sine the input signal
     */
    private void createSines(Sine sine) {
        LOGGER.info("create sine");
        double w = sine.getOmega();
        double urGes = sine.getAmplitude() * (resistor + RL) / Math.sqrt(sqr(resistor + RL) + sqr(w * inductor - 1 / (w * capacitor)));
        double ur = urGes * resistor / (resistor + RL);
        double phi = Math.atan((w * inductor - 1 / (w * capacitor)) / (resistor + RL));
        double phase = sine.getPhase() - phi;

        double uc = sine.getAmplitude() / Math.sqrt(sqr(capacitor * w) * (sqr(inductor * w) + sqr(resistor + RL)) - 2 * capacitor * inductor * sqr(w) + 1);

        // the "+ Math.PI" in "phase + Math.PI" changes the sign.
        resistorVoltageSignal.setSignal(new Sine(ur, w, phase + Math.PI, 0));
        capacitorVoltageSignal.setSignal(new Sine(uc, w, phase - Math.PI / 2, sine.getOffset()));
    }

}
