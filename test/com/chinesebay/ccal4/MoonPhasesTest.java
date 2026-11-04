package com.chinesebay.ccal4;

final class MoonPhasesTest {
    private MoonPhasesTest() {
    }

    static void run() {
        double epochNewMoon = MoonPhases.phaseByLunation(0, 0);
        Assertions.near(2451550.260, epochNewMoon, 0.01, "Meeus epoch new moon");
        double nextNewMoon = MoonPhases.phaseByLunation(1, 0);
        Assertions.near(29.78445, nextNewMoon - epochNewMoon, 0.01,
                "first post-epoch new-moon interval");
        double firstQuarter = MoonPhases.phaseByLunation(0, 1);
        Assertions.isTrue(firstQuarter > epochNewMoon && firstQuarter < nextNewMoon,
                "first quarter falls between adjacent new moons");
    }
}
