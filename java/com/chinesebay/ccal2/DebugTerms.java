package com.chinesebay.ccal2;

import java.util.ArrayList;

public class DebugTerms {
    public static void main(String[] args) {
        short year = 2026;

        // Get solar terms for 2024
        SolarTerm.SolarTermResult stResult = SolarTerm.solarterm(year);
        ArrayList<Double> vterms = stResult.vjdterms;

        System.out.println("Solar Terms for 2024:");
        String[] jieqi = {"XH", "DH", "LC", "YS", "JZ", "CF", "QM", "GY",
                         "LX", "XM", "MZ", "XZ", "XS", "DS", "LQ", "CS",
                         "BL", "QF", "HL", "SJ", "LD", "XX", "DX", "DZ"};

        for (int i = 0; i < 24; i++) {
            double jd = vterms.get(i);
            int intJd = (int)jd;
            double frac = jd - intJd;

            // Calculate the date from Julian day
            int month = (i / 2) + 1;

            // Calculate what day the solar term falls on
            double jd_month_start = Novas.julianDate(year, (short)month, (short)1, 12.0);
            int day_of_month = (int)(jd - jd_month_start) + 1;

            System.out.printf("%2d. %s: JD=%.6f (int=%d, frac=%.6f) -> Month %d, Day %d\n",
                i, jieqi[i], jd, intJd, frac, month, day_of_month);
        }

        // Check January specifically
        System.out.println("\nJanuary 2024 days:");
        for (int day = 1; day <= 10; day++) {
            double jdcnt = Novas.julianDate(year, (short)1, (short)day, 12.0);
            System.out.printf("Jan %2d: JD=%.6f (int=%d)\n", day, jdcnt, (int)jdcnt);
        }

        // Check if XH matches
        double xh_jd = vterms.get(0);
        System.out.println("\nXH term JD: " + xh_jd);
        for (int day = 1; day <= 10; day++) {
            double jdcnt = Novas.julianDate(year, (short)1, (short)day, 12.0);
            System.out.printf("Jan %2d: JD=%.6f, equals XH? %b\n", day, jdcnt, jdcnt == xh_jd);
        }
    }
}
