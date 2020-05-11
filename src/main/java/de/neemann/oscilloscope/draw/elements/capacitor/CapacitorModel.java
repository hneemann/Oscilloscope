package de.neemann.oscilloscope.draw.elements.capacitor;

import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.Interpolate;
import de.neemann.oscilloscope.signal.InterpolateLinear;
import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * The model of the capacitor
 */
public class CapacitorModel implements Observer {

    private static final int POINTS = 1000;
    private static final double R = 1000;
    private static final double C = 100e-9;
    private static final double TAU = R * C;

    private final Interpolate capacitorVoltageSignal;
    private final Interpolate resistorVoltageSignal;
    private PeriodicSignal input = PeriodicSignal.GND;

    /**
     * Creates a new diode model
     */
    public CapacitorModel() {
        capacitorVoltageSignal = new InterpolateLinear();
        resistorVoltageSignal = new InterpolateLinear();
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
        System.out.println("recalculate capacitor");
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
        capacitorVoltageSignal.setValues(period, capacitorVoltage);
        resistorVoltageSignal.setValues(period, resistorVoltage);
    }

}
