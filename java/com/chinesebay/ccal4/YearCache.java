/*
 * Generated from the original ccal yearcache.cpp data table.
 * The compressed literal keeps this class self-contained while avoiding the
 * JVM's 64 KiB method-size limit for large array initializers.
 */
package com.chinesebay.ccal4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

final class YearCache {
    private static final int FIRST_YEAR = 1900;
    private static final int LAST_YEAR = 2100;
    private static final double JULIAN_BASE = 2415021.0;

    private static final String COMPRESSED_DATA =
        "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";

    private static final int[] SOLAR_TERMS;
    private static final int[] NEW_MOONS;
    private static final double[] LUNAR_MONTHS;

    static {
        CacheArrays arrays = loadArrays();
        SOLAR_TERMS = arrays.solarTerms;
        NEW_MOONS = arrays.newMoons;
        LUNAR_MONTHS = arrays.lunarMonths;
    }

    private YearCache() {
    }

    static boolean contains(int year) {
        return year >= FIRST_YEAR && year <= LAST_YEAR;
    }

    static LunarYearData get(int year) {
        if (!contains(year)) {
            return null;
        }
        double start = CalendarMath.julianDate(year, 1, 1, 0.0) - JULIAN_BASE;
        double end = CalendarMath.julianDate(year + 1, 1, 1, 0.0) - JULIAN_BASE;
        int termOffset = (year - FIRST_YEAR) * 24;
        int termEnd = termOffset;
        while (termEnd < SOLAR_TERMS.length && SOLAR_TERMS[termEnd] < end) {
            termEnd++;
        }
        double[] terms = new double[termEnd - termOffset];
        for (int i = 0; i < terms.length; i++) {
            terms[i] = SOLAR_TERMS[termOffset + i] + JULIAN_BASE;
        }

        int moonIndex = lowerBound(NEW_MOONS, start);
        if (moonIndex == 0 || moonIndex >= NEW_MOONS.length) {
            throw new IllegalStateException("Incomplete lunar cache for " + year);
        }
        int moonEnd = moonIndex;
        while (moonEnd < NEW_MOONS.length && NEW_MOONS[moonEnd] < end) {
            moonEnd++;
        }
        if (moonEnd >= NEW_MOONS.length) {
            throw new IllegalStateException("Incomplete lunar cache for " + year);
        }
        double[] moons = new double[moonEnd - moonIndex];
        double[] months = new double[moonEnd - moonIndex];
        for (int i = 0; i < moons.length; i++) {
            moons[i] = NEW_MOONS[moonIndex + i] + JULIAN_BASE;
            months[i] = LUNAR_MONTHS[moonIndex + i];
        }
        LunarYearData data = new LunarYearData(terms,
                NEW_MOONS[moonIndex - 1] + JULIAN_BASE,
                LUNAR_MONTHS[moonIndex - 1], moons, months,
                NEW_MOONS[moonEnd] + JULIAN_BASE, 0.0);
        return LunarYear.withLeapMonthCalendarPosition(year, data);
    }

    private static int lowerBound(int[] values, double target) {
        int result = Arrays.binarySearch(values, (int) Math.ceil(target));
        if (result < 0) {
            return -result - 1;
        }
        while (result > 0 && values[result - 1] >= target) {
            result--;
        }
        return result;
    }

    private static CacheArrays loadArrays() {
        try {
            byte[] compressed = Base64.getDecoder().decode(COMPRESSED_DATA);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
            }
            String[] tables = new String(output.toByteArray(), StandardCharsets.UTF_8).split("\\n", -1);
            if (tables.length != 3) {
                throw new IllegalStateException("Invalid embedded year-cache table count");
            }
            return new CacheArrays(parseInts(tables[0]), parseInts(tables[1]), parseDoubles(tables[2]));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read embedded year-cache data", exception);
        }
    }

    private static int[] parseInts(String csv) {
        String[] values = csv.split(",");
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Integer.parseInt(values[i]);
        }
        return result;
    }

    private static double[] parseDoubles(String csv) {
        String[] values = csv.split(",");
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Double.parseDouble(values[i]);
        }
        return result;
    }

    private static final class CacheArrays {
        final int[] solarTerms;
        final int[] newMoons;
        final double[] lunarMonths;

        CacheArrays(int[] solarTerms, int[] newMoons, double[] lunarMonths) {
            this.solarTerms = solarTerms;
            this.newMoons = newMoons;
            this.lunarMonths = lunarMonths;
        }
    }
}