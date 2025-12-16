package com.chinesebay.ccal;

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Arrays;

public class Solsys3 {

	/*
	 * 'obl' is the obliquity of ecliptic at epoch J2000.0 in degrees.
	 */
	private static final double OBL = 23.4392794444;

	private static double tlast = 0.0;
	private static double tmass;
	private static double[][] a = new double[3][4];
	private static double[][] b = new double[3][4];
	private static double[] pbary = new double[3];
	private static double[] vbary = new double[3];

	private static class SunCon {
		double l;
		double r;
		double alpha;
		double nu;

		public SunCon(double l, double r, double alpha, double nu) {
			this.l = l;
			this.r = r;
			this.alpha = alpha;
			this.nu = nu;
		}
	}

	private static final SunCon[] CON = {
			new SunCon(403406.0,      0.0, 4.721964,     1.621043),
			new SunCon(195207.0, -97597.0, 5.937458, 62830.348067),
			new SunCon(119433.0, -59715.0, 1.115589, 62830.821524),
			new SunCon(112392.0, -56188.0, 5.781616, 62829.634302),
			new SunCon(  3891.0,  -1556.0, 5.5474  , 125660.5691 ),
			new SunCon(  2819.0,  -1126.0, 1.5120  , 125660.9845 ),
			new SunCon(  1721.0,   -861.0, 4.1897  ,  62832.4766 ),
			new SunCon(     0.0,    941.0, 1.163   ,      0.813  ),
			new SunCon(   660.0,   -264.0, 5.415   , 125659.310  ),
			new SunCon(   350.0,   -163.0, 4.315   ,  57533.850  ),
			new SunCon(   334.0,      0.0, 4.553   ,    -33.931  ),
			new SunCon(   314.0,    309.0, 5.198   , 777137.715  ),
			new SunCon(   268.0,   -158.0, 5.989   ,  78604.191  ),
			new SunCon(   242.0,      0.0, 2.911   ,      5.412  ),
			new SunCon(   234.0,    -54.0, 1.423   ,  39302.098  ),
			new SunCon(   158.0,      0.0, 0.061   ,    -34.861  ),
			new SunCon(   132.0,    -93.0, 2.317   , 115067.698  ),
			new SunCon(   129.0,    -20.0, 3.193   ,  15774.337  ),
			new SunCon(   114.0,      0.0, 2.828   ,   5296.670  ),
			new SunCon(    99.0,    -47.0, 0.52    ,  58849.27   ),
			new SunCon(    93.0,      0.0, 4.65    ,   5296.11   ),
			new SunCon(    86.0,      0.0, 4.35    ,  -3980.70   ),
			new SunCon(    78.0,    -33.0, 2.75    ,  52237.69   ),
			new SunCon(    72.0,    -32.0, 4.50    ,  55076.47   ),
			new SunCon(    68.0,      0.0, 3.23    ,    261.08   ),
			new SunCon(    64.0,    -10.0, 1.22    ,  15773.85   ),
			new SunCon(    46.0,    -16.0, 0.14    ,  188491.03  ),
			new SunCon(    38.0,      0.0, 3.44    ,   -7756.55  ),
			new SunCon(    37.0,      0.0, 4.37    ,     264.89  ),
			new SunCon(    32.0,    -24.0, 1.14    ,  117906.27  ),
			new SunCon(    29.0,    -13.0, 2.84    ,   55075.75  ),
			new SunCon(    28.0,      0.0, 5.96    ,   -7961.39  ),
			new SunCon(    27.0,     -9.0, 5.09    ,  188489.81  ),
			new SunCon(    27.0,      0.0, 1.72    ,    2132.19  ),
			new SunCon(    25.0,    -17.0, 2.56    ,  109771.03  ),
			new SunCon(    24.0,    -11.0, 1.92    ,   54868.56  ),
			new SunCon(    21.0,      0.0, 0.09    ,   25443.93  ),
			new SunCon(    21.0,     31.0, 5.98    ,  -55731.43  ),
			new SunCon(    20.0,    -10.0, 4.03    ,   60697.74  ),
			new SunCon(    18.0,      0.0, 4.27    ,    2132.79  ),
			new SunCon(    17.0,    -12.0, 0.79    ,  109771.63  ),
			new SunCon(    14.0,      0.0, 4.24    ,   -7752.82  ),
			new SunCon(    13.0,     -5.0, 2.01    ,  188491.91  ),
			new SunCon(    13.0,      0.0, 2.65    ,     207.81  ),
			new SunCon(    13.0,      0.0, 4.98    ,   29424.63  ),
			new SunCon(    12.0,      0.0, 0.93    ,      -7.99  ),
			new SunCon(    10.0,      0.0, 2.21    ,   46941.14  ),
			new SunCon(    10.0,      0.0, 3.59    ,     -68.29  ),
			new SunCon(    10.0,      0.0, 1.50    ,   21463.25  ),
			new SunCon(    10.0,     -9.0, 2.55    ,  157208.40  )};


