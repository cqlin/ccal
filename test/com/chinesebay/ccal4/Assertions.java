package com.chinesebay.ccal4;
import com.chinesebay.ccal4.*;

/** Tiny assertion helper so tests require no external test framework. */
final class Assertions {
    private Assertions() {
    }

    static void equalsInt(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    static void equalsString(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected [" + expected + "], got [" + actual + "]");
        }
    }

    static void near(double expected, double actual, double tolerance, String message) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual
                    + " (tolerance " + tolerance + ")");
        }
    }

    static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void contains(String text, String expectedFragment, String message) {
        if (!text.contains(expectedFragment)) {
            throw new AssertionError(message + ": missing [" + expectedFragment + "]");
        }
    }

    static void throwsUsage(ThrowingRunnable operation, String message) {
        try {
            operation.run();
        } catch (CliOptions.UsageException expected) {
            return;
        } catch (Exception exception) {
            throw new AssertionError(message + ": wrong exception " + exception, exception);
        }
        throw new AssertionError(message + ": expected a usage exception");
    }

    interface ThrowingRunnable {
        void run();
    }
}
