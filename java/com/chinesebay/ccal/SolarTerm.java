package com.chinesebay.ccal;

import java.util.List;
import static com.chinesebay.ccal.Novascon.*;

public class SolarTerm {
    public static final double PI = 3.141592653589793;
    public static final double errlimit = 360.0 / 365.2422 / 86400.0; /* One second */

    /* Computes the angle in degrees of given UTC time,
       with 0 being winter solstice
    */
    static double timeangle(double t) {
        double[] sposg = new double[3];
        double[] spos = new double[3];
        double[] eposb = new double[3];
        double[] evelb = new double[3];
        double[] eposh = new double[3];
        double[] evelh = new double[3];
        double y, angle;
        MutableDouble dummy = new MutableDouble(0);
        MutableDouble secdiff = new MutableDouble(0);
        double tdb, lighttime;

        Novas.tdb2tt(t, dummy, secdiff);
        tdb = t + secdiff.value / 86400.0;
        Solsys3.solarsystem(tdb, (short) 3, (short) Novas.HELIOC, eposh, evelh);
        Solsys3.solarsystem(tdb, (short) 3, (short) Novas.BARYC, eposb, evelb);
        /* Convert to geocentric sun position */
        spos[0] = -eposh[0];
        spos[1] = -eposh[1];
        spos[2] = -eposh[2];
        lighttime = Math.sqrt(spos[0] * spos[0] + spos[1] * spos[1] + spos[2] * spos[2]) / Novascon.C_AUDAY;
        Novas.aberration(spos, evelb, lighttime, sposg);
        
        Novas.precession(Novascon.T0, sposg, tdb, spos);

        Novas.nutation(tdb, Novascon.FN0, (short) 0, spos, sposg);

        y = Math.sqrt(sposg[1] * sposg[1] + sposg[2] * sposg[2]);
        y *= (sposg[1] > 0.0) ? 1.0 : -1.0;
        angle = Math.atan2(y, sposg[0]) + PI / 2.0;
        if (angle < 0.0)
            angle += 2.0 * PI;
        angle *= 180.0 / PI;
        return angle;
    }

    static void chklowvalue(MutableDouble tl, MutableDouble vl, MutableDouble vu, double termang) {
        /* For discontinuity at 0 */
        if (vl.value > vu.value)
            vl.value -= 360.0;
        /* In case the range start time is not early enough */
        /* As Gregorian year is slightly longer than tropical year, term time shifts backwards */
        if (vl.value > errlimit) {
            tl.value -= 4;
            vl.value = timeangle(tl.value) - termang;
            chklowvalue(tl, vl, vu, termang);
        }
    }

    /* Computes the UTC time for given solar term:
       tstart:  search start time
       tend:    search end time
       termang: angle value of the given term
       returns the UTC time for the solar term
    */
    static double termtime(double tstart, double tend, double termang) {
        double tl, tu, t, f;
        double v, vl_val, vu_val;

        tl = tstart;
        tu = tend;
        vl_val = timeangle(tl) - termang;
        vu_val = timeangle(tu) - termang;
        
        MutableDouble vl_mut = new MutableDouble(vl_val);
        MutableDouble vu_mut = new MutableDouble(vu_val);
        MutableDouble tl_mut = new MutableDouble(tl);
        MutableDouble tu_mut = new MutableDouble(tu); // Initialize tu_mut here

        chklowvalue(tl_mut, vl_mut, vu_mut, termang);
        vl_val = vl_mut.value;
        tl = tl_mut.value;

        do {
            if (Math.abs(vu_mut.value - vl_mut.value) < errlimit) {
                t = (tu_mut.value + tl_mut.value) / 2.0;
                v = (vu_mut.value + vl_mut.value) / 2.0;
                break;
            }
            f = (tu_mut.value - tl_mut.value) / (vu_mut.value - vl_mut.value);
            t = tl_mut.value - f * vl_mut.value;
            v = timeangle(t) - termang;
            if (v < 0 || v > vu_mut.value) {
                vl_mut.value = v;
                tl_mut.value = t;
                // Re-call chklowvalue with updated tl_mut and vl_mut, using the new vu_mut
                chklowvalue(tl_mut, vl_mut, vu_mut, termang);
            } else {
                vu_mut.value = v;
                tu_mut.value = t;
            }
        } while (Math.abs(v) > errlimit);
        return Tt2ut.tt2ut(t);
    }

    /* Given year, computes the julian date (Beijing Time) of the winter solstice
       of previous year (jdpws) and all solarterms of the given year (vjdterms)
    */
    public static void solarterm(short year, MutableDouble jdpws, List<Double> vjdterms) {
        int dstart, dend;
        short j, month, day;
        double angle, offs;

        if (year < 1928)
            offs = (116.0 + 25.0 / 60.0) / 360.0;
        else
            offs = 120.0 / 360.0;
        /* Determine the time of winter solstice of previous year */
        dstart = (int) Novas.julian_date((short) (year - 1), (short) 12, (short) 12, 12.0);
        dend = (int) Novas.julian_date((short) (year - 1), (short) 12, (short) 25, 12.0);
        jdpws.value = termtime(dstart, dend, 0.0) + offs;
        
        vjdterms.clear(); // Clear existing elements
        // vjdterms.ensureCapacity(24); // Removed as List does not have this method

        for (j = 0; j < 24; j++) {
            month = (short) (j / 2 + 1);
            day = (short) ((j % 2) * 14);
            dstart = (int) Novas.julian_date(year, month, (short) (1 + day), 12.0);
            dend = (int) Novas.julian_date(year, month, (short) (11 + day), 12.0);
            angle = ((j + 1) % 24) * 15.0;
            vjdterms.add(termtime(dstart, dend, angle) + offs);
        }
    }
}
