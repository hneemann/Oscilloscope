package de.neemann.oscilloscope.draw.elements.resonantCircuit;

import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.InterpolateLinear;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.PeriodicSignalWrapper;
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

    private final PeriodicSignalWrapper resistorVoltageSignal;
    private PeriodicSignal input = PeriodicSignal.GND;
    private double resistor;

    /**
     * Creates a new diode model
     */
    public ResonantCircuitModel() {
        resistorVoltageSignal = new PeriodicSignalWrapper();
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
        if (sinParams != null) {
            LOGGER.info("is sine");
            resistorVoltageSignal.setSignal(new RCLSine(sinParams));
//            resistorVoltageSignal.setSignal(solveDGL());
        } else {
            resistorVoltageSignal.setSignal(solveDGL());
        }
    }

    private PeriodicSignal solveDGL() {
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

    private final class RCLSine extends PeriodicSignal {
        private final double ampl;
        private final double period;
        private final double w;
        private final double phase;

        private RCLSine(SinParams sinParams) {
            w = sinParams.getOmega();
            double a = sinParams.getAmpl() * resistor / Math.sqrt(sqr(resistor) + sqr(w * L - 1 / (w * C)));
            ampl = a * resistor / (resistor + RL);

            double phi = Math.atan((w * L - 1 / (w * C)) / (resistor + RL));

            phase = sinParams.getPhase() - phi;

            period = 2 * Math.PI / w;
        }

        @Override
        public double period() {
            return period;
        }

        @Override
        public double v(double t) {
            return ampl * Math.sin(w * t + phase);
        }
    }

    private static double sqr(double v) {
        return v * v;
    }

}
