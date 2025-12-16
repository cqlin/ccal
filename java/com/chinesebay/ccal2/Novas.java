package com.chinesebay.ccal2;

/*
   Naval Observatory Vector Astrometry Software (NOVAS)
   Main library - Java Conversion

   Adapted from NOVAS C Edition, Version 3.1
   U. S. Naval Observatory
   Astronomical Applications Dept.
   Washington, DC
   http://www.usno.navy.mil/USNO/astronomical-applications
*/

import static java.lang.Math.*;

public class Novas {
    // Static global variables for caching (celestial pole offsets)
    private static double PSI_COR = 0.0;
    private static double EPS_COR = 0.0;

    // Static variables for e_tilt caching
    private static short accuracyLast = 0;
    private static double jdLast = 0.0;
    private static double dp = 0.0;
    private static double de = 0.0;
    private static double cTerms = 0.0;

    // Static variables for precession caching
    private static boolean firstTime = true;
    private static double tLast = 0.0;
    private static double xx, yx, zx, xy, yy, zy, xz, yz, zz;

    /**
     * Computes quantities related to the orientation of the Earth's rotation axis
     * at Julian date 'jdTdb'.
     *
     * @param jdTdb TDB Julian Date
     * @param accuracy Selection for accuracy (0 = full, 1 = reduced)
     * @return ETiltResult containing mean obliquity, true obliquity, equation of equinoxes,
     *         nutation in longitude, and nutation in obliquity
     */
    public static ETiltResult e_tilt(double jdTdb, short accuracy) {
        short accDiff;
        double t, dPsi, dEps, meanOb, trueOb, eqEq;

        // Compute time in Julian centuries from epoch J2000.0
        t = (jdTdb - NovasConstants.T0) / 36525.0;

        // Check for difference in accuracy mode from last call
        accDiff = (short)(accuracy - accuracyLast);

        // Compute the nutation angles (arcseconds) if the input Julian date
        // is significantly different from the last Julian date, or the
        // accuracy mode has changed from the last call
        if ((abs(jdTdb - jdLast) > 1.0e-8) || (accDiff != 0)) {
            double[] nutAngles = nutationAngles(t, accuracy);
            dp = nutAngles[0];
            de = nutAngles[1];

            // Obtain complementary terms for equation of the equinoxes in arcseconds
            cTerms = ee_ct(jdTdb, 0.0, accuracy) / NovasConstants.ASEC2RAD;

            // Reset the values of the last Julian date and last mode
            jdLast = jdTdb;
            accuracyLast = accuracy;
        }

        // Apply observed celestial pole offsets
        dPsi = dp + PSI_COR;
        dEps = de + EPS_COR;

        // Compute mean obliquity of the ecliptic in arcseconds
        meanOb = meanObliq(jdTdb);

        // Compute true obliquity of the ecliptic in arcseconds
        trueOb = meanOb + dEps;

        // Convert obliquity values to degrees
        meanOb /= 3600.0;
        trueOb /= 3600.0;

        // Compute equation of the equinoxes in seconds of time
        eqEq = dPsi * cos(meanOb * NovasConstants.DEG2RAD) + cTerms;
        eqEq /= 15.0;

        return new ETiltResult(meanOb, trueOb, eqEq, dPsi, dEps);
    }

