package com.chinesebay.ccal2;

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
 * LunarYear.java
 * Determines the lunar month numbers for a given year in the Chinese calendar.
 */
public class LunarYear {
    /**
     * Result class for lunaryear calculation
     */
    public static class LunarYearResult {
        public ArrayList<Double> vterms;  // Solar term days for the given year
        public double lastnew;            // Julian day for the last new moon of previous year
        public double lastmon;            // Month number that started on last new moon day of previous year
        public ArrayList<Double> vmoons;  // New moon days for the given year
        public ArrayList<Double> vmonth;  // Lunar month numbers (with .5 for leap months)
        public double nextnew;            // Julian day for the first new moon of next year
        public double leapMonth;          // Calendar month number with leap month (0.0 if no leap)

        public LunarYearResult(ArrayList<Double> vterms, double lastnew, double lastmon,
                              ArrayList<Double> vmoons, ArrayList<Double> vmonth,
                              double nextnew, double leapMonth) {
            this.vterms = vterms;
            this.lastnew = lastnew;
            this.lastmon = lastmon;
            this.vmoons = vmoons;
            this.vmonth = vmonth;
            this.nextnew = nextnew;
            this.leapMonth = leapMonth;
        }
    }

    /**
     * Determines if the year is a normal year (12 months) or leap year (13 months)
     * @param jdws Time of winter solstice of given year
     * @param vmoons Vector of new moon times since day after winter solstice of previous year
     * @return true if normal year (12 months), false if leap year (13 months)
     */
    private static boolean isNormalYear(double jdws, ArrayList<Double> vmoons) {
        int i = 0, nmonth = 0;
        double jend = jdws + 0.5;
        jend = (int)jend + 0.5;
        while (i < vmoons.size() && vmoons.get(i++) < jend)
            nmonth++;
        assert nmonth == 12 || nmonth == 13 : "Invalid month count: " + nmonth;
        return (nmonth == 12);
    }

    /**
     * Checks if a Zhongqi (major solar term) falls within a lunar month
     * @param jstart Time of new moon for the starting of the month
     * @param jend Time of new moon for the starting of the next month
     * @param vterms Vector of solar terms of the given year
     * @return true if a Zhongqi is in the month, false otherwise
     */
    private static boolean isZhongQiInMonth(double jstart, double jend, ArrayList<Double> vterms) {
        jstart -= 0.5;
        jstart = (int)jstart + 0.5;
        jend -= 0.5;
        jend = (int)jend + 0.5;
        int i;
        for (i = 1; i < vterms.size(); i += 2) {
            if (vterms.get(i) >= jstart && vterms.get(i) < jend)
                return true;
            if (vterms.get(i) >= jend)
                return false;
        }
        return false;
    }

    /**
     * Trims the hour portion of Julian dates, rounding to nearest day
     * @param vjds Vector of Julian dates to trim
     */
    private static void trimHour(ArrayList<Double> vjds) {
        for (int i = 0; i < vjds.size(); i++) {
            int t = (int)(vjds.get(i) + 0.5);
            vjds.set(i, (double)t);
        }
    }

