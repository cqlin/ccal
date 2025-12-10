package com.chinesebay.ccal2;

public class DebugTimeangle {
    public static void main(String[] args) {
        // Test timeangle for LC 2026 around Feb 4
        System.out.println("Testing timeangle for 2026 LC (around Feb 4):");

        // Try a few Julian dates around Feb 4
        double jd_feb3 = Novas.julianDate((short)2026, (short)2, (short)3, 12.0);
        double jd_feb4 = Novas.julianDate((short)2026, (short)2, (short)4, 12.0);
        double jd_feb5 = Novas.julianDate((short)2026, (short)2, (short)5, 12.0);

        System.out.println("\nJD Feb 3: " + jd_feb3);
        System.out.println("JD Feb 4: " + jd_feb4);
        System.out.println("JD Feb 5: " + jd_feb5);

        System.out.println("\nCalculating solar term for 2026:");
        SolarTerm.SolarTermResult stResult = SolarTerm.solarterm((short)2026);
        double lc_jd = stResult.vjdterms.get(2);  // LC is index 2

        System.out.println("LC JD from solarterm: " + lc_jd);
        System.out.println("Expected (from C++): 2461075.667884");
        System.out.println("Difference: " + (lc_jd - 2461075.667884));
    }
}