    /**
     * Computes the "complementary terms" of the equation of the equinoxes.
     *
     * @param jdHigh High-order part of TT Julian date
     * @param jdLow Low-order part of TT Julian date
     * @param accuracy Selection for accuracy (0 = full, 1 = reduced)
     * @return Complementary terms, in radians
     */
    public static double ee_ct(double jdHigh, double jdLow, short accuracy) {
        short i, j;
        double t;
        double[] fa = new double[14];
        double[] fa2 = new double[5];
        double s0, s1, a, cTermsResult;

        // Argument coefficients for t^0
        final short[][] ke0_t = {
            {0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  0,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2, -2,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2, -2,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2, -2,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2,  0,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  0,  0,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1,  0,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0,  0,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1,  2, -2,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1,  2, -2,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  4, -4,  4,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  1, -1,  1,  0, -8, 12,  0,  0,  0,  0,  0,  0},
            {0,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0,  2,  0,  3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0,  2,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2, -2,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1, -2,  2, -3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1, -2,  2, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  0,  0,  0,  0,  8,-13,  0,  0,  0,  0,  0, -1},
            {0,  0,  0,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {2,  0, -2,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0,  0, -2,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  1,  2, -2,  2,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0,  0, -2, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  4, -2,  4,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0,  0,  2, -2,  4,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0, -2,  0, -3,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {1,  0, -2,  0, -1,  0,  0,  0,  0,  0,  0,  0,  0,  0}
        };

        // Argument coefficients for t^1
        final short[] ke1 = {0,  0,  0,  0,  1,  0,  0,  0,  0,  0,  0,  0,  0,  0};

        // Sine and cosine coefficients for t^0
        final double[][] se0_t = {
            {+2640.96e-6,          -0.39e-6},
            {  +63.52e-6,          -0.02e-6},
            {  +11.75e-6,          +0.01e-6},
            {  +11.21e-6,          +0.01e-6},
            {   -4.55e-6,          +0.00e-6},
            {   +2.02e-6,          +0.00e-6},
            {   +1.98e-6,          +0.00e-6},
            {   -1.72e-6,          +0.00e-6},
            {   -1.41e-6,          -0.01e-6},
            {   -1.26e-6,          -0.01e-6},
            {   -0.63e-6,          +0.00e-6},
            {   -0.63e-6,          +0.00e-6},
            {   +0.46e-6,          +0.00e-6},
            {   +0.45e-6,          +0.00e-6},
            {   +0.36e-6,          +0.00e-6},
            {   -0.24e-6,          -0.12e-6},
            {   +0.32e-6,          +0.00e-6},
            {   +0.28e-6,          +0.00e-6},
            {   +0.27e-6,          +0.00e-6},
            {   +0.26e-6,          +0.00e-6},
            {   -0.21e-6,          +0.00e-6},
            {   +0.19e-6,          +0.00e-6},
            {   +0.18e-6,          +0.00e-6},
            {   -0.10e-6,          +0.05e-6},
            {   +0.15e-6,          +0.00e-6},
            {   -0.14e-6,          +0.00e-6},
            {   +0.14e-6,          +0.00e-6},
            {   -0.14e-6,          +0.00e-6},
            {   +0.14e-6,          +0.00e-6},
            {   +0.13e-6,          +0.00e-6},
            {   -0.11e-6,          +0.00e-6},
            {   +0.11e-6,          +0.00e-6},
            {   +0.11e-6,          +0.00e-6}
        };

        // Sine and cosine coefficients for t^1
        final double[] se1 = {-0.87e-6, +0.00e-6};

        // Interval between fundamental epoch J2000.0 and current date
        t = ((jdHigh - NovasConstants.T0) + jdLow) / 36525.0;

        // High accuracy mode
        if (accuracy == 0) {
            // Fundamental Arguments

            // Mean Anomaly of the Moon
            fa[0] = normAng((485868.249036 +
                            (715923.2178 +
                            (    31.8792 +
                            (     0.051635 +
                            (    -0.00024470)
                            * t) * t) * t) * t) * NovasConstants.ASEC2RAD
                            + IEEEremainder(1325.0*t, 1.0) * NovasConstants.TWOPI);

            // Mean Anomaly of the Sun
            fa[1] = normAng((1287104.793048 +
                            (1292581.0481 +
                            (     -0.5532 +
                            (     +0.000136 +
                            (     -0.00001149)
                            * t) * t) * t) * t) * NovasConstants.ASEC2RAD
                            + IEEEremainder(99.0*t, 1.0) * NovasConstants.TWOPI);

            // Mean Longitude of the Moon minus Mean Longitude of the Ascending Node of the Moon
            fa[2] = normAng(( 335779.526232 +
                            ( 295262.8478 +
                            (    -12.7512 +
                            (     -0.001037 +
                            (      0.00000417)
                            * t) * t) * t) * t) * NovasConstants.ASEC2RAD
                            + IEEEremainder(1342.0*t, 1.0) * NovasConstants.TWOPI);

            // Mean Elongation of the Moon from the Sun
            fa[3] = normAng((1072260.703692 +
                            (1105601.2090 +
                            (     -6.3706 +
                            (      0.006593 +
                            (     -0.00003169)
                            * t) * t) * t) * t) * NovasConstants.ASEC2RAD
                            + IEEEremainder(1236.0*t, 1.0) * NovasConstants.TWOPI);

            // Mean Longitude of the Ascending Node of the Moon
            fa[4] = normAng(( 450160.398036 +
                            (-482890.5431 +
                            (      7.4722 +
                            (      0.007702 +
                            (     -0.00005939)
                            * t) * t) * t) * t) * NovasConstants.ASEC2RAD
                            + IEEEremainder(-5.0*t, 1.0) * NovasConstants.TWOPI);

            fa[ 5] = normAng(4.402608842 + 2608.7903141574 * t);
            fa[ 6] = normAng(3.176146697 + 1021.3285546211 * t);
            fa[ 7] = normAng(1.753470314 +  628.3075849991 * t);
            fa[ 8] = normAng(6.203480913 +  334.0612426700 * t);
            fa[ 9] = normAng(0.599546497 +   52.9690962641 * t);
            fa[10] = normAng(0.874016757 +   21.3299104960 * t);
            fa[11] = normAng(5.481293872 +    7.4781598567 * t);
            fa[12] = normAng(5.311886287 +    3.8133035638 * t);
            fa[13] =         (0.024381750 +    0.00000538691 * t) * t;

            // Evaluate the complementary terms
            s0 = 0.0;
            s1 = 0.0;

            for (i = 32; i >= 0; i--) {
                a = 0.0;

                for (j = 0; j < 14; j++) {
                    a += (double) ke0_t[i][j] * fa[j];
                }

                s0 += (se0_t[i][0] * sin(a) + se0_t[i][1] * cos(a));
            }

            a = 0.0;

            for (j = 0; j < 14; j++) {
                a += (double) (ke1[j]) * fa[j];
            }

            s1 += (se1[0] * sin(a) + se1[1] * cos(a));

            cTermsResult = (s0 + s1 * t);
        } else {
            // Low accuracy mode: Terms smaller than 2 microarcseconds omitted
            fundArgs(t, fa2);
            cTermsResult =
                 2640.96e-6 * sin(fa2[4])
                +  63.52e-6 * sin(2.0 * fa2[4])
                +  11.75e-6 * sin(2.0 * fa2[2] - 2.0 * fa2[3] + 3.0 * fa2[4])
                +  11.21e-6 * sin(2.0 * fa2[2] - 2.0 * fa2[3] +       fa2[4])
                -   4.55e-6 * sin(2.0 * fa2[2] - 2.0 * fa2[3] + 2.0 * fa2[4])
                +   2.02e-6 * sin(2.0 * fa2[2]                + 3.0 * fa2[4])
                +   1.98e-6 * sin(2.0 * fa2[2]                +       fa2[4])
                -   1.72e-6 * sin(3.0 * fa2[4])
                -   0.87e-6 * t * sin(fa2[4]);
        }

        return cTermsResult * NovasConstants.ASEC2RAD;
    }

    /**
     * Corrects position vector for aberration of light. Algorithm includes relativistic terms.
     *
     * @param pos Position vector, referred to origin at center of mass of the Earth, components in AU
     * @param ve Velocity vector of center of mass of the Earth, referred to origin at solar system
     *           barycenter, components in AU/day
     * @param lighttime Light time from object to Earth in days (if 0, will be computed)
     * @return Position vector corrected for aberration, components in AU
     */
    public static double[] aberration(double[] pos, double[] ve, double lighttime) {
        double p1mag, vemag, beta, dot, cosd, gammai, p, q, r;
        double[] pos2 = new double[3];

        if (lighttime == 0.0) {
            p1mag = sqrt(pos[0] * pos[0] + pos[1] * pos[1] + pos[2] * pos[2]);
            lighttime = p1mag / NovasConstants.C_AUDAY;
        } else {
            p1mag = lighttime * NovasConstants.C_AUDAY;
        }

        vemag = sqrt(ve[0] * ve[0] + ve[1] * ve[1] + ve[2] * ve[2]);
        beta = vemag / NovasConstants.C_AUDAY;
        dot = pos[0] * ve[0] + pos[1] * ve[1] + pos[2] * ve[2];

        cosd = dot / (p1mag * vemag);
        gammai = sqrt(1.0 - beta * beta);
        p = beta * cosd;
        q = (1.0 + p / (1.0 + gammai)) * lighttime;
        r = 1.0 + p;

        pos2[0] = (gammai * pos[0] + q * ve[0]) / r;
        pos2[1] = (gammai * pos[1] + q * ve[1]) / r;
        pos2[2] = (gammai * pos[2] + q * ve[2]) / r;

        return pos2;
    }

    /**
     * Precesses equatorial rectangular coordinates from one epoch to another.
     * One of the two epochs must be J2000.0. The coordinates are referred to the mean
     * dynamical equator and equinox of the two respective epochs.
     *
     * @param jdTdb1 TDB Julian date of first epoch (must be NovasConstants.T0 or jdTdb2 must be NovasConstants.T0)
     * @param pos1 Position vector, geocentric equatorial rectangular coordinates,
     *             referred to mean dynamical equator and equinox of first epoch
     * @param jdTdb2 TDB Julian date of second epoch (must be NovasConstants.T0 or jdTdb1 must be NovasConstants.T0)
     * @return Position vector, geocentric equatorial rectangular coordinates,
     *         referred to mean dynamical equator and equinox of second epoch
     * @throws IllegalArgumentException if neither jdTdb1 nor jdTdb2 is NovasConstants.T0
     */
    public static double[] precession(double jdTdb1, double[] pos1, double jdTdb2) {
        double[] pos2 = new double[3];
        double eps0 = 84381.406;
        double t, psia, omegaa, chia, sa, ca, sb, cb, sc, cc, sd, cd;

        // Check to be sure that either 'jdTdb1' or 'jdTdb2' is equal to NovasConstants.T0
        if ((jdTdb1 != NovasConstants.T0) && (jdTdb2 != NovasConstants.T0)) {
            throw new IllegalArgumentException("Precession not to or from J2000.0; jdTdb1 or jdTdb2 must be 2451545.0");
        }

        // 't' is time in TDB centuries between the two epochs
        t = (jdTdb2 - jdTdb1) / 36525.0;

        if (jdTdb2 == NovasConstants.T0) {
            t = -t;
        }

        if ((abs(t - tLast) >= 1.0e-15) || firstTime) {
            // Numerical coefficients of psi_a, omega_a, and chi_a, along with
            // epsilon_0, the obliquity at J2000.0, are 4-angle formulation from
            // Capitaine et al. (2003), eqs. (4), (37), & (39)

            psia   = ((((-    0.0000000951  * t
                         +    0.000132851 ) * t
                         -    0.00114045  ) * t
                         -    1.0790069   ) * t
                         + 5038.481507    ) * t;

            omegaa = ((((+    0.0000003337  * t
                         -    0.000000467 ) * t
                         -    0.00772503  ) * t
                         +    0.0512623   ) * t
                         -    0.025754    ) * t + eps0;

            chia   = ((((-    0.0000000560  * t
                         +    0.000170663 ) * t
                         -    0.00121197  ) * t
                         -    2.3814292   ) * t
                         +   10.556403    ) * t;

            eps0 = eps0 * NovasConstants.ASEC2RAD;
            psia = psia * NovasConstants.ASEC2RAD;
            omegaa = omegaa * NovasConstants.ASEC2RAD;
            chia = chia * NovasConstants.ASEC2RAD;

            sa = sin(eps0);
            ca = cos(eps0);
            sb = sin(-psia);
            cb = cos(-psia);
            sc = sin(-omegaa);
            cc = cos(-omegaa);
            sd = sin(chia);
            cd = cos(chia);

            // Compute elements of precession rotation matrix equivalent to
            // R3(chi_a) R1(-omega_a) R3(-psi_a) R1(epsilon_0)
            xx =  cd * cb - sb * sd * cc;
            yx =  cd * sb * ca + sd * cc * cb * ca - sa * sd * sc;
            zx =  cd * sb * sa + sd * cc * cb * sa + ca * sd * sc;
            xy = -sd * cb - sb * cd * cc;
            yy = -sd * sb * ca + cd * cc * cb * ca - sa * cd * sc;
            zy = -sd * sb * sa + cd * cc * cb * sa + ca * cd * sc;
            xz =  sb * sc;
            yz = -sc * cb * ca - sa * cc;
            zz = -sc * cb * sa + cc * ca;

            tLast = t;
            firstTime = false;
        }

        if (jdTdb2 == NovasConstants.T0) {
            // Perform rotation from epoch to J2000.0
            pos2[0] = xx * pos1[0] + xy * pos1[1] + xz * pos1[2];
            pos2[1] = yx * pos1[0] + yy * pos1[1] + yz * pos1[2];
            pos2[2] = zx * pos1[0] + zy * pos1[1] + zz * pos1[2];
        } else {
            // Perform rotation from J2000.0 to epoch
            pos2[0] = xx * pos1[0] + yx * pos1[1] + zx * pos1[2];
            pos2[1] = xy * pos1[0] + yy * pos1[1] + zy * pos1[2];
            pos2[2] = xz * pos1[0] + yz * pos1[1] + zz * pos1[2];
        }

        return pos2;
    }

    /**
     * Nutates equatorial rectangular coordinates from mean equator and equinox of epoch
     * to true equator and equinox of epoch. Inverse transformation may be applied by
     * setting flag 'direction'.
     *
     * @param jdTdb TDB Julian date of epoch
     * @param direction Flag determining direction of transformation (0 = mean to true, != 0 = true to mean)
     * @param accuracy Selection for accuracy (0 = full, 1 = reduced)
     * @param pos Position vector, geocentric equatorial rectangular coordinates,
     *            referred to mean equator and equinox of epoch
     * @return Position vector, geocentric equatorial rectangular coordinates,
     *         referred to true equator and equinox of epoch
     */
    public static double[] nutation(double jdTdb, short direction, short accuracy, double[] pos) {
        double[] pos2 = new double[3];
        double cobm, sobm, cobt, sobt, cpsi, spsi;
        double xxn, yxn, zxn, xyn, yyn, zyn, xzn, yzn, zzn;

        // Call 'e_tilt' to get the obliquity and nutation angles
        ETiltResult tilt = e_tilt(jdTdb, accuracy);

        cobm = cos(tilt.mobl * NovasConstants.DEG2RAD);
        sobm = sin(tilt.mobl * NovasConstants.DEG2RAD);
        cobt = cos(tilt.tobl * NovasConstants.DEG2RAD);
        sobt = sin(tilt.tobl * NovasConstants.DEG2RAD);
        cpsi = cos(tilt.dpsi * NovasConstants.ASEC2RAD);
        spsi = sin(tilt.dpsi * NovasConstants.ASEC2RAD);

        // Nutation rotation matrix follows
        xxn = cpsi;
        yxn = -spsi * cobm;
        zxn = -spsi * sobm;
        xyn = spsi * cobt;
        yyn = cpsi * cobm * cobt + sobm * sobt;
        zyn = cpsi * sobm * cobt - cobm * sobt;
        xzn = spsi * sobt;
        yzn = cpsi * cobm * sobt - sobm * cobt;
        zzn = cpsi * sobm * sobt + cobm * cobt;

        if (direction == 0) {
            // Perform rotation (mean to true)
            pos2[0] = xxn * pos[0] + yxn * pos[1] + zxn * pos[2];
            pos2[1] = xyn * pos[0] + yyn * pos[1] + zyn * pos[2];
            pos2[2] = xzn * pos[0] + yzn * pos[1] + zzn * pos[2];
        } else {
            // Perform inverse rotation (true to mean)
            pos2[0] = xxn * pos[0] + xyn * pos[1] + xzn * pos[2];
            pos2[1] = yxn * pos[0] + yyn * pos[1] + yzn * pos[2];
            pos2[2] = zxn * pos[0] + zyn * pos[1] + zzn * pos[2];
        }

        return pos2;
    }

    /**
     * Returns the values for nutation in longitude and nutation in obliquity
     * for a given TDB Julian date. The nutation model selected depends upon the
     * input value of 'accuracy'.
     *
     * @param t TDB time in Julian centuries since J2000.0
     * @param accuracy Selection for accuracy (0 = full, 1 = reduced)
     * @return Array with [0] = nutation in longitude (arcseconds), [1] = nutation in obliquity (arcseconds)
     */
    public static double[] nutationAngles(double t, short accuracy) {
        double t1;
        double[] result = new double[2];

        t1 = t * 36525.0;

        // High accuracy mode -- use IAU 2000A
        NutationData.iau2000a(NovasConstants.T0, t1, result);

        // Convert output to arcseconds
        result[0] /= NovasConstants.ASEC2RAD;
        result[1] /= NovasConstants.ASEC2RAD;

        return result;
    }

    /**
     * Computes the fundamental arguments (mean elements) of the Sun and Moon.
     *
     * @param t TDB time in Julian centuries since J2000.0
     * @param a Array to receive fundamental arguments, in radians:
     *          a[0] = l (mean anomaly of the Moon)
     *          a[1] = l' (mean anomaly of the Sun)
     *          a[2] = F (mean argument of the latitude of the Moon)
     *          a[3] = D (mean elongation of the Moon from the Sun)
     *          a[4] = Omega (mean longitude of the Moon's ascending node)
     */
    public static void fundArgs(double t, double[] a) {
        // Note: Using fmod() equivalent, not IEEEremainder()
        // fmod returns remainder with sign of dividend, needed for astronomical calculations
        a[0] = fmod(485868.249036 +
                  t * (1717915923.2178 +
                  t * (        31.8792 +
                  t * (         0.051635 +
                  t * (       - 0.00024470)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[1] = fmod(1287104.79305 +
                  t * ( 129596581.0481 +
                  t * (       - 0.5532 +
                  t * (         0.000136 +
                  t * (       - 0.00001149)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[2] = fmod(335779.526232 +
                  t * (1739527262.8478 +
                  t * (      - 12.7512 +
                  t * (      -  0.001037 +
                  t * (         0.00000417)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[3] = fmod(1072260.70369 +
                  t * (1602961601.2090 +
                  t * (       - 6.3706 +
                  t * (         0.006593 +
                  t * (       - 0.00003169)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;

        a[4] = fmod(450160.398036 +
                  t * ( - 6962890.5431 +
                  t * (         7.4722 +
                  t * (         0.007702 +
                  t * (       - 0.00005939)))), NovasConstants.ASEC360) * NovasConstants.ASEC2RAD;
    }

    /**
     * Java equivalent of C's fmod() function.
     * Returns the floating-point remainder of x/y (rounded towards zero).
     * This is different from IEEEremainder which rounds to nearest.
     * The result has the same sign as x (the dividend).
     */
    private static double fmod(double x, double y) {
    	//return Math.IEEEremainder(x,y);
        // C fmod: result = x - trunc(x/y) * y
        // trunc rounds towards zero, floor rounds towards negative infinity
        // For negative x: trunc(x/y) != floor(x/y)
        double quotient = x / y;
        double truncQuotient = quotient >= 0 ? Math.floor(quotient) : Math.ceil(quotient);
        return x - truncQuotient * y;
    }

    /**
     * Computes the mean obliquity of the ecliptic.
     *
     * @param jdTdb TDB Julian Date
     * @return Mean obliquity of the ecliptic in arcseconds
     */
    public static double meanObliq(double jdTdb) {
        double t, epsilon;

        // Compute time in Julian centuries from epoch J2000.0
        t = (jdTdb - NovasConstants.T0) / 36525.0;

        // Compute the mean obliquity in arcseconds. Use expression from
        // Capitaine et al. (2003) eq. (39) with obliquity at J2000.0 taken from eq. (37)
        epsilon = (((( -  0.0000000434   * t
                       -  0.000000576  ) * t
                       +  0.00200340   ) * t
                       -  0.0001831    ) * t
                       - 46.836769     ) * t + 84381.406;

        return epsilon;
    }

    /**
     * Converts equatorial spherical coordinates to a vector (equatorial rectangular coordinates).
     *
     * @param ra Right ascension (hours)
     * @param dec Declination (degrees)
     * @param dist Distance (AU)
     * @return Position vector, equatorial rectangular coordinates (AU)
     */
    public static double[] radec2vector(double ra, double dec, double dist) {
        double[] vector = new double[3];

        vector[0] = dist * cos(NovasConstants.DEG2RAD * dec) * cos(NovasConstants.DEG2RAD * 15.0 * ra);
        vector[1] = dist * cos(NovasConstants.DEG2RAD * dec) * sin(NovasConstants.DEG2RAD * 15.0 * ra);
        vector[2] = dist * sin(NovasConstants.DEG2RAD * dec);

        return vector;
    }

    /**
     * Computes the Terrestrial Time (TT) or Terrestrial Dynamical Time (TDT) Julian date
     * corresponding to a Barycentric Dynamical Time (TDB) Julian date.
     *
     * @param tdbJd TDB Julian date
     * @return TDB2TTResult containing TT Julian date and difference in seconds
     */
    public static TDB2TTResult tdb2tt(double tdbJd) {
        double t;
        double secDiff, ttJd;

        t = (tdbJd - NovasConstants.T0) / 36525.0;

        // Expression given in USNO Circular 179, eq. 2.6
        secDiff = 0.001657 * sin( 628.3076 * t + 6.2401)
                + 0.000022 * sin( 575.3385 * t + 4.2970)
                + 0.000014 * sin(1256.6152 * t + 6.1969)
                + 0.000005 * sin( 606.9777 * t + 4.0212)
                + 0.000005 * sin(  52.9691 * t + 0.4444)
                + 0.000002 * sin(  21.3299 * t + 5.5431)
                + 0.000010 * t * sin( 628.3076 * t + 4.2490);

        ttJd = tdbJd - secDiff / 86400.0;

        return new TDB2TTResult(ttJd, secDiff);
    }

    /**
     * Computes the Julian date for a given calendar date (year, month, day, hour).
     *
     * @param year Year
     * @param month Month number
     * @param day Day-of-month
     * @param hour Hour-of-day
     * @return Julian date
     */
    public static double julianDate(short year, short month, short day, double hour) {
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
     * Computes a date on the Gregorian calendar given the Julian date.
     *
     * @param tjd Julian date
     * @return CalDate object containing year, month, day, and hour
     */
    public static CalDate calDate(double tjd) {
        long jd, k, m, n;
        double djd;
        short year, month, day;
        double hour;

        djd = tjd + 0.5;
        jd = (long) djd;

        hour = IEEEremainder(djd, 1.0) * 24.0;
        if (hour < 0.0) hour += 24.0;

        k     = jd + 68569L;
        n     = 4L * k / 146097L;

        k     = k - (146097L * n + 3L) / 4L;
        m     = 4000L * (k + 1L) / 1461001L;
        k     = k - 1461L * m / 4L + 31L;

        month = (short) (80L * k / 2447L);
        day   = (short) (k - 2447L * (long) month / 80L);
        k      = (long) month / 11L;

        month = (short) ((long) month + 2L - 12L * k);
        year  = (short) (100L * (n - 49L) + m + k);

        return new CalDate(year, month, day, hour);
    }

    /**
     * Normalize angle into the range 0 <= angle < (2 * pi).
     *
     * @param angle Input angle (radians)
     * @return The input angle, normalized as described above (radians)
     */
    public static double normAng(double angle) {
        double a;

        a = IEEEremainder(angle, NovasConstants.TWOPI);
        if (a < 0.0) {
            a += NovasConstants.TWOPI;
        }

        return a;
    }
}
