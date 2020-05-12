package de.neemann.oscilloscope.signal.interpolate;

/**
 * A function that is defined by solving (finding a zero) ofan other function.
 * Because solving is a slow operation use tihs function only in combination with
 * the {@link InterpolateLinear} function.
 */
public class InverterFunc implements Func {
    private final double eps;
    private final CreateSolver cs;
    private double initial;

    /**
     * Creates a new instance
     *
     * @param eps     th precition
     * @param initial the initial value
     * @param cs      the function to solve
     */
    public InverterFunc(double eps, double initial, CreateSolver cs) {
        this.eps = eps;
        this.initial = initial;
        this.cs = cs;
    }

    @Override
    public double f(double x) {
        Solver.FunctionDeriv f = cs.createFunc(x);
        initial = new Solver(f).newton(this.initial, eps);
        return initial;
    }

    /**
     * Interface used to create the function to solve
     */
    public interface CreateSolver {
        /**
         * Creates the function to solve
         *
         * @param x the argument
         * @return the function to solve
         */
        Solver.FunctionDeriv createFunc(double x);
    }
}
