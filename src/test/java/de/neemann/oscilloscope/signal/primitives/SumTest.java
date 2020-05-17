package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;
import junit.framework.TestCase;

public class SumTest extends TestCase {

    public static class TC {
        double p1, p2, ps;

        public TC(double p1, double p2, double ps) {
            this.p1 = p1;
            this.p2 = p2;
            this.ps = ps;
        }

        @Override
        public String toString() {
            return "TC{" +
                    "p1=" + p1 +
                    ", p2=" + p2 +
                    ", ps=" + ps +
                    '}';
        }
    }

    public TC[] tests = new TC[]{
            new TC(1, 1, 1),
            new TC(2, 2, 2),
            new TC(1, 2, 2),
            new TC(1, 3, 1.5),
            new TC(2, 3, 6),
            new TC(2, 2.1, 42),
    };

    public void testSimple() {
        for (TC tc : tests) {
            Sum s = new Sum(new TPS(tc.p1), new TPS(tc.p2));
            assertEquals(tc.toString(), tc.ps, s.period(), 1e-7);
        }
    }

    private static class TPS implements PeriodicSignal {
        private final double period;

        public TPS(double period) {
            this.period = period;
        }

        @Override
        public double v(double t) {
            return 0;
        }

        @Override
        public double period() {
            return period;
        }

        @Override
        public double mean() {
            return 0;
        }
    }
}