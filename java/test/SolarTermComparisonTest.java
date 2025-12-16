package test;

import java.util.ArrayList;
import java.util.List;

import com.chinesebay.ccal.MutableDouble;

/**
 * Test to compare solar term calculations between ccal and ccal2
 * Solar terms are critical for determining lunar calendar months
 */
public class SolarTermComparisonTest {

	private static final String[] JIEQI_NAMES = {
			"XH", "DH", "LC", "YS", "JZ", "CF",
			"QM", "GY", "LX", "XM", "MZ", "XZ",
			"XS", "DS", "LQ", "CS", "BL", "QF",
			"HL", "SJ", "LD", "XX", "DX", "DZ"
	};

	public static void main(String[] args) {
		System.out.println("=== Comparing Solar Term Calculations (ccal vs ccal2) ===\n");

		// Test multiple years to see if differences are consistent
		short[] testYears = {3000, 2222, 2026, 2025, 2024, 2000, 1990, 1980, 1600, 1000};

		System.out.println("angle3 without fmod:"+com.chinesebay.ccal3.SolarTerm.timeangle(10000));
		System.out.println("angle2 with fmod:"+com.chinesebay.ccal2.SolarTerm.timeangle(10000));
		System.out.println("angle1 simplified nutation:"+com.chinesebay.ccal.SolarTerm.timeangle(10000));

		for (short year : testYears) {
			System.out.println("\n" + "=".repeat(70));
			System.out.printf("Testing Year: %d\n", year);
			System.out.println("=".repeat(70));

			// Calculate using ccal3
			com.chinesebay.ccal3.SolarTerm.SolarTermResult result3 =
					com.chinesebay.ccal3.SolarTerm.solarterm(year);

			// Calculate using ccal2
			com.chinesebay.ccal2.SolarTerm.SolarTermResult result2 =
					com.chinesebay.ccal2.SolarTerm.solarterm(year);


			// Compare previous winter solstice
			double pwsDiff = Math.abs(result3.jdpws - result2.jdpws);
			System.out.printf("\nPrevious Winter Solstice (JD, Beijing Time):\n");
			System.out.printf("  ccal3:  %.10f\n", result3.jdpws);
			System.out.printf("  ccal2: %.10f\n", result2.jdpws);
			System.out.printf("  Diff:  %.10f days (%.2f hours)\n", pwsDiff, pwsDiff * 24);

			// Compare all 24 solar terms
			System.out.printf("\nSolar Terms Comparison:\n");
			System.out.printf("%-4s %-10s %18s %18s %12s %12s\n",
					"No.", "Term", "ccal3 (JD)", "ccal2 (JD)", "Diff (days)", "Diff (hours)");
			System.out.println("-".repeat(80));

			int significantDiffCount = 0;
			double maxDiff = 0.0;
			int maxDiffIndex = -1;

			for (int i = 0; i < 24; i++) {
				double term1 = result3.vjdterms.get(i);
				double term2 = result2.vjdterms.get(i);
				double diff = term2 - term1;
				double absDiff = Math.abs(diff);
				double diffHours = diff * 24;

				if (absDiff > maxDiff) {
					maxDiff = absDiff;
					maxDiffIndex = i;
				}

				// Flag if difference is more than 1 minute (1/1440 days)
				String marker = "";
				if (absDiff > 1.0 / 1440.0) {
					significantDiffCount++;
					marker = " ***";
				}

				System.out.printf("%2d   %-10s %18.10f %18.10f %12.6f %12.2f%s\n",
						i + 1, JIEQI_NAMES[i], term1, term2, diff, diffHours, marker);
			}

			System.out.println("-".repeat(80));
			System.out.printf("\nYear %d Summary:\n", year);
			System.out.printf("  Terms with diff > 1 minute: %d / 24\n", significantDiffCount);
			System.out.printf("  Maximum difference: %.6f days (%.2f hours)\n", maxDiff, maxDiff * 24);
			if (maxDiffIndex >= 0) {
				System.out.printf("  Occurred at: %s (term #%d)\n",
						JIEQI_NAMES[maxDiffIndex], maxDiffIndex + 1);
			}

			// Convert to dates for readability
			if (significantDiffCount > 0) {
				System.out.println("\n  Example date differences:");
				for (int i = 0; i < 24; i++) {
					double diff = Math.abs(result2.vjdterms.get(i) - result3.vjdterms.get(i));
					if (diff > 1.0 / 1440.0) {
						System.out.printf("    %s: JD diff = %d (%.1f minutes)\n",
								JIEQI_NAMES[i], result2.vjdterms.get(i).intValue()-result3.vjdterms.get(i).intValue(), diff * 24 * 60);
					}
				}
			}

			// Calculate using ccal
			MutableDouble jdpws1 = new MutableDouble(0);
			List<Double> vterms1 = new ArrayList<>();
			com.chinesebay.ccal.SolarTerm.solarterm(year, jdpws1, vterms1);

			// Compare previous winter solstice
			pwsDiff = Math.abs(jdpws1.value - result2.jdpws);
			System.out.printf("\nPrevious Winter Solstice (JD, Beijing Time):\n");
			System.out.printf("  ccal:  %.10f\n", jdpws1.value);
			System.out.printf("  ccal2: %.10f\n", result2.jdpws);
			System.out.printf("  Diff:  %.10f days (%.2f hours)\n", pwsDiff, pwsDiff * 24);

			// Compare all 24 solar terms
			System.out.printf("\nSolar Terms Comparison:\n");
			System.out.printf("%-4s %-10s %18s %18s %12s %12s\n",
					"No.", "Term", "ccal (JD)", "ccal2 (JD)", "Diff (days)", "Diff (hours)");
			System.out.println("-".repeat(80));

			significantDiffCount = 0;
			maxDiff = 0.0;
			maxDiffIndex = -1;

			for (int i = 0; i < 24; i++) {
				double term1 = vterms1.get(i);
				double term2 = result2.vjdterms.get(i);
				double diff = term2 - term1;
				double absDiff = Math.abs(diff);
				double diffHours = diff * 24;

				if (absDiff > maxDiff) {
					maxDiff = absDiff;
					maxDiffIndex = i;
				}

				// Flag if difference is more than 1 minute (1/1440 days)
				String marker = "";
				if (absDiff > 1.0 / 1440.0) {
					significantDiffCount++;
					marker = " ***";
				}

				System.out.printf("%2d   %-10s %18.10f %18.10f %12.6f %12.2f%s\n",
						i + 1, JIEQI_NAMES[i], term1, term2, diff, diffHours, marker);
			}

			System.out.println("-".repeat(80));
			System.out.printf("\nYear %d Summary:\n", year);
			System.out.printf("  Terms with diff > 1 minute: %d / 24\n", significantDiffCount);
			System.out.printf("  Maximum difference: %.6f days (%.2f hours)\n", maxDiff, maxDiff * 24);
			if (maxDiffIndex >= 0) {
				System.out.printf("  Occurred at: %s (term #%d)\n",
						JIEQI_NAMES[maxDiffIndex], maxDiffIndex + 1);
			}

			// Convert to dates for readability
			if (significantDiffCount > 0) {
				System.out.println("\n  Example date differences:");
				for (int i = 0; i < 24; i++) {
					double diff = Math.abs(result2.vjdterms.get(i) - vterms1.get(i));
					if (diff > 1.0 / 1440.0) {
						System.out.printf("    %s: JD diff = %d (%.1f minutes)\n",
								JIEQI_NAMES[i], result2.vjdterms.get(i).intValue() - vterms1.get(i).intValue(), diff * 24 * 60);
					}
				}
			}

			System.out.println("\n" + "=".repeat(70));
			System.out.println("*** These differences in solar terms will cause different ***");
			System.out.println("*** lunar month boundaries and leap month determination.  ***");
			System.out.println("=".repeat(70));
		}
	}
}
