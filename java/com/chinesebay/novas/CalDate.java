package com.chinesebay.novas;

/**
 * Calendar date structure
 */
public class CalDate {
    public short year;
    public short month;
    public short day;
    public double hour;

    public CalDate(short year, short month, short day, double hour) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }
}
