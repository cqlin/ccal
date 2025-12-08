package com.chinesebay.ccal;

import static com.chinesebay.ccal.Novascon.*;

public class Nutation {

    /*
     * This function returns the nutation in longitude (delta_psi)
     * and obliquity (delta_epsilon) for a given Julian date.
     *
     * References:
     * - Explanatory Supplement to the Astronomical Almanac, 1992, pages 111-114.
     * - Jean Meeus, Astronomical Algorithms, 2nd Edition, 1998, Chapter 22.
     *
     * Input:
     *   t          = Julian date as a Terrestrial Dynamical Time (TDT) argument,
     *                expressed as centuries from J2000.0 (JD 2451545.0).
     *
     * Output:
     *   deltapsi   = nutation in longitude (arcseconds)
     *   deltaepsilon = nutation in obliquity (arcseconds)
     */
    public static void nutation(double t, MutableDouble deltapsi, MutableDouble deltaepsilon) {
        // Fundamental arguments (in degrees)
        double F_val = F(t);
        double D_val = D(t);
        double l_val = l(t);
        double l_prime_val = l_prime(t);
        double omega_val = omega(t);

        // Convert degrees to radians for trigonometric functions
        double F_rad = Math.toRadians(F_val);
        double D_rad = Math.toRadians(D_val);
        double l_rad = Math.toRadians(l_val);
        double l_prime_rad = Math.toRadians(l_prime_val);
        double omega_rad = Math.toRadians(omega_val);

        deltapsi.value = 0.0;
        deltaepsilon.value = 0.0;

        /* Nutation series for delta_psi and delta_epsilon (from Meeus) */
        /* Table 22.A and 22.B in "Astronomical Algorithms" */

        // Terms for delta_psi
        deltapsi.value += (-17.206 * Math.sin(omega_rad)
                         - 1.319 * Math.sin(2.0 * F_rad)
                         - 0.227 * Math.sin(2.0 * l_rad)
                         + 0.206 * Math.sin(2.0 * omega_rad)
                         + 0.107 * Math.sin(2.0 * l_prime_rad)
                         + 0.071 * Math.sin(D_rad)
                         + 0.063 * Math.sin(l_rad - F_rad)
                         - 0.046 * Math.sin(l_rad + F_rad)
                         - 0.033 * Math.sin(D_rad + omega_rad)
                         - 0.032 * Math.sin(D_rad - omega_rad)
                         - 0.028 * Math.sin(2.0 * l_prime_rad + F_rad)
                         + 0.027 * Math.sin(2.0 * l_prime_rad - F_rad)
                         + 0.021 * Math.sin(l_rad + D_rad)
                         + 0.017 * Math.sin(2.0 * D_rad)
                         + 0.016 * Math.sin(2.0 * F_rad + omega_rad)
                         - 0.015 * Math.sin(2.0 * F_rad - omega_rad)
                         - 0.013 * Math.sin(l_rad - D_rad)
                         - 0.011 * Math.sin(2.0 * l_rad + F_rad)
                         + 0.01 * Math.sin(2.0 * l_rad - F_rad)
                         + 0.008 * Math.sin(4.0 * F_rad)
                         + 0.007 * Math.sin(3.0 * l_rad)
                         + 0.006 * Math.sin(2.0 * D_rad + omega_rad)
                         + 0.005 * Math.sin(2.0 * D_rad - omega_rad)
                         + 0.005 * Math.sin(2.0 * l_prime_rad + 2.0 * F_rad)
                         - 0.005 * Math.sin(2.0 * l_prime_rad - 2.0 * F_rad)
                         - 0.005 * Math.sin(2.0 * F_rad + 2.0 * omega_rad)
                         + 0.004 * Math.sin(2.0 * F_rad - 2.0 * omega_rad)
                         + 0.004 * Math.sin(2.0 * l_rad + omega_rad)
                         - 0.004 * Math.sin(2.0 * l_rad - omega_rad)
                         - 0.004 * Math.sin(l_prime_rad + F_rad)
                         + 0.004 * Math.sin(l_prime_rad - F_rad)
                         + 0.003 * Math.sin(2.0 * l_prime_rad + D_rad)
                         - 0.003 * Math.sin(2.0 * l_prime_rad - D_rad)
                         - 0.003 * Math.sin(l_rad + D_rad + omega_rad)
                         + 0.003 * Math.sin(l_rad + D_rad - omega_rad)
                         + 0.003 * Math.sin(l_rad - D_rad + omega_rad)
                         - 0.003 * Math.sin(l_rad - D_rad - omega_rad));

        // Terms for delta_epsilon
        deltaepsilon.value += (9.202 * Math.cos(omega_rad)
                             + 0.573 * Math.cos(2.0 * F_rad)
                             + 0.098 * Math.cos(2.0 * l_rad)
                             - 0.090 * Math.cos(2.0 * omega_rad)
                             + 0.057 * Math.cos(2.0 * l_prime_rad)
                             + 0.030 * Math.cos(D_rad - omega_rad)
                             - 0.029 * Math.cos(D_rad + omega_rad)
                             - 0.025 * Math.cos(2.0 * l_prime_rad - F_rad)
                             + 0.024 * Math.cos(2.0 * l_prime_rad + F_rad)
                             + 0.013 * Math.cos(2.0 * D_rad)
                             + 0.012 * Math.cos(2.0 * F_rad - omega_rad)
                             - 0.012 * Math.cos(2.0 * F_rad + omega_rad)
                             + 0.007 * Math.cos(2.0 * l_rad + F_rad)
                             - 0.007 * Math.cos(2.0 * l_rad - F_rad)
                             - 0.006 * Math.cos(3.0 * l_rad)
                             - 0.005 * Math.cos(2.0 * D_rad - omega_rad)
                             + 0.005 * Math.cos(2.0 * D_rad + omega_rad)
                             - 0.005 * Math.cos(2.0 * l_prime_rad - 2.0 * F_rad)
                             + 0.005 * Math.cos(2.0 * l_prime_rad + 2.0 * F_rad)
                             + 0.004 * Math.cos(2.0 * F_rad - 2.0 * omega_rad)
                             - 0.004 * Math.cos(2.0 * F_rad + 2.0 * omega_rad)
                             - 0.004 * Math.cos(2.0 * l_rad - omega_rad)
                             + 0.004 * Math.cos(2.0 * l_rad + omega_rad)
                             + 0.003 * Math.cos(l_prime_rad - F_rad)
                             - 0.003 * Math.cos(l_prime_rad + F_rad)
                             - 0.003 * Math.cos(2.0 * l_prime_rad - D_rad)
                             + 0.003 * Math.cos(2.0 * l_prime_rad + D_rad)
                             - 0.003 * Math.cos(l_rad - D_rad - omega_rad)
                             + 0.003 * Math.cos(l_rad - D_rad + omega_rad)
                             + 0.003 * Math.cos(l_rad + D_rad - omega_rad)
                             - 0.003 * Math.cos(l_rad + D_rad + omega_rad));
    }

