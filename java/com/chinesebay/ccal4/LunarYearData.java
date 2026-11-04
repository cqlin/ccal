package com.chinesebay.ccal4;

/** Computed Chinese-calendar events for one Gregorian year, in Julian days. */
final class LunarYearData {
    final double[] solarTerms;
    final double lastNewMoon;
    final double lastLunarMonth;
    final double[] newMoons;
    final double[] lunarMonths;
    final double nextNewMoon;
    final double leapMonthCalendarPosition;

    LunarYearData(double[] solarTerms, double lastNewMoon, double lastLunarMonth,
            double[] newMoons, double[] lunarMonths, double nextNewMoon,
            double leapMonthCalendarPosition) {
        this.solarTerms = solarTerms;
        this.lastNewMoon = lastNewMoon;
        this.lastLunarMonth = lastLunarMonth;
        this.newMoons = newMoons;
        this.lunarMonths = lunarMonths;
        this.nextNewMoon = nextNewMoon;
        this.leapMonthCalendarPosition = leapMonthCalendarPosition;
    }
}
