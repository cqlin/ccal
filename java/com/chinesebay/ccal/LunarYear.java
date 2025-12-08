package com.chinesebay.ccal;

import java.util.List;

public class LunarYear {
    public static class LunarYearResult {
        public List<Double> vterms;
        public double lastnew;
        public double lastmon;
        public List<Double> vmoons;
        public List<Double> vmonth;
        public double nextnew;
    }

    static boolean IsNormalYear(double jdws, List<Double> vmoons) {
        int i = 0, nmonth = 0;
        double jend = jdws + 0.5;
        jend = (int) jend + 0.5;
        while (i < vmoons.size() && vmoons.get(i++) < jend)
            nmonth++;
        if (nmonth != 12 && nmonth != 13)
            throw new RuntimeException("nmonth is not 12 or 13");
        if (nmonth == 12) /* Normal year */
            return true;
        return false;
    }

    static boolean IsZhongQiInMonth(double jstart, double jend, List<Double> vterms) {
        jstart -= 0.5;
        jstart = (int) jstart + 0.5;
        jend -= 0.5;
        jend = (int) jend + 0.5;
        for (int i = 1; i < vterms.size(); i += 2) {
            if (vterms.get(i) >= jstart && vterms.get(i) < jend)
                return true;
            if (vterms.get(i) >= jend)
                return false;
        }
        return false;
    }

    static void TrimHour(List<Double> vjds) {
        for (int i = 0; i < vjds.size(); i++) {
            vjds.set(i, (double) ((int) (vjds.get(i) + 0.5)));
        }
    }

    public static double lunaryear(short year, List<Double> vterms, MutableDouble lastnew, MutableDouble lastmon, List<Double> vmoons, List<Double> vmonth, MutableDouble nextnew) {
        final boolean USE_YEARCACHE = true;

        if (USE_YEARCACHE && YearCache.IsInRange(year)) {
            YearCache.GetYear(year, vterms, lastnew, lastmon, vmoons, vmonth, nextnew);
        } else {
            MutableDouble jdpws = new MutableDouble(0);
            SolarTerm.solarterm(year, jdpws, vterms);
            double jstart = jdpws.value + 0.5;
            jstart = (int) jstart + 0.5;
            double jend = Novas.julian_date(year, (short) 12, (short) 31, 23.9999);
            MPhases.mphases(jstart, jend, 0, vmoons);

            for (int i = 0; i < vmoons.size(); i++) {
                vmonth.add(0.0);
            }

            if (IsNormalYear(vterms.get(23), vmoons)) {
                int n = 12;
                for (int i = 0; i < vmoons.size(); i++) {
                    vmonth.set(i, (double) n);
                    if (n == 12)
                        n = 0;
                    n++;
                }
                if (n == 1) {
                    MutableDouble jdnwsp = new MutableDouble(0);
                    List<Double> vjdnterms = new java.util.ArrayList<>();
                    SolarTerm.solarterm((short) (year + 1), jdnwsp, vjdnterms);
                    List<Double> vnmoons = new java.util.ArrayList<>();
                    jstart = jdnwsp.value + 0.5;
                    jstart = (int) jstart + 0.5;
                    jend = Novas.julian_date((short) (year + 1), (short) 12, (short) 31, 23.999);
                    MPhases.mphases(jstart, jend, 0, vnmoons);
                    if (!IsNormalYear(vjdnterms.get(23), vnmoons)) {
                        if (!IsZhongQiInMonth(vnmoons.get(0), vnmoons.get(1), vjdnterms))
                            vmonth.set(vmonth.size() - 1, 11.5);
                    }
                }
            } else {
                boolean bleaped = false;
                int n = 11;
                for (int i = 0; i < vmoons.size() - 1; i++) {
                    if (bleaped || IsZhongQiInMonth(vmoons.get(i), vmoons.get(i + 1), vterms))
                        vmonth.set(i, (double) ++n);
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
                vmonth.set(vmonth.size() - 1, (double) (n + 1));
            }
            if (vmoons.get(0) < Novas.julian_date(year, (short) 1, (short) 1, 0.0)) {
                lastnew.value = Math.floor(vmoons.get(0) + 0.5);
                lastmon.value = vmonth.get(0);
                vmoons.remove(0);
                vmonth.remove(0);
            } else {
                List<Double> vlastnew = new java.util.ArrayList<>();
                MPhases.mphases(vmoons.get(0) - 35.0, vmoons.get(0) - 25.0, 0, vlastnew);
                TrimHour(vlastnew);
                lastnew.value = vlastnew.get(vlastnew.size() - 1);
                lastmon.value = 11.0;
            }
            List<Double> vnextnew = new java.util.ArrayList<>();
            MPhases.mphases(vmoons.get(vmoons.size() - 1) + 25.0, vmoons.get(vmoons.size() - 1) + 35.0, 0, vnextnew);
            TrimHour(vnextnew);
            nextnew.value = vnextnew.get(vnextnew.size() - 1);
            TrimHour(vmoons);
            TrimHour(vterms);
            if (year == 1774)
                vterms.set(2, vterms.get(2) + 1);
            if (year == 1878)
                vterms.set(8, vterms.get(8) + 1);
            if (year == 1951)
                vterms.set(23, vterms.get(23) + 1);
        }
        if ((int) (lastmon.value + 0.9) != (int) lastmon.value) {
            if (Novas.julian_date(year, (short) 1, (short) 1, 12.0) < vmoons.get(0))
                return 0.5;
        }
        short monnum = 2;
        for (int i = 0; i < vmoons.size(); i++) {
            if ((int) (vmonth.get(i) + 0.9) != (int) (vmonth.get(i).doubleValue())) {
                double jdfirst;
                while ((jdfirst = Novas.julian_date(year, monnum, (short) 1, 12.0)) < vmoons.get(i) && monnum <= 12)
                    monnum++;
                if (monnum == 13)
                    return 12.0;
                if (i != vmoons.size() - 1 && jdfirst < vmoons.get(i + 1))
                    return (monnum - 0.5);
                else
                    return (monnum - 1.0);
            }
        }
        return 0.0;
    }
}
