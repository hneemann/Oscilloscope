package de.neemann.oscilloscope.draw.elements.diode;

import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.SignalProvider;
import de.neemann.oscilloscope.signal.interpolate.InterpolateLinear;
import de.neemann.oscilloscope.signal.interpolate.InverterFunc;
import de.neemann.oscilloscope.signal.interpolate.Solver.FunctionDeriv;
import de.neemann.oscilloscope.signal.primitives.Signal;
import de.neemann.oscilloscope.signal.primitives.SignalFunc;

/**
 * The model of the diode
 */
public class DiodeModel implements Observer {
    private static final int POINTS = 10000;
    private static final double R = 1000;
    private static final double UT = 0.025;
    private static final double IS = 1e-10;
    private static final double N = 1.5;
    private final SignalProvider inputProvider;
    private final SignalProvider diodeVoltageSignal;
    private final SignalProvider resistorVoltageSignal;
    private final InterpolateLinear ud;

    /**
     * Creates a new diode model
     *
     * @param inputProvider the provider for the input signal
     */
    public DiodeModel(SignalProvider inputProvider) {
        this.inputProvider = inputProvider;
        inputProvider.addObserver(this);
        diodeVoltageSignal = new SignalProvider();
        resistorVoltageSignal = new SignalProvider();

        ud = new InterpolateLinear(
                -Generator.MAX_AMPL, Generator.MAX_AMPL, POINTS,
                new InverterFunc(1e-6, -10, UDiodeFunc::new));
    }

    /**
     * @return the signal describing the diodes voltage
     */
    public SignalProvider getVoltageDiode() {
        return diodeVoltageSignal;
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
        final PeriodicSignal uGes = inputProvider.getSignal();

        double udMean = 0;
        double urMean = 0;
        if (uGes instanceof Signal) {
            Signal s = (Signal) uGes;
            udMean = (0.7 - s.getAmplitude()+s.getOffset()) / 2;
            urMean = (-s.getAmplitude()-s.getOffset()) / 4;
        }

        diodeVoltageSignal.setSignal(new SignalFunc(uGes, ud, udMean));
        resistorVoltageSignal.setSignal(new SignalFunc(uGes, ug -> ud.f(ug) - ug, urMean));
    }

    /**
     * Function to calculate Ud at a given uGes
     */
    private static final class UDiodeFunc extends FunctionDeriv {
        private final double uGes;
        private double iGes;

        private UDiodeFunc(double uGes) {
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

        @Override
        public String toString() {
            return "UDiodeFunc{" + "uGes=" + uGes + '}';
        }
    }

}
