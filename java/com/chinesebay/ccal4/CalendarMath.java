/*
 * Copyright (c) 2026.
 *
 * Plain-Java equivalents of the Gregorian/Julian-date helpers used by the
 * original ccal NOVAS integration.  Julian dates use the astronomical
 * convention: a day begins at noon and 2000-01-01 12:00 is 2451545.0.
 */
package com.chinesebay.ccal4;

final class CalendarMath {
    private CalendarMath() {
    }

    static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    static int daysInMonth(int year, int month) {
        switch (month) {
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                if (month < 1 || month > 12) {
                    throw new IllegalArgumentException("month must be between 1 and 12");
                }
                return 31;
        }
    }

    /**
     * Returns the proleptic Gregorian Julian date.  This is the same
     * Fliegel--Van Flandern calculation used by the C++ source, expressed
     * without C integer-division edge cases.
     */
    static double julianDate(int year, int month, int day, double hour) {
        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;
        long jdn = day + (153L * m + 2L) / 5L + 365L * y + y / 4L
                - y / 100L + y / 400L - 32045L;
        return jdn - 0.5 + hour / 24.0;
    }

    static int dayOfWeek(int year, int month, int day) {
        // Sunday is zero, matching ccal's printed calendar.
        long noonJulianDay = (long) Math.floor(julianDate(year, month, day, 12.0));
        return (int) ((noonJulianDay + 1L) % 7L);
    }

    static CivilDate civilDate(double julianDate) {
        long z = (long) Math.floor(julianDate + 0.5);
        double fraction = julianDate + 0.5 - z;
        long a = z + 32044L;
        long b = (4L * a + 3L) / 146097L;
        long c = a - (146097L * b) / 4L;
        long d = (4L * c + 3L) / 1461L;
        long e = c - (1461L * d) / 4L;
        long m = (5L * e + 2L) / 153L;
        int day = (int) (e - (153L * m + 2L) / 5L + 1L);
        int month = (int) (m + 3L - 12L * (m / 10L));
        int year = (int) (100L * b + d - 4800L + m / 10L);
        return new CivilDate(year, month, day, fraction * 24.0);
    }

    static int wholeDay(double julianDate) {
        // ccal rounds event times to the local Beijing calendar day.
        return (int) Math.floor(julianDate + 0.5);
    }

    static double normalizeDegrees(double degrees) {
        double result = degrees % 360.0;
        return result < 0.0 ? result + 360.0 : result;
    }

    static int floorMod(int value, int modulus) {
        int result = value % modulus;
        return result < 0 ? result + modulus : result;
    }

    static final class CivilDate {
        final int year;
        final int month;
        final int day;
        final double hour;

        CivilDate(int year, int month, int day, double hour) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
        }
    }
}
