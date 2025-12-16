package com.chinesebay.ccal;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.chinesebay.ccal3.NutationData;

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
	 *   deltapsi   = nutation in longitude (radian)
	 *   deltaepsilon = nutation in obliquity (radian)
	 */
	public static void nutation(double t, MutableDouble deltapsi, MutableDouble deltaepsilon) {
		//iau2000a
		double t1;
		t1 = t * 36525.0;
		NutationData data = com.chinesebay.ccal3.Nutation.iau2000a(Novascon.T0, t1);
		//deltapsi.value = data.dpsi;
		//deltaepsilon.value = data.deps;

		//iau1980
		double DegToRad = 3.14159265358979323846 / 180;
		deltapsi.value = delta_psi(t)*DegToRad;
		deltaepsilon.value = delta_epsilon(t)*DegToRad;
		//System.out.printf("%f,psi2000=%.16f,epsilon2000=%.16f,psi1980=%.16f,epsilon1980=%.16f\n",t1,data.dpsi,data.deps,deltapsi.value,deltaepsilon.value);
	}

	public static void nutation2(double t, MutableDouble deltapsi, MutableDouble deltaepsilon) {
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

	/*

	From: https://neoprogrammics.com/nutations/
    ========================================================
    This CPP/C++ function computes the nutation in obliquity
    of the ecliptic (delta epsilon) in decimal degrees.

    The theoretical precision is to about �0.001"

    Source code provided under the provisions of the
    GNU General Public License (GPL), version 3.
    http://www.fsf.org/licensing/licenses/gpl.html

    -----
    REFS.

    Astronomical Algorithms - Second Edition
    Jean Meeus - 1998
    Chapter 22
    Willmann-Bell, Inc.
    ISBN: 0-943396-61-1



    Explanatory Supplement to the Astronomical Almanac
    U.S. Naval Observatory
    ISBN 0-935702-68-7

    The 1980 IAU Theory of Nutation and Reduction
    p111-p114


    --------
    ARGUMENT

    JDTT = Astronomical JD number for a given date/time


    ----------
    VARIABLES

    T = Julian centuries corresponding to the
        JDTT value and reckoned from J2000.0

    T2 = T to the power of 2
    T3 = T to the power of 3

    w  = Nutation series accumulator
    w1 = Moon's mean elongation from the sun
    w2 = Sun's mean anomaly
    w3 = Moon's mean anomaly
    w4 = Moon's argument of latitude
    w5 = Moon's longitude of ascending node


    ------
    ERRORS

    This function does NOT check for errors.

	 */

	public static double delta_epsilon(double T) {

		// -------------------------
		// Define working variables.

		double T2, T3, DegToRad;
		double w, w1, w2, w3, w4, w5;

		// ---------------------------------
		// Compute (T) in Julian centuries
		// from J2000.0 and powers of (T).

		//T = (JDTT - 2451545.0) / 36525.0;
		T2 = T * T;
		T3 = T * T2;

		// ------------------------------------------------
		// Define conversion factor for degrees to radians.
		// Multiply degrees by this factor to get radians.

		DegToRad = 3.14159265358979323846 / 180;

		// -----------------------------------------------
		// Compute mean elongation of the moon in radians.

		w1 = DegToRad*(297.85036 + 445267.11148*T
				- 0.0019142*T2 + (T3 / 189474));

		// -------------------------------------------
		// Compute mean anomaly of the sun in radians.

		w2 = DegToRad*(357.52772 + 35999.05034*T
				- 0.0001603*T2 - (T3 / 300000));

		// --------------------------------------------
		// Compute mean anomaly of the moon in radians.

		w3 = DegToRad*(134.96298 + 477198.867398*T
				+ 0.0086972*T2 + (T3 / 56250));

		// -----------------------------------------------
		// Compute moon's argument of latitude in radians.

		w4 = DegToRad*(93.27191 + 483202.017538*T
				- 0.0036825*T2 + (T3 / 327270));

		// ------------------------------------------------------
		// Compute longitude of moon's ascending node in radians.

		w5 = DegToRad*(125.04452 - 1934.136261*T
				+ 0.0020708*T2 + (T3 / 450000));

		// ---------------------------------------------
		// Compute the nutation in obliquity in terms of
		// arc seconds * 10000

		w  = cos(w5)*(92025 + 8.9*T);

		w += cos(2*(w4 - w1 + w5))*(5736 - 3.1*T);
		w += cos(2*(w4 + w5))*(977 - 0.5*T);
		w += cos(2*w5)*(0.5*T - 895);
		w += cos(w2)*(54 - 0.1*T);
		w -= 7*cos(w3);
		w += cos(w2 + 2*(w4 - w1 + w5))*(224 - 0.6*T);
		w += 200*cos(2*w4 + w5);
		w += cos(w3 + 2*(w4 + w5))*(129 - 0.1*T);
		w += cos(2*(w4 - w1 + w5) - w2)*(0.3*T - 95);
		w -= 70*cos(2*(w4 - w1) + w5);
		w -= 53*cos(2*(w4 + w5) - w3);
		w -= 33*cos(w3 + w5);
		w += 26*cos(2*(w1 + w4 + w5) - w3);
		w += 32*cos(w5 - w3);
		w += 27*cos(w3 + 2*w4 + w5);
		w -= 24*cos(2*(w4 - w3) + w5);
		w += 16*cos(2*(w1 + w4 + w5));
		w += 13*cos(2*(w3 + w4 + w5));
		w -= 12*cos(w3 + 2*(w4 - w1 + w5));
		w -= 10*cos(2*w4 + w5 - w3);
		w -= 8*cos(2*w1 - w3 + w5);
		w += 7*cos(2*(w2 - w1 + w4 + w5));
		w += 9*cos(w2 + w5);
		w += 7*cos(w3 + w5 - 2*w1);
		w += 6*cos(w5 - w2);
		w += 5*cos(2*(w1 + w4) - w3 + w5);
		w += 3*cos(w3 + 2*(w4 + w1 + w5));
		w -= 3*cos(w2 + 2*(w4 + w5));
		w += 3*cos(2*(w4 + w5) - w2);
		w += 3*cos(2*(w1 + w4) + w5);
		w -= 3*cos(2*(w3 + w4 + w5 - w1));
		w -= 3*cos(w3 + 2*(w4 - w1) + w5);
		w += 3*cos(2*(w1 - w3) + w5);
		w += 3*cos(2*w1 + w5);
		w += 3*cos(2*(w4 - w1) + w5 - w2);
		w += 3*cos(w5 - 2*w1);
		w += 3*cos(2*(w3 + w4) + w5);

		// -----------------------------------
		// Done.  Return nutation in obliquity
		// expressed in decimal degrees.

		return w / 36000000.0;

	}

	public static double delta_psi(double T) {

		//-------------------------
		//DEFINE WORKING VARIABLES.

		double T2, T3;

		double w, w1, w2, w3, w4, w5;

		double DegToRad, dPsiDeg;

		//---------------------------------
		//COMPUTE (T) IN JULIAN CENTURIES
		//FROM J2000.0 AND POWERS OF (T).
		//T = (JD - 2451545.0) / 36525.0;
		T2 = T*T;
		T3 = T*T*T;

		//------------------------------------------------
		//DEFINE CONVERSION FACTOR FOR DEGREES TO RADIANS.
		//MULTIPLY DEGREES BY THIS FACTOR TO GET RADIANS.

		DegToRad = 3.14159265358979323846 / 180;

		//---------------------------------------------------
		//COMPUTE THE MEAN ELONGATION OF THE MOON IN RADIANS.

		w1 = DegToRad*(297.85036 + 445267.11148*T
				- 0.0019142*T2 + (T3 / 189474));

		//-----------------------------------------------
		//COMPUTE THE MEAN ANOMALY OF THE SUN IN RADIANS.

		w2 = DegToRad*(357.52772 + 35999.05034*T
				- 0.0001603*T2 - (T3 / 300000));

		//------------------------------------------------
		//COMPUTE THE MEAN ANOMALY OF THE MOON IN RADIANS.

		w3 = DegToRad*(134.96298 + 477198.867398*T
				+ 0.0086972*T2 + (T3 / 56250));

		//---------------------------------------------------
		//COMPUTE THE MOON'S ARGUMENT OF LATITUDE IN RADIANS.

		w4 = DegToRad*(93.27191 + 483202.017538*T
				- 0.0036825*T2 + (T3 / 327270));

		//----------------------------------------------------------
		//COMPUTE THE LONGITUDE OF MOON'S ASCENDING NODE IN RADIANS.

		w5 = DegToRad*(125.04452 - 1934.136261*T + 0.0020708*T2
				+ (T3 / 450000));

		//---------------------------------------------
		//COMPUTE THE NUTATION IN ECLIPTICAL LONGITUDE.

		w = sin(w5)*(-174.2*T - 171996);

		w += sin(2*(w4 + w5 - w1))*(-1.6*T - 13187);
		w += sin(2*(w4 + w5))*(-2274 - 0.2*T);
		w += sin(2 * w5)*(0.2*T + 2062);
		w += sin(w2)*(1426 - 3.4*T);
		w += sin(w3)*(0.1*T + 712);
		w += sin(2*(w4 + w5 - w1) + w2)*(1.2*T - 517);
		w += sin(2*w4 + w5)*(-0.4*T - 386);
		w -= 301*sin(2*(w4 + w5) + w3);
		w += sin(2*(w4 + w5 - w1) - w2)*(217 - 0.5*T);
		w -= 158*sin(w3 - 2*w1);
		w += sin(2*(w4 - w1) + w5)*(129 + 0.1*T);
		w += 123*sin(2*(w4 + w5) - w3);
		w +=  63*sin(2*w1);
		w += sin(w3 + w5)*(0.1*T + 63);
		w -=  59*sin(2*(w1 + w4 + w5) - w3);
		w += sin(w5 - w3)*(-0.1*T - 58);
		w -=  51*sin(2*w4 + w3 + w5);
		w +=  48*sin(2*(w3 - w1));
		w +=  46*sin(2*(w4 - w3) + w5);
		w -=  38*sin(2*(w1 + w4 + w5));
		w -=  31*sin(2*(w3 + w4 + w5));
		w +=  29*sin(2*w3);
		w +=  29*sin(2*(w4 + w5 - w1) + w3);
		w +=  26*sin(2*w4);
		w -=  22*sin(2*(w4 - w1));
		w +=  21*sin(2*w4 + w5 - w3);
		w += sin(2*w2)*(17 - 0.1*T);
		w +=  16*sin(2*w1 - w3 + w5);
		w += sin(2*(w2 + w4 + w5 - w1))*(0.1*T - 16);
		w -=  15*sin(w2 + w5);
		w -=  13*sin(w3 + w5 - 2*w1);
		w -=  12*sin(w5 - w2);
		w +=  11*sin(2*(w3 - w4));
		w -=  10*sin(2*(w4 + w1) + w5 - w3);
		w -=   8*sin(2*(w4 + w1 + w5) + w3);
		w +=   7*sin(2*(w4 + w5) + w2);
		w -=   7*sin(w3 - 2*w1 + w2);
		w -=   7*sin(2*(w4 + w5) - w2);
		w -=   7*sin(2*w1 + 2*w4 + w5);
		w +=   6*sin(2*w1 + w3);
		w +=   6*sin(2*(w3 + w4 + w5 - w1));
		w +=   6*sin(2*(w4 - w1) + w3 + w5);
		w -=   6*sin(2*(w1 - w3) + w5);
		w -=   6*sin(2*w1 + w5);
		w +=   5*sin(w3 - w2);
		w -=   5*sin(2*(w4 - w1) + w5 - w2);
		w -=   5*sin(w5 - 2*w1);
		w -=   5*sin(2*(w3 + w4) + w5);
		w +=   4*sin(2*(w3 - w1) + w5);
		w +=   4*sin(2*(w4 - w1) + w2 + w5);
		w +=   4*sin(w3 - 2*w4);
		w -=   4*sin(w3 - w1);
		w -=   4*sin(w2 - 2*w1);
		w -=   4*sin(w1);
		w +=   3*sin(2*w4 + w3);
		w -=   3*sin(2*(w4 + w5 - w3));
		w -=   3*sin(w3 - w1 - w2);
		w -=   3*sin(w2 + w3);
		w -=   3*sin(2*(w4 + w5) + w3 - w2);
		w -=   3*sin(2*(w1 + w4 + w5) - w2 - w3);
		w -=   3*sin(2*(w4 + w5) + 3*w3);
		w -=   3*sin(2*(w1 + w4 + w5) - w2);

		//------------------------------------
		//COVERT DELTA PSI TO DECIMAL DEGREES.

		dPsiDeg = w / 36000000.0;

		//-----
		//Done.

		return dPsiDeg;

	}
}
