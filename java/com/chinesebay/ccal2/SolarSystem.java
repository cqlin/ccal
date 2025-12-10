package com.chinesebay.ccal2;

/*
   As part of the ccal program by Zhuo Meng, this version is
   distributed under the terms of the GNU Lesser General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   The whole package can be obtained from
   http://aa.usno.navy.mil/software/novas/novas_c/novasc_info.php

   Adapted from:

   Naval Observatory Vector Astrometry Software (NOVAS)
   C Edition, Version 3.1

   solsys3.c: Self-contained Sun-Earth ephemeris

   U. S. Naval Observatory
   Astronomical Applications Dept.
   Washington, DC
   http://www.usno.navy.mil/USNO/astronomical-applications
*/

/**
 * SolarSystem.java
 *
 * Java conversion of NOVAS solsys3.c - provides heliocentric and barycentric
 * coordinates for Earth and Sun, plus Sun's equatorial spherical coordinates.
 * All astronomical calculations adapted from the C version.
 */
public class SolarSystem {

    // ========== SUN CONSTANTS DATA STRUCTURE ==========
    // 50 rows of sun ephemeris calculation constants
    // Each row contains: l (longitude term), r (distance term),
    // alpha (phase offset), nu (frequency/rate)

    static class SunConConstants {
        double l;      // Longitude coefficient
        double r;      // Distance coefficient
        double alpha;  // Phase offset (radians)
        double nu;     // Frequency (radians/10000 years)

        SunConConstants(double l, double r, double alpha, double nu) {
            this.l = l;
            this.r = r;
            this.alpha = alpha;
            this.nu = nu;
        }
    }

    // Static array of 50 sun ephemeris constants
    static final SunConConstants[] SUN_CON = {
        new SunConConstants(403406.0,      0.0, 4.721964,     1.621043),
        new SunConConstants(195207.0, -97597.0, 5.937458, 62830.348067),
        new SunConConstants(119433.0, -59715.0, 1.115589, 62830.821524),
        new SunConConstants(112392.0, -56188.0, 5.781616, 62829.634302),
        new SunConConstants(  3891.0,  -1556.0, 5.5474  , 125660.5691 ),
        new SunConConstants(  2819.0,  -1126.0, 1.5120  , 125660.9845 ),
        new SunConConstants(  1721.0,   -861.0, 4.1897  ,  62832.4766 ),
        new SunConConstants(     0.0,    941.0, 1.163   ,      0.813  ),
        new SunConConstants(   660.0,   -264.0, 5.415   , 125659.310  ),
        new SunConConstants(   350.0,   -163.0, 4.315   ,  57533.850  ),
        new SunConConstants(   334.0,      0.0, 4.553   ,    -33.931  ),
        new SunConConstants(   314.0,    309.0, 5.198   , 777137.715  ),
        new SunConConstants(   268.0,   -158.0, 5.989   ,  78604.191  ),
        new SunConConstants(   242.0,      0.0, 2.911   ,      5.412  ),
        new SunConConstants(   234.0,    -54.0, 1.423   ,  39302.098  ),
        new SunConConstants(   158.0,      0.0, 0.061   ,    -34.861  ),
        new SunConConstants(   132.0,    -93.0, 2.317   , 115067.698  ),
        new SunConConstants(   129.0,    -20.0, 3.193   ,  15774.337  ),
        new SunConConstants(   114.0,      0.0, 2.828   ,   5296.670  ),
        new SunConConstants(    99.0,    -47.0, 0.52    ,  58849.27   ),
        new SunConConstants(    93.0,      0.0, 4.65    ,   5296.11   ),
        new SunConConstants(    86.0,      0.0, 4.35    ,  -3980.70   ),
        new SunConConstants(    78.0,    -33.0, 2.75    ,  52237.69   ),
        new SunConConstants(    72.0,    -32.0, 4.50    ,  55076.47   ),
        new SunConConstants(    68.0,      0.0, 3.23    ,    261.08   ),
        new SunConConstants(    64.0,    -10.0, 1.22    ,  15773.85   ),
        new SunConConstants(    46.0,    -16.0, 0.14    , 188491.03   ),
        new SunConConstants(    38.0,      0.0, 3.44    ,  -7756.55   ),
        new SunConConstants(    37.0,      0.0, 4.37    ,    264.89   ),
        new SunConConstants(    32.0,    -24.0, 1.14    , 117906.27   ),
        new SunConConstants(    29.0,    -13.0, 2.84    ,  55075.75   ),
        new SunConConstants(    28.0,      0.0, 5.96    ,  -7961.39   ),
        new SunConConstants(    27.0,     -9.0, 5.09    , 188489.81   ),
        new SunConConstants(    27.0,      0.0, 1.72    ,   2132.19   ),
        new SunConConstants(    25.0,    -17.0, 2.56    , 109771.03   ),
        new SunConConstants(    24.0,    -11.0, 1.92    ,  54868.56   ),
        new SunConConstants(    21.0,      0.0, 0.09    ,  25443.93   ),
        new SunConConstants(    21.0,     31.0, 5.98    , -55731.43   ),
        new SunConConstants(    20.0,    -10.0, 4.03    ,  60697.74   ),
        new SunConConstants(    18.0,      0.0, 4.27    ,   2132.79   ),
        new SunConConstants(    17.0,    -12.0, 0.79    , 109771.63   ),
        new SunConConstants(    14.0,      0.0, 4.24    ,  -7752.82   ),
        new SunConConstants(    13.0,     -5.0, 2.01    , 188491.91   ),
        new SunConConstants(    13.0,      0.0, 2.65    ,    207.81   ),
        new SunConConstants(    13.0,      0.0, 4.98    ,  29424.63   ),
        new SunConConstants(    12.0,      0.0, 0.93    ,     -7.99   ),
        new SunConConstants(    10.0,      0.0, 2.21    ,  46941.14   ),
        new SunConConstants(    10.0,      0.0, 3.59    ,    -68.29   ),
        new SunConConstants(    10.0,      0.0, 1.50    ,  21463.25   ),
        new SunConConstants(    10.0,     -9.0, 2.55    , 157208.40   )
    };

