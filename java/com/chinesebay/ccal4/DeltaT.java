package com.chinesebay.ccal4;

/** NASA delta-T polynomials retained from the original tt2ut.cpp. */
final class DeltaT {
    private DeltaT() {
    }

    static double ttToUt(double terrestrialTimeJulianDate) {
        CalendarMath.CivilDate date = CalendarMath.civilDate(terrestrialTimeJulianDate);
        double year = date.year + (date.month - 0.5) / 12.0;
        double t;
        double seconds;

        if (year < 1700.0) {
            t = year - 1600.0;
            seconds = 120.0 - 0.9808 * t - 0.01532 * t * t + t * t * t / 7129.0;
        } else if (year < 1800.0) {
            t = year - 1700.0;
            seconds = 8.83 + 0.1603 * t - 0.0059285 * t * t
                    + 0.00013336 * t * t * t - Math.pow(t, 4) / 1174000.0;
        } else if (year < 1860.0) {
            t = year - 1800.0;
            seconds = 13.72 - 0.332447 * t + 0.0068612 * t * t
                    + 0.0041116 * Math.pow(t, 3) - 0.00037436 * Math.pow(t, 4)
                    + 0.0000121272 * Math.pow(t, 5) - 0.0000001699 * Math.pow(t, 6)
                    + 0.000000000875 * Math.pow(t, 7);
        } else if (year < 1900.0) {
            t = year - 1860.0;
            seconds = 7.62 + 0.5737 * t - 0.251754 * t * t
                    + 0.01680668 * Math.pow(t, 3) - 0.0004473624 * Math.pow(t, 4)
                    + Math.pow(t, 5) / 233174.0;
        } else if (year < 1920.0) {
            t = year - 1900.0;
            seconds = -2.79 + 1.494119 * t - 0.0598939 * t * t
                    + 0.0061966 * Math.pow(t, 3) - 0.000197 * Math.pow(t, 4);
        } else if (year < 1941.0) {
            t = year - 1920.0;
            seconds = 21.20 + 0.84493 * t - 0.076100 * t * t + 0.0020936 * Math.pow(t, 3);
        } else if (year < 1961.0) {
            t = year - 1950.0;
            seconds = 29.07 + 0.407 * t - t * t / 233.0 + Math.pow(t, 3) / 2547.0;
        } else if (year < 1986.0) {
            t = year - 1975.0;
            seconds = 45.45 + 1.067 * t - t * t / 260.0 - Math.pow(t, 3) / 718.0;
        } else if (year < 2005.0) {
            t = year - 2000.0;
            seconds = 63.86 + 0.3345 * t - 0.060374 * t * t + 0.0017275 * Math.pow(t, 3)
                    + 0.000651814 * Math.pow(t, 4) + 0.00002373599 * Math.pow(t, 5);
        } else if (year < 2050.0) {
            t = year - 2000.0;
            seconds = 62.92 + 0.32217 * t + 0.005589 * t * t;
        } else if (year < 2150.0) {
            t = (year - 1820.0) / 100.0;
            seconds = -20.0 + 32.0 * t * t - 0.5628 * (2150.0 - year);
        } else {
            t = (year - 1820.0) / 100.0;
            seconds = -20.0 + 32.0 * t * t;
        }
        return terrestrialTimeJulianDate - seconds / 86400.0;
    }
}
