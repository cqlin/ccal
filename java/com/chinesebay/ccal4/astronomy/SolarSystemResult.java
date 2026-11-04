package com.chinesebay.ccal4.astronomy;

public class SolarSystemResult {
    public double[] position;
    public double[] velocity;
    public int error;

    public SolarSystemResult() {
        position = new double[3];
        velocity = new double[3];
        error = 0;
    }
}
