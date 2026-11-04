package com.chinesebay.ccal4;

import java.util.ArrayList;
import java.util.List;

/** Lunar-month numbering and leap-month selection. */
final class LunarYear {
    private LunarYear() {
    }

    static LunarYearData forYear(int year) {
        LunarYearData cached = YearCache.get(year);
        if (cached != null) {
            return cached;
        }
        return calculate(year);
    }

    static LunarYearData withLeapMonthCalendarPosition(int year, LunarYearData data) {
        double position = findLeapMonthCalendarPosition(year, data);
        return new LunarYearData(data.solarTerms, data.lastNewMoon, data.lastLunarMonth,
                data.newMoons, data.lunarMonths, data.nextNewMoon, position);
    }

    private static LunarYearData calculate(int year) {
        double previousWinterSolstice = SolarTerms.previousWinterSolstice(year);
        double[] terms = SolarTerms.forYear(year);
        List<Double> moons = moonPhases(
                CalendarMath.wholeDay(previousWinterSolstice + 0.5) + 0.5,
                CalendarMath.julianDate(year, 12, 31, 23.9999));
        List<Double> months = numberLunarMonths(year, terms, moons);

        double lastNew;
        double lastMonth;
        if (moons.get(0) < CalendarMath.julianDate(year, 1, 1, 0.0)) {
            lastNew = wholeDay(moons.get(0));
            lastMonth = months.get(0);
            moons.remove(0);
            months.remove(0);
        } else {
            List<Double> previous = moonPhases(moons.get(0) - 35.0, moons.get(0) - 25.0);
            lastNew = wholeDay(previous.get(previous.size() - 1));
            lastMonth = 11.0;
        }
        List<Double> next = moonPhases(moons.get(moons.size() - 1) + 25.0,
                moons.get(moons.size() - 1) + 35.0);
        double nextNew = wholeDay(next.get(next.size() - 1));

        double[] wholeTerms = wholeDays(terms);
        double[] wholeMoons = wholeDays(moons);
        if (year == 1774) {
            wholeTerms[2]++;
        }
        if (year == 1878) {
            wholeTerms[8]++;
        }
        if (year == 1951) {
            wholeTerms[23]++;
        }
        double[] lunarMonths = toArray(months);
        LunarYearData raw = new LunarYearData(wholeTerms, lastNew, lastMonth,
                wholeMoons, lunarMonths, nextNew, 0.0);
        return withLeapMonthCalendarPosition(year, raw);
    }

    private static List<Double> numberLunarMonths(int year, double[] terms, List<Double> moons) {
        List<Double> result = new ArrayList<Double>();
        if (isNormalYear(terms[23], moons)) {
            int month = 12;
            for (int i = 0; i < moons.size(); i++) {
                result.add((double) month);
                if (month == 12) {
                    month = 0;
                }
                month++;
            }
            if (month == 1) {
                double[] nextTerms = SolarTerms.forYear(year + 1);
                List<Double> nextMoons = moonPhases(
                        CalendarMath.wholeDay(SolarTerms.previousWinterSolstice(year + 1) + 0.5) + 0.5,
                        CalendarMath.julianDate(year + 1, 12, 31, 23.999));
                if (!isNormalYear(nextTerms[23], nextMoons)
                        && !hasZhongQi(nextMoons.get(0), nextMoons.get(1), nextTerms)) {
                    result.set(result.size() - 1, 11.5);
                }
            }
        } else {
            boolean leapAdded = false;
            int month = 11;
            for (int i = 0; i < moons.size() - 1; i++) {
                if (leapAdded || hasZhongQi(moons.get(i), moons.get(i + 1), terms)) {
                    month++;
                    result.add((double) month);
                } else {
                    result.add(month == 0 ? 12.5 : month + 0.5);
                    leapAdded = true;
                }
                if (month == 12) {
                    month = 0;
                }
            }
            result.add((double) (month + 1));
        }
        return result;
    }

    private static boolean isNormalYear(double winterSolstice, List<Double> moons) {
        int count = 0;
        double end = Math.floor(winterSolstice + 0.5) + 0.5;
        for (double moon : moons) {
            if (moon < end) {
                count++;
            }
        }
        return count == 12;
    }

    private static boolean hasZhongQi(double start, double end, double[] terms) {
        double firstDay = Math.floor(start - 0.5) + 0.5;
        double lastDay = Math.floor(end - 0.5) + 0.5;
        for (int i = 1; i < terms.length; i += 2) {
            if (terms[i] >= firstDay && terms[i] < lastDay) {
                return true;
            }
            if (terms[i] >= lastDay) {
                return false;
            }
        }
        return false;
    }

    private static List<Double> moonPhases(double start, double end) {
        double offset = start < 2425245.0 ? (116.0 + 25.0 / 60.0) / 360.0 : 120.0 / 360.0;
        double period = 29.53058853;
        int lunation = (int) ((start - offset - 2451550.09765) / period);
        double event = MoonPhases.phaseByLunation(lunation, 0) + offset;
        while (event - start > 29.0) {
            lunation--;
            event = MoonPhases.phaseByLunation(lunation, 0) + offset;
        }
        while (start > event) {
            lunation++;
            event = MoonPhases.phaseByLunation(lunation, 0) + offset;
        }
        List<Double> result = new ArrayList<Double>();
        event = DeltaT.ttToUt(event);
        result.add(event);
        while (event < end - 29.0) {
            lunation++;
            event = DeltaT.ttToUt(MoonPhases.phaseByLunation(lunation, 0) + offset);
            if (event < end) {
                result.add(event);
            }
        }
        return result;
    }

    private static double findLeapMonthCalendarPosition(int year, LunarYearData data) {
        if (isLeapMonth(data.lastLunarMonth)
                && CalendarMath.julianDate(year, 1, 1, 12.0) < data.newMoons[0]) {
            return 0.5;
        }
        int month = 2;
        for (int index = 0; index < data.lunarMonths.length; index++) {
            if (!isLeapMonth(data.lunarMonths[index])) {
                continue;
            }
            double firstOfMonth = 0.0;
            while (month <= 12) {
                firstOfMonth = CalendarMath.julianDate(year, month, 1, 12.0);
                if (firstOfMonth >= data.newMoons[index]) {
                    break;
                }
                month++;
            }
            if (month == 13) {
                return 12.0;
            }
            boolean runsIntoNextMonth = index < data.newMoons.length - 1
                    && firstOfMonth < data.newMoons[index + 1];
            return runsIntoNextMonth ? month - 0.5 : month - 1.0;
        }
        return 0.0;
    }

    static boolean isLeapMonth(double month) {
        return ((int) Math.floor(month + 0.9)) != (int) Math.floor(month);
    }

    private static double wholeDay(double value) {
        return Math.floor(value + 0.5);
    }

    private static double[] wholeDays(List<Double> values) {
        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = wholeDay(values.get(i));
        }
        return result;
    }

    private static double[] wholeDays(double[] values) {
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = wholeDay(values[i]);
        }
        return result;
    }

    private static double[] toArray(List<Double> values) {
        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }
}
