package com.chinesebay.ccal2;

public class TestFundArgs {
    public static void main(String[] args) {
        // Test for 2026-02-04 (LC solar term day)
        // JD = 2461075.667884 (from C++)
        double jd = 2461075.667884;

        // Convert to centuries since J2000.0 (JD 2451545.0)
        double t = (jd - 2451545.0) / 36525.0;

        System.out.println("Testing fundArgs for 2026-02-04:");
        System.out.println("JD: " + jd);
        System.out.println("t (centuries since J2000): " + t);
        System.out.println();

        double[] a = new double[5];
        Novas.fundArgs(t, a);

        System.out.println("Fundamental arguments (radians):");
        System.out.println("a[0] (l, Moon anomaly): " + a[0]);
        System.out.println("a[1] (l', Sun anomaly): " + a[1]);
        System.out.println("a[2] (F, Moon - Omega): " + a[2]);
        System.out.println("a[3] (D, elongation): " + a[3]);
        System.out.println("a[4] (Omega, asc node): " + a[4]);
        System.out.println();

        // Convert to degrees for readability
        System.out.println("Fundamental arguments (degrees):");
        System.out.println("a[0]: " + Math.toDegrees(a[0]));
        System.out.println("a[1]: " + Math.toDegrees(a[1]));
        System.out.println("a[2]: " + Math.toDegrees(a[2]));
        System.out.println("a[3]: " + Math.toDegrees(a[3]));
        System.out.println("a[4]: " + Math.toDegrees(a[4]));
    }
}
