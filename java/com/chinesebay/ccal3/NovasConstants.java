package com.chinesebay.ccal3;

/**
 * Constants for use with NOVAS.
 * Adapted from novascon.h and novascon.c
 */
public final class NovasConstants {

    private NovasConstants() {
        // Private constructor to prevent instantiation
    }

    /**
     * TDB Julian date of epoch J2000.0.
     */
    public static final double T0 = 2451545.00000000;

    /**
     * Value of 2 * pi in radians.
     */
    public static final double TWOPI = 6.283185307179586476925287;

    /**
     * Angle conversion constant: Arcseconds to Radians.
     */
    public static final double ASEC2RAD = 4.848136811095359935899141e-6;

    /**
     * Number of arcseconds in 360 degrees.
     */
    public static final double ASEC360 = 1296000.0;

    /**
     * Angle conversion constant: Degrees to Radians.
     */
    public static final double DEG2RAD = 0.017453292519943296;

    /**
     * Angle conversion constant: Radians to Degrees.
     */
    public static final double RAD2DEG = 57.295779513082321;

    /**
     * Speed of light in AU/day.
     */
    public static final double C_AUDAY = 173.1446326846693;

    // Other constants from novascon.h/c can be added here as needed.
    // For now, only those directly used by nutation.cpp are included.
}