    /**
     * Computes lunar calendar information for a given year
     * @param year The Gregorian calendar year
     * @return LunarYearResult containing all computed information
     */
    public static LunarYearResult lunaryear(short year) {
        // Determine solar terms
        SolarTerm.SolarTermResult stResult = SolarTerm.solarterm(year);
        double jdpws = stResult.jdpws;
        ArrayList<Double> vterms = stResult.vjdterms;

        // Determine new moons since day after previous winter solstice
        double jstart = jdpws + 0.5;
        jstart = (int)jstart + 0.5;
        double jend = Novas.julianDate(year, (short)12, (short)31, 23.9999);
        ArrayList<Double> vmoons = MPhases.mphases(jstart, jend, 0);

        // Determine the month numbers
        ArrayList<Double> vmonth = new ArrayList<>(vmoons.size());
        for (int i = 0; i < vmoons.size(); i++)
            vmonth.add(0.0);

        int i;
        if (isNormalYear(vterms.get(23), vmoons)) { // Normal year
            int n = 12;
            for (i = 0; i < vmoons.size(); i++) {
                vmonth.set(i, (double)n);
                if (n == 12)
                    n = 0;
                n++;
            }
            if (n == 1) { // Involves next lunar year
                SolarTerm.SolarTermResult stNextResult = SolarTerm.solarterm((short)(year + 1));
                double jdnwsp = stNextResult.jdpws;
                ArrayList<Double> vjdnterms = stNextResult.vjdterms;

                ArrayList<Double> vnmoons;
                jstart = jdnwsp + 0.5;
                jstart = (int)jstart + 0.5;
                jend = Novas.julianDate((short)(year + 1), (short)12, (short)31, 23.999);
                vnmoons = MPhases.mphases(jstart, jend, 0);

                if (!isNormalYear(vjdnterms.get(23), vnmoons)) {
                    // Check if a zhongqi falls inside the month
                    if (!isZhongQiInMonth(vnmoons.get(0), vnmoons.get(1), vjdnterms))
                        vmonth.set(vmonth.size() - 1, 11.5);
                }
            }
        } else { // Leap year
            boolean bleaped = false;
            int n = 11;
            for (i = 0; i < vmoons.size() - 1; i++) {
                // Check if a zhongqi falls inside the month
                if (bleaped || isZhongQiInMonth(vmoons.get(i), vmoons.get(i + 1), vterms))
                    vmonth.set(i, (double)(++n));
                else {
                    if (n == 0)
                        vmonth.set(i, 12.5);
                    else
                        vmonth.set(i, n + 0.5);
                    bleaped = true;
                }
                if (n == 12)
                    n = 0;
            }
            vmonth.set(vmonth.size() - 1, (double)(n + 1));
        }

        double lastnew, lastmon;
        if (vmoons.get(0) < Novas.julianDate(year, (short)1, (short)1, 0.0)) {
            lastnew = Math.floor(vmoons.get(0) + 0.5);
            lastmon = vmonth.get(0);
            vmoons.remove(0);
            vmonth.remove(0);
        } else { // Need to find the last new moon for previous year
            ArrayList<Double> vlastnew = MPhases.mphases(vmoons.get(0) - 35.0, vmoons.get(0) - 25.0, 0);
            trimHour(vlastnew);
            lastnew = vlastnew.get(vlastnew.size() - 1);
            lastmon = 11.0;
        }

        // Need to find the first new moon for next year
        ArrayList<Double> vnextnew = MPhases.mphases(vmoons.get(vmoons.size() - 1) + 25.0,
                                                     vmoons.get(vmoons.size() - 1) + 35.0, 0);
        trimHour(vnextnew);
        double nextnew = vnextnew.get(vnextnew.size() - 1);

        // Convert to whole day numbers
        trimHour(vmoons);
        trimHour(vterms);

        // Apply correction from DE405
        if (year == 1774)
            vterms.set(2, vterms.get(2) + 1);
        if (year == 1878)
            vterms.set(8, vterms.get(8) + 1);
        if (year == 1951)
            vterms.set(23, vterms.get(23) + 1);

        // Scan for leap month and return the calendar month
        double leapMonth = 0.0;
        if ((int)(lastmon + 0.9) != (int)lastmon) { // lastmon is a leap month
            if (Novas.julianDate(year, (short)1, (short)1, 12.0) < vmoons.get(0)) { // runs into new year
                leapMonth = 0.5;
            }
        }
        if (leapMonth == 0.0) {
            short monnum = 2;
            for (i = 0; i < vmoons.size(); i++) {
                if ((int)(vmonth.get(i) + 0.9) != (int)(vmonth.get(i).doubleValue())) { // found leap month
                    double jdfirst;
                    while ((jdfirst = Novas.julianDate(year, monnum, (short)1, 12.0)) < vmoons.get(i) && monnum <= 12)
                        monnum++;
                    if (monnum == 13) {
                        leapMonth = 12.0;
                        break;
                    }
                    // See if leap month runs into next month
                    if (i != vmoons.size() - 1 && jdfirst < vmoons.get(i + 1))
                        leapMonth = monnum - 0.5; // Yes
                    else
                        leapMonth = monnum - 1.0; // No
                    break;
                }
            }
        }

        return new LunarYearResult(vterms, lastnew, lastmon, vmoons, vmonth, nextnew, leapMonth);
    }
}