	public static short solarsystem(double tjd, short body, short origin,
			double[] position, double[] velocity) {
		short ierr = 0;
		double[][] p = new double[3][3];

		// The arrays below contain masses and orbital elements for the four
		// largest planets -- Jupiter, Saturn, Uranus, and Neptune --  (see
		// Explanatory Supplement (1992), p. 316) with angles in radians.  These
		// data are used for barycenter computations only.
		final double[] pm = {1047.349, 3497.898, 22903.0, 19412.2};
		final double[] pa = {5.203363, 9.537070, 19.191264, 30.068963};
		final double[] pe = {0.048393, 0.054151, 0.047168, 0.008586};
		final double[] pj = {0.022782, 0.043362, 0.013437, 0.030878};
		final double[] po = {1.755036, 1.984702, 1.295556, 2.298977};
		final double[] pw = {0.257503, 1.613242, 2.983889, 0.784898};
		final double[] pl = {0.600470, 0.871693, 5.466933, 5.321160};
		final double[] pn = {1.450138e-3, 5.841727e-4, 2.047497e-4,
				1.043891e-4};

		/*
		 * Initialize constants.
		 * Initial value of 'tmass' is mass of Sun plus four inner planets.
		 */
		if (tlast < 1.0) {
			tmass = 1.0 + 5.977e-6;
			double oblr = OBL * Novascon.TWOPI / 360.0;
			double se = sin(oblr);
			double ce = cos(oblr);

			for (int i = 0; i < 4; i++) {
				tmass += 1.0 / pm[i];
				/*
				 * Compute sine and cosine of orbital angles.
				 */
				double si = sin(pj[i]);
				double ci = cos(pj[i]);
				double sn = sin(po[i]);
				double cn = cos(po[i]);
				double sw = sin(pw[i] - po[i]);
				double cw = cos(pw[i] - po[i]);
				/*
				 * Compute p and q vectors (see Brouwer & Clemence (1961), Methods of
				 * Celestial Mechanics, pp. 35-36.)
				 */
				double p1 =   cw * cn - sw * sn * ci;
				double p2 = ( cw * sn + sw * cn * ci) * ce - sw * si * se;
				double p3 = ( cw * sn + sw * cn * ci) * se + sw * si * ce;
				double q1 =  -sw * cn - cw * sn * ci;
				double q2 = (-sw * sn + cw * cn * ci) * ce - cw * si * se;
				double q3 = (-sw * sn + cw * cn * ci) * se + cw * si * ce;
				double roote = sqrt(1.0 - pe[i] * pe[i]);
				a[0][i] = pa[i] * p1;
				a[1][i] = pa[i] * p2;
				a[2][i] = pa[i] * p3;
				b[0][i] = pa[i] * roote * q1;
				b[1][i] = pa[i] * roote * q2;
				b[2][i] = pa[i] * roote * q3;
			}

			tlast = 1.0;
		}

		/*
		 * Form heliocentric coordinates of the Sun or Earth, depending on
		 * 'body'.  Velocities are obtained from crude numerical differentiation.
		 */
		if ((body == 0) || (body == 1) || (body == 10)) { // Sun
			for (int i = 0; i < 3; i++) {
				position[i] = 0.0;
				velocity[i] = 0.0;
			}
		} else if ((body == 2) || (body == 3)) { // Earth
			for (int i = 0; i < 3; i++) {
				double qjd = tjd + (double) (i - 1) * 0.1;
				MutableDouble ras = new MutableDouble(0);
				MutableDouble decs = new MutableDouble(0);
				MutableDouble diss = new MutableDouble(0);
				sun_eph(qjd, ras, decs, diss);
				double[] pos1 = new double[3];
				Novas.radec2vector(ras.value, decs.value, diss.value, pos1);
				double[] tempPosition = new double[3]; // Temporary array for precession output
				Novas.precession(qjd, pos1, Novascon.T0, tempPosition); // Store output in tempPosition
				p[i][0] = -tempPosition[0];
				p[i][1] = -tempPosition[1];
				p[i][2] = -tempPosition[2];
			}
			for (int i = 0; i < 3; i++) {
				position[i] = p[1][i];
				velocity[i] = (p[2][i] - p[0][i]) / 0.2;
			}
		} else {
			return (ierr = 2);
		}

		/*
		 * If 'origin' = 0, move origin to solar system barycenter.
		 *
		 * Solar system barycenter coordinates are computed from Keplerian
		 * approximations of the coordinates of the four largest planets.
		 */
		if (origin == 0) {
			if (abs(tjd - tlast) >= 1.0e-06) {
				for (int i = 0; i < 3; i++) {
					pbary[i] = 0.0;
					vbary[i] = 0.0;
				}

				/*
				 * The following loop cycles once for each of the four planets.
				 */
				for (int i = 0; i < 4; i++) {
					/*
					 * Compute mean longitude, mean anomaly, and eccentric anomaly.
					 */
					double e = pe[i];
					double mlon = pl[i] + pn[i] * (tjd - Novascon.T0);
					double ma = fmod(mlon - pw[i], Novascon.TWOPI); // Need to make sure fmod behavior is same as C++
					double u = ma + e * sin(ma) + 0.5 * e * e * sin(2.0 * ma);
					double sinu = sin(u);
					double cosu = cos(u);

					/*
					 * Compute velocity factor.
					 */
					double anr = pn[i] / (1.0 - e * cosu);

					/*
					 * Compute planet's position and velocity wrt eq & eq J2000.
					 */
					double[] pplan = new double[3];
					double[] vplan = new double[3];
					pplan[0] = a[0][i] * (cosu - e) + b[0][i] * sinu;
					pplan[1] = a[1][i] * (cosu - e) + b[1][i] * sinu;
					pplan[2] = a[2][i] * (cosu - e) + b[2][i] * sinu;
					vplan[0] = anr * (-a[0][i] * sinu + b[0][i] * cosu);
					vplan[1] = anr * (-a[1][i] * sinu + b[1][i] * cosu);
					vplan[2] = anr * (-a[2][i] * sinu + b[2][i] * cosu);

					/*
					 * Compute mass factor and add in to total displacement.
					 */
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

			for (int i = 0; i < 3; i++) {
				position[i] -= pbary[i];
				velocity[i] -= vbary[i];
			}
		}
		//System.out.println(String.format("#2:pos=%s,vloc=%s",Arrays.toString(position),Arrays.toString(velocity)));

		return ierr;
	}


	public static void sun_eph(double jd,
			MutableDouble ra, MutableDouble dec, MutableDouble dis) {
		double sum_lon = 0.0;
		double sum_r = 0.0;
		final double factor = 1.0e-07;

		/*
		 * Define the time units 'u', measured in units of 10000 Julian years
		 * from J2000.0, and 't', measured in Julian centuries from J2000.0.
		 */
		double u = (jd - Novascon.T0) / 3652500.0;
		double t = u * 100.0;

		/*
		 * Compute longitude and distance terms from the series.
		 */
		for (int i = 0; i < 50; i++) {
			double arg = CON[i].alpha + CON[i].nu * u;
			sum_lon += CON[i].l * sin(arg);
			sum_r += CON[i].r * cos(arg);
		}

		/*
		 * Compute longitude, latitude, and distance referred to mean equinox
		 * and ecliptic of date.  Apply correction to longitude based on a
		 * linear fit to DE405 in the interval 1900-2100.
		 */
		double lon = 4.9353929 + 62833.1961680 * u + factor * sum_lon;
		lon += ((-0.1371679461 - 0.2918293271 * t) * Novascon.ASEC2RAD);

		lon = fmod(lon, Novascon.TWOPI); // Need to make sure fmod behavior is same as C++
		if (lon < 0.0) {
			lon += Novascon.TWOPI;
		}

		// lat = 0.0; (Not used explicitly, just for clarity from C++ version)

		dis.value = 1.0001026 + factor * sum_r;

		/*
		 * Compute mean obliquity of the ecliptic.
		 */
		double emean = (84381.406 + (-46.836769 +
				(-0.0001831 + 0.00200340 * t) * t) * t) * Novascon.ASEC2RAD;

		/*
		 * Compute equatorial spherical coordinates referred to the mean equator
		 * and equinox of date.
		 */
		double sin_lon = sin(lon);
		ra.value = atan2((cos(emean) * sin_lon), cos(lon)) * Novascon.RAD2DEG;
		ra.value = fmod(ra.value, 360.0); // Need to make sure fmod behavior is same as C++
		if (ra.value < 0.0) {
			ra.value += 360.0;
		}
		ra.value = ra.value / 15.0;

		dec.value = asin(sin(emean) * sin_lon) * Novascon.RAD2DEG;
		//System.out.println(String.format("lon=%.16f,emean=%.16f,ra=%.16f,dec=%.16f", lon,emean,ra.value,dec.value));
	}

	// Custom fmod implementation for Java to match C++ behavior
	private static double fmod(double x, double y) {
		/*
        double result = x % y;
        if ((result < 0 && y > 0) || (result > 0 && y < 0)) {
            result += den;
        }
		 */
		//return x %y;
		//return x - Math.floor(x / y) * y;
		//return Math.IEEEremainder(x,y);
		// C fmod: result = x - trunc(x/y) * y
		// trunc rounds towards zero, floor rounds towards negative infinity
		// For negative x: trunc(x/y) != floor(x/y)
		double quotient = x / y;
		double truncQuotient = quotient >= 0 ? Math.floor(quotient) : Math.ceil(quotient);
		return x - truncQuotient * y;
	}
}
