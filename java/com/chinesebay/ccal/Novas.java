package com.chinesebay.ccal;

public class Novas {
    public static final int BARYC = 0;
    public static final int HELIOC = 1;

    private static double PSI_COR = 0.0;
    private static double EPS_COR = 0.0;

    private static short accuracy_last = 0;
    private static double jd_last = 0.0;
    private static double dp, de, c_terms;

    public static void e_tilt(double jd_tdb, short accuracy, MutableDouble mobl, MutableDouble tobl, MutableDouble ee, MutableDouble dpsi, MutableDouble deps) {
        short acc_diff;
        double t, d_psi, d_eps, mean_ob, true_ob, eq_eq;

        t = (jd_tdb - Novascon.T0) / 36525.0;

        acc_diff = (short) (accuracy - accuracy_last);

        if (((Math.abs(jd_tdb - jd_last)) > 1.0e-8) || (acc_diff != 0)) {
            MutableDouble dp_ = new MutableDouble(dp);
            MutableDouble de_ = new MutableDouble(de);
            nutation_angles(t, accuracy, dp_, de_);
            dp = dp_.value;
            de = de_.value;

            c_terms = ee_ct(jd_tdb, 0.0, accuracy) / Novascon.ASEC2RAD;

            jd_last = jd_tdb;
            accuracy_last = accuracy;
        }

        d_psi = dp + PSI_COR;
        d_eps = de + EPS_COR;

        mean_ob = mean_obliq(jd_tdb);

        true_ob = mean_ob + d_eps;

        mean_ob /= 3600.0;
        true_ob /= 3600.0;

        eq_eq = d_psi * Math.cos(mean_ob * Novascon.DEG2RAD) + c_terms;
        eq_eq /= 15.0;

        dpsi.value = d_psi;
        deps.value = d_eps;
        ee.value = eq_eq;
        mobl.value = mean_ob;
        tobl.value = true_ob;
    }

