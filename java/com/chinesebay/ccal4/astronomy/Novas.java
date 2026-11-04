package com.chinesebay.ccal4.astronomy;

import static com.chinesebay.ccal4.astronomy.NovasConstants.*;

public class Novas {

    /**
     * To compute the fundamental arguments (mean elements) of the Sun and Moon.
     * Original C function: `void fund_args (double t, double a[5])`
     *
     * @param t TDB time in Julian centuries since J2000.0
     * @param a Fundamental arguments, in radians:
     *          a[0] = l (mean anomaly of the Moon)
     *          a[1] = l' (mean anomaly of the Sun)
     *          a[2] = F (mean argument of the latitude of the Moon)
     *          a[3] = D (mean elongation of the Moon from the Sun)
     *          a[4] = Omega (mean longitude of the Moon's ascending node)
     */
    public static void fund_args(double t, double[] a) {
        a[0] = Math.IEEEremainder(485868.249036 +
                t * (1717915923.2178 +
                t * (        31.8792 +
                t * (         0.051635 +
                t * (       - 0.00024470)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[1] = Math.IEEEremainder(1287104.79305 +
                t * ( 129596581.0481 +
                t * (       - 0.5532 +
                t * (         0.000136 +
                t * (       - 0.00001149)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[2] = Math.IEEEremainder(335779.526232 +
                t * (1739527262.8478 +
                t * (      - 12.7512 +
                t * (      -  0.001037 +
                t * (         0.00000417)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[3] = Math.IEEEremainder(1072260.70369 +
                t * (1602961601.2090 +
                t * (       - 6.3706 +
                t * (         0.006593 +
                t * (       - 0.00003169)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[4] = Math.IEEEremainder(450160.398036 +
                t * ( - 6962890.5431 +
                t * (         7.4722 +
                t * (         0.007702 +
                t * (       - 0.00005939)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        // Reduction followed by adjustment keeps the angle in [0, 2*pi).
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0.0) {
                a[i] += NovasConstants.TWOPI;
            }
        }
    }

    /**
     * Normalize angle into the range 0 <= angle < (2 * pi).
     * Original C function: `double norm_ang (double angle)`
     *
     * @param angle Input angle (radians).
     * @return The input angle, normalized as described above (radians).
     */
    public static double norm_ang(double angle) {
        double a = Math.IEEEremainder(angle, NovasConstants.TWOPI);
        if (a < 0.0) {
            a += NovasConstants.TWOPI;
        }
        return a;
    }

    /**
     * To compute the mean obliquity of the ecliptic.
     * @param jd_tdb TDB Julian Date.
     * @return Mean obliquity of the ecliptic in arcseconds.
     */
    /**
     * This function returns the values for nutation in longitude and
     * nutation in obliquity for a given TDB Julian date.
     * @param t TDB time in Julian centuries since J2000.0
     * @param accuracy Selection for accuracy (0 for full, 1 for reduced)
     * @param nutationAngles Output array for dpsi and deps (in arcseconds)
     */
    public static void nutation_angles(double t, short accuracy, double[] nutationAngles) {
        double t1 = t * 36525.0;
        NutationData nutationData = Nutation.iau2000a(NovasConstants.T0, t1);
        nutationAngles[0] = nutationData.dpsi / NovasConstants.ASEC2RAD;
        nutationAngles[1] = nutationData.deps / NovasConstants.ASEC2RAD;
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
        {1, 0, -2, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
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
        {+0.11e-6, +0.00e-6}
    };
    private static final double[] se1 = {-0.87e-6, +0.00e-6};

    public static double ee_ct(double jd_high, double jd_low, short accuracy) {
        double t, s0, s1, a, c_terms;
        double[] fa = new double[14];
        double[] fa2 = new double[5];

        t = ((jd_high - T0) + jd_low) / 36525.0;

        if (accuracy == 0) {
            // High accuracy mode.
            fa[0] = norm_ang((485868.249036 + (715923.2178 + (31.8792 + (0.051635 + (-0.00024470) * t) * t) * t) * t) * NovasConstants.ASEC2RAD + Math.IEEEremainder(1325.0 * t, 1.0) * NovasConstants.TWOPI);
            fa[1] = norm_ang((1287104.793048 + (1292581.0481 + (-0.5532 + (0.000136 + (-0.00001149) * t) * t) * t) * t) * NovasConstants.ASEC2RAD + Math.IEEEremainder(99.0 * t, 1.0) * NovasConstants.TWOPI);
            fa[2] = norm_ang((335779.526232 + (295262.8478 + (-12.7512 + (-0.001037 + (0.00000417) * t) * t) * t) * t) * NovasConstants.ASEC2RAD + Math.IEEEremainder(1342.0 * t, 1.0) * NovasConstants.TWOPI);
            fa[3] = norm_ang((1072260.703692 + (1105601.2090 + (-6.3706 + (0.006593 + (-0.00003169) * t) * t) * t) * t) * NovasConstants.ASEC2RAD + Math.IEEEremainder(1236.0 * t, 1.0) * NovasConstants.TWOPI);
            fa[4] = norm_ang((450160.398036 + (-482890.5431 + (7.4722 + (0.007702 + (-0.00005939) * t) * t) * t) * t) * NovasConstants.ASEC2RAD + Math.IEEEremainder(-5.0 * t, 1.0) * NovasConstants.TWOPI);
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
            c_terms = 2640.96e-6 * Math.sin(fa2[4])
                    + 63.52e-6 * Math.sin(2.0 * fa2[4])
                    + 11.75e-6 * Math.sin(2.0 * fa2[2] - 2.0 * fa2[3] + 3.0 * fa2[4])
                    + 11.21e-6 * Math.sin(2.0 * fa2[2] - 2.0 * fa2[3] + fa2[4])
                    - 4.55e-6 * Math.sin(2.0 * fa2[2] - 2.0 * fa2[3] + 2.0 * fa2[4])
                    + 2.02e-6 * Math.sin(2.0 * fa2[2] + 3.0 * fa2[4])
                    + 1.98e-6 * Math.sin(2.0 * fa2[2] + fa2[4])
                    - 1.72e-6 * Math.sin(3.0 * fa2[4])
                    - 0.87e-6 * t * Math.sin(fa2[4]);
        }
        return (c_terms *= NovasConstants.ASEC2RAD);
    }

    private static double PSI_COR = 0.0;
    private static double EPS_COR = 0.0;

    private static short accuracy_last = 0;
    private static double jd_last = 0.0;
    private static double dp = 0.0;
    private static double de = 0.0;
    private static double c_terms = 0.0;

    public static ETiltResult e_tilt(double jd_tdb, short accuracy) {
        int acc_diff;
        double t, d_psi, d_eps, mean_ob, true_ob, eq_eq;
        double[] nutationAngles = new double[2];
        ETiltResult result = new ETiltResult();

        t = (jd_tdb - T0) / 36525.0;

        acc_diff = accuracy - accuracy_last;

        if (((Math.abs(jd_tdb - jd_last)) > 1.0e-8) || (acc_diff != 0)) {
            nutation_angles(t, accuracy, nutationAngles);
            dp = nutationAngles[0];
            de = nutationAngles[1];

            c_terms = ee_ct(jd_tdb, 0.0, accuracy) / NovasConstants.ASEC2RAD;

            jd_last = jd_tdb;
            accuracy_last = accuracy;
        }

        d_psi = dp + PSI_COR;
        d_eps = de + EPS_COR;

        mean_ob = mean_obliq(jd_tdb);

        true_ob = mean_ob + d_eps;

        result.mobl = mean_ob / 3600.0;
        result.tobl = true_ob / 3600.0;

        eq_eq = d_psi * Math.cos(result.mobl * NovasConstants.DEG2RAD) + c_terms;
        result.ee = eq_eq / 15.0;

        result.dpsi = d_psi;
        result.deps = d_eps;

        return result;
    }

    private static short precession_first_time = 1;
    private static double precession_t_last = 0.0;
    private static double p_xx, p_yx, p_zx, p_xy, p_yy, p_zy, p_xz, p_yz, p_zz;

    /**
     * Precesses equatorial rectangular coordinates from one epoch to another.
     * @param jd_tdb1 TDB Julian date of first epoch.
     * @param pos1 Position vector, geocentric equatorial rectangular coordinates,
     *             referred to mean dynamical equator and equinox of first epoch.
     * @param jd_tdb2 TDB Julian date of second epoch.
     * @return Position vector, geocentric equatorial rectangular coordinates,
     *         referred to mean dynamical equator and equinox of second epoch.
     *         Returns null if one of the two epochs is not J2000.0.
     */
    public static double[] precession(double jd_tdb1, double[] pos1, double jd_tdb2) {
        if ((jd_tdb1 != T0) && (jd_tdb2 != T0))
            return null;

        double t = (jd_tdb2 - jd_tdb1) / 36525.0;
        if (jd_tdb2 == T0)
            t = -t;

        if ((Math.abs(t - precession_t_last) >= 1.0e-15) || (precession_first_time == 1)) {
            double eps0 = 84381.406;
            double psia = ((((-0.0000000951 * t + 0.000132851) * t - 0.00114045) * t - 1.0790069) * t + 5038.481507) * t;
            double omegaa = ((((+0.0000003337 * t - 0.000000467) * t - 0.00772503) * t + 0.0512623) * t - 0.025754) * t + eps0;
            double chia = ((((-0.0000000560 * t + 0.000170663) * t - 0.00121197) * t - 2.3814292) * t + 10.556403) * t;

            eps0 *= NovasConstants.ASEC2RAD;
            psia *= NovasConstants.ASEC2RAD;
            omegaa *= NovasConstants.ASEC2RAD;
            chia *= NovasConstants.ASEC2RAD;

            double sa = Math.sin(eps0);
            double ca = Math.cos(eps0);
            double sb = Math.sin(-psia);
            double cb = Math.cos(-psia);
            double sc = Math.sin(-omegaa);
            double cc = Math.cos(-omegaa);
            double sd = Math.sin(chia);
            double cd = Math.cos(chia);

            p_xx = cd * cb - sb * sd * cc;
            p_yx = cd * sb * ca + sd * cc * cb * ca - sa * sd * sc;
            p_zx = cd * sb * sa + sd * cc * cb * sa + ca * sd * sc;
            p_xy = -sd * cb - sb * cd * cc;
            p_yy = -sd * sb * ca + cd * cc * cb * ca - sa * cd * sc;
            p_zy = -sd * sb * sa + cd * cc * cb * sa + ca * cd * sc;
            p_xz = sb * sc;
            p_yz = -sc * cb * ca - sa * cc;
            p_zz = -sc * cb * sa + cc * ca;

            precession_t_last = t;
            precession_first_time = 0;
        }

        double[] pos2 = new double[3];
        if (jd_tdb2 == T0) {
            pos2[0] = p_xx * pos1[0] + p_xy * pos1[1] + p_xz * pos1[2];
            pos2[1] = p_yx * pos1[0] + p_yy * pos1[1] + p_yz * pos1[2];
            pos2[2] = p_zx * pos1[0] + p_zy * pos1[1] + p_zz * pos1[2];
        } else {
            pos2[0] = p_xx * pos1[0] + p_yx * pos1[1] + p_zx * pos1[2];
            pos2[1] = p_xy * pos1[0] + p_yy * pos1[1] + p_zy * pos1[2];
            pos2[2] = p_xz * pos1[0] + p_yz * pos1[1] + p_zz * pos1[2];
        }
        return pos2;
    }

    /**
     * Nutates equatorial rectangular coordinates from mean equator and
     * equinox of epoch to true equator and equinox of epoch.
     * @param jd_tdb TDB Julian date of epoch.
     * @param direction Flag determining 'direction' of transformation;
     *                  direction = 0 transformation applied, mean to true.
     *                  direction != 0 inverse transformation applied, true to mean.
     * @param accuracy Selection for accuracy (0 for full, 1 for reduced)
     * @param pos Position vector, geocentric equatorial rectangular coordinates,
     *            referred to mean equator and equinox of epoch.
     * @return Position vector, geocentric equatorial rectangular coordinates,
     *         referred to true equator and equinox of epoch.
     */
    public static double[] nutation(double jd_tdb, short direction, short accuracy, double[] pos) {
        double cobm, sobm, cobt, sobt, cpsi, spsi, xx, yx, zx, xy, yy, zy, xz, yz, zz;

        ETiltResult tilt = e_tilt(jd_tdb, accuracy);

        cobm = Math.cos(tilt.mobl * NovasConstants.DEG2RAD);
        sobm = Math.sin(tilt.mobl * NovasConstants.DEG2RAD);
        cobt = Math.cos(tilt.tobl * NovasConstants.DEG2RAD);
        sobt = Math.sin(tilt.tobl * NovasConstants.DEG2RAD);
        cpsi = Math.cos(tilt.dpsi * NovasConstants.ASEC2RAD);
        spsi = Math.sin(tilt.dpsi * NovasConstants.ASEC2RAD);

        xx = cpsi;
        yx = -spsi * cobm;
        zx = -spsi * sobm;
        xy = spsi * cobt;
        yy = cpsi * cobm * cobt + sobm * sobt;
        zy = cpsi * sobm * cobt - cobm * sobt;
        xz = spsi * sobt;
        yz = cpsi * cobm * sobt - sobm * cobt;
        zz = cpsi * sobm * sobt + cobm * cobt;

        double[] pos2 = new double[3];
        if (direction == 0) {
            pos2[0] = xx * pos[0] + yx * pos[1] + zx * pos[2];
            pos2[1] = xy * pos[0] + yy * pos[1] + zy * pos[2];
            pos2[2] = xz * pos[0] + yz * pos[1] + zz * pos[2];
        } else {
            pos2[0] = xx * pos[0] + xy * pos[1] + xz * pos[2];
            pos2[1] = yx * pos[0] + yy * pos[1] + yz * pos[2];
            pos2[2] = zx * pos[0] + zy * pos[1] + zz * pos[2];
        }

        return pos2;
    }

    /**
     * Corrects position vector for aberration of light.
     * @param pos Position vector, referred to origin at center of mass of the Earth, components in AU.
     * @param ve Velocity vector of center of mass of the Earth, referred to origin at solar system barycenter, components in AU/day.
     * @param lighttime Light time from object to Earth in days.
     * @return Position vector, referred to origin at center of mass of the Earth, corrected for aberration, components in AU
     */
    public static double[] aberration(double[] pos, double[] ve, double lighttime) {
        double p1mag, vemag, beta, dot, cosd, gammai, p, q, r;
        double[] pos2 = new double[3];

        if (lighttime == 0.0) {
            p1mag = Math.sqrt(pos[0] * pos[0] + pos[1] * pos[1] + pos[2] * pos[2]);
            lighttime = p1mag / C_AUDAY;
        } else {
            p1mag = lighttime * C_AUDAY;
        }

        vemag = Math.sqrt(ve[0] * ve[0] + ve[1] * ve[1] + ve[2] * ve[2]);
        beta = vemag / C_AUDAY;
        dot = pos[0] * ve[0] + pos[1] * ve[1] + pos[2] * ve[2];

        cosd = dot / (p1mag * vemag);
        gammai = Math.sqrt(1.0 - beta * beta);
        p = beta * cosd;
        q = (1.0 + p / (1.0 + gammai)) * lighttime;
        r = 1.0 + p;

        pos2[0] = (gammai * pos[0] + q * ve[0]) / r;
        pos2[1] = (gammai * pos[1] + q * ve[1]) / r;
        pos2[2] = (gammai * pos[2] + q * ve[2]) / r;

        return pos2;
    }

    public static double mean_obliq(double jd_tdb) {
        double t, epsilon;
        t = (jd_tdb - NovasConstants.T0) / 36525.0;
        epsilon = ((((-0.0000000434 * t
                - 0.000000576) * t
                + 0.00200340) * t
                - 0.0001831) * t
                - 46.836769) * t + 84381.406;
        return (epsilon);
    }

    /**
     * Converts equatorial spherical coordinates to a vector (equatorial rectangular coordinates).
     * @param ra Right ascension (hours).
     * @param dec Declination (degrees).
     * @param dist Distance (AU)
     * @return Position vector, equatorial rectangular coordinates (AU).
     */
    public static double[] radec2vector(double ra, double dec, double dist) {
        double[] vector = new double[3];
        vector[0] = dist * Math.cos(NovasConstants.DEG2RAD * dec) * Math.cos(NovasConstants.DEG2RAD * 15.0 * ra);
        vector[1] = dist * Math.cos(NovasConstants.DEG2RAD * dec) * Math.sin(NovasConstants.DEG2RAD * 15.0 * ra);
        vector[2] = dist * Math.sin(NovasConstants.DEG2RAD * dec);
        return vector;
    }

    /**
     * Computes the Terrestrial Time (TT) or Terrestrial Dynamical Time (TDT)
     * Julian date corresponding to a Barycentric Dynamical Time (TDB) Julian date.
     * @param tdb_jd TDB Julian date.
     * @return TDB2TTResult object containing TT Julian date and the difference in seconds.
     */
    public static TDB2TTResult tdb2tt(double tdb_jd) {
        TDB2TTResult result = new TDB2TTResult();
        double t = (tdb_jd - T0) / 36525.0;

        result.secdiff = 0.001657 * Math.sin(628.3076 * t + 6.2401)
                + 0.000022 * Math.sin(575.3385 * t + 4.2970)
                + 0.000014 * Math.sin(1256.6152 * t + 6.1969)
                + 0.000005 * Math.sin(606.9777 * t + 4.0212)
                + 0.000005 * Math.sin(52.9691 * t + 0.4444)
                + 0.000002 * Math.sin(21.3299 * t + 5.5431)
                + 0.000010 * t * Math.sin(628.3076 * t + 4.2490);

        result.tt_jd = tdb_jd - result.secdiff / 86400.0;
        return result;
    }

    /**
     * This function will compute the Julian date for a given calendar
     * date (year, month, day, hour).
     * @param year Year.
     * @param month Month number.
     * @param day Day-of-month.
     * @param hour Hour-of-day.
     * @return Julian date.
     */
    public static double julian_date(short year, short month, short day, double hour) {
        long jd12h;
        double tjd;

        jd12h = (long) day - 32075L + 1461L * ((long) year + 4800L
                + ((long) month - 14L) / 12L) / 4L
                + 367L * ((long) month - 2L - ((long) month - 14L) / 12L * 12L)
                / 12L - 3L * (((long) year + 4900L + ((long) month - 14L) / 12L)
                / 100L) / 4L;
        tjd = (double) jd12h - 0.5 + hour / 24.0;

        return tjd;
    }

    /**
     * This function will compute a date on the Gregorian calendar given the Julian date.
     * @param tjd Julian date.
     * @return CalDate object with year, month, day, and hour.
     */
    public static CalDate cal_date(double tjd) {
        CalDate date = new CalDate();
        long jd, k, m, n;
        double djd;

        djd = tjd + 0.5;
        jd = (long) djd;

        date.hour = Math.IEEEremainder(djd, 1.0) * 24.0;

        k = jd + 68569L;
        n = 4L * k / 146097L;

        k = k - (146097L * n + 3L) / 4L;
        m = 4000L * (k + 1L) / 1461001L;
        k = k - 1461L * m / 4L + 31L;

        date.month = (short) (80L * k / 2447L);
        date.day = (short) (k - 2447L * (long) date.month / 80L);
        k = (long) date.month / 11L;

        date.month = (short) ((long) date.month + 2L - 12L * k);
        date.year = (short) (100L * (n - 49L) + m + k);

        return date;
    }
}








