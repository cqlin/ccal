package com.chinesebay.ccal4;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/** Native ccal EPS layout and embedded Chinese glyph rendering. */
final class PostScriptRenderer {
    private PostScriptRenderer() {
    }

    static String render(CliOptions options, LunarYearData data, ChineseText text) {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        String title = options.singleMonth
                ? ChineseText.MONTH_NAMES[options.month - 1] + " " + options.year : "Year " + options.year;
        boolean simplified = !options.traditionalChinese();
        PostScriptFonts.PrintHeaderPS(out, title, simplified,
                !options.singleMonth && data.leapMonthCalendarPosition != 0.0);
        int first = options.singleMonth ? options.month : 1;
        int last = options.singleMonth ? options.month : 12;
        for (int month = first; month <= last; month++) {
            appendMonth(out, options, month, data, text, simplified);
        }
        out.print("grestore\nshowpage\n%%Trailer\n%%EOF\n");
        out.flush();
        return buffer.toString();
    }

    private static int monthDays(LunarYearData data, int index) {
        double next = index + 1 < data.newMoons.length ? data.newMoons[index + 1] : data.nextNewMoon;
        return (int) (next - data.newMoons[index]);
    }

    private static String monthHeader(int year, int month, LunarYearData data, double firstDay, double nextDay) {
        int index = 0;
        while (index < data.newMoons.length && data.newMoons[index] < firstDay)
            index++;
        int cycle = CalendarMath.floorMod(year - 1984, 60);
        boolean previousYear = month == 1 && (int) data.lunarMonths[index] != 1;
        boolean twoMonths = index + 1 < data.newMoons.length && data.newMoons[index + 1] < nextDay;
        boolean noNewMoon = month == 2 && data.newMoons[index] >= nextDay;
        int stem = previousYear ? (cycle + 59) % 10 : cycle % 10;
        int branch = previousYear ? (cycle + 59) % 12 : cycle % 12;
        int start = (int) (data.newMoons[index] - firstDay + 1);
        String lunar;
        if (noNewMoon) {
            lunar = PostScriptFonts.number2MonthPS(data.lunarMonths[index - 1], start,
                    (int) (data.newMoons[index] - data.newMoons[index - 1]), false);
            lunar = lunar.substring(0, lunar.indexOf(PostScriptFonts.getPSBigMChar(14)) + 8);
        } else {
            lunar = PostScriptFonts.number2MonthPS(data.lunarMonths[index], start, monthDays(data, index), false);
        }
        StringBuilder result = new StringBuilder();
        result.append("20 1 SF (").append(ChineseText.MONTH_NAMES[month - 1]).append(' ').append(year)
                .append(") S\n/fsc 21 def gsave ptc\n").append(PostScriptFonts.getPSBigMChar(20))
                .append(PostScriptFonts.getPSTianGan(stem)).append(PostScriptFonts.getPSDiZhi(branch))
                .append(PostScriptFonts.getPSBigMChar(16)).append(lunar);
        if (twoMonths) {
            if (previousYear)
                result.append('\n');
            result.append(PostScriptFonts.getPSBigMChar(19));
            if (previousYear)
                result.append(PostScriptFonts.getPSTianGan(cycle % 10))
                        .append(PostScriptFonts.getPSDiZhi(cycle % 12)).append(PostScriptFonts.getPSBigMChar(16));
            result.append(PostScriptFonts.number2MonthPS(data.lunarMonths[index + 1],
                    (int) (data.newMoons[index + 1] - firstDay + 1), monthDays(data, index + 1), false));
        }
        return result.append("grestore").toString();
    }

