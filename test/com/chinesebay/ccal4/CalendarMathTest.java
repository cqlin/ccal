package com.chinesebay.ccal4;
import com.chinesebay.ccal4.*;


final class CalendarMathTest {
    private CalendarMathTest() {
    }

    static void run() {
        Assertions.isTrue(CalendarMath.isLeapYear(2000), "2000 is a leap year");
        Assertions.isTrue(!CalendarMath.isLeapYear(1900), "1900 is not a leap year");
        Assertions.isTrue(CalendarMath.isLeapYear(2024), "2024 is a leap year");
        Assertions.equalsInt(29, CalendarMath.daysInMonth(2024, 2), "February 2024 length");
        Assertions.equalsInt(28, CalendarMath.daysInMonth(2023, 2), "February 2023 length");
        Assertions.near(2451545.0, CalendarMath.julianDate(2000, 1, 1, 12.0), 0.0,
                "J2000 Julian date");
        CalendarMath.CivilDate j2000 = CalendarMath.civilDate(2451545.0);
        Assertions.equalsInt(2000, j2000.year, "J2000 year");
        Assertions.equalsInt(1, j2000.month, "J2000 month");
        Assertions.equalsInt(1, j2000.day, "J2000 day");
        Assertions.near(12.0, j2000.hour, 0.000001, "J2000 hour");
        Assertions.equalsInt(1, CalendarMath.dayOfWeek(2024, 1, 1), "2024-01-01 is Monday");
    }
}