    private static final short[][] ke0_t = {
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, -2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, -2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, -2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 2, -2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 2, -2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 4, -4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, -1, 1, 0, -8, 12, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, -2, 2, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, -2, 2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 8, -13, 0, 0, 0, 0, 0, -1},
            {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 0, -2, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, -2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 2, -2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, -2, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 4, -2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 2, -2, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, -2, 0, -3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, -2, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    private static final short[] ke1 = {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private static final double[][] se0_t = {
            {+2640.96e-6, -0.39e-6},
            {+63.52e-6, -0.02e-6},
            {+11.75e-6, +0.01e-6},
            {+11.21e-6, +0.01e-6},
            {-4.55e-6, +0.00e-6},
            {+2.02e-6, +0.00e-6},
            {+1.98e-6, +0.00e-6},
            {-1.72e-6, +0.00e-6},
            {-1.41e-6, -0.01e-6},
            {-1.26e-6, -0.01e-6},
            {-0.63e-6, +0.00e-6},
            {-0.63e-6, +0.00e-6},
            {+0.46e-6, +0.00e-6},
            {+0.45e-6, +0.00e-6},
            {+0.36e-6, +0.00e-6},
            {-0.24e-6, -0.12e-6},
            {+0.32e-6, +0.00e-6},
            {+0.28e-6, +0.00e-6},
            {+0.27e-6, +0.00e-6},
            {+0.26e-6, +0.00e-6},
            {-0.21e-6, +0.00e-6},
            {+0.19e-6, +0.00e-6},
            {+0.18e-6, +0.00e-6},
            {-0.10e-6, +0.05e-6},
            {+0.15e-6, +0.00e-6},
            {-0.14e-6, +0.00e-6},
            {+0.14e-6, +0.00e-6},
            {-0.14e-6, +0.00e-6},
            {+0.14e-6, +0.00e-6},
            {+0.13e-6, +0.00e-6},
            {-0.11e-6, +0.00e-6},
            {+0.11e-6, +0.00e-6},
            {+0.11e-6, +0.00e-6}};

    private static final double[] se1 = {-0.87e-6, +0.00e-6};

    public static double ee_ct(double jd_high, double jd_low, short accuracy) {
        double t, s0, s1, a;
        double[] fa = new double[14];
        double[] fa2 = new double[5];

        t = ((jd_high - Novascon.T0) + jd_low) / 36525.0;

        if (accuracy == 0) {
            fa[0] = norm_ang((485868.249036 +
                    (715923.2178 +
                            (31.8792 +
                                    (0.051635 +
                                            (-0.00024470)
                                                    * t) * t) * t) * t) * Novascon.ASEC2RAD
                    + (1325.0 * t % 1.0) * Novascon.TWOPI);

            fa[1] = norm_ang((1287104.793048 +
                    (1292581.0481 +
                            (-0.5532 +
                                    (+0.000136 +
                                            (-0.00001149)
                                                    * t) * t) * t) * t) * Novascon.ASEC2RAD
                    + (99.0 * t % 1.0) * Novascon.TWOPI);

            fa[2] = norm_ang((335779.526232 +
                    (295262.8478 +
                            (-12.7512 +
                                    (-0.001037 +
                                            (0.00000417)
                                                    * t) * t) * t) * t) * Novascon.ASEC2RAD
                    + (1342.0 * t % 1.0) * Novascon.TWOPI);

            fa[3] = norm_ang((1072260.703692 +
                    (1105601.2090 +
                            (-6.3706 +
                                    (0.006593 +
                                            (-0.00003169)
                                                    * t) * t) * t) * t) * Novascon.ASEC2RAD
                    + (1236.0 * t % 1.0) * Novascon.TWOPI);

            fa[4] = norm_ang((450160.398036 +
                    (-482890.5431 +
                            (7.4722 +
                                    (0.007702 +
                                            (-0.00005939)
                                                    * t) * t) * t) * t) * Novascon.ASEC2RAD
                    + (-5.0 * t % 1.0) * Novascon.TWOPI);

            fa[5] = norm_ang(4.402608842 + 2608.7903141574 * t);
            fa[6] = norm_ang(3.176146697 + 1021.3285546211 * t);
            fa[7] = norm_ang(1.753470314 + 628.3075849991 * t);
            fa[8] = norm_ang(6.203480913 + 334.0612426700 * t);
            fa[9] = norm_ang(0.599546497 + 52.9690962641 * t);
            fa[10] = norm_ang(0.874016757 + 21.3299104960 * t);
            fa[11] = norm_ang(5.481293872 + 7.4781598567 * t);
            fa[12] = norm_ang(5.311886287 + 3.8133035638 * t);
            fa[13] = (0.024381750 + 0.00000538691 * t) * t;

            s0 = 0.0;
            s1 = 0.0;

            for (int i = 32; i >= 0; i--) {
                a = 0.0;
                for (int j = 0; j < 14; j++) {
                    a += (double) ke0_t[i][j] * fa[j];
                }
                s0 += (se0_t[i][0] * Math.sin(a) + se0_t[i][1] * Math.cos(a));
            }

            a = 0.0;
            for (int j = 0; j < 14; j++) {
                a += (double) (ke1[j]) * fa[j];
            }
            s1 += (se1[0] * Math.sin(a) + se1[1] * Math.cos(a));

            c_terms = (s0 + s1 * t);
        } else {
            fund_args(t, fa2);
            c_terms =
                    2640.96e-6 * Math.sin(fa2[4])
                            + 63.52e-6 * Math.sin(2.0 * fa2[4])
                            + 11.75e-6 * Math.sin(2.0 * fa2[2] - 2.0 * fa2[3] + 3.0 * fa2[4])
                            + 11.21e-6 * Math.sin(2.0 * fa2[2] - 2.0 * fa2[3] + fa2[4])
                            - 4.55e-6 * Math.sin(2.0 * fa2[2] - 2.0 * fa2[3] + 2.0 * fa2[4])
                            + 2.02e-6 * Math.sin(2.0 * fa2[2] + 3.0 * fa2[4])
                            + 1.98e-6 * Math.sin(2.0 * fa2[2] + fa2[4])
                            - 1.72e-6 * Math.sin(3.0 * fa2[4])
                            - 0.87e-6 * t * Math.sin(fa2[4]);
        }

        return (c_terms *= Novascon.ASEC2RAD);
    }

    public static void aberration(double[] pos, double[] ve, double lighttime, double[] pos2) {
        double p1mag, vemag, beta, dot, cosd, gammai, p, q, r;

        if (lighttime == 0.0) {
            p1mag = Math.sqrt(pos[0] * pos[0] + pos[1] * pos[1] + pos[2] *
                    pos[2]);
            lighttime = p1mag / Novascon.C_AUDAY;
        } else
            p1mag = lighttime * Novascon.C_AUDAY;

        vemag = Math.sqrt(ve[0] * ve[0] + ve[1] * ve[1] + ve[2] * ve[2]);
        beta = vemag / Novascon.C_AUDAY;
        dot = pos[0] * ve[0] + pos[1] * ve[1] + pos[2] * ve[2];

        cosd = dot / (p1mag * vemag);
        gammai = Math.sqrt(1.0 - beta * beta);
        p = beta * cosd;
        q = (1.0 + p / (1.0 + gammai)) * lighttime;
        r = 1.0 + p;

        pos2[0] = (gammai * pos[0] + q * ve[0]) / r;
        pos2[1] = (gammai * pos[1] + q * ve[1]) / r;
        pos2[2] = (gammai * pos[2] + q * ve[2]) / r;
    }

    private static boolean first_time_precession = true;
    private static double t_last_precession = 0.0;
    private static double xx_precession, yx_precession, zx_precession, xy_precession, yy_precession, zy_precession, xz_precession, yz_precession, zz_precession;

    public static short precession(double jd_tdb1, double[] pos1, double jd_tdb2, double[] pos2) {
        if ((jd_tdb1 != Novascon.T0) && (jd_tdb2 != Novascon.T0))
            return 1;

        double t = (jd_tdb2 - jd_tdb1) / 36525.0;

        if (jd_tdb2 == Novascon.T0)
            t = -t;

        if ((Math.abs(t - t_last_precession) >= 1.0e-15) || (first_time_precession)) {
            double eps0 = 84381.406;
            double psia, omegaa, chia, sa, ca, sb, cb, sc, cc, sd, cd;

            psia = ((((-0.0000000951 * t
                    + 0.000132851) * t
                    - 0.00114045) * t
                    - 1.0790069) * t
                    + 5038.481507) * t;

            omegaa = ((((+0.0000003337 * t
                    - 0.000000467) * t
                    - 0.00772503) * t
                    + 0.0512623) * t
                    - 0.025754) * t + eps0;

            chia = ((((-0.0000000560 * t
                    + 0.000170663) * t
                    - 0.00121197) * t
                    - 2.3814292) * t
                    + 10.556403) * t;

            eps0 = eps0 * Novascon.ASEC2RAD;
            psia = psia * Novascon.ASEC2RAD;
            omegaa = omegaa * Novascon.ASEC2RAD;
            chia = chia * Novascon.ASEC2RAD;

            sa = Math.sin(eps0);
            ca = Math.cos(eps0);
            sb = Math.sin(-psia);
            cb = Math.cos(-psia);
            sc = Math.sin(-omegaa);
            cc = Math.cos(-omegaa);
            sd = Math.sin(chia);
            cd = Math.cos(chia);

            xx_precession = cd * cb - sb * sd * cc;
            yx_precession = cd * sb * ca + sd * cc * cb * ca - sa * sd * sc;
            zx_precession = cd * sb * sa + sd * cc * cb * sa + ca * sd * sc;
            xy_precession = -sd * cb - sb * cd * cc;
            yy_precession = -sd * sb * ca + cd * cc * cb * ca - sa * cd * sc;
            zy_precession = -sd * sb * sa + cd * cc * cb * sa + ca * cd * sc;
            xz_precession = sb * sc;
            yz_precession = -sc * cb * ca - sa * cc;
            zz_precession = -sc * cb * sa + cc * ca;

            t_last_precession = t;
            first_time_precession = false;
        }

        if (jd_tdb2 == Novascon.T0) {
            pos2[0] = xx_precession * pos1[0] + xy_precession * pos1[1] + xz_precession * pos1[2];
            pos2[1] = yx_precession * pos1[0] + yy_precession * pos1[1] + yz_precession * pos1[2];
            pos2[2] = zx_precession * pos1[0] + zy_precession * pos1[1] + zz_precession * pos1[2];
        } else {
            pos2[0] = xx_precession * pos1[0] + yx_precession * pos1[1] + zx_precession * pos1[2];
            pos2[1] = xy_precession * pos1[0] + yy_precession * pos1[1] + zy_precession * pos1[2];
            pos2[2] = xz_precession * pos1[0] + yz_precession * pos1[1] + zz_precession * pos1[2];
        }

        return 0;
    }

    public static void nutation(double jd_tdb, short direction, short accuracy, double[] pos, double[] pos2) {
        double cobm, sobm, cobt, sobt, cpsi, spsi, xx, yx, zx, xy, yy, zy,
                xz, yz, zz;
        MutableDouble oblm = new MutableDouble(0);
        MutableDouble oblt = new MutableDouble(0);
        MutableDouble eqeq = new MutableDouble(0);
        MutableDouble psi = new MutableDouble(0);
        MutableDouble eps = new MutableDouble(0);

        e_tilt(jd_tdb, accuracy, oblm, oblt, eqeq, psi, eps);

        cobm = Math.cos(oblm.value * Novascon.DEG2RAD);
        sobm = Math.sin(oblm.value * Novascon.DEG2RAD);
        cobt = Math.cos(oblt.value * Novascon.DEG2RAD);
        sobt = Math.sin(oblt.value * Novascon.DEG2RAD);
        cpsi = Math.cos(psi.value * Novascon.ASEC2RAD);
        spsi = Math.sin(psi.value * Novascon.ASEC2RAD);

        xx = cpsi;
        yx = -spsi * cobm;
        zx = -spsi * sobm;
        xy = spsi * cobt;
        yy = cpsi * cobm * cobt + sobm * sobt;
        zy = cpsi * sobm * cobt - cobm * sobt;
        xz = spsi * sobt;
        yz = cpsi * cobm * sobt - sobm * cobt;
        zz = cpsi * sobm * sobt + cobm * cobt;

        if (direction == 0) {
            pos2[0] = xx * pos[0] + yx * pos[1] + zx * pos[2];
            pos2[1] = xy * pos[0] + yy * pos[1] + zy * pos[2];
            pos2[2] = xz * pos[0] + yz * pos[1] + zz * pos[2];
        } else {
            pos2[0] = xx * pos[0] + xy * pos[1] + xz * pos[2];
            pos2[1] = yx * pos[0] + yy * pos[1] + yz * pos[2];
            pos2[2] = zx * pos[0] + zy * pos[1] + zz * pos[2];
        }
    }

    public static void nutation_angles(double t, short accuracy, MutableDouble dpsi, MutableDouble deps) {
        Nutation.nutation(t, dpsi, deps);
        dpsi.value /= Novascon.ASEC2RAD;
        deps.value /= Novascon.ASEC2RAD;
    }

    public static void fund_args(double t, double[] a) {
        a[0] = (485868.249036 +
                t * (1717915923.2178 +
                        t * (31.8792 +
                                t * (0.051635 +
                                        t * (-0.00024470))))) % Novascon.ASEC360 * Novascon.ASEC2RAD;

        a[1] = (1287104.79305 +
                t * (129596581.0481 +
                        t * (-0.5532 +
                                t * (0.000136 +
                                        t * (-0.00001149))))) % Novascon.ASEC360 * Novascon.ASEC2RAD;

        a[2] = (335779.526232 +
                t * (1739527262.8478 +
                        t * (-12.7512 +
                                t * (-0.001037 +
                                        t * (0.00000417))))) % Novascon.ASEC360 * Novascon.ASEC2RAD;

        a[3] = (1072260.70369 +
                t * (1602961601.2090 +
                        t * (-6.3706 +
                                t * (0.006593 +
                                        t * (-0.00003169))))) % Novascon.ASEC360 * Novascon.ASEC2RAD;

        a[4] = (450160.398036 +
                t * (-6962890.5431 +
                        t * (7.4722 +
                                t * (0.007702 +
                                        t * (-0.00005939))))) % Novascon.ASEC360 * Novascon.ASEC2RAD;
    }

    public static double mean_obliq(double jd_tdb) {
        double t, epsilon;
        t = (jd_tdb - Novascon.T0) / 36525.0;
        epsilon = ((((-0.0000000434 * t
                - 0.000000576) * t
                + 0.00200340) * t
                - 0.0001831) * t
                - 46.836769) * t + 84381.406;
        return (epsilon);
    }

    public static void radec2vector(double ra, double dec, double dist, double[] vector) {
        vector[0] = dist * Math.cos(Novascon.DEG2RAD * dec) * Math.cos(Novascon.DEG2RAD * 15.0 * ra);
        vector[1] = dist * Math.cos(Novascon.DEG2RAD * dec) * Math.sin(Novascon.DEG2RAD * 15.0 * ra);
        vector[2] = dist * Math.sin(Novascon.DEG2RAD * dec);
    }

    public static void tdb2tt(double tdb_jd, MutableDouble tt_jd, MutableDouble secdiff) {
        double t;
        t = (tdb_jd - Novascon.T0) / 36525.0;
        secdiff.value = 0.001657 * Math.sin(628.3076 * t + 6.2401)
                + 0.000022 * Math.sin(575.3385 * t + 4.2970)
                + 0.000014 * Math.sin(1256.6152 * t + 6.1969)
                + 0.000005 * Math.sin(606.9777 * t + 4.0212)
                + 0.000005 * Math.sin(52.9691 * t + 0.4444)
                + 0.000002 * Math.sin(21.3299 * t + 5.5431)
                + 0.000010 * t * Math.sin(628.3076 * t + 4.2490);
        tt_jd.value = tdb_jd - secdiff.value / 86400.0;
    }

    public static double julian_date(short year, short month, short day, double hour) {
        long jd12h;
        double tjd;
        jd12h = (long) day - 32075L + 1461L * ((long) year + 4800L
                + ((long) month - 14L) / 12L) / 4L
                + 367L * ((long) month - 2L - ((long) month - 14L) / 12L * 12L)
                / 12L - 3L * (((long) year + 4900L + ((long) month - 14L) / 12L)
                / 100L) / 4L;
        tjd = (double) jd12h - 0.5 + hour / 24.0;
        return (tjd);
    }

    public static void cal_date(double tjd, short[] year, short[] month, short[] day, double[] hour) {
        long jd, k, m, n;
        double djd;
        djd = tjd + 0.5;
        jd = (long) djd;
        hour[0] = djd % 1.0 * 24.0;
        k = jd + 68569L;
        n = 4L * k / 146097L;
        k = k - (146097L * n + 3L) / 4L;
        m = 4000L * (k + 1L) / 1461001L;
        k = k - 1461L * m / 4L + 31L;
        month[0] = (short) (80L * k / 2447L);
        day[0] = (short) (k - 2447L * (long) month[0] / 80L);
        k = (long) month[0] / 11L;
        month[0] = (short) ((long) month[0] + 2L - 12L * k);
        year[0] = (short) (100L * (n - 49L) + m + k);
    }

    public static double norm_ang(double angle) {
        double a;
        a = angle % Novascon.TWOPI;
        if (a < 0.0)
            a += Novascon.TWOPI;
        return (a);
    }
}
