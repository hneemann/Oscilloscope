package de.neemann.oscilloscope.signal.interpolate;

/**
 * A solver to solve a equation
 */
public class Solver {
    private final FunctionDeriv f;

    /**
     * Creates a new solver
     *
     * @param af     the function to solve
     * @param aderiv the derivative of the function to solve
     */
    public Solver(Function af, Function aderiv) {
        this(new FunctionDeriv() {
            @Override
            public double f() {
                return af.f(getX());
            }

            @Override
            public double deriv() {
                return aderiv.f(getX());
            }
        });
    }

    /**
     * Creates a new solver
     *
     * @param f the function to solve
     */
    public Solver(FunctionDeriv f) {
        this.f = f;
    }

    /**
     * solves the function using a simple newton iteration
     *
     * @param x   the initial x value
     * @param eps precision of the result
     * @return the solving value
     */
    public double newton(double x, double eps) {
        int n = 0;
        while (n++ < 40) {
            f.setX(x);
            double delta = f.f() / f.deriv();
            x = x - delta;
            if (Math.abs(delta) < eps)
                break;
        }
        if (n == 20)
            System.out.println("newton does not converge " + x + "," + f);
        return x;
    }

    /**
     * Abstraction of a function
     */
    public interface Function {
        /**
         * Returns the functions value
         *
         * @param x the argument
         * @return the functions value
         */
        double f(double x);
    }

    /**
     * Abstraction of a function and its derivative
     */
    public static abstract class FunctionDeriv {
        private double x;

        /**
         * @return the x value
         */
        public final double getX() {
            return x;
        }

        /**
         * sets the x value
         *
         * @param x the x value
         */
        public void setX(double x) {
            this.x = x;
        }

        /**
         * Implements the function
         *
         * @return the function value
         */
        public abstract double f();

        /**
         * @return the derivative of the function
         */
        public abstract double deriv();
    }

}
