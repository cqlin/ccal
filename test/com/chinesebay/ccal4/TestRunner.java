package com.chinesebay.ccal4;

/** Executes all project tests without JUnit or another external library. */
public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] arguments) {
        run("CalendarMathTest", new Runnable() {
            @Override
            public void run() {
                CalendarMathTest.run();
            }
        });
        run("MoonPhasesTest", new Runnable() {
            @Override
            public void run() {
                MoonPhasesTest.run();
            }
        });
        run("LunarYearTest", new Runnable() {
            @Override
            public void run() {
                LunarYearTest.run();
            }
        });
        run("AstronomyTest", new Runnable() {
            @Override
            public void run() {
                AstronomyTest.run();
            }
        });
        run("RendererAndCliTest", new Runnable() {
            @Override
            public void run() {
                RendererAndCliTest.run();
            }
        });
        System.out.println("All plain-Java tests passed.");
    }

    private static void run(String name, Runnable test) {
        test.run();
        System.out.println("PASS " + name);
    }
}
