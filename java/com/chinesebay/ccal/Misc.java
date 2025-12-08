package com.chinesebay.ccal;

public class Misc {
    public static double torad(double x) {
        x = x % 360.0;
        return ((x) * 0.01745329251994329576);
    }
}