    /*
     * Helper functions to calculate fundamental arguments (in degrees).
     * These are based on constants and formulas from astronomical references.
     */

    // Mean anomaly of the Moon
    private static double l(double t) {
        return (218.3164477 + 481267.88123421 * t - 0.0015786 * t * t + t * t * t / 545868.0 - t * t * t * t / 113333333.0) % 360.0;
    }

    // Mean anomaly of the Sun
    private static double l_prime(double t) {
        return (134.9634025 + 1717915.2263 * t + 0.0000306 * t * t + t * t * t / 252000.0) % 360.0;
    }

    // Mean argument of the latitude of the Moon
    private static double F(double t) {
        return (93.272191 + 483202.0175 * t - 0.0036539 * t * t - t * t * t / 3660000.0) % 360.0;
    }

    // Mean elongation of the Moon from the Sun
    private static double D(double t) {
        return (297.8501921 + 445267.1115168 * t - 0.0016345 * t * t + t * t * t / 538841.0 - t * t * t * t / 65194000.0) % 360.0;
    }

    // Longitude of the ascending node of the Moon's orbit
    private static double omega(double t) {
        return (125.044555 - 1934.1361849 * t + 0.0020762 * t * t + t * t * t / 467410.0 - t * t * t * t / 60600000.0) % 360.0;
    }
}
