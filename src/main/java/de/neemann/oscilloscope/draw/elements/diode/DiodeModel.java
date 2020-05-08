package de.neemann.oscilloscope.draw.elements.diode;

import de.neemann.oscilloscope.draw.elements.diode.Solver.FunctionDeriv;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.PeriodicSignalMean;

/**
 * The model of the diode
 */
public class DiodeModel {

    private PeriodicSignal input = PeriodicSignal.GND;

    private static final double R = 1000;
    private static final double UT = 0.025;
    private static final double IS = 1e-10;
    private static final double N = 1.5;
    private double lastTime = -Double.MAX_VALUE;

    /**
     * Sets the input signal
     *
     * @param signal the input signal
     */
    public void setInput(PeriodicSignal signal) {
        input = signal;
    }

    /**
     * @return the signal describing the diodes voltage
     */
    public PeriodicSignal getVoltageDiode() {
        return new PeriodicSignalMean(new Ud());
    }

    /**
     * @return the signal describing the resistance voltage
     */
    public PeriodicSignal getVoltageResistor() {
        return new PeriodicSignalMean(new Ur());
    }

    private double iGes;
    private double uD;

    private void update(double t) {
        if (t != lastTime) {
            double uGes = input.v(t);
            if (uGes < 0) {
                iGes = 0;
                uD = uGes;
            } else {
                Solver s = new Solver(new DiodeFunc(uGes));
                uD = s.newton(0.6, 1e-4);
                iGes = (uGes - uD) / R;
            }
            lastTime = t;
        }
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

    private class Ud implements PeriodicSignal {
        @Override
        public double period() {
            return input.period();
        }

        @Override
        public double v(double t) {
            update(t);
            return uD;
        }
    }

    private class Ur implements PeriodicSignal {
        @Override
        public double period() {
            return input.period();
        }

        @Override
        public double v(double t) {
            update(t);
            return -iGes * R;
        }
    }

}
