package de.neemann.oscilloscope.draw.elements.diode;

import de.neemann.oscilloscope.draw.elements.diode.Solver.FunctionDeriv;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.PeriodicSignalInterpolate;

/**
 * The model of the diode
 */
public class DiodeModel implements Observer {

    private static final int POINTS = 100;
    private static final double R = 1000;
    private static final double UT = 0.025;
    private static final double IS = 1e-10;
    private static final double N = 1.5;
    private final PeriodicSignalInterpolate diodeVoltageSignal;
    private final PeriodicSignalInterpolate resistorVoltageSignal;
    private PeriodicSignal input = PeriodicSignal.GND;

    public DiodeModel() {
        diodeVoltageSignal = new PeriodicSignalInterpolate();
        resistorVoltageSignal = new PeriodicSignalInterpolate();
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
     * @return the signal describing the diodes voltage
     */
    public PeriodicSignal getVoltageDiode() {
        return diodeVoltageSignal;
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
        System.out.println("recalculate diode");
        double[] diodeVoltage = new double[POINTS];
        double[] resistorVoltage = new double[POINTS];
        double period = input.period();
        for (int i = 0; i < POINTS; i++) {
            double uGes = input.v(period * i / POINTS);
            if (uGes < 0) {
                diodeVoltage[i] = uGes;
                resistorVoltage[i] = 0;
            } else {
                Solver s = new Solver(new DiodeFunc(uGes));
                double uD = s.newton(0.6, 1e-4);
                diodeVoltage[i] = uD;
                resistorVoltage[i] = uD - uGes;
            }
        }
        diodeVoltageSignal.setValues(period, diodeVoltage);
        resistorVoltageSignal.setValues(period, resistorVoltage);
    }

    private static final class DiodeFunc extends FunctionDeriv {
        private final double uGes;
        private double iGes;

        private DiodeFunc(double uGes) {
            this.uGes = uGes;
        }

        @Override
        public void setX(double ud) {
            super.setX(ud);
            iGes = IS * Math.exp(ud / N / UT);
        }

        @Override
        public double f() {
            return R * iGes + getX() - uGes;
        }

        @Override
        public double deriv() {
            return R * iGes / N / UT + 1;
        }
    }

}
