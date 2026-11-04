package com.chinesebay.ccal3;

import static com.chinesebay.ccal3.NovasConstants.*;

import java.util.Arrays;

public class SolarSystem {

    private static class SunCon {
        double l;
        double r;
        double alpha;
        double nu;

        SunCon(double l, double r, double alpha, double nu) {
            this.l = l;
            this.r = r;
            this.alpha = alpha;
            this.nu = nu;
        }
    }

    private static final SunCon[] con = {
        new SunCon(403406.0, 0.0, 4.721964, 1.621043),
        new SunCon(195207.0, -97597.0, 5.937458, 62830.348067),
        new SunCon(119433.0, -59715.0, 1.115589, 62830.821524),
        new SunCon(112392.0, -56188.0, 5.781616, 62829.634302),
        new SunCon(3891.0, -1556.0, 5.5474, 125660.5691),
        new SunCon(2819.0, -1126.0, 1.5120, 125660.9845),
        new SunCon(1721.0, -861.0, 4.1897, 62832.4766),
        new SunCon(0.0, 941.0, 1.163, 0.813),
        new SunCon(660.0, -264.0, 5.415, 125659.310),
        new SunCon(350.0, -163.0, 4.315, 57533.850),
        new SunCon(334.0, 0.0, 4.553, -33.931),
        new SunCon(314.0, 309.0, 5.198, 777137.715),
        new SunCon(268.0, -158.0, 5.989, 78604.191),
        new SunCon(242.0, 0.0, 2.911, 5.412),
        new SunCon(234.0, -54.0, 1.423, 39302.098),
        new SunCon(158.0, 0.0, 0.061, -34.861),
        new SunCon(132.0, -93.0, 2.317, 115067.698),
        new SunCon(129.0, -20.0, 3.193, 15774.337),
        new SunCon(114.0, 0.0, 2.828, 5296.670),
        new SunCon(99.0, -47.0, 0.52, 58849.27),
        new SunCon(93.0, 0.0, 4.65, 5296.11),
        new SunCon(86.0, 0.0, 4.35, -3980.70),
        new SunCon(78.0, -33.0, 2.75, 52237.69),
        new SunCon(72.0, -32.0, 4.50, 55076.47),
        new SunCon(68.0, 0.0, 3.23, 261.08),
        new SunCon(64.0, -10.0, 1.22, 15773.85),
        new SunCon(46.0, -16.0, 0.14, 188491.03),
        new SunCon(38.0, 0.0, 3.44, -7756.55),
        new SunCon(37.0, 0.0, 4.37, 264.89),
        new SunCon(32.0, -24.0, 1.14, 117906.27),
        new SunCon(29.0, -13.0, 2.84, 55075.75),
        new SunCon(28.0, 0.0, 5.96, -7961.39),
        new SunCon(27.0, -9.0, 5.09, 188489.81),
        new SunCon(27.0, 0.0, 1.72, 2132.19),
        new SunCon(25.0, -17.0, 2.56, 109771.03),
        new SunCon(24.0, -11.0, 1.92, 54868.56),
        new SunCon(21.0, 0.0, 0.09, 25443.93),
        new SunCon(21.0, 31.0, 5.98, -55731.43),
        new SunCon(20.0, -10.0, 4.03, 60697.74),
        new SunCon(18.0, 0.0, 4.27, 2132.79),
        new SunCon(17.0, -12.0, 0.79, 109771.63),
        new SunCon(14.0, 0.0, 4.24, -7752.82),
        new SunCon(13.0, -5.0, 2.01, 188491.91),
        new SunCon(13.0, 0.0, 2.65, 207.81),
        new SunCon(13.0, 0.0, 4.98, 29424.63),
        new SunCon(12.0, 0.0, 0.93, -7.99),
        new SunCon(10.0, 0.0, 2.21, 46941.14),
        new SunCon(10.0, 0.0, 3.59, -68.29),
        new SunCon(10.0, 0.0, 1.50, 21463.25),
        new SunCon(10.0, -9.0, 2.55, 157208.40)
    };

