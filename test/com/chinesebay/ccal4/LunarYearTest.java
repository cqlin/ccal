package com.chinesebay.ccal4;

final class LunarYearTest {
    private LunarYearTest() {
    }

    static void run() {
        LunarYearData year2024 = LunarYear.forYear(2024);
        Assertions.equalsInt(24, year2024.solarTerms.length, "2024 has 24 solar terms");
        Assertions.isTrue(year2024.newMoons.length >= 12 && year2024.newMoons.length <= 13,
                "2024 has a valid number of new moons");

        ChineseText text = new ChineseText(false);
        CalendarRenderer.MonthModel january = new CalendarRenderer.MonthModel(2024, 1, year2024, text);
        CalendarRenderer.DayModel january10 = january.day(10);
        Assertions.equalsInt(11, january10.lunarMonthNumber, "2024-01-10 lunar month");
        Assertions.equalsInt(29, january10.lunarDay, "2024-01-10 lunar day");
        CalendarRenderer.DayModel january11 = january.day(11);
        Assertions.equalsInt(12, january11.lunarMonthNumber, "2024-01-11 lunar month");
        Assertions.equalsInt(1, january11.lunarDay, "2024-01-11 lunar day");
        Assertions.isTrue(january11.newLunarMonth, "2024-01-11 begins a lunar month");

        CalendarRenderer.MonthModel february = new CalendarRenderer.MonthModel(2024, 2, year2024, text);
        CalendarRenderer.DayModel february10 = february.day(10);
        Assertions.equalsInt(1, february10.lunarMonthNumber, "2024-02-10 lunar month");
        Assertions.equalsInt(1, february10.lunarDay, "2024-02-10 lunar day");

        LunarYearData year2033 = LunarYear.forYear(2033);
        CalendarRenderer.MonthModel december2033 = new CalendarRenderer.MonthModel(2033, 12, year2033, text);
        Assertions.contains(december2033.chineseHeaderWithoutGregorian(), "闰十一月",
                "2033 includes the documented leap eleventh month");

        // 2201 is outside the embedded 1900-2100 reference table and
        // exercises the self-contained astronomy fallback.
        LunarYearData year2201 = LunarYear.forYear(2201);
        Assertions.equalsInt(24, year2201.solarTerms.length,
                "fallback astronomy provides all solar terms");
        Assertions.isTrue(year2201.newMoons.length >= 12 && year2201.newMoons.length <= 13,
                "fallback astronomy provides a valid number of new moons");
    }
}
