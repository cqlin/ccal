package com.chinesebay.ccal;

import java.util.ArrayList;
import java.util.List;

/** JDK-only regression checks for the original Java port's conversion errors. */
public final class PortRegressionTest {
    private PortRegressionTest() {
    }

    public static void main(String[] args) {
        // Reference values from nutation.cpp: iau2000a(2451545.0, t * 36525.0).
        // This bypasses YearCache, which would otherwise hide model differences.
        double[][] reference = {
            {-3.55, -3.9196179484651e-05, -4.2473047629425867e-05},
            {0.0, -6.754422426417299e-05, -2.797083119237414e-05},
            {1.0, 1.5859873904841475e-05, 4.1623267794269478e-05},
            {5.0, -4.0843847268188421e-06, -4.7269482214567157e-05},
            {50.0, 9.4647243263592244e-05, -4.2834251509980533e-06}
        };
        for (double[] row : reference) {
            MutableDouble longitude = new MutableDouble(0);
            MutableDouble obliquity = new MutableDouble(0);
            Nutation.nutation(row[0], longitude, obliquity);
            close(row[1], longitude.value, "IAU 2000A longitude at t=" + row[0]);
            close(row[2], obliquity.value, "IAU 2000A obliquity at t=" + row[0]);
        }

        List<Double> terms = new ArrayList<>();
        YearCache.GetYear((short) 2100, terms, new MutableDouble(0), new MutableDouble(0),
            new ArrayList<Double>(), new ArrayList<Double>(), new MutableDouble(0));
        if (terms.size() != 24)
            throw new AssertionError("Expected 24 solar terms in the final cached year");

        char[] buffer = new char[100];
        PsMonth.Number2MonthPS(12, 10, 30, false, buffer);
        PsMonth.Number2MonthPS(1, 1, 30, true, buffer);
        equal("m02 m04 ", terminated(buffer), "PostScript month buffer and trailing space");
        PsMonth.Number2DayPS(2, buffer);
        equal("m00 n02 ", terminated(buffer), "PostScript day buffer");
        HtmlMonth.Number2MonthCH(12, 10, 30, 'u', buffer);
        HtmlMonth.Number2DayCH(2, 'u', buffer);
        equal("\u521d\u4e8c", terminated(buffer), "Chinese day buffer");
        HtmlMonth.Number2MonthCH(1, 1, 30, 'u', buffer);
        equal("\u6b63\u6708\u59271\u65e5\u59cb", terminated(buffer), "Chinese month buffer");

        HtmlMonth.SetU8Characters(false);
        equal("\u7a40\u96e8", CcalData.U8jieqi[7][0], "traditional Grain Rain");
        equal("\u5c0f\u6eff", CcalData.U8jieqi[9][0], "traditional Grain Full");
        equal("\u8655\u6691", CcalData.U8jieqi[15][0], "traditional End of Heat");
        HtmlMonth.SetU8Characters(true);
        equal("\u60ca\u86f0", CcalData.U8jieqi[4][0], "simplified Awakening of Insects");
        equal("\u8c37\u96e8", CcalData.U8jieqi[7][0], "simplified Grain Rain");
        equal("\u5904\u6691", CcalData.U8jieqi[15][0], "simplified End of Heat");
        System.out.println("ccal port regression checks passed.");
    }

    private static String terminated(char[] buffer) {
        int length = 0;
        while (length < buffer.length && buffer[length] != 0)
            length++;
        if (length == buffer.length)
            throw new AssertionError("Missing character-buffer terminator");
        return new String(buffer, 0, length);
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
