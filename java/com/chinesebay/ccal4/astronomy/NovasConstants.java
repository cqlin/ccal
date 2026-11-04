package com.chinesebay.ccal4.astronomy;

/**
 * Constants for use with NOVAS.
 * Adapted from novascon.h and novascon.c
 */
public final class NovasConstants {

    private NovasConstants() {
        // Private constructor to prevent instantiation
    }


    public static final short FN0 = 0;
    public static final short FN1 = 1;

    // TDB Julian date of epoch J2000.0
    public static final double T0 = 2451545.00000000;

    // Speed of light in meters/second
    public static final double C = 299792458.0;

    // Light-time for one astronomical unit (AU) in seconds, from DE-405
    public static final double AU_SEC = 499.0047838061;

    // Speed of light in AU/day. Value is 86400 / AU_SEC
    public static final double C_AUDAY = 173.1446326846693;

    // Astronomical unit in meters. Value is AU_SEC * C
    public static final double AU = 1.4959787069098932e+11;

    // Astronomical Unit in kilometers
    public static final double AU_KM = 1.4959787069098932e+8;

    // Heliocentric gravitational constant in meters^3 / second^2, from DE-405
    public static final double GS = 1.32712440017987e+20;

    // Geocentric gravitational constant in meters^3 / second^2, from DE-405
    public static final double GE = 3.98600433e+14;

    // Radius of Earth in meters from IERS Conventions (2003)
    public static final double ERAD = 6378136.6;

    // Earth ellipsoid flattening from IERS Conventions (2003). Value is 1 / 298.25642
    public static final double F = 0.003352819697896;

    // Rotational angular velocity of Earth in radians/sec from IERS Conventions (2003)
    public static final double ANGVEL = 7.2921150e-5;

    // Reciprocal masses of solar system bodies, from DE-405 (Sun mass / body mass)
    // RMASS[0] = Earth/Moon barycenter, RMASS[1] = Mercury, ..., RMASS[9] = Pluto,
    // RMASS[10] = Sun, RMASS[11] = Moon
    public static final double[] RMASS = {328900.561400, 6023600.0, 408523.71,
            332946.050895, 3098708.0, 1047.3486, 3497.898, 22902.98,
            19412.24, 135200000.0, 1.0, 27068700.387534};

    // Value of 2 * pi in radians
    public static final double TWOPI = 6.283185307179586476925287;

    // Number of arcseconds in 360 degrees
    public static final double ASEC360 = 1296000.0;

    // Angle conversion constants
    public static final double ASEC2RAD = 4.848136811095359935899141e-6;
    public static final double DEG2RAD = 0.017453292519943296;
    public static final double RAD2DEG = 57.295779513082321;

    // Origin constants
    public static final short BARYC = 0;
    public static final short HELIOC = 1;

}