    private static void appendMonth(PrintWriter out, CliOptions options, int month, LunarYearData data,
            ChineseText text, boolean simplified) {
        CalendarRenderer.MonthModel model = new CalendarRenderer.MonthModel(options.year, month, data, text);
        boolean single = options.singleMonth;
        int weekday = CalendarMath.dayOfWeek(options.year, month, 1);
        int days = CalendarMath.daysInMonth(options.year, month);
        int weeks = (weekday > 4 && days == 31) || (weekday > 5 && days == 30) ? 6 : 5;
        if (single) {
            String header = monthHeader(options.year, month, data, model.firstJulianDay, model.nextJulianDay);
            double leap = data.leapMonthCalendarPosition;
            boolean needsLeap = (int) leap == month || (int) (leap + 0.9) == month;
            PostScriptFonts.PrintHeaderMonthPS(out, header, (short) month, true, simplified, needsLeap, weeks);
            int titleLength = header.indexOf(')') - header.indexOf('(') - 1;
            int length = header.length();
            int move;
            if (length - titleLength - 66 > 56)
                move = (4200 - 78 * (titleLength + (length - titleLength - 86) / 2 + 3)) / 2;
            else if (length - titleLength - 66 > 30)
                move = (4200 - 78 * (titleLength + (length - titleLength - 66) / 2 + 3)) / 2;
            else
                move = (4200 - 78 * (titleLength + (length - titleLength - 46) / 2 + 2)) / 2;
            out.printf(Locale.ROOT, "%d 2475 m\n%s\n", Math.max(0, move), header);
            out.print("% Day names\n14 1 SF\n/fsc 15 def\n");
            for (int i = 0; i < 7; i++) {
                String name = ChineseText.DAY_NAMES[i].substring(0, 3);
                if (i == 0)
                    out.printf("gsave 1 0 0 K\n100 2270 m (%s) S\n400 2270 m "
                            + "gsave ptc %s grestore\ngrestore\n", name, PostScriptFonts.getPSBigMChar(15));
                else if (i == 6)
                    out.printf("gsave 0 0.8 0 K\n3700 2270 m (%s) S\n4000 2270 m "
                            + "gsave ptc %s grestore\ngrestore\n", name, PostScriptFonts.getPSBigMChar(i));
                else
                    out.printf(Locale.ROOT, "%d 2270 m (%s) S\n%d 2270 m gsave ptc %s grestore\n",
                            i * 600 + 100, name, i * 600 + 400, PostScriptFonts.getPSBigMChar(i));
            }
            out.print("% Days\n17 1 SF\n/fsc 7.5 def\n");
        } else {
            out.printf(Locale.ROOT, "%% %s %d\n%d lpts %d m gsave ct\n", ChineseText.MONTH_NAMES[month - 1],
                    options.year, (month - 1) % 4 * 140, 3450 - (month - 1) / 4 * 1150 - 173);
            out.printf(Locale.ROOT, "%% Month number\n%d lpts -750 m gsave\n100 1 SF 1 0.9 1 K (%d) S grestore\n",
                    month < 10 ? 35 : 10, month);
            out.print("% Week heading\n0 0 m Ymh\n% Days\n9 0 SF\n");
        }

        for (int day = 1; day <= days; day++) {
            int slot = weekday + day - 1;
            int column = slot % 7;
            int week = slot / 7;
            CalendarRenderer.DayModel value = model.day(day);
            if (column == 0)
                out.print("gsave 1 0 0 K\n");
            else if (column == 6)
                out.print("gsave 0 0.8 0 K\n");
            if (single) {
                int x = column * 600 + 50;
                int y = weeks == 5 ? 1800 - week * 450 + 295 : 1875 - week * 375 + 220;
                out.printf(Locale.ROOT, "%d %d m (%2d) S\ngsave 8 0 SF ", x, y, day);
                x += 170;
                if (day == 1 || value.lunarDay == 1)
                    out.printf(Locale.ROOT, "%d %d m (%d%s.%d) S grestore\n%d %d m", x + 5, y + 56,
                            value.lunarMonthNumber, value.leapMonth ? "R" : "", value.lunarDay, x, y - 2);
                else
                    out.printf(Locale.ROOT, "%d %d m (%2d) S grestore\n%d %d m",
                            x + 5, y + 56, value.lunarDay, x, y - 2);
                out.print(" gsave ptc");
            } else {
                out.printf(Locale.ROOT, "%d %d moveto (%2d) S\n%d %d moveto gsave ptc",
                        column * 19, -week * 19 - 14, day, column * 19, -week * 19 - 21);
            }
            appendDay(out, value, column, single);
        }
        if (!single)
            out.print("grestore\n");
    }

    private static void appendDay(PrintWriter out, CalendarRenderer.DayModel value, int column, boolean single) {
        boolean scaled = false;
        if (value.solarTermIndex >= 0) {
            if (value.newLunarMonth) {
                String name = PostScriptFonts.number2MonthPS(value.lunarMonth, 1, 30, true);
                if (!single)
                    out.print(" grestore -6 0 rmoveto gsave ptc");
                scaled = !single && name.length() > 8;
                if (scaled)
                    out.printf(Locale.ROOT, " gsave %f 1 scale %s", 16.0 / (name.length() + 8.0), name);
                else
                    out.print(" " + name);
            } else {
                out.print(" ");
            }
            out.printf("%s\n", PostScriptFonts.getPSJieQi(value.solarTermIndex));
        } else if (value.newLunarMonth) {
            String name = PostScriptFonts.number2MonthPS(value.lunarMonth, 1, 30, true);
            if (!single && name.length() > 8)
                out.printf(Locale.ROOT, " grestore -%d 0 rmoveto gsave ptc", (name.length() - 8) / 4 * 3);
            out.printf(" %s\n", name);
        } else {
            if (single && value.gregorianDay == 1)
                out.print(" " + PostScriptFonts.number2MonthPS(value.lunarMonth, 1, 30, true));
            else
                out.print(" ");
            out.printf("%s\n", PostScriptFonts.number2DayPS(value.lunarDay));
        }
        if (scaled)
            out.print("grestore\n");
        if (column == 0 || column == 6)
            out.print("grestore\n");
        out.print("grestore\n");
    }
}
