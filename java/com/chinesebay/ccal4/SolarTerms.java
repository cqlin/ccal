package com.chinesebay.ccal4;

/**
 * Computes the 24 solar terms in Beijing local time.
 *
 * <p>The original source uses NOVAS vectors.  The cache keeps its exact
 * NOVAS-derived event days from 1900 through 2100.  For the documented
 * extended range this class uses the standard apparent-solar-longitude
 * equations from Meeus and solves each term by bisection.  It is entirely
 * self-contained and is deliberately used only when no preserved cache data
 * is available.</p>
 */
final class SolarTerms {
    private static final double BEIJING_LONGITUDE_BEFORE_1928 = (116.0 + 25.0 / 60.0) / 360.0;
    private static final double BEIJING_LONGITUDE = 120.0 / 360.0;

    private SolarTerms() {
    }

    static double[] forYear(int year) {
        double[] result = new double[24];
        for (int index = 0; index < result.length; index++) {
            int month = index / 2 + 1;
            int day = (index % 2) * 14 + 1;
            double start = CalendarMath.julianDate(year, month, day, 12.0);
            double end = CalendarMath.julianDate(year, month, day + 10, 12.0);
            // In ccal's coordinate system zero is winter solstice.  Convert
            // its requested angle to ordinary apparent ecliptic longitude.
            double longitude = CalendarMath.normalizeDegrees(((index + 1) % 24) * 15.0 - 90.0);
            result[index] = CalendarMath.wholeDay(solveSolarLongitude(start, end, longitude)
                    + longitudeOffset(year));
        }
        return result;
    }

    static double previousWinterSolstice(int year) {
        double start = CalendarMath.julianDate(year - 1, 12, 12, 12.0);
        double end = CalendarMath.julianDate(year - 1, 12, 25, 12.0);
        return solveSolarLongitude(start, end, 270.0) + longitudeOffset(year - 1);
    }

    private static double longitudeOffset(int year) {
        return year < 1928 ? BEIJING_LONGITUDE_BEFORE_1928 : BEIJING_LONGITUDE;
    }

    private static double solveSolarLongitude(double start, double end, double target) {
        double midpoint = (start + end) / 2.0;
        double midpointLongitude = apparentSolarLongitudeUnwrapped(midpoint);
        double targetUnwrapped = target + 360.0 * Math.rint((midpointLongitude - target) / 360.0);
        double lower = start;
        double upper = end;
        double lowerValue = apparentSolarLongitudeUnwrapped(lower) - targetUnwrapped;
        double upperValue = apparentSolarLongitudeUnwrapped(upper) - targetUnwrapped;

        // The nominal ranges match the original program.  The fallback makes
        // the solver robust at the ends of the supported calendar range.
        for (int attempts = 0; lowerValue * upperValue > 0.0 && attempts < 8; attempts++) {
            lower -= 2.0;
            upper += 2.0;
            lowerValue = apparentSolarLongitudeUnwrapped(lower) - targetUnwrapped;
            upperValue = apparentSolarLongitudeUnwrapped(upper) - targetUnwrapped;
        }
        if (lowerValue * upperValue > 0.0) {
            throw new IllegalStateException("Could not bracket solar term at longitude " + target);
        }
        for (int iteration = 0; iteration < 60; iteration++) {
            double middle = (lower + upper) / 2.0;
            double value = apparentSolarLongitudeUnwrapped(middle) - targetUnwrapped;
            if (value == 0.0) {
                return DeltaT.ttToUt(middle);
            }
            if ((lowerValue < 0.0 && value < 0.0) || (lowerValue > 0.0 && value > 0.0)) {
                lower = middle;
                lowerValue = value;
            } else {
                upper = middle;
                upperValue = value;
            }
        }
        return DeltaT.ttToUt((lower + upper) / 2.0);
    }

    /** Returns apparent geocentric solar longitude without reducing turns. */
    private static double apparentSolarLongitudeUnwrapped(double julianDate) {
        double t = (julianDate - 2451545.0) / 36525.0;
        double meanLongitude = 280.46646 + 36000.76983 * t + 0.0003032 * t * t;
        double meanAnomaly = Math.toRadians(CalendarMath.normalizeDegrees(
                357.52911 + 35999.05029 * t - 0.0001537 * t * t));
        double equationOfCenter = (1.914602 - 0.004817 * t - 0.000014 * t * t) * Math.sin(meanAnomaly)
                + (0.019993 - 0.000101 * t) * Math.sin(2.0 * meanAnomaly)
                + 0.000289 * Math.sin(3.0 * meanAnomaly);
        double omega = Math.toRadians(125.04 - 1934.136 * t);
        return meanLongitude + equationOfCenter - 0.00569 - 0.00478 * Math.sin(omega);
    }
}
