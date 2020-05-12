package de.neemann.oscilloscope.draw.elements.diode;

import de.neemann.oscilloscope.signal.interpolate.Solver;
import junit.framework.TestCase;


public class SolverTest extends TestCase {

    public static final TC[] tests = new TC[]{
            new TC(x -> Math.exp(x) - 2, Math::exp, 1, Math.log(2)),
            new TC(x -> Math.exp(x) - 0.5, Math::exp, 1, Math.log(0.5)),
            new TC(x -> x * x * x - 2, x -> 3 * x * x, 1, Math.pow(2.0, 1.0 / 3.0)),
            new TC(x -> x * x * x + 2, x -> 3 * x * x, -1, -Math.pow(2.0, 1.0 / 3.0)),
    };

    public void testSolver() {
        for (TC t : tests) {
            double x = new Solver(t.f, t.deriv).newton(t.start, 1e-7);
            assertEquals(t.expected, x, 1e-6);
        }
    }

    private static class TC {
        private final Solver.Function f;
        private final Solver.Function deriv;
        private final double start;
        private final double expected;

        private TC(Solver.Function f, Solver.Function deriv, double start, double expected) {
            this.f = f;
            this.deriv = deriv;
            this.start = start;
            this.expected = expected;
        }
    }
}