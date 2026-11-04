package com.chinesebay.ccal3;

/*
   Copyright (c) 2000-2012, by Zhuo Meng (zxm8@case.edu).
   All rights reserved.

   Distributed under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Ccal.java - Chinese Calendar Main Program
 * Complete Java translation of ccal.cpp with full support for:
 * - ASCII (pmode=0), HTML table (pmode=1), PostScript (pmode=2), XML (pmode=3) output
 * - Multiple encodings: ASCII, GB2312 ('g'), Big5 ('b'), UTF-8 ('u')
 * - All calendar formatting and Chinese character rendering
 */
public class Ccal {
    private static final String VERSION = "2.5.3";

    // Solar term abbreviations (ASCII)
    private static final String[] JIEQI = {
        "XH", "DH", "LC", "YS", "JZ", "CF", "QM", "GY",
        "LX", "XM", "MZ", "XZ", "XS", "DS", "LQ", "CS",
        "BL", "QF", "HL", "SJ", "LD", "XX", "DX", "DZ"
    };

    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    private static final String[] DAY_NAMES = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    // Day names with GB2312 encoding (Sun 日, Mon 一, etc.)
    private static final String[] DAY_NAMES_GB = {
        "Sun  \u65e5", "Mon  \u4e00", "Tue  \u4e8c",
        "Wed  \u4e09", "Thu  \u56db", "Fri  \u4e94",
        "Sat  \u516d"
    };

    // Day names with Big5 encoding
    private static final String[] DAY_NAMES_B5 = {
        "Sun  \u65e5", "Mon  \u4e00", "Tue  \u4e8c",
        "Wed  \u4e09", "Thu  \u56db", "Fri  \u4e94",
        "Sat  \u516d"
    };

    // Day names with UTF-8 encoding
    private static final String[] DAY_NAMES_U8 = {
        "Sun  \u65e5", "Mon  \u4e00", "Tue  \u4e8c",
        "Wed  \u4e09", "Thu  \u56db", "Fri  \u4e94",
        "Sat  \u516d"
    };

    private static int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final String[] TIANGAN = {
        "Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"
    };

    private static final String[] DIZHI = {
        "Zi", "Chou", "Yin", "Mou", "Chen", "Si",
        "Wu", "Wei", "Shen", "You", "Xu", "Hai"
    };

    /**
     * Checks if a Gregorian year is a leap year
     */
    public static boolean isLeapYear(short year) {
        if (year % 100 != 0) {
            return (year % 4 == 0);
        } else {
            return (year % 400 == 0);
        }
    }

    /**
     * Gets month number and leap indicator from month number
     * @param mnumber Month number with .5 indicating leap month
     * @return Array [month, leap] where leap is 0 for normal, 'R' for leap month
     */
    private static int[] monthNumber(double mnumber) {
        int month = (int)mnumber;
        int leap = (mnumber - month == 0.0) ? 0 : 'R';
        return new int[]{month, leap};
    }

    public static class MonthInfo {
        int month;
        char leap;
    }

    public static MonthInfo getMonthNumber(double mnumber) {
        int[] number = monthNumber(mnumber);
        MonthInfo info = new MonthInfo();
        info.month = number[0];
        info.leap = (char) number[1];
        return info;
    }

    /**
     * Prints month number in ASCII format
     */
    public static void printMonthNumber(double mnumber) {
        int[] result = monthNumber(mnumber);
        int month = result[0];
        char leap = (char)result[1];
        System.out.printf(" [%2d]Y%c ", month, leap == 0 ? ' ' : leap);
    }

    private static String postScriptMonthHeader(short year, short month, int cyear,
            int moncnt, double jdnext, int nstartlm, int ndayslm, double nextnew,
            ArrayList<Double> vmoons, ArrayList<Double> vmonth) {
        boolean twoMonths = moncnt < vmoons.size() - 1 && vmoons.get(moncnt + 1) < jdnext;
        boolean noNewMoon = month == 2 && vmoons.get(moncnt) >= jdnext;
        boolean previousYear = month == 1 && vmonth.get(moncnt).intValue() != 1;
        int stem = previousYear ? (cyear + 59) % 10 : cyear % 10;
        int branch = previousYear ? (cyear + 59) % 12 : cyear % 12;
        String lunar;
        if (noNewMoon) {
            int days = (int)(vmoons.get(moncnt) - vmoons.get(moncnt - 1));
            lunar = PSMonth.number2MonthPS(vmonth.get(moncnt - 1), nstartlm, days, false);
            lunar = lunar.substring(0, lunar.indexOf(PSMonth.getPSBigMChar(14)) + 8);
        } else {
            lunar = PSMonth.number2MonthPS(vmonth.get(moncnt), nstartlm, ndayslm, false);
        }
        String header = String.format("20 1 SF (%s %d) S\n/fsc 21 def gsave ptc\n%s%s%s%s%s",
            MONTH_NAMES[month - 1], year, PSMonth.getPSBigMChar(20),
            PSMonth.getPSTianGan(stem), PSMonth.getPSDiZhi(branch), PSMonth.getPSBigMChar(16), lunar);
        if (twoMonths) {
            int start = nstartlm + (int)(vmoons.get(moncnt + 1) - vmoons.get(moncnt));
            int days = (int)((moncnt < vmoons.size() - 2 ? vmoons.get(moncnt + 2) : nextnew)
                - vmoons.get(moncnt + 1));
            if (previousYear)
                header += "\n";
            header += PSMonth.getPSBigMChar(19);
            if (previousYear)
                header += PSMonth.getPSTianGan(cyear % 10) + PSMonth.getPSDiZhi(cyear % 12)
                    + PSMonth.getPSBigMChar(16);
            header += PSMonth.number2MonthPS(vmonth.get(moncnt + 1), start, days, false);
        }
        return header + "grestore";
    }

