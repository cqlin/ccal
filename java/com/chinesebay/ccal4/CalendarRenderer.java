package com.chinesebay.ccal4;

import java.time.LocalDate;

/** Renders a computed Chinese calendar in the formats offered by ccal. */
final class CalendarRenderer {
    private CalendarRenderer() {
    }

    static String render(CliOptions options, LocalDate today) {
        LunarYearData data = LunarYear.forYear(options.year);
        ChineseText text = new ChineseText(options.traditionalChinese());
        switch (options.outputMode) {
            case HTML:
                return renderHtml(options, data, text);
            case XML:
                return renderXml(options, data, text);
            case POSTSCRIPT:
                return PostScriptRenderer.render(options, data, text);
            case ASCII:
            default:
                return renderAscii(options, data, text, today);
        }
    }

    private static String renderAscii(CliOptions options, LunarYearData data, ChineseText text,
            LocalDate today) {
        StringBuilder result = new StringBuilder();
        int first = options.singleMonth ? options.month : 1;
        int last = options.singleMonth ? options.month : 12;
        for (int month = first; month <= last; month++) {
            MonthModel model = new MonthModel(options.year, month, data, text);
            String heading = options.encoding == 'a' ? model.asciiHeader() : model.chineseHeader();
            int width = visualWidth(heading);
            appendSpaces(result, Math.max(0, (68 - width) / 2));
            result.append(heading).append('\n');
            if (options.encoding == 'a') {
                for (String name : ChineseText.DAY_NAMES) {
                    result.append(padRight(name, 10));
                }
            } else {
                String[] days = {"Sun 日", "Mon 一", "Tue 二", "Wed 三", "Thu 四", "Fri 五", "Sat 六"};
                for (String day : days) {
                    result.append(padRight(day, 10));
                }
            }
            result.append('\n');

            int firstDayOfWeek = CalendarMath.dayOfWeek(options.year, month, 1);
            int days = CalendarMath.daysInMonth(options.year, month);
            int day = 1;
            int weeks = weekCount(firstDayOfWeek, days);
            for (int week = 0; week < weeks; week++) {
                for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek) {
                        result.append("          ");
                    } else if (day > days) {
                        // The original character renderer finishes the last
                        // physical line rather than padding it to seven cells.
                        break;
                    } else {
                        DayModel dayModel = model.day(day++);
                        String cell = options.encoding == 'a'
                                ? asciiCell(dayModel)
                                : chineseCell(dayModel, text);
                        if (today.getYear() == options.year && today.getMonthValue() == month
                                && today.getDayOfMonth() == dayModel.gregorianDay) {
                            result.append("\u001B[7m").append(cell).append("\u001B[0m");
                        } else {
                            result.append(cell);
                        }
                    }
                }
                result.append('\n');
            }
        }
        return result.toString();
    }

    private static String asciiCell(DayModel day) {
        String label;
        if (day.solarTermIndex >= 0) {
            label = "[" + ChineseText.ASCII_SOLAR_TERMS[day.solarTermIndex] + "]";
        } else if (day.newLunarMonth) {
            label = "[" + padLeft(Integer.toString(day.lunarMonthNumber), 2) + "]Y"
                    + (day.leapMonth ? "R" : "");
        } else {
            label = "[" + padLeft(Integer.toString(day.lunarDay), 2) + "]";
        }
        return padRight(padLeft(Integer.toString(day.gregorianDay), 2) + " " + label, 10);
    }

    private static String chineseCell(DayModel day, ChineseText text) {
        String label = day.solarTermIndex >= 0 ? text.solarTerm(day.solarTermIndex)
                : day.newLunarMonth ? text.lunarMonthShort(day.lunarMonth)
                : text.lunarDay(day.lunarDay);
        return padRight(padLeft(Integer.toString(day.gregorianDay), 2) + " " + label, 10);
    }

    private static String renderHtml(CliOptions options, LunarYearData data, ChineseText text) {
        String charset = options.encoding == 'u' ? "utf-8" : options.encoding == 'b' ? "big5" : "gb2312";
        StringBuilder result = new StringBuilder();
        result.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=")
                .append(charset).append("\">\n")
                .append("<meta name=\"GENERATOR\" content=\"ccal-2.5.3 Java port\">\n")
                .append("<link rel=\"stylesheet\" href=\"ccal.css\">\n")
                .append("<title>Chinese Calendar for ");
        if (options.singleMonth) {
            result.append(ChineseText.MONTH_NAMES[options.month - 1]).append(' ').append(options.year);
        } else {
            result.append("Year ").append(options.year);
        }
        result.append("</title>\n</head>\n<body>\n<center>\n")
                .append("<table class=\"ccal-calendar\">\n");
        int first = options.singleMonth ? options.month : 1;
        int last = options.singleMonth ? options.month : 12;
        for (int month = first; month <= last; month++) {
            MonthModel model = new MonthModel(options.year, month, data, text);
            result.append("<tr class=\"ccal-month-heading\">\n<th colspan=\"7\">")
                    .append(escapeHtml(model.chineseHeader())).append("</th>\n</tr>\n");
            result.append("<tr class=\"ccal-weekdays\">\n");
            for (int day = 0; day < 7; day++) {
                String shortName = ChineseText.DAY_NAMES[day].substring(0, 3);
                String value = shortName + " " + new String[] {"日", "一", "二", "三", "四", "五", "六"}[day];
                if (day == 0) {
                    result.append("<th scope=\"col\" class=\"ccal-sunday\">").append(value).append("</th>\n");
                } else if (day == 6) {
                    result.append("<th scope=\"col\" class=\"ccal-saturday\">").append(value).append("</th>\n");
                } else {
                    result.append("<th scope=\"col\">").append(value).append("</th>\n");
                }
            }
            result.append("</tr>\n");
            appendHtmlWeeks(result, options.year, month, model, text);
        }
        result.append("</table>\n</center>\n</body>\n</html>\n");
        return result.toString();
    }

    private static void appendHtmlWeeks(StringBuilder result, int year, int month, MonthModel model,
            ChineseText text) {
        int firstDayOfWeek = CalendarMath.dayOfWeek(year, month, 1);
        int days = CalendarMath.daysInMonth(year, month);
        int day = 1;
        int weeks = weekCount(firstDayOfWeek, days);
        for (int week = 0; week < weeks; week++) {
            result.append("<tr>\n");
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                String open = dayOfWeek == 0 ? "<td class=\"ccal-sunday\">"
                        : dayOfWeek == 6 ? "<td class=\"ccal-saturday\">" : "<td>";
                result.append(open);
                if ((week == 0 && dayOfWeek < firstDayOfWeek) || day > days) {
                    result.append("&#160;");
                } else {
                    DayModel dayModel = model.day(day++);
                    String label = dayModel.solarTermIndex >= 0 ? text.solarTerm(dayModel.solarTermIndex)
                            : dayModel.newLunarMonth ? text.lunarMonthShort(dayModel.lunarMonth)
                            : text.lunarDay(dayModel.lunarDay);
                    result.append(dayModel.gregorianDay).append(' ').append(escapeHtml(label));
                }
                result.append("</td>\n");
            }
            result.append("</tr>\n");
        }
    }

    private static String renderXml(CliOptions options, LunarYearData data, ChineseText text) {
        StringBuilder result = new StringBuilder();
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<ccal:year value=\"").append(options.year)
                .append("\" xmlns:ccal=\"http://ccal.chinesebay.com/ccal/\">\n");
        int first = options.singleMonth ? options.month : 1;
        int last = options.singleMonth ? options.month : 12;
        for (int month = first; month <= last; month++) {
            MonthModel model = new MonthModel(options.year, month, data, text);
            result.append("<ccal:month value=\"").append(month).append("\" name=\"")
                    .append(ChineseText.MONTH_NAMES[month - 1]).append("\" cname=\"")
                    .append(escapeXml(model.chineseHeaderWithoutGregorian())).append("\">\n");
            appendXmlWeeks(result, options.year, month, model, text);
            result.append("</ccal:month>\n");
        }
        result.append("</ccal:year>\n");
        return result.toString();
    }

    private static void appendXmlWeeks(StringBuilder result, int year, int month, MonthModel model,
            ChineseText text) {
        int firstDayOfWeek = CalendarMath.dayOfWeek(year, month, 1);
        int days = CalendarMath.daysInMonth(year, month);
        int day = 1;
        int weeks = weekCount(firstDayOfWeek, days);
        for (int week = 0; week < weeks; week++) {
            result.append("<ccal:week>\n");
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                if ((week == 0 && dayOfWeek < firstDayOfWeek) || day > days) {
                    result.append("<ccal:day />\n");
                    continue;
                }
                DayModel dayModel = model.day(day++);
                String dateName = dayModel.solarTermIndex >= 0 ? text.solarTerm(dayModel.solarTermIndex)
                        : text.lunarDay(dayModel.lunarDay);
                result.append("<ccal:day value=\"").append(dayModel.gregorianDay)
                        .append("\" cmonth=\"").append(dayModel.lunarMonthNumber)
                        .append("\" leap=\"").append(dayModel.leapMonth ? "R" : "")
                        .append("\" cdate=\"").append(dayModel.lunarDay)
                        .append("\" cmonthname=\"").append(escapeXml(text.lunarMonthShort(dayModel.lunarMonth)))
                        .append("\" cdatename=\"").append(escapeXml(dateName)).append("\" />\n");
            }
            result.append("</ccal:week>\n");
        }
    }

    static final class MonthModel {
        final int year;
        final int month;
        final LunarYearData data;
        final ChineseText text;
        final double firstJulianDay;
        final double nextJulianDay;

        MonthModel(int year, int month, LunarYearData data, ChineseText text) {
            this.year = year;
            this.month = month;
            this.data = data;
            this.text = text;
            this.firstJulianDay = CalendarMath.julianDate(year, month, 1, 12.0);
            this.nextJulianDay = month == 12 ? CalendarMath.julianDate(year + 1, 1, 1, 12.0)
                    : CalendarMath.julianDate(year, month + 1, 1, 12.0);
        }

        DayModel day(int gregorianDay) {
            double julianDay = firstJulianDay + gregorianDay - 1;
            int index = latestMoonIndex(julianDay);
            boolean beforeFirstMoon = index < 0;
            double newMoon = beforeFirstMoon ? data.lastNewMoon : data.newMoons[index];
            double lunarMonth = beforeFirstMoon ? data.lastLunarMonth : data.lunarMonths[index];
            int lunarDay = (int) Math.round(julianDay - newMoon + 1.0);
            int solarTerm = solarTermIndex(julianDay);
            return new DayModel(gregorianDay, lunarMonth, lunarDay, solarTerm,
                    !beforeFirstMoon && julianDay == newMoon);
        }

        String asciiHeader() {
            HeaderMonth first = headerMonth();
            int headerYear = month == 1 && first.number != 1 ? year - 1 : year;
            StringBuilder result = new StringBuilder();
            result.append(ChineseText.MONTH_NAMES[month - 1]).append(' ').append(year)
                    .append(" (Year ").append(text.romanStemBranch(headerYear)).append(", Month ")
                    .append(first.number);
            if (first.leap) {
                result.append('R');
            }
            result.append(first.days == 30 ? 'D' : 'X').append(" S").append(first.startDay);
            HeaderMonth second = secondHeaderMonth(first.index);
            if (second != null) {
                result.append(", ").append(second.number);
                if (second.leap) {
                    result.append('R');
                }
                result.append(second.days == 30 ? 'D' : 'X').append(" S").append(second.startDay);
            }
            return result.append(')').toString();
        }

        String chineseHeader() {
            return ChineseText.MONTH_NAMES[month - 1] + " " + year + "  "
                    + chineseHeaderWithoutGregorian();
        }

        String chineseHeaderWithoutGregorian() {
            HeaderMonth first = headerMonth();
            int headerYear = month == 1 && first.number != 1 ? year - 1 : year;
            StringBuilder result = new StringBuilder();
            result.append(text.stemBranch(headerYear)).append(text.year())
                    .append(text.lunarMonth(first.value, first.startDay, first.days));
            HeaderMonth second = secondHeaderMonth(first.index);
            if (second != null) {
                result.append('，').append(text.lunarMonth(second.value, second.startDay, second.days));
            }
            return result.toString();
        }

        private HeaderMonth headerMonth() {
            int index = firstMoonIndexOnOrAfter(firstJulianDay);
            if (index >= data.newMoons.length || data.newMoons[index] >= nextJulianDay) {
                // This rare case normally occurs in February.  The original
                // program labels it with the lunar month already in progress.
                int active = latestMoonIndex(firstJulianDay);
                if (active < 0) {
                    return new HeaderMonth(-1, data.lastLunarMonth, firstJulianDay - data.lastNewMoon + 1,
                            (int) Math.round(data.newMoons[0] - data.lastNewMoon));
                }
                double next = active + 1 < data.newMoons.length ? data.newMoons[active + 1] : data.nextNewMoon;
                return new HeaderMonth(active, data.lunarMonths[active], 1,
                        (int) Math.round(next - data.newMoons[active]));
            }
            double next = index + 1 < data.newMoons.length ? data.newMoons[index + 1] : data.nextNewMoon;
            return new HeaderMonth(index, data.lunarMonths[index], data.newMoons[index] - firstJulianDay + 1,
                    (int) Math.round(next - data.newMoons[index]));
        }

        private HeaderMonth secondHeaderMonth(int firstIndex) {
            if (firstIndex < 0 || firstIndex + 1 >= data.newMoons.length) {
                return null;
            }
            int index = firstIndex + 1;
            if (data.newMoons[index] >= nextJulianDay) {
                return null;
            }
            double next = index + 1 < data.newMoons.length ? data.newMoons[index + 1] : data.nextNewMoon;
            return new HeaderMonth(index, data.lunarMonths[index], data.newMoons[index] - firstJulianDay + 1,
                    (int) Math.round(next - data.newMoons[index]));
        }

        private int latestMoonIndex(double julianDay) {
            int result = -1;
            for (int i = 0; i < data.newMoons.length && data.newMoons[i] <= julianDay; i++) {
                result = i;
            }
            return result;
        }

        private int firstMoonIndexOnOrAfter(double julianDay) {
            for (int i = 0; i < data.newMoons.length; i++) {
                if (data.newMoons[i] >= julianDay) {
                    return i;
                }
            }
            return data.newMoons.length;
        }

        private int solarTermIndex(double julianDay) {
            for (int i = 0; i < data.solarTerms.length; i++) {
                if (data.solarTerms[i] == julianDay) {
                    return i;
                }
            }
            return -1;
        }
    }

    static final class DayModel {
        final int gregorianDay;
        final double lunarMonth;
        final int lunarMonthNumber;
        final boolean leapMonth;
        final int lunarDay;
        final int solarTermIndex;
        final boolean newLunarMonth;

        DayModel(int gregorianDay, double lunarMonth, int lunarDay, int solarTermIndex, boolean newLunarMonth) {
            this.gregorianDay = gregorianDay;
            this.lunarMonth = lunarMonth;
            this.lunarMonthNumber = (int) Math.floor(lunarMonth);
            this.leapMonth = LunarYear.isLeapMonth(lunarMonth);
            this.lunarDay = lunarDay;
            this.solarTermIndex = solarTermIndex;
            this.newLunarMonth = newLunarMonth;
        }
    }

    private static final class HeaderMonth {
        final int index;
        final double value;
        final int number;
        final boolean leap;
        final int startDay;
        final int days;

        HeaderMonth(int index, double value, double startDay, int days) {
            this.index = index;
            this.value = value;
            this.number = (int) Math.floor(value);
            this.leap = LunarYear.isLeapMonth(value);
            this.startDay = (int) Math.round(startDay);
            this.days = days;
        }
    }

    private static int visualWidth(String value) {
        int width = 0;
        for (int i = 0; i < value.length(); i++) {
            width += value.charAt(i) < 128 ? 1 : 2;
        }
        return width;
    }

    private static int weekCount(int firstDayOfWeek, int daysInMonth) {
        int weeks = 5;
        if ((firstDayOfWeek > 4 && daysInMonth == 31)
                || (firstDayOfWeek > 5 && daysInMonth == 30)) {
            weeks = 6;
        }
        return weeks;
    }

    static String padRight(String value, int width) {
        StringBuilder result = new StringBuilder(value);
        while (result.length() < width) {
            result.append(' ');
        }
        return result.toString();
    }

    static String padLeft(String value, int width) {
        StringBuilder result = new StringBuilder();
        while (result.length() + value.length() < width) {
            result.append(' ');
        }
        return result.append(value).toString();
    }

    private static void appendSpaces(StringBuilder result, int count) {
        for (int i = 0; i < count; i++) {
            result.append(' ');
        }
    }

    static String escapeXml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private static String escapeHtml(String value) {
        return escapeXml(value);
    }
}
