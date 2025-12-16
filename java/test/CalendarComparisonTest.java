package test;

import java.util.ArrayList;
import java.util.List;
import com.chinesebay.ccal.MutableDouble;

/**
 * Test to compare complete calendar calculations between ccal and ccal2
 * This includes lunar months, new moons, and leap month determination
 */
public class CalendarComparisonTest {

    public static void main(String[] args) {
        System.out.println("=== Comparing Complete Calendar Calculations (ccal vs ccal2) ===\n");

        // Test multiple years
        short[] testYears = {2024, 2023, 2020, 2017, 2012,2500,1000,3300};

        for (short year : testYears) {
            System.out.println("\n" + "=".repeat(80));
            System.out.printf("Year: %d\n", year);
            System.out.println("=".repeat(80));

            // Calculate using ccal
            List<Double> vterms1 = new ArrayList<>();
            List<Double> vmoons1 = new ArrayList<>();
            List<Double> vmonth1 = new ArrayList<>();
            MutableDouble lastnew1 = new MutableDouble(0);
            MutableDouble lastmon1 = new MutableDouble(0);
            MutableDouble nextnew1 = new MutableDouble(0);
            double lmon1 = com.chinesebay.ccal.LunarYear.lunaryear(year, vterms1,
                lastnew1, lastmon1, vmoons1, vmonth1, nextnew1);

            // Calculate using ccal2
            com.chinesebay.ccal2.LunarYear.LunarYearResult result2 =
                com.chinesebay.ccal2.LunarYear.lunaryear(year);

            // Compare leap month determination
            System.out.println("\nLeap Month:");
            System.out.printf("  ccal:  %.1f", lmon1);
            if (lmon1 > 0) {
                int baseMonth = (int) lmon1;
                boolean isLeap = (lmon1 - baseMonth > 0.1);
                if (isLeap) {
                    System.out.printf(" (Leap month after month %d)\n", baseMonth);
                } else {
                    System.out.printf(" (No leap month)\n");
                }
            } else {
                System.out.println(" (No leap month)");
            }

            System.out.printf("  ccal2: %.1f", result2.leapMonth);
            if (result2.leapMonth > 0) {
                int baseMonth = (int) result2.leapMonth;
                boolean isLeap = (result2.leapMonth - baseMonth > 0.1);
                if (isLeap) {
                    System.out.printf(" (Leap month after month %d)\n", baseMonth);
                } else {
                    System.out.printf(" (No leap month)\n");
                }
            } else {
                System.out.println(" (No leap month)");
            }

            boolean leapMonthDiffers = Math.abs(lmon1 - result2.leapMonth) > 0.01;
            if (leapMonthDiffers) {
                System.out.println("  *** LEAP MONTH DIFFERS! ***");
            }

            // Compare number of lunar months
            System.out.println("\nNumber of Lunar Months:");
            System.out.printf("  ccal:  %d new moons, %d months\n", vmoons1.size(), vmonth1.size());
            System.out.printf("  ccal2: %d new moons, %d months\n",
                result2.vmoons.size(), result2.vmonth.size());

            // Compare new moon times
            System.out.println("\nNew Moon Times (JD, Beijing Time):");
            System.out.printf("%-4s %20s %20s %15s %15s\n",
                "No.", "ccal", "ccal2", "Diff (days)", "Diff (hours)");
            System.out.println("-".repeat(80));

            int moonCount = Math.min(vmoons1.size(), result2.vmoons.size());
            int significantMoonDiff = 0;
            double maxMoonDiff = 0.0;

            for (int i = 0; i < moonCount; i++) {
                double moon1 = vmoons1.get(i);
                double moon2 = result2.vmoons.get(i);
                double diff = moon2 - moon1;
                double absDiff = Math.abs(diff);

                if (absDiff > maxMoonDiff) {
                    maxMoonDiff = absDiff;
                }

                String marker = "";
                if (absDiff > 1.0 / 1440.0) { // > 1 minute
                    significantMoonDiff++;
                    marker = " ***";
                }

                System.out.printf("%2d   %20.10f %20.10f %15.6f %15.2f%s\n",
                    i + 1, moon1, moon2, diff, diff * 24, marker);
            }

            // Compare month numbers
            System.out.println("\nLunar Month Numbers:");
            System.out.printf("%-4s %10s %10s %15s\n", "No.", "ccal", "ccal2", "Status");
            System.out.println("-".repeat(50));

            int monthCount = Math.min(vmonth1.size(), result2.vmonth.size());
            int monthNumberDiff = 0;

            for (int i = 0; i < monthCount; i++) {
                double month1 = vmonth1.get(i);
                double month2 = result2.vmonth.get(i);
                boolean differs = Math.abs(month1 - month2) > 0.01;

                if (differs) {
                    monthNumberDiff++;
                }

                System.out.printf("%2d   %10.1f %10.1f %15s\n",
                    i + 1, month1, month2, differs ? "DIFFERS ***" : "Same");
            }

            // Summary for this year
            System.out.println("\n" + "-".repeat(80));
            System.out.printf("Year %d Summary:\n", year);
            System.out.printf("  Leap month differs: %s\n", leapMonthDiffers ? "YES ***" : "No");
            System.out.printf("  New moons with diff > 1 min: %d / %d\n",
                significantMoonDiff, moonCount);
            System.out.printf("  Max new moon difference: %.6f days (%.2f hours)\n",
                maxMoonDiff, maxMoonDiff * 24);
            System.out.printf("  Month numbers differ: %d / %d\n", monthNumberDiff, monthCount);

            if (leapMonthDiffers || monthNumberDiff > 0) {
                System.out.println("\n  *** CRITICAL: This year has significant calendar differences! ***");
            }
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("=== Test Complete ===");
        System.out.println("\nConclusion:");
        System.out.println("  Differences in solar terms lead to differences in:");
        System.out.println("  - New moon determination");
        System.out.println("  - Lunar month boundaries");
        System.out.println("  - Leap month identification");
        System.out.println("  - Overall calendar structure");
        System.out.println("\n  Root cause: norm_ang() vs normAng() implementation difference");
        System.out.println("=".repeat(80));
    }
}
