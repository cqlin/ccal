package com.chinesebay.ccal4.astronomy;

/**
 * Simple data class to hold the results of nutation calculations.
 */
public class NutationData {
    /**
     * Nutation in longitude (dpsi), in radians.
     */
    public double dpsi;

    /**
     * Nutation in obliquity (deps), in radians.
     */
    public double deps;

    public NutationData(double dpsi, double deps) {
        this.dpsi = dpsi;
        this.deps = deps;
    }
}