    /**
     * To compute equatorial spherical coordinates of Sun referred to
     * the mean equator and equinox of date.
     * @param jd Julian date on TDT or ET time scale.
     * @param ra Right ascension referred to mean equator and equinox of date (hours).
     * @param dec Declination referred to mean equator and equinox of date (degrees).
     * @param dis Geocentric distance (AU).
     */
    private static void sun_eph(double jd, double[] ra, double[] dec, double[] dis) {
        double sum_lon = 0.0;
        double sum_r = 0.0;
        final double factor = 1.0e-07;
        double u, arg, lon, lat, t, emean, sin_lon;

        u = (jd - T0) / 3652500.0;
        t = u * 100.0;

        for (int i = 0; i < 50; i++) {
            arg = con[i].alpha + con[i].nu * u;
            sum_lon += con[i].l * Math.sin(arg);
            sum_r += con[i].r * Math.cos(arg);
        }

        lon = 4.9353929 + 62833.1961680 * u + factor * sum_lon;
        lon += ((-0.1371679461 - 0.2918293271 * t) * ASEC2RAD);
        
        lon = lon % TWOPI;
        if (lon < 0.0) {
            lon += TWOPI;
        }
        //System.out.println(String.format("3#lon=%.16f",lon));
        
        dis[0] = 1.0001026 + factor * sum_r;
        
        emean = (84381.406 + (-46.836769 + (-0.0001831 + 0.00200340 * t) * t) * t) * ASEC2RAD;
        
        sin_lon = Math.sin(lon);
        ra[0] = Math.atan2((Math.cos(emean) * sin_lon), Math.cos(lon)) * RAD2DEG;
        ra[0] = ra[0] % 360.0;
        if (ra[0] < 0.0) {
            ra[0] += 360.0;
        }
        ra[0] = ra[0] / 15.0;

        dec[0] = Math.asin(sin_lon * Math.sin(emean)) * RAD2DEG;
        //System.out.println(String.format("lon=%.16f,emean=%.16f,ra=%.16f,dec=%.16f", lon,emean,ra[0],dec[0]));
    }

    private static final double[] pm = {1047.349, 3497.898, 22903.0, 19412.2};
    private static final double[] pa = {5.203363, 9.537070, 19.191264, 30.068963};
    private static final double[] pe = {0.048393, 0.054151, 0.047168, 0.008586};
    private static final double[] pj = {0.022782, 0.043362, 0.013437, 0.030878};
    private static final double[] po = {1.755036, 1.984702, 1.295556, 2.298977};
    private static final double[] pw = {0.257503, 1.613242, 2.983889, 0.784898};
    private static final double[] pl = {0.600470, 0.871693, 5.466933, 5.321160};
    private static final double[] pn = {1.450138e-3, 5.841727e-4, 2.047497e-4, 1.043891e-4};
    private static final double obl = 23.4392794444;

    private static double tlast = 0.0;
    private static double tmass;
    private static double[][] a = new double[3][4];
    private static double[][] b = new double[3][4];
    private static double[] pbary = new double[3];
    private static double[] vbary = new double[3];

