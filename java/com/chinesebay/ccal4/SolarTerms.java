package com.chinesebay.ccal4;

import com.chinesebay.ccal4.astronomy.SolarTerm;
import com.chinesebay.ccal4.astronomy.SolarTerm.SolarTermResult;

/** Original NOVAS solar-term calculation used when the day cache is unavailable. */
final class SolarTerms {
    private static int lastYear;
    private static SolarTermResult lastResult;

    private SolarTerms() {
    }

    private static synchronized SolarTermResult calculate(int year) {
        if (lastResult == null || lastYear != year) {
            lastResult = SolarTerm.solarterm((short) year);
            lastYear = year;
        }
        return lastResult;
    }

    static double[] forYear(int year) {
        SolarTermResult terms = calculate(year);
        double[] result = new double[terms.vjdterms.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = terms.vjdterms.get(i);
        // Retain event times until month numbering is finished, as lunaryear.cpp does.
        return result;
    }

    static double previousWinterSolstice(int year) {
        return calculate(year).jdpws;
    }
}