    private static void printPostScriptDay(String name, int weekday, boolean single) {
        if (!single && name.length() > 8)
            System.out.printf(" grestore -%d 0 rmoveto gsave ptc", (name.length() - 8) / 4 * 3);
        System.out.printf(" %s\n", name);
        if (weekday == 0 || weekday == 6)
            System.out.println("grestore");
        System.out.println("grestore");
    }

    /**
     * Main calendar printing method
     * Prints a calendar for a given month with Chinese calendar information
     */
    public static void printMonth(short year, short month, ArrayList<Double> vterms,
                                 double lastnew, double lastmon, ArrayList<Double> vmoons,
                                 ArrayList<Double> vmonth, double nextnew, int pmode,
                                 boolean bSingle, int nEncoding, boolean bNeedsRun) {
        int realDaysInMonth = month == 2 ? (isLeapYear(year) ? 29 : 28) : daysInMonth[month - 1];
        // Get current date for highlighting today
        Calendar now = Calendar.getInstance();
        int todayYear = now.get(Calendar.YEAR);
        int todayMonth = now.get(Calendar.MONTH) + 1;
        int todayDay = now.get(Calendar.DAY_OF_MONTH);

        // Select character arrays based on encoding
        String[] CHtiangan = MiscData.getTianGanArray(nEncoding);
        String[] CHdizhi = MiscData.getDiZhiArray(nEncoding);
        String[] CHmiscchar = MiscData.getMiscCharArray(nEncoding);
        String[] CHjieqi = MiscData.getJieQiArray(nEncoding);
        String[] daynamesCH = DAY_NAMES;
        boolean bIsSim = (nEncoding == 'g');

        if (nEncoding == 'u') {
            daynamesCH = DAY_NAMES_U8;
        } else if (nEncoding == 'g') {
            daynamesCH = DAY_NAMES_GB;
        } else if (nEncoding == 'b') {
            daynamesCH = DAY_NAMES_B5;
        }

        // Java strings contain characters, not the encoded bytes used by C++.
        int nCHchars = CHmiscchar[14].length();
        String sp = (pmode == 1) ? "&#160;" : " ";

        // Set julian day counter to 1st of the month
        double jdcnt = Novas.julian_date(year, month, (short)1, 12.0);

        // Find julian day of the start of the next month
        double jdnext;
        if (month < 12)
            jdnext = Novas.julian_date(year, (short)(month + 1), (short)1, 12.0);
        else
            jdnext = Novas.julian_date((short)(year + 1), (short)1, (short)1, 12.0);

        // Set solarterm counter to day of 1st term of the calendar month
        int termcnt = (month - 1) * 2;
        if (vterms.get(termcnt) < jdcnt)
            termcnt++;

        // Set lunar month counter to the 1st lunar month of the calendar month
        int moncnt = 0;
        while (moncnt < vmoons.size() && vmoons.get(moncnt) < jdcnt)
            moncnt++;

        // In case solarterm and 1st of lunar month falls on the same day
        boolean sameday = false;

        // Initialize counters for lunar days and days of month
        int ldcnt, dcnt = 1;
        if (month != 1)
            ldcnt = (int)(jdcnt - vmoons.get(moncnt - 1) + 1);
        else
            ldcnt = (int)(jdcnt - lastnew + 1);
        if (jdcnt == vmoons.get(moncnt))
            ldcnt = 1;

        // Day of week of the 1st of month
        int dofw = ((int)jdcnt + 1) % 7;
        int nWeeks = 5;
        if ((dofw > 4 && realDaysInMonth == 31) ||
            (dofw > 5 && realDaysInMonth == 30))
            nWeeks = 6;

        // Header of the month
        short cyear = (short)((year - 1984) % 60);
        if (cyear < 0)
            cyear += 60;
        int tiancnt = cyear % 10;
        int dicnt = cyear % 12;

        int[] monthInfo = monthNumber(vmonth.get(moncnt));
        int cmonth = monthInfo[0];
        char leap = (char)monthInfo[1];

        StringBuilder monthhead = new StringBuilder();
        String cmonname = "";
        int nstartlm = (int)(vmoons.get(moncnt) - jdcnt + 1);
        int ndayslm;
        if (moncnt < vmoons.size() - 1)
            ndayslm = (int)(vmoons.get(moncnt + 1) - vmoons.get(moncnt));
        else
            ndayslm = (int)(nextnew - vmoons.get(vmoons.size() - 1));

        if ((pmode == 0 && nEncoding != 'a') || pmode == 1 || pmode == 3)
            cmonname = MiscData.number2MonthCH(vmonth.get(moncnt), nstartlm, ndayslm);
        else if (pmode == 2)
            cmonname = PSMonth.number2MonthPS(vmonth.get(moncnt), nstartlm, ndayslm, !bSingle);

        // January is special if lunar New Year is in February
        if (month == 1 && cmonth != 1) {
            int tiancnt0 = (cyear + 59) % 10;
            int dicnt0 = (cyear + 59) % 12;
            if (moncnt < vmoons.size() - 1 && vmoons.get(moncnt + 1) < jdnext) {
                // Two lunar months in January
                int[] monthInfo1 = monthNumber(vmonth.get(moncnt + 1));
                int cmonth1 = monthInfo1[0];
                char leap1 = (char)monthInfo1[1];
                int nstartlm1 = (int)(vmoons.get(moncnt + 1) - jdcnt + 1);
                int ndayslm1;
                if (moncnt < vmoons.size() - 2)
                    ndayslm1 = (int)(vmoons.get(moncnt + 2) - vmoons.get(moncnt + 1));
                else
                    ndayslm1 = (int)(nextnew - vmoons.get(vmoons.size() - 1));

                if (pmode == 0 && nEncoding == 'a') {
                    monthhead.append(String.format(
                        "%s %d (Year %s%s, Month %s%d%c S%d, Year %s%s, Month %s%d%c S%d)",
                        MONTH_NAMES[month - 1], year,
                        TIANGAN[tiancnt0], DIZHI[dicnt0],
                        (leap == 'R' ? "R" : ""), cmonth, (ndayslm == 30) ? 'D' : 'X', nstartlm,
                        TIANGAN[tiancnt], DIZHI[dicnt],
                        (leap1 == 'R' ? "R" : ""), cmonth1, (ndayslm1 == 30) ? 'D' : 'X', nstartlm1));
                } else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
                    String cmonname1 = MiscData.number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1);
                    monthhead.append(String.format("%s %d%s%s%s%s%s%s%s%s%s%s%s",
                        MONTH_NAMES[month - 1], year, sp, sp,
                        CHtiangan[tiancnt0], CHdizhi[dicnt0],
                        CHmiscchar[16], cmonname, CHmiscchar[19],
                        CHtiangan[tiancnt], CHdizhi[dicnt],
                        CHmiscchar[16], cmonname1));
                } else if (pmode == 3) {
                    String cmonname1 = MiscData.number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1);
                    monthhead.append(String.format("%s%s%s%s%s%s%s%s%s",
                        CHtiangan[tiancnt0], CHdizhi[dicnt0],
                        CHmiscchar[16], cmonname, CHmiscchar[19],
                        CHtiangan[tiancnt], CHdizhi[dicnt],
                        CHmiscchar[16], cmonname1));
                }
            } else {
                // Single lunar month in January, but uses previous year
                if (pmode == 0 && nEncoding == 'a') {
                    monthhead.append(String.format("%s %d (Year %s%s, Month %s%d%c S%d)",
                        MONTH_NAMES[month - 1], year,
                        TIANGAN[tiancnt0], DIZHI[dicnt0],
                        (leap == 'R' ? "R" : ""), cmonth, (ndayslm == 30) ? 'D' : 'X', nstartlm));
                } else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
                    monthhead.append(String.format("%s %d%s%s%s%s%s%s",
                        MONTH_NAMES[month - 1], year, sp, sp,
                        CHtiangan[tiancnt0], CHdizhi[dicnt0],
                        CHmiscchar[16], cmonname));
                } else if (pmode == 3) {
                    monthhead.append(String.format("%s%s%s%s",
                        CHtiangan[tiancnt0], CHdizhi[dicnt0],
                        CHmiscchar[16], cmonname));
                }
            }
        } else {
            // Standard month header
            if (moncnt < vmoons.size() - 1 && vmoons.get(moncnt + 1) < jdnext) {
                // Two lunar months in one Gregorian month
                int[] monthInfo1 = monthNumber(vmonth.get(moncnt + 1));
                int cmonth1 = monthInfo1[0];
                char leap1 = (char)monthInfo1[1];
                int nstartlm1 = (int)(vmoons.get(moncnt + 1) - jdcnt + 1);
                int ndayslm1;
                if (moncnt < vmoons.size() - 2)
                    ndayslm1 = (int)(vmoons.get(moncnt + 2) - vmoons.get(moncnt + 1));
                else
                    ndayslm1 = (int)(nextnew - vmoons.get(vmoons.size() - 1));

                if (pmode == 0 && nEncoding == 'a') {
                    monthhead.append(String.format(
                        "%s %d (Year %s%s, Month %s%d%c S%d, %s%d%c S%d)",
                        MONTH_NAMES[month - 1], year,
                        TIANGAN[tiancnt], DIZHI[dicnt],
                        (leap == 'R' ? "R" : ""), cmonth, (ndayslm == 30) ? 'D' : 'X', nstartlm,
                        (leap1 == 'R' ? "R" : ""), cmonth1, (ndayslm1 == 30) ? 'D' : 'X', nstartlm1));
                } else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
                    String cmonname1 = MiscData.number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1);
                    monthhead.append(String.format("%s %d%s%s%s%s%s%s%s%s",
                        MONTH_NAMES[month - 1], year, sp, sp,
                        CHtiangan[tiancnt], CHdizhi[dicnt],
                        CHmiscchar[16], cmonname, CHmiscchar[19], cmonname1));
                } else if (pmode == 3) {
                    String cmonname1 = MiscData.number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1);
                    monthhead.append(String.format("%s%s%s%s%s%s",
                        CHtiangan[tiancnt], CHdizhi[dicnt],
                        CHmiscchar[16], cmonname, CHmiscchar[19], cmonname1));
                }
            } else if (month == 2 && vmoons.get(moncnt) >= jdnext) {
                // No new moon in February: describe the current lunar month.
                ndayslm = (int)(vmoons.get(moncnt) - vmoons.get(moncnt - 1));
                int[] currentMonth = monthNumber(vmonth.get(moncnt - 1));
                cmonth = currentMonth[0];
                leap = (char)currentMonth[1];
                if (pmode == 0 && nEncoding == 'a') {
                    monthhead.append(String.format("%s %d (Year %s%s, Month %s%d%c)",
                        MONTH_NAMES[month - 1], year, TIANGAN[tiancnt], DIZHI[dicnt],
                        (leap == 'R' ? "R" : ""), cmonth, (ndayslm == 30) ? 'D' : 'X'));
                } else if (pmode != 2) {
                    cmonname = MiscData.number2MonthCH(vmonth.get(moncnt - 1), nstartlm, ndayslm);
                    int end = cmonname.indexOf(CHmiscchar[14]) + 2 * nCHchars;
                    cmonname = cmonname.substring(0, end);
                    if (pmode != 3)
                        monthhead.append(MONTH_NAMES[month - 1]).append(' ').append(year).append(sp).append(sp);
                    monthhead.append(CHtiangan[tiancnt]).append(CHdizhi[dicnt])
                        .append(CHmiscchar[16]).append(cmonname);
                }
            } else {
                // Single lunar month
                if (pmode == 0 && nEncoding == 'a') {
                    monthhead.append(String.format("%s %d (Year %s%s, Month %s%d%c S%d)",
                        MONTH_NAMES[month - 1], year, TIANGAN[tiancnt], DIZHI[dicnt],
                        (leap == 'R' ? "R" : ""), cmonth, (ndayslm == 30) ? 'D' : 'X', nstartlm));
                } else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
                    monthhead.append(String.format("%s %d%s%s%s%s%s%s",
                        MONTH_NAMES[month - 1], year, sp, sp,
                        CHtiangan[tiancnt], CHdizhi[dicnt],
                        CHmiscchar[16], cmonname));
                } else if (pmode == 3) {
                    monthhead.append(String.format("%s%s%s%s",
                        CHtiangan[tiancnt], CHdizhi[dicnt],
                        CHmiscchar[16], cmonname));
                }
            }
        }

        // Print month header
        if (pmode == 0) {
            int nHeadLen = monthhead.length();
            if (nEncoding != 'a') {
                for (int i = 0; i < monthhead.length(); i++)
                    if (monthhead.charAt(i) >= 0x80)
                        nHeadLen++;
            }
            int nmove = (68 - nHeadLen) / 2;
            if (nmove < 0)
                nmove = 0;
            for (int i = 0; i < nmove; i++)
                System.out.print(" ");
            System.out.println(monthhead);
        } else if (pmode == 1) {
            System.out.printf("<section class=\"month\" data-month=\"%d\" aria-labelledby=\"month-%d\">\n", month, month);
            System.out.printf("<h2 class=\"sr-only\" id=\"month-%d\">%s</h2>\n", month, monthhead);
            System.out.printf("<table aria-labelledby=\"month-%d\">\n", month);
        } else if (pmode == 3) {
            System.out.printf("<ccal:month value=\"%d\" name=\"%s\" cname=\"%s\">\n",
                month, MONTH_NAMES[month - 1], monthhead);
        } else if (pmode == 2 && bSingle) {
            monthhead = new StringBuilder(postScriptMonthHeader(year, month, cyear, moncnt,
                jdnext, nstartlm, ndayslm, nextnew, vmoons, vmonth));
            PSMonth.printHeaderMonthPS(monthhead.toString(), month, true, bIsSim, bNeedsRun, nWeeks);
            int nlen = monthhead.indexOf(")") - monthhead.indexOf("(") - 1;
            int nheadlen = monthhead.length();
            int nmove;
            if (nheadlen - nlen - 66 > 56)
                nmove = (4200 - 78 * (nlen + (nheadlen - nlen - 86) / 2 + 3)) / 2;
            else if (nheadlen - nlen - 66 > 30)
                nmove = (4200 - 78 * (nlen + (nheadlen - nlen - 66) / 2 + 3)) / 2;
            else
                nmove = (4200 - 78 * (nlen + (nheadlen - nlen - 46) / 2 + 2)) / 2;
            System.out.printf("%d 2475 m\n%s\n", Math.max(0, nmove), monthhead);
        } else if (pmode == 2) {
            System.out.printf("%% %s %d\n", MONTH_NAMES[month - 1], year);
            int posx = (month - 1) % 4 * 140;
            int posy = 3450 - (month - 1) / 4 * 1150 - 173;
            System.out.printf("%d lpts %d m gsave ct\n", posx, posy);
            System.out.printf("%% Month number\n%d lpts %d m gsave\n100 1 SF 1 0.9 1 K",
                ((month < 10) ? 35 : 10), -750);
            System.out.printf(" (%d) S grestore\n", month);
        }

        // Print day of week headers
        if (pmode == 0) {
            for (int i = 0; i < 7; i++) {
                if (nEncoding == 'a')
                    System.out.printf("%-10s", daynamesCH[i]);
                else
                    System.out.printf("%s   ", daynamesCH[i]);
            }
            System.out.println();
        } else if (pmode == 1) {
            System.out.println("<thead><tr>");
            for (int i = 0; i < 7; i++)
                System.out.printf("<th scope=\"col\" aria-label=\"%s\">%s</th>\n",
                    DAY_NAMES[i], i == 0 ? CHmiscchar[15] : CHmiscchar[i]);
            System.out.println("</tr></thead><tbody>");
        } else if (pmode == 2 && bSingle) {
            System.out.println("% Day names\n14 1 SF\n/fsc 15 def");
            for (int i = 0; i < 7; i++) {
                String dayshort = DAY_NAMES[i].substring(0, 3);
                if (i == 0)
                    System.out.printf("gsave 1 0 0 K\n100 2270 m (%s) S\n400 2270 m " +
                        "gsave ptc %s grestore\ngrestore\n", dayshort, PSMonth.getPSBigMChar(15));
                else if (i == 6)
                    System.out.printf("gsave 0 0.8 0 K\n3700 2270 m (%s) S\n4000 2270 m " +
                        "gsave ptc %s grestore\ngrestore\n", dayshort, PSMonth.getPSBigMChar(i));
                else {
                    int posx = i * 600 + 100;
                    System.out.printf("%d 2270 m (%s) S\n%d 2270 m gsave ptc %s grestore\n",
                        posx, dayshort, (posx + 300), PSMonth.getPSBigMChar(i));
                }
            }
            System.out.println("% Days\n17 1 SF\n/fsc 7.5 def");
        } else if (pmode == 2) {
            System.out.println("% Week heading\n0 0 m Ymh");
            System.out.println("% Days\n9 0 SF");
        }

        // Print calendar days (at most 6 weeks)
        for (int w = 0; w < nWeeks; w++) {
            if (pmode == 1) {
                System.out.println("<tr>");
            }
            if (pmode == 3) {
                System.out.println("<ccal:week>");
            }

            for (int i = 0; i < 7; i++) {
                if (pmode == 1) {
                    System.out.print("<td>");
                }
                if (pmode == 3) {
                    System.out.print("<ccal:day ");
                }

                if (dcnt > realDaysInMonth) {
                    if (pmode == 1) {
                        System.out.println("&#160;</td>");
                        continue;
                    }
                    if (pmode == 3) {
                        System.out.println("/>");
                        continue;
                    }
                    break;
                }

                if (w == 0) {
                    if (i < dofw) {
                        if (pmode == 0)
                            System.out.printf("%10s", " ");
                        else if (pmode == 1) {
                            System.out.println("&#160;</td>");
                        } else if (pmode == 3) {
                            System.out.println("/>");
                        }
                        continue;
                    }
                }

                // Check if this is the start of a lunar month
                if (dcnt == 1 || ldcnt == 1) {
                    int mcnt = moncnt;
                    if (ldcnt != 1)
                        mcnt--;
                    if (mcnt >= 0) {
                        int[] mi = monthNumber(vmonth.get(mcnt));
                        cmonth = mi[0];
                        leap = (char)mi[1];
                    } else {
                        int[] mi = monthNumber(lastmon);
                        cmonth = mi[0];
                        leap = (char)mi[1];
                    }
                    if (nEncoding != 'a') {
                        if (mcnt >= 0)
                            cmonname = MiscData.number2MonthCH(vmonth.get(mcnt), 1, 30);
                        else
                            cmonname = MiscData.number2MonthCH(lastmon, 1, 30);
                        // Extract just the month name without the size/start info
                        int idx = cmonname.indexOf(CHmiscchar[14]);
                        if (idx >= 0)
                            cmonname = cmonname.substring(0, idx + CHmiscchar[14].length());
                    }
                }

                // Highlight today (ASCII mode only on non-Windows)
                boolean isToday = (todayYear == year && todayMonth == month && todayDay == dcnt);

                // Print the day number
                if (pmode == 0) {
                    System.out.printf("%2d", dcnt);
                } else if (pmode == 1) {
                    System.out.printf("<span class=\"day\">%d</span><span class=\"lunar\">", dcnt);
                } else if (pmode == 3) {
                    System.out.printf("value=\"%d\" cmonth=\"%d\" leap=\"%s\" cdate=\"%d\" ",
                        dcnt, cmonth, (leap == 'R' ? "R" : ""), ldcnt);
                } else if (bSingle) {
                    int posx = i * 600 + 50;
                    int posy = nWeeks == 5 ? 1800 - w * 450 + 295 : 1875 - w * 375 + 220;
                    if (i == 0)
                        System.out.println("gsave 1 0 0 K");
                    else if (i == 6)
                        System.out.println("gsave 0 0.8 0 K");
                    System.out.printf("%d %d m (%2d) S\ngsave 8 0 SF ", posx, posy, dcnt);
                    posx += 170;
                    if (dcnt == 1 || ldcnt == 1)
                        System.out.printf("%d %d m (%d%s.%d) S grestore\n%d %d m",
                            posx + 5, posy + 56, cmonth, leap == 'R' ? "R" : "", ldcnt, posx, posy - 2);
                    else
                        System.out.printf("%d %d m (%2d) S grestore\n%d %d m",
                            posx + 5, posy + 56, ldcnt, posx, posy - 2);
                    System.out.print(" gsave ptc");
                } else {
                    int posx = i * 19;
                    int posy = -w * 19 - 14;
                    if (i == 0)
                        System.out.println("gsave 1 0 0 K");
                    else if (i == 6)
                        System.out.println("gsave 0 0.8 0 K");
                    System.out.printf("%d %d moveto (%2d) S\n", posx, posy, dcnt);
                    System.out.printf("%d %d moveto gsave ptc", posx, posy - 7);
                }

                // Print lunar date or solar term
                String cdayname = (pmode == 3) ? MiscData.number2DayCH(ldcnt) : "";
                if (!sameday && (termcnt >= vterms.size() || jdcnt != vterms.get(termcnt)) &&
                    (moncnt >= vmoons.size() || jdcnt != vmoons.get(moncnt))) {
                    // Regular lunar date
                    if (pmode == 0) {
                        if (nEncoding == 'a')
                            System.out.printf(" [%2d]   ", ldcnt);
                        else {
                            cdayname = MiscData.number2DayCH(ldcnt);
                            System.out.printf(" %s   ", cdayname);
                        }
                    } else if (pmode == 1) {
                        if (dcnt == 1)
                            System.out.print(cmonname);
                        else
                            System.out.print(" ");
                        cdayname = MiscData.number2DayCH(ldcnt);
                        System.out.print(cdayname);

                        System.out.println("</span></td>");
                    } else if (pmode == 2) {
                        if (bSingle) {
                            if (dcnt == 1) {
                                cmonname = PSMonth.number2MonthPS(moncnt > 0 ? vmonth.get(moncnt - 1) : lastmon,
                                    1, 30, true);
                                System.out.print(" " + cmonname);
                            } else
                                System.out.print(" ");
                            System.out.printf("%s\n", PSMonth.number2DayPS(ldcnt));
                            if (i == 0 || i == 6)
                                System.out.println("grestore");
                            System.out.println("grestore");
                        } else
                            printPostScriptDay(PSMonth.number2DayPS(ldcnt), i, false);
                    }
                } else if (sameday) {
                    // Lunar month starts on a solar term day
                    if (pmode == 0) {
                        if (nEncoding == 'a')
                            printMonthNumber(vmonth.get(moncnt++));
                        else {
                            cmonname = MiscData.number2MonthCH(vmonth.get(moncnt++), 1, 30);
                            int idx = cmonname.indexOf(CHmiscchar[14]);
                            if (idx >= 0)
                                cmonname = cmonname.substring(0, idx + CHmiscchar[14].length());
                            if (cmonname.length() <= 3 * nCHchars)
                                System.out.print(" ");
                            System.out.print(cmonname);
                            if (cmonname.length() == 2 * nCHchars)
                                System.out.print("   ");
                            if (cmonname.length() == 3 * nCHchars)
                                System.out.print(" ");
                        }
                    } else if (pmode == 1) {
                        cdayname = MiscData.number2DayCH(ldcnt);
                        System.out.print(" " + cdayname);

                        System.out.println("</span></td>");
                    }
                    if (pmode == 2)
                        printPostScriptDay(PSMonth.number2DayPS(ldcnt), i, true);
                    sameday = false;
                } else if (termcnt < vterms.size() && jdcnt == vterms.get(termcnt)) {
                    // Solar term day
                    if (moncnt < vmoons.size() && jdcnt == vmoons.get(moncnt))
                        sameday = true;
                    if (pmode == 0) {
                        if (nEncoding == 'a')
                            System.out.printf(" [%s]   ", JIEQI[termcnt++]);
                        else
                            System.out.printf(" %s   ", CHjieqi[termcnt++]);
                    } else if (pmode == 1) {
                        if (sameday)
                            System.out.print(" " + cmonname);
                        else
                            System.out.print(" ");
                        System.out.print(CHjieqi[termcnt++]);

                        System.out.println("</span></td>");
                    } else if (pmode == 3) {
                        cdayname = CHjieqi[termcnt++];
                    } else if (pmode == 2) {
                        if (sameday) {
                            cmonname = PSMonth.number2MonthPS(vmonth.get(moncnt++), 1, 30, true);
                            int nlen = cmonname.length();
                            if (!bSingle)
                                System.out.print(" grestore -6 0 rmoveto gsave ptc");
                            if (!bSingle && nlen > 8)
                                System.out.printf(Locale.ROOT, " gsave %f 1 scale %s", 16.0 / (nlen + 8.0), cmonname);
                            else
                                System.out.print(" " + cmonname);
                        } else
                            System.out.print(" ");
                        System.out.printf("%s\n", PSMonth.getPSJieQi(termcnt++));
                        if (sameday && !bSingle && cmonname.length() > 8)
                            System.out.println("grestore");
                        if (i == 0 || i == 6)
                            System.out.println("grestore");
                        System.out.println("grestore");
                    }
                } else {
                    // Start of lunar month
                    if (pmode == 0) {
                        if (nEncoding == 'a')
                            printMonthNumber(vmonth.get(moncnt++));
                        else {
                            cmonname = MiscData.number2MonthCH(vmonth.get(moncnt++), 1, 30);
                            int idx = cmonname.indexOf(CHmiscchar[14]);
                            if (idx >= 0)
                                cmonname = cmonname.substring(0, idx + CHmiscchar[14].length());
                            if (cmonname.length() <= 3 * nCHchars)
                                System.out.print(" ");
                            System.out.print(cmonname);
                            if (cmonname.length() == 2 * nCHchars)
                                System.out.print("   ");
                            if (cmonname.length() == 3 * nCHchars)
                                System.out.print(" ");
                        }
                    } else if (pmode == 1 || pmode == 3) {
                        cmonname = MiscData.number2MonthCH(vmonth.get(moncnt++), 1, 30);
                        int idx = cmonname.indexOf(CHmiscchar[14]);
                        if (idx >= 0)
                            cmonname = cmonname.substring(0, idx + CHmiscchar[14].length());
                        if (pmode == 1) {
                            System.out.print(" " + cmonname);

                            System.out.println("</span></td>");
                        }
                    } else if (pmode == 2) {
                        cmonname = PSMonth.number2MonthPS(vmonth.get(moncnt++), 1, 30, true);
                        printPostScriptDay(cmonname, i, bSingle);
                    }
                }

                if (pmode == 3)
                    System.out.printf("cmonthname=\"%s\" cdatename=\"%s\" />\n", cmonname, cdayname);

                dcnt++;
                jdcnt++;
                if (moncnt < vmoons.size() && jdcnt == vmoons.get(moncnt))
                    ldcnt = 1;
                else
                    ldcnt++;
            }

            if (pmode == 0) {
                System.out.println();
            } else if (pmode == 1) {
                System.out.println("</tr>");
            } else if (pmode == 3) {
                System.out.println("</ccal:week>");
            }
        }

        if (pmode == 1) {
            System.out.println("</tbody></table></section>");
        }
        if (pmode == 2 && !bSingle) {
            System.out.println("grestore");
        } else if (pmode == 3) {
            System.out.println("</ccal:month>");
        }
    }

    /**
     * Process command-line arguments
     */
    public static class ProcessArgResult {
        public short year;
        public short month;
        public int pmode;
        public boolean bSingle;
        public int nEncoding;
        public boolean success;

        public ProcessArgResult(short year, short month, int pmode, boolean bSingle,
                               int nEncoding, boolean success) {
            this.year = year;
            this.month = month;
            this.pmode = pmode;
            this.bSingle = bSingle;
            this.nEncoding = nEncoding;
            this.success = success;
        }
    }

    public static ProcessArgResult processArg(String[] args) {
        MiscData.setSimplified(true);
        Calendar cal = Calendar.getInstance();
        short year = (short)cal.get(Calendar.YEAR);
        short month = (short)(cal.get(Calendar.MONTH) + 1);
        int pmode = 0;
        boolean bSingle = true;
        int nEncoding = 'a';
        boolean bIsUTF8 = false;

        if (args.length == 0)
            return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, true);

        if (args.length > 5)
            return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, false);

        int nParam = 0;
        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                if (nParam == 0) {
                    try {
                        year = Short.parseShort(args[i]);
                        bSingle = false;
                        nParam++;
                    } catch (NumberFormatException e) {
                        System.out.println("ccal: Invalid year value.");
                        return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, false);
                    }
                } else if (nParam == 1) {
                    month = year;
                    try {
                        year = Short.parseShort(args[i]);
                        bSingle = true;
                        nParam++;
                    } catch (NumberFormatException e) {
                        System.out.println("ccal: Invalid year value.");
                        return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, false);
                    }
                } else {
                    System.out.println("ccal: Too many parameters.");
                    return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, false);
                }
            } else if (args[i].equals("-x")) {
                pmode = 3;
            } else if (args[i].equals("-p")) {
                pmode = 2;
            } else if (args[i].equals("-t")) {
                pmode = 1;
            } else if (args[i].equals("-g")) {
                nEncoding = 'g';
            } else if (args[i].equals("-b")) {
                nEncoding = 'b';
            } else if (args[i].equals("-u")) {
                bIsUTF8 = true;
            } else {
                System.out.println("ccal: Unrecognized option.");
                return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, false);
            }
        }

        MiscData.setSimplified(nEncoding != 'b');
        if (pmode != 2) {
            if (bIsUTF8 || pmode == 3) {
                nEncoding = 'u';
            }
        }
        if (pmode != 0) {
            if (nEncoding == 'a')
                nEncoding = 'g';
        }

        return new ProcessArgResult(year, month, pmode, bSingle, nEncoding, true);
    }

    /**
     * Main program entry point
     */
    public static void main(String[] args) {
        ProcessArgResult argResult = processArg(args);

        if (!argResult.success) {
            System.out.println("ccal version " + VERSION +
                ": Displays Chinese calendar (Gregorian with Chinese dates).");
            System.out.println("Usage: ccal [-t|-p|-x] [-g|-b] [-u] [[<month>] <year>].");
            System.out.println("\t-t:\tGenerates HTML table output.");
            System.out.println("\t-p:\tGenerates encapsulated PostScript output.");
            System.out.println("\t-x:\tGenerates XML output.");
            System.out.println("\t-g:\tGenerates simplified Chinese output.");
            System.out.println("\t-b:\tGenerates traditional Chinese output.");
            System.out.println("\t-u:\tUses UTF-8 rather than GB or Big5 for Chinese output.");
            System.exit(1);
        }

        short year = argResult.year;
        short month = argResult.month;
        int pmode = argResult.pmode;
        boolean bSingle = argResult.bSingle;
        int nEncoding = argResult.nEncoding;

        // The CLI encoding must also apply when stdout is redirected on Windows.
        String charset = nEncoding == 'g' ? "GB2312" : nEncoding == 'b' ? "Big5" : "UTF-8";
        System.out.flush();
        try {
            System.setOut(new PrintStream(System.out, true, charset));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Required calendar output encoding is unavailable: " + charset, e);
        }

        if (month < 1 || month > 12) {
            System.out.println("ccal: Invalid month value: month 1-12.");
            System.exit(1);
        }
        if (year < 1645 || year > 7000) {
            System.out.println("ccal: Invalid year value: year 1645-7000.");
            System.exit(1);
        }

        daysInMonth[1] = isLeapYear(year) ? 29 : 28;

        // Compute lunar year data
        LunarYear.LunarYearResult lunar = LunarYear.lunaryear(year);
        ArrayList<Double> vterms = lunar.vterms;
        double lastnew = lunar.lastnew;
        double lastmon = lunar.lastmon;
        ArrayList<Double> vmoons = lunar.vmoons;
        ArrayList<Double> vmonth = lunar.vmonth;
        double nextnew = lunar.nextnew;
        double lmon = lunar.leapMonth;

        boolean bIsSim = (nEncoding == 'g');
        String titlestr;

        if (pmode == 3) {
            System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            System.out.printf("<ccal:year value=\"%d\" xmlns:ccal=\"http://ccal.chinesebay.com/ccal/\">\n",
                year);
        }

        if (bSingle) {
            titlestr = String.format("%s %d", MONTH_NAMES[month - 1], year);
            if (pmode == 1)
                MiscData.printHeaderHTML(titlestr, month, year, nEncoding);
            else if (pmode == 2)
                PSMonth.printHeaderPS(titlestr, bIsSim, false);

            printMonth(year, month, vterms, lastnew, lastmon, vmoons, vmonth, nextnew,
                      pmode, bSingle, nEncoding,
                      ((short)(lmon) == month || (short)(lmon + 0.9) == month));
        } else {
            titlestr = String.format("Year %d", year);
            if (pmode == 1)
                MiscData.printHeaderHTML(titlestr, 0, year, nEncoding);
            else if (pmode == 2)
                PSMonth.printHeaderPS(titlestr, bIsSim, (lmon != 0.0));

            for (short i = 1; i <= 12; i++)
                printMonth(year, i, vterms, lastnew, lastmon, vmoons, vmonth, nextnew,
                          pmode, bSingle, nEncoding, false);
        }

        if (pmode == 1)
            MiscData.printClosingHTML();
        else if (pmode == 2)
            PSMonth.printClosingPS();
        else if (pmode == 3)
            System.out.println("</ccal:year>");
    }
}
