package com.chinesebay.ccal2;

/**
 * Result structure for TDB to TT conversion
 */
public class TDB2TTResult {
    public double ttJd;    // TT Julian date
    public double secDiff; // Difference in seconds

    public TDB2TTResult(double ttJd, double secDiff) {
        this.ttJd = ttJd;
        this.secDiff = secDiff;
    }
}