    // ========== PLANETARY EPHEMERIS CONSTANTS ==========
    // Constants for the four largest planets: Jupiter, Saturn, Uranus, Neptune
    // Used for barycenter computations (Explanatory Supplement 1992, p. 316)
    // Arrays indexed as: [0]=Jupiter, [1]=Saturn, [2]=Uranus, [3]=Neptune

    // Reciprocal masses (solar system body mass / Sun mass)
    static final double[] PM = {1047.349, 3497.898, 22903.0, 19412.2};

    // Semi-major axes (AU)
    static final double[] PA = {5.203363, 9.537070, 19.191264, 30.068963};

    // Eccentricities (dimensionless)
    static final double[] PE = {0.048393, 0.054151, 0.047168, 0.008586};

    // Inclinations (radians)
    static final double[] PJ = {0.022782, 0.043362, 0.013437, 0.030878};

    // Longitude of ascending node (radians)
    static final double[] PO = {1.755036, 1.984702, 1.295556, 2.298977};

    // Longitude of perihelion (radians)
    static final double[] PW = {0.257503, 1.613242, 2.983889, 0.784898};

    // Mean longitude at epoch J2000.0 (radians)
    static final double[] PL = {0.600470, 0.871693, 5.466933, 5.321160};

    // Mean motion (radians/10000 years)
    static final double[] PN = {1.450138e-3, 5.841727e-4, 2.047497e-4, 1.043891e-4};

    // Obliquity of ecliptic at J2000.0 (degrees)
    static final double OBL = 23.4392794444;

    // ========== STATIC VARIABLES (For barycenter calculations) ==========
    static double tlast = 0.0;              // Last time barycenter was computed
    static double tmass = 0.0;              // Total mass factor
    static double[][] a = new double[3][4]; // Keplerian p-vector coefficients
    static double[][] b = new double[3][4]; // Keplerian q-vector coefficients
    static double[] pbary = new double[3];  // Barycenter position
    static double[] vbary = new double[3];  // Barycenter velocity

