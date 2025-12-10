package com.chinesebay.novas;

/**
 * Result structure for e_tilt function
 */
public class ETiltResult {
    public double mobl;  // Mean obliquity of the ecliptic in degrees
    public double tobl;  // True obliquity of the ecliptic in degrees
    public double ee;    // Equation of the equinoxes in seconds of time
    public double dpsi;  // Nutation in longitude in arcseconds
    public double deps;  // Nutation in obliquity in arcseconds

    public ETiltResult(double mobl, double tobl, double ee, double dpsi, double deps) {
        this.mobl = mobl;
        this.tobl = tobl;
        this.ee = ee;
        this.dpsi = dpsi;
        this.deps = deps;
    }
}
