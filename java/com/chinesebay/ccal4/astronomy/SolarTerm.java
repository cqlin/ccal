package com.chinesebay.ccal4.astronomy;

/*
   Copyright (c) 2000-2012, by Zhuo Meng (zxm8@case.edu).
   All rights reserved.

   Distributed under the terms of the GNU Lesser General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.util.ArrayList;

/**
 * SolarTerm.java
 * Computes Beijing time of solar terms for a given year.
 *
 * Solar terms (Jieqi) are 24 points in the traditional Chinese calendar
 * marking important astronomical events based on the Sun's position along
 * the ecliptic. Each term is 15 degrees apart.
 */
public class SolarTerm {
    private static final double PI = 3.14159265358979323846;
    private static final double ERR_LIMIT = 360.0 / 365.2422 / 86400.0; // One second
    private static final int maxIterations = 200;

    /**
     * Result class for solarterm calculation
     */
    public static class SolarTermResult {
        public double jdpws;  // Julian date of previous year's winter solstice (Beijing Time)
        public ArrayList<Double> vjdterms;  // Julian dates of all 24 solar terms (Beijing Time)

        public SolarTermResult(double jdpws, ArrayList<Double> vjdterms) {
            this.jdpws = jdpws;
            this.vjdterms = vjdterms;
        }
    }

    /**
     * Computes the angle in degrees of given UTC time,
     * with 0 being winter solstice
     */
    public static double timeangle(double t) {
        double[] sposg = new double[3];
        double[] spos = new double[3];
        double[] eposb = new double[3];
        double[] evelb = new double[3];
        double[] eposh = new double[3];
        double[] evelh = new double[3];
        double y, angle;
        double secdiff, tdb, lighttime;

        TDB2TTResult tdbResult = Novas.tdb2tt(t);
        secdiff = tdbResult.secdiff;
        tdb = t + secdiff / 86400.0;

        // Get Earth position
        SolarSystemResult result = SolarSystem.solarsystem(tdb, (short)3, NovasConstants.HELIOC);
        eposh = result.position;
        evelh = result.velocity;
        result = SolarSystem.solarsystem(tdb, (short)3, NovasConstants.BARYC);
        eposb = result.position;
        evelb = result.velocity;

        // Convert to geocentric sun position
        spos[0] = -eposh[0];
        spos[1] = -eposh[1];
        spos[2] = -eposh[2];

        lighttime = Math.sqrt(spos[0]*spos[0] + spos[1]*spos[1] + spos[2]*spos[2])
                    / NovasConstants.C_AUDAY;

        sposg = Novas.aberration(spos, evelb, lighttime);
        spos = Novas.precession(NovasConstants.T0, sposg, tdb);
        sposg = Novas.nutation(tdb, NovasConstants.FN0, (short)0, spos);

        y = Math.sqrt(sposg[1] * sposg[1] + sposg[2] * sposg[2]);
        y *= (sposg[1] > 0.0) ? 1.0 : -1.0;
        angle = Math.atan2(y, sposg[0]) + PI / 2.0;
        if (angle < 0.0)
            angle += 2.0 * PI;
        angle *= 180.0 / PI;

        return angle;
    }

    /**
     * Adjusts lower bound for discontinuity and early range start
     */
    private static class BoundaryValues {
        double tl, vl;
        BoundaryValues(double tl, double vl) {
            this.tl = tl;
            this.vl = vl;
        }
    }

    private static BoundaryValues chklowvalue(double tl, double vl, double vu, double termang) {
        // For discontinuity at 0
        if (vl > vu)
            vl -= 360.0;
        // In case the range start time is not early enough
        // As Gregorian year is slightly longer than tropical year, term time shifts backwards
        while (vl > ERR_LIMIT) {
            tl -= 4;
            vl = timeangle(tl) - termang;
            // Check discontinuity again after adjustment
            if (vl > vu)
                vl -= 360.0;
        }
        return new BoundaryValues(tl, vl);
    }

    /**
     * Computes the UTC time for given solar term using regula falsi method
     * @param tstart search start time (Julian date)
     * @param tend search end time (Julian date)
     * @param termang angle value of the given term (degrees)
     * @return the UTC time for the solar term (Julian date)
     */
    private static double termtime(double tstart, double tend, double termang) {
        double tl, tu, t, f;
        double v, vl, vu;
        int iteration = 0;

        tl = tstart;
        tu = tend;
        vl = timeangle(tl) - termang;
        vu = timeangle(tu) - termang;

        BoundaryValues bv = chklowvalue(tl, vl, vu, termang);
        tl = bv.tl;
        vl = bv.vl;

        t = tl;
        v = vl;

        do {
            if (Math.abs(vu - vl) < ERR_LIMIT) {
                t = (tu + tl) / 2.0;
                v = (vu + vl) / 2.0;
                break;
            }

            f = (tu - tl) / (vu - vl);
            t = tl - f * vl;
            v = timeangle(t) - termang;

            if (v < 0 || v > vu) {
                bv = chklowvalue(t, v, vu, termang);
                tl = bv.tl;
                vl = bv.vl;
                t = tl;
                v = vl;
            } else {
                vu = v;
                tu = t;
            }

            iteration++;
            if (iteration > maxIterations) {
                // Fallback to bisection if not converging
                t = (tstart + tend) / 2.0;
                break;
            }
        } while (Math.abs(v) > ERR_LIMIT);

        return TT2UT.tt2ut(t);
    }

    /**
     * Given year, computes the julian date (Beijing Time) of the winter solstice
     * of previous year and all solar terms of the given year
     *
     * @param year The Gregorian calendar year
     * @return SolarTermResult containing jdpws and vjdterms
     */
    public static SolarTermResult solarterm(short year) {
        int dstart, dend;
        short j, month, day;
        double angle, offs;
        double jdpws;
        ArrayList<Double> vjdterms = new ArrayList<>(24);

        if (year < 1928)
            offs = (116.0 + 25.0 / 60.0) / 360.0;
        else
            offs = 120.0 / 360.0;

        // Determine the time of winter solstice of previous year
        dstart = (int) Novas.julian_date((short)(year - 1), (short)12, (short)12, 12.0);
        dend = (int) Novas.julian_date((short)(year - 1), (short)12, (short)25, 12.0);
        jdpws = termtime(dstart, dend, 0.0) + offs;

        for (j = 0; j < 24; j++) {
            month = (short)(j / 2 + 1);
            day = (short)((j % 2) * 14);
            dstart = (int) Novas.julian_date(year, month, (short)(1 + day), 12.0);
            dend = (int) Novas.julian_date(year, month, (short)(11 + day), 12.0);
            angle = ((j + 1) % 24) * 15.0;
            vjdterms.add(termtime(dstart, dend, angle) + offs);
        }

        return new SolarTermResult(jdpws, vjdterms);
    }
}