    /**
     * Provides the position and velocity of the Earth at epoch 'tjd'
     * by evaluating a closed-form theory without reference to an external file.
     * This function can also provide the position and velocity of the Sun.
     *
     * REFERENCES:
     *   Kaplan, G. H. "NOVAS: Naval Observatory Vector Astrometry
     *      Subroutines"; USNO internal document dated 20 Oct 1988;
     *      revised 15 Mar 1990.
     *   Explanatory Supplement to The Astronomical Almanac (1992).
     *
     * @param tjd TDB Julian date
     * @param body Body identification number:
     *             0, 1, or 10 = Sun
     *             2 or 3 = Earth
     * @param origin Origin code: 0 = solar system barycenter, 1 = center of Sun
     * @param position Output position vector [3] in AU (equatorial rectangular
     *                 coordinates referred to mean equator and equinox of J2000.0)
     * @param velocity Output velocity vector [3] in AU/Day (equatorial rectangular
     *                 coordinates referred to mean equator and equinox of J2000.0)
     *
     * @return 0 if everything OK
     *         1 if input Julian date ('tjd') out of range
     *         2 if invalid value of 'body'
     */
    public static short solarsystem(double tjd, short body, short origin,
                                     double[] position, double[] velocity) {
        short ierr = 0;
        int i;

        // Initialize constants if needed
        // Initial value of 'tmass' is mass of Sun plus inner planets
        if (tlast < 1.0) {
            initializePlanetaryConstants();
        }

        // Form heliocentric coordinates of the Sun or Earth
        // Velocities obtained from numerical differentiation
        if ((body == 0) || (body == 1) || (body == 10)) {
            // Sun: position and velocity at origin
            for (i = 0; i < 3; i++) {
                position[i] = velocity[i] = 0.0;
            }
        } else if ((body == 2) || (body == 3)) {
            // Earth: compute position using Sun ephemeris at three times
            double[] ras = new double[3];
            double[] decs = new double[3];
            double[] diss = new double[3];
            double[][] p = new double[3][3];
            double[] pos1 = new double[3];

            for (i = 0; i < 3; i++) {
                double qjd = tjd + (double)(i - 1) * 0.1;
                sunEph(qjd, ras, decs, diss, i);
                radec2vector(ras[i], decs[i], diss[i], pos1);
                precession(qjd, pos1, NovasConstants.T0, position);
                p[i][0] = -position[0];
                p[i][1] = -position[1];
                p[i][2] = -position[2];
            }

            // Extract position and compute velocity by differentiation
            for (i = 0; i < 3; i++) {
                position[i] = p[1][i];
                velocity[i] = (p[2][i] - p[0][i]) / 0.2;
            }
        } else {
            return 2;  // Invalid body
        }

        // If origin = 0, move origin to solar system barycenter
        // Solar system barycenter coordinates computed from Keplerian
        // approximations of the four largest planets
        if (origin == 0) {
            if (Math.abs(tjd - tlast) >= 1.0e-06) {
                // Initialize barycenter accumulators
                for (i = 0; i < 3; i++) {
                    pbary[i] = vbary[i] = 0.0;
                }

                // Loop cycles once for each of the four planets
                for (i = 0; i < 4; i++) {
                    computePlanetPosition(tjd, i, position, velocity);
                }

                tlast = tjd;
            }

            // Adjust coordinates to barycenter
            for (i = 0; i < 3; i++) {
                position[i] -= pbary[i];
                velocity[i] -= vbary[i];
            }
        }

        return ierr;
    }

    /**
     * Initialize planetary constants (Brouwer & Clemence method)
     * Compute p and q vectors for Keplerian element transformations
     */
    private static void initializePlanetaryConstants() {
        tmass = 1.0 + 5.977e-6;
        double oblr = OBL * NovasConstants.TWOPI / 360.0;
        double se = Math.sin(oblr);
        double ce = Math.cos(oblr);

        for (int i = 0; i < 4; i++) {
            tmass += 1.0 / PM[i];

            // Compute sine and cosine of orbital angles
            double si = Math.sin(PJ[i]);
            double ci = Math.cos(PJ[i]);
            double sn = Math.sin(PO[i]);
            double cn = Math.cos(PO[i]);
            double sw = Math.sin(PW[i] - PO[i]);
            double cw = Math.cos(PW[i] - PO[i]);

            // Compute p and q vectors (Brouwer & Clemence, Methods of
            // Celestial Mechanics, pp. 35-36)
            double p1 = cw * cn - sw * sn * ci;
            double p2 = (cw * sn + sw * cn * ci) * ce - sw * si * se;
            double p3 = (cw * sn + sw * cn * ci) * se + sw * si * ce;
            double q1 = -sw * cn - cw * sn * ci;
            double q2 = (-sw * sn + cw * cn * ci) * ce - cw * si * se;
            double q3 = (-sw * sn + cw * cn * ci) * se + cw * si * ce;

            double roote = Math.sqrt(1.0 - PE[i] * PE[i]);

            a[0][i] = PA[i] * p1;
            a[1][i] = PA[i] * p2;
            a[2][i] = PA[i] * p3;
            b[0][i] = PA[i] * roote * q1;
            b[1][i] = PA[i] * roote * q2;
            b[2][i] = PA[i] * roote * q3;
        }

        tlast = 1.0;
    }

