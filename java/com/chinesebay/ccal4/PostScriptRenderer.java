package com.chinesebay.ccal4;

/**
 * Small, self-contained EPS renderer.  It intentionally uses only the
 * standard PostScript Helvetica font, so lunar annotations are rendered with
 * the original ASCII abbreviations rather than depending on an installed CJK
 * PostScript font.
 */
final class PostScriptRenderer {
    private PostScriptRenderer() {
    }

    static String render(CliOptions options, LunarYearData data, ChineseText text) {
        StringBuilder result = new StringBuilder();
        result.append("%!PS-Adobe-3.0 EPSF-3.0\n")
                .append("%%Creator: ccal 2.5.3 Java port\n")
                .append("%%BoundingBox: 0 0 612 792\n")
                .append("%%Pages: 1\n%%EndComments\n")
                .append("/H { /Helvetica findfont exch scalefont setfont } bind def\n")
                .append("/S { moveto show } bind def\n")
                .append("/L { newpath moveto lineto stroke } bind def\n")
                .append("0 setgray 12 H\n");
        int first = options.singleMonth ? options.month : 1;
        int last = options.singleMonth ? options.month : 12;
        if (options.singleMonth) {
            appendMonth(result, new CalendarRenderer.MonthModel(options.year, options.month, data, text),
                    36, 742, 76, 82, true);
        } else {
            int ordinal = 0;
            for (int month = first; month <= last; month++) {
                int column = ordinal % 3;
                int row = ordinal / 3;
                appendMonth(result, new CalendarRenderer.MonthModel(options.year, month, data, text),
                        22 + column * 198, 750 - row * 185, 27, 22, false);
                ordinal++;
            }
        }
        result.append("showpage\n%%EOF\n");
        return result.toString();
    }

    private static void appendMonth(StringBuilder result, CalendarRenderer.MonthModel model,
            int x, int y, int columnWidth, int rowHeight, boolean fullPage) {
        int titleSize = fullPage ? 18 : 10;
        result.append(titleSize).append(" H\n(")
                .append(escape(ChineseText.MONTH_NAMES[model.month - 1] + " " + model.year))
                .append(") ").append(x).append(' ').append(y).append(" S\n");
        result.append(fullPage ? "10 H\n" : "6 H\n");
        String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        int weekdayY = y - (fullPage ? 28 : 14);
        for (int i = 0; i < weekdays.length; i++) {
            result.append('(').append(weekdays[i]).append(") ")
                    .append(x + i * columnWidth).append(' ').append(weekdayY).append(" S\n");
        }
        int firstDayOfWeek = CalendarMath.dayOfWeek(model.year, model.month, 1);
        int days = CalendarMath.daysInMonth(model.year, model.month);
        for (int day = 1; day <= days; day++) {
            int slot = firstDayOfWeek + day - 1;
            int column = slot % 7;
            int row = slot / 7;
            CalendarRenderer.DayModel lunarDay = model.day(day);
            String annotation = lunarDay.solarTermIndex >= 0
                    ? ChineseText.ASCII_SOLAR_TERMS[lunarDay.solarTermIndex]
                    : lunarDay.newLunarMonth
                    ? lunarDay.lunarMonthNumber + (lunarDay.leapMonth ? "R" : "") + ".1"
                    : Integer.toString(lunarDay.lunarDay);
            int cellX = x + column * columnWidth;
            int cellY = weekdayY - (row + 1) * rowHeight;
            result.append('(').append(day).append(") ").append(cellX).append(' ').append(cellY).append(" S\n")
                    .append('(').append(escape(annotation)).append(") ")
                    .append(cellX + (fullPage ? 22 : 11)).append(' ').append(cellY).append(" S\n");
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
