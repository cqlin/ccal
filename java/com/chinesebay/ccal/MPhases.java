package com.chinesebay.ccal;

import java.util.List;

public class MPhases {
    public static void mphases(double tstart, double tend, int phase, List<Double> vjdphases) {
        double offs;
        if (tstart < 2425245) /* Before 1928 */
            offs = (116.0 + 25.0 / 60.0) / 360.0;
        else
            offs = 120.0 / 360.0;
        /* Find the lunation number of the first given phase after tstart */
        double period = 29.53058853;
        int lun = (int) ((tstart - offs - 2451550.09765 - phase * 7.375) / period);
        double jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
        while (jd1 - tstart > 29) {
            lun--;
            jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
        }
        while (tstart > jd1) {
            lun++;
            jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
        }
        /* Compute subsequent phases until after tend */
        vjdphases.clear();
        jd1 = Tt2ut.tt2ut(jd1);
        vjdphases.add(jd1);
        while (jd1 < tend - 29) {
            lun++;
            jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
            jd1 = Tt2ut.tt2ut(jd1);
            if (jd1 < tend)
                vjdphases.add(jd1);
        }
    }
}