    public static SolarSystemResult solarsystem(double tjd, short body, short origin) {
        SolarSystemResult result = new SolarSystemResult();
        int i;
        
        if (tlast < 1.0) {
            tmass = 1.0 + 5.977e-6;
            double oblr = obl * TWOPI / 360.0;
            double se = Math.sin(oblr);
            double ce = Math.cos(oblr);

            for (i = 0; i < 4; i++) {
                tmass += 1.0 / pm[i];
                double si = Math.sin(pj[i]);
                double ci = Math.cos(pj[i]);
                double sn = Math.sin(po[i]);
                double cn = Math.cos(po[i]);
                double sw = Math.sin(pw[i] - po[i]);
                double cw = Math.cos(pw[i] - po[i]);
                
                double p1 = cw * cn - sw * sn * ci;
                double p2 = (cw * sn + sw * cn * ci) * ce - sw * si * se;
                double p3 = (cw * sn + sw * cn * ci) * se + sw * si * ce;
                double q1 = -sw * cn - cw * sn * ci;
                double q2 = (-sw * sn + cw * cn * ci) * ce - cw * si * se;
                double q3 = (-sw * sn + cw * cn * ci) * se + cw * si * ce;
                double roote = Math.sqrt(1.0 - pe[i] * pe[i]);
                a[0][i] = pa[i] * p1;
                a[1][i] = pa[i] * p2;
                a[2][i] = pa[i] * p3;
                b[0][i] = pa[i] * roote * q1;
                b[1][i] = pa[i] * roote * q2;
                b[2][i] = pa[i] * roote * q3;
            }
            tlast = 1.0;
        }

        if ((body == 0) || (body == 1) || (body == 10)) { // Sun
            for (i = 0; i < 3; i++) {
                result.position[i] = result.velocity[i] = 0.0;
            }
        } else if ((body == 2) || (body == 3)) { // Earth
            double[][] p = new double[3][3];
            for (i = 0; i < 3; i++) {
                double qjd = tjd + (double) (i - 1) * 0.1;
                double[] ras = new double[1];
                double[] decs = new double[1];
                double[] diss = new double[1];
                sun_eph(qjd, ras, decs, diss);
                //System.out.println(String.format("#sunEph:qjd=%f, ras=%s, decs=%s, diss=%s, i=%d",qjd,Arrays.toString(ras),Arrays.toString(decs),Arrays.toString(diss),i));
                double[] pos1 = Novas.radec2vector(ras[0], decs[0], diss[0]);
                double[] pos2 = Novas.precession(qjd, pos1, T0);
                p[i][0] = -pos2[0];
                p[i][1] = -pos2[1];
                p[i][2] = -pos2[2];
            }
            for (i = 0; i < 3; i++) {
                result.position[i] = p[1][i];
                result.velocity[i] = (p[2][i] - p[0][i]) / 0.2;
            }
        } else {
            result.error = 2;
            return result;
        }

        //System.out.println(String.format("#1:pos=%s,vloc=%s", Arrays.toString(result.position),Arrays.toString(result.velocity)));
        if (origin == 0) {
            if (Math.abs(tjd - tlast) >= 1.0e-06) {
                for (i = 0; i < 3; i++) {
                    pbary[i] = vbary[i] = 0.0;
                }

                for (i = 0; i < 4; i++) {
                    double e = pe[i];
                    double mlon = pl[i] + pn[i] * (tjd - T0);
                    double ma = mlon - pw[i];
                    ma = ma % TWOPI;
                    double u = ma + e * Math.sin(ma) + 0.5 * e * e * Math.sin(2.0 * ma);
                    double sinu = Math.sin(u);
                    double cosu = Math.cos(u);

                    double anr = pn[i] / (1.0 - e * cosu);

                    double[] pplan = new double[3];
                    double[] vplan = new double[3];
                    
                    pplan[0] = a[0][i] * (cosu - e) + b[0][i] * sinu;
                    pplan[1] = a[1][i] * (cosu - e) + b[1][i] * sinu;
                    pplan[2] = a[2][i] * (cosu - e) + b[2][i] * sinu;
                    vplan[0] = anr * (-a[0][i] * sinu + b[0][i] * cosu);
                    vplan[1] = anr * (-a[1][i] * sinu + b[1][i] * cosu);
                    vplan[2] = anr * (-a[2][i] * sinu + b[2][i] * cosu);
                    
                    double f = 1.0 / (pm[i] * tmass);
                    pbary[0] += pplan[0] * f;
                    pbary[1] += pplan[1] * f;
                    pbary[2] += pplan[2] * f;
                    vbary[0] += vplan[0] * f;
                    vbary[1] += vplan[1] * f;
                    vbary[2] += vplan[2] * f;
                }
                tlast = tjd;
            }
            for (i = 0; i < 3; i++) {
                result.position[i] -= pbary[i];
                result.velocity[i] -= vbary[i];
            }
        }
        //System.out.println(String.format("#2:pos=%s,vloc=%s", Arrays.toString(result.position),Arrays.toString(result.velocity)));

        return result;
    }
}
