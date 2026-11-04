package com.chinesebay.ccal4;

import com.chinesebay.ccal4.astronomy.NovasConstants;
import com.chinesebay.ccal4.astronomy.Nutation;
import com.chinesebay.ccal4.astronomy.NutationData;

/** Reference checks independent of the preserved 1900-2100 calendar cache. */
final class AstronomyTest {
    private AstronomyTest() {
    }

    static void run() {
        // Generated from nutation.cpp: iau2000a(2451545.0, t * 36525.0).
        double[][] reference = {
            {-3.55, -3.9196179484651e-05, -4.2473047629425867e-05},
            {0.0, -6.754422426417299e-05, -2.797083119237414e-05},
            {1.0, 1.5859873904841475e-05, 4.1623267794269478e-05},
            {5.0, -4.0843847268188421e-06, -4.7269482214567157e-05},
            {50.0, 9.4647243263592244e-05, -4.2834251509980533e-06}
        };
        for (double[] row : reference) {
            NutationData result = Nutation.iau2000a(NovasConstants.T0, row[0] * 36525.0);
            Assertions.isTrue(Double.isFinite(result.dpsi) && Double.isFinite(result.deps),
                    "nutation reference results are finite");
            Assertions.near(row[1], result.dpsi, 1e-12, "native nutation longitude at t=" + row[0]);
            Assertions.near(row[2], result.deps, 1e-12, "native nutation obliquity at t=" + row[0]);
        }
        double[] terms = SolarTerms.forYear(7000);
        double first = terms[0];
        terms[0] = 0.0;
        Assertions.near(first, SolarTerms.forYear(7000)[0], 0.0,
                "callers cannot mutate cached solar-term calculations");
    }
}
