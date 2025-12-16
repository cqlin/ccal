package test;

import com.chinesebay.ccal.Novascon;
import com.chinesebay.ccal2.NovasConstants;

/**
 * Test to compare the norm_ang vs normAng function implementations
 * This is the ROOT CAUSE of calendar differences between ccal and ccal2
 */
public class NormAngComparisonTest {

    public static void main(String[] args) {
        System.out.println("=== Comparing norm_ang (ccal) vs normAng (ccal2) ===\n");

        // Test various angles including edge cases
        double[] testAngles = {
            0.0,
            Math.PI / 2,
            Math.PI,
            3 * Math.PI / 2,
            2 * Math.PI,
            -Math.PI / 2,
            -Math.PI,
            10 * Math.PI,
            -10 * Math.PI,
            100.0,
            -100.0,
            1000.0,
            -1000.0,
            // Edge cases near TWOPI boundaries
            Novascon.TWOPI - 0.001,
            Novascon.TWOPI + 0.001,
            -Novascon.TWOPI + 0.001,
            -Novascon.TWOPI - 0.001,
            // Large values (astronomical calculations)
            485868.249036 * Novascon.ASEC2RAD,
            1287104.793048 * Novascon.ASEC2RAD
        };

        System.out.println("TWOPI = " + Novascon.TWOPI);
        System.out.println();

        boolean foundDifference = false;
        int differenceCount = 0;

        for (double angle : testAngles) {
            double result1 = com.chinesebay.ccal.Novas.norm_ang(angle);
            double result2 = com.chinesebay.ccal2.Novas.normAng(angle);
            double diff = Math.abs(result1 - result2);

            if (diff > 1e-10) {
                foundDifference = true;
                differenceCount++;
                System.out.printf("DIFFERENCE FOUND:\n");
                System.out.printf("  Input angle:     %.15f\n", angle);
                System.out.printf("  ccal  result:    %.15f\n", result1);
                System.out.printf("  ccal2 result:    %.15f\n", result2);
                System.out.printf("  Absolute diff:   %.15e\n", diff);
                System.out.printf("  Diff in degrees: %.10f°\n", Math.toDegrees(diff));
                System.out.printf("  Diff in arcsec:  %.6f\"\n\n", Math.toDegrees(diff) * 3600);
            } else {
                System.out.printf("SAME: angle=%.6f -> result=%.15f\n", angle, result1);
            }
        }

        System.out.println("\n=== Summary ===");
        System.out.printf("Total tests: %d\n", testAngles.length);
        System.out.printf("Differences found: %d\n", differenceCount);
        System.out.printf("Match rate: %.1f%%\n", 100.0 * (testAngles.length - differenceCount) / testAngles.length);

        if (foundDifference) {
            System.out.println("\n*** CRITICAL: The two implementations produce different results! ***");
            System.out.println("This explains why solar terms and lunar months differ.");
            System.out.println("\nCause: Java's % operator vs Math.IEEEremainder()");
            System.out.println("  % operator:       returns remainder with sign of dividend");
            System.out.println("  IEEEremainder():  returns IEEE 754 remainder (more accurate)");
        }
    }
}