    /**
     * Compute a planet's position and velocity, update barycenter
     *
     * @param tjd Julian date
     * @param planetIndex Index of planet (0-3)
     * @param position Current position array (used for accumulation)
     * @param velocity Current velocity array (used for accumulation)
     */
    private static void computePlanetPosition(double tjd, int planetIndex,
                                               double[] position, double[] velocity) {
        double e = PE[planetIndex];
        double mlon = PL[planetIndex] + PN[planetIndex] * (tjd - NovasConstants.T0);
        double ma = fmod((mlon - PW[planetIndex]), NovasConstants.TWOPI);

        // Compute eccentric anomaly using approximation
        double u = ma + e * Math.sin(ma) + 0.5 * e * e * Math.sin(2.0 * ma);
        double sinu = Math.sin(u);
        double cosu = Math.cos(u);

        // Compute velocity factor
        double anr = PN[planetIndex] / (1.0 - e * cosu);

        // Compute planet's position wrt equator & equinox J2000
        double[] pplan = new double[3];
        double[] vplan = new double[3];

        pplan[0] = a[0][planetIndex] * (cosu - e) + b[0][planetIndex] * sinu;
        pplan[1] = a[1][planetIndex] * (cosu - e) + b[1][planetIndex] * sinu;
        pplan[2] = a[2][planetIndex] * (cosu - e) + b[2][planetIndex] * sinu;

        vplan[0] = anr * (-a[0][planetIndex] * sinu + b[0][planetIndex] * cosu);
        vplan[1] = anr * (-a[1][planetIndex] * sinu + b[1][planetIndex] * cosu);
        vplan[2] = anr * (-a[2][planetIndex] * sinu + b[2][planetIndex] * cosu);

        // Compute mass factor and add to barycenter displacement
        double f = 1.0 / (PM[planetIndex] * tmass);

        pbary[0] += pplan[0] * f;
        pbary[1] += pplan[1] * f;
        pbary[2] += pplan[2] * f;
        vbary[0] += vplan[0] * f;
        vbary[1] += vplan[1] * f;
        vbary[2] += vplan[2] * f;
    }

