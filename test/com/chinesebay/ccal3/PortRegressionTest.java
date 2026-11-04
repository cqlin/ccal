package com.chinesebay.ccal3;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/** JDK-only regression checks for the ccal3 C++ port. */
public final class PortRegressionTest {
    private PortRegressionTest() {
    }

    public static void main(String[] args) throws Exception {
        // Reference values generated from nutation.cpp, in radians.
        double[][] reference = {
            {-3.55, -3.9196179484651e-05, -4.2473047629425867e-05},
            {0.0, -6.754422426417299e-05, -2.797083119237414e-05},
            {1.0, 1.5859873904841475e-05, 4.1623267794269478e-05},
            {5.0, -4.0843847268188421e-06, -4.7269482214567157e-05},
            {50.0, 9.4647243263592244e-05, -4.2834251509980533e-06}
        };
        for (double[] row : reference) {
            NutationData result = Nutation.iau2000a(NovasConstants.T0, row[0] * 36525.0);
            close(row[1], result.dpsi, "nutation longitude");
            close(row[2], result.deps, "nutation obliquity");
        }

        ArrayList<Double> terms = new ArrayList<>();
        YearCache.GetYear((short) 2100, terms, new YearCache.MutableDouble(0),
            new YearCache.MutableDouble(0), new ArrayList<Double>(), new ArrayList<Double>(),
            new YearCache.MutableDouble(0));
        if (terms.size() != 24)
            throw new AssertionError("Expected 24 terms in the last cached year");

        equal("\u9a5a\u87c4", MiscData.getJieQiArray('b')[4], "Big5 Awakening of Insects");
        equal("\u958f\u5341\u4e00\u6708\u5c0f22\u65e5\u59cb",
            MiscData.numberToMonthCH(11.5, 22, 29, 'b'), "traditional leap month");
        MiscData.setSimplified(false);
        equal("\u7a40\u96e8", MiscData.getJieQiArray('u')[7], "traditional UTF-8 Grain Rain");
        equal("\u5c0f\u6eff", MiscData.getJieQiArray('u')[9], "traditional UTF-8 Grain Full");
        equal("\u60ca\u86f0", MiscData.getJieQiArray('g')[4], "GB2312 remains simplified");
        MiscData.setSimplified(true);
        equal("\u60ca\u86f0", MiscData.getJieQiArray('u')[4], "simplified UTF-8 reset");

        StringBuilder buffer = new StringBuilder();
        PSMonth.Number2MonthPS(12, 10, 30, false, buffer);
        PSMonth.Number2MonthPS(1, 1, 30, true, buffer);
        equal("m02 m04 ", buffer.toString(), "reused PostScript month buffer");
        PSMonth.Number2DayPS(2, buffer);
        equal("m00 n02 ", buffer.toString(), "reused PostScript day buffer");
        if (Ccal.getMonthNumber(11.5).leap != 'R')
            throw new AssertionError("Leap-month API must retain R");
        equal(" [11]YR ", capture(new Runnable() {
            @Override
            public void run() {
                Ccal.printMonthNumber(11.5);
            }
        }), "ASCII leap-month marker");

        // Public printMonth must handle leap days without first calling main.
        final LunarYear.LunarYearResult lunar = LunarYear.lunaryear((short) 2024);
        String february = capture(new Runnable() {
            @Override
            public void run() {
                Ccal.printMonth((short) 2024, (short) 2, lunar.vterms, lunar.lastnew, lunar.lastmon,
                    lunar.vmoons, lunar.vmonth, lunar.nextnew, 0, true, 'a', false);
            }
        });
        if (!february.contains("29 [20]"))
            throw new AssertionError("Direct monthly rendering must include February 29");
        System.out.println("ccal3 port regression checks passed.");
    }

    private static String capture(Runnable render) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(bytes, true, "UTF-8");
        try {
            System.setOut(output);
            render.run();
            output.flush();
            return bytes.toString("UTF-8");
        } finally {
            System.setOut(original);
            output.close();
        }
    }

    private static void equal(String expected, String actual, String label) {
        if (!expected.equals(actual))
            throw new AssertionError(label + ": expected [" + expected + "], got [" + actual + "]");
    }

    private static void close(double expected, double actual, String label) {
        if (!Double.isFinite(actual) || Math.abs(expected - actual) > 1e-12)
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
    }
}