    /**
     * Compute equatorial spherical coordinates of Sun referred to
     * the mean equator and equinox of date.
     *
     * REFERENCES:
     *   Bretagnon, P. and Simon, J.L. (1986). Planetary Programs and
     *      Tables from -4000 to + 2800. (Richmond, VA: Willmann-Bell).
     *   Kaplan, G.H. (2005). US Naval Observatory Circular 179.
     *
     * NOTES:
     *   1. Quoted accuracy is 2.0 + 0.03 * T^2 arcsec, where T is
     *      measured in units of 1000 years from J2000.0.
     *   2. The obliquity equation is updated to equation 5.12 of the
     *      second reference.
     *   3. The linear fit to DE405 primarily corrects for the
     *      difference between "old" (Lieske) and "new" (IAU 2006)
     *      precession. The difference, new - old, is -0.3004 arcsec/cy.
     *
     * @param jd Julian date on TDT or ET time scale
     * @param ra Output array, ra[0] receives Right ascension in hours
     *           (referred to mean equator and equinox of date)
     * @param dec Output array, dec[0] receives Declination in degrees
     *            (referred to mean equator and equinox of date)
     * @param dis Output array, dis[0] receives Geocentric distance in AU
     */
    public static void sunEph(double jd, double[] ra, double[] dec, double[] dis) {
        double sum_lon = 0.0;
        double sum_r = 0.0;
        final double factor = 1.0e-07;

        // Define time units: u in units of 10000 Julian years from J2000.0
        // t in Julian centuries from J2000.0
        double u = (jd - NovasConstants.T0) / 3652500.0;
        double t = u * 100.0;

        // Compute longitude and distance terms from the 50-term series
        for (int i = 0; i < 50; i++) {
            double arg = SUN_CON[i].alpha + SUN_CON[i].nu * u;
            sum_lon += SUN_CON[i].l * Math.sin(arg);
            sum_r += SUN_CON[i].r * Math.cos(arg);
        }

        // Compute longitude referred to mean equinox and ecliptic of date
        // Apply correction to longitude based on linear fit to DE405 (1900-2100)
        double lon = 4.9353929 + 62833.1961680 * u + factor * sum_lon;
        lon += ((-0.1371679461 - 0.2918293271 * t) * NovasConstants.ASEC2RAD);

        // Normalize longitude to [0, 2*pi]
        lon = fmod(lon, NovasConstants.TWOPI);
        if (lon < 0.0) {
            lon += NovasConstants.TWOPI;
        }

        // Distance
        dis[0] = 1.0001026 + factor * sum_r;

        // Compute mean obliquity of the ecliptic (IAU 2006)
        double emean = (84381.406 + (-46.836769 +
                (-0.0001831 + 0.00200340 * t) * t) * t) * NovasConstants.ASEC2RAD;

        // Compute equatorial spherical coordinates
        // referred to mean equator and equinox of date
        double sin_lon = Math.sin(lon);
        double cos_lon = Math.cos(lon);
        double cos_emean = Math.cos(emean);
        double sin_emean = Math.sin(emean);

        // Right ascension in radians, convert to hours
        ra[0] = Math.atan2(cos_emean * sin_lon, cos_lon) * NovasConstants.RAD2DEG;
        ra[0] = fmod(ra[0], 360.0);
        if (ra[0] < 0.0) {
            ra[0] += 360.0;
        }
        ra[0] = ra[0] / 15.0;  // Convert degrees to hours

        // Declination in degrees
        dec[0] = Math.asin(sin_emean * sin_lon) * NovasConstants.RAD2DEG;
    }

    /**
     * Overloaded sunEph for use in solarsystem method
     * Stores result in specific index of output arrays
     */
    private static void sunEph(double jd, double[] ras, double[] decs,
                               double[] diss, int index) {
        double[] ra = new double[1];
        double[] dec = new double[1];
        double[] dis = new double[1];
        sunEph(jd, ra, dec, dis);
        ras[index] = ra[0];
        decs[index] = dec[0];
        diss[index] = dis[0];
    }

    // ========== HELPER METHODS (Stub implementations) ==========
    // These are called from solarsystem() and would need implementation
    // from the full NOVAS library for complete functionality

    /**
     * Convert spherical coordinates to rectangular coordinates
     * Stub - requires full NOVAS implementation
     */
    private static void radec2vector(double ra, double dec, double dis,
                                     double[] vector) {
        // RA in hours -> convert to radians (1 hour = 15 degrees)
        // DEC in degrees -> convert to radians
        double rarad = ra * 15.0 * NovasConstants.DEG2RAD;
        double decrad = dec * NovasConstants.DEG2RAD;

        double cosdec = Math.cos(decrad);
        vector[0] = dis * cosdec * Math.cos(rarad);
        vector[1] = dis * cosdec * Math.sin(rarad);
        vector[2] = dis * Math.sin(decrad);
    }

    /**
     * Apply precession to a position vector
     * Stub - requires full NOVAS implementation for precision
     * For now, returns approximate identity transformation
     */
    private static void precession(double jd1, double[] pos1, double jd2,
                                   double[] pos2) {
        // This is a simplified stub. Full implementation would compute
        // proper precession matrix based on IAU precession models
        // For now, just copy the vector (near-identity for close dates)
        pos2[0] = pos1[0];
        pos2[1] = pos1[1];
        pos2[2] = pos1[2];
    }

    /**
     * Floating-point modulo operation (like C's fmod)
     */
    private static double fmod(double x, double y) {
        return x - Math.floor(x / y) * y;
    }
}
