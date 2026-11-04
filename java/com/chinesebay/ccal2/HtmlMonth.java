package com.chinesebay.ccal2;

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

/**
 * HtmlMonth.java
 * For generating calendar output in HTML format with Chinese characters
 */
public class HtmlMonth {
    // Solar terms in UTF-8
    private static String[] jieqi = {
        "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
        "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
        "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
    };

    // Simplified vs Traditional character differences
    private static String[] jieqiDiffSim = {"惊", "蛰", "谷", "满", "种", "处", "闰"};
    private static String[] jieqiDiffTra = {"驚", "蟄", "穀", "滿", "種", "處", "閏"};

    // Misc characters
    private static String[] miscchar = {
        "初", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
        "廿", "正", "闰", "月", "日", "年", "大", "小", "，", "  ", "始"
    };

    // Heavenly Stems
    private static String[] tiangan = {
        "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"
    };

    // Earthly Branches
    private static String[] dizhi = {
        "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"
    };

    private static boolean isSimplified = true;

    /**
     * Sets whether to use simplified or traditional characters
     * @param bIsSim true for simplified, false for traditional
     */
    public static void setSimplified(boolean bIsSim) {
        isSimplified = bIsSim;
        if (bIsSim) {
            jieqi[4] = jieqiDiffSim[0] + jieqiDiffSim[1];  // 惊蛰
            jieqi[7] = jieqiDiffSim[2] + "雨";              // 谷雨
            jieqi[9] = "小" + jieqiDiffSim[3];             // 小满
            jieqi[10] = "芒" + jieqiDiffSim[4];            // 芒种
            jieqi[15] = jieqiDiffSim[5] + "暑";            // 处暑
            miscchar[13] = jieqiDiffSim[6];                // 闰
        } else {
            jieqi[4] = jieqiDiffTra[0] + jieqiDiffTra[1];  // 驚蟄
            jieqi[7] = jieqiDiffTra[2] + "雨";              // 穀雨
            jieqi[9] = "小" + jieqiDiffTra[3];             // 小滿
            jieqi[10] = "芒" + jieqiDiffTra[4];            // 芒種
            jieqi[15] = jieqiDiffTra[5] + "暑";            // 處暑
            miscchar[13] = jieqiDiffTra[6];                // 閏
        }
    }

    /**
     * Converts month number to Chinese characters
     * @param month Month number with .5 indicating leap month
     * @param nstart Day of calendar month that starts this lunar month
     * @param ndays Number of days in this lunar month
     * @return Chinese string for the month
     */
    public static String number2MonthCH(double month, int nstart, int ndays) {
        StringBuilder monname = new StringBuilder();
        int nmonth = (int)month;

        if (month - nmonth == 0.5) // Leap month
            monname.append(miscchar[13]);

        if (nmonth > 10) {
            monname.append(miscchar[10]);
            nmonth -= 10;
        }

        if (nmonth == 1 && monname.length() == 0) // Zheng month
            monname.append(miscchar[12]);
        else
            monname.append(miscchar[nmonth]);

        monname.append(miscchar[14]);

        if (ndays == 30)
            monname.append(miscchar[17]);
        else
            monname.append(miscchar[18]);

        monname.append(nstart);
        monname.append(miscchar[15]);
        monname.append(miscchar[21]);

        return monname.toString();
    }

    /**
     * Converts day number to Chinese characters
     * @param nday Day number (1 - 30)
     * @return Chinese string for the day
     */
    public static String number2DayCH(int nday) {
        StringBuilder dayname = new StringBuilder();

        if (nday <= 10)
            dayname.append(miscchar[0]);
        if (nday > 10 && nday < 20) {
            dayname.append(miscchar[10]);
            nday -= 10;
        }
        if (nday == 20) {
            dayname.append(miscchar[2]);
            nday -= 10;
        }
        if (nday > 20 && nday < 30) {
            dayname.append(miscchar[11]);
            nday -= 20;
        }
        if (nday == 30) {
            dayname.append(miscchar[3]);
            nday -= 20;
        }
        dayname.append(miscchar[nday]);

        return dayname.toString();
    }

    /**
     * Prints HTML header
     * @param titlestr String to be included in the English part of title
     * @param month Gregorian month number (1-12), 0 for whole year
     * @param year Gregorian year number
     */
    public static void printHeaderHTML(String titlestr, int month, int year) {
        printHeaderHTML(titlestr, month, year, 'u');
    }

    public static void printHeaderHTML(String titlestr, int month, int year, int encoding) {
        System.out.println("<html>");
        System.out.println("<head>");
        String charset = encoding == 'g' ? "gb2312" : encoding == 'b' ? "big5" : "utf-8";
        System.out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + charset + "\">");
        System.out.println("<meta name=\"GENERATOR\" content=\"ccal-2.5.3 by Zhuo Meng, http://thunder.cwru.edu/ccal/\">");

        if (month != 0)
            System.out.println("<title>Chinese Calendar for " + titlestr + " / " + year + miscchar[16] + month + miscchar[14] + "</title>");
        else
            System.out.println("<title>Chinese Calendar for " + titlestr + " / " + year + miscchar[16] + "</title>");

        System.out.println("</head>");
        System.out.println("<body>");
        System.out.println("<center>");
        System.out.println("<table border=\"1\" cellspacing=\"1\" width=\"90%\">");
    }

    /**
     * Prints HTML closing tags
     */
    public static void printClosingHTML() {
        System.out.println("</table>");
        System.out.println("</center>");
        System.out.println("</body>");
        System.out.println("</html>");
    }

    /**
     * Gets solar term name
     * @param index Solar term index (0-23)
     * @return Solar term name
     */
    public static String getJieQi(int index) {
        if (index >= 0 && index < 24)
            return jieqi[index];
        return "";
    }

    /**
     * Gets Tiangan (Heavenly Stem) character
     * @param index Index (0-9)
     * @return Tiangan character
     */
    public static String getTianGan(int index) {
        if (index >= 0 && index < 10)
            return tiangan[index];
        return "";
    }

    /**
     * Gets Dizhi (Earthly Branch) character
     * @param index Index (0-11)
     * @return Dizhi character
     */
    public static String getDiZhi(int index) {
        if (index >= 0 && index < 12)
            return dizhi[index];
        return "";
    }

    /**
     * Gets array of Tiangan characters based on encoding
     * @param encoding Encoding type ('a', 'g', 'b', 'u')
     * @return Array of Tiangan strings
     */
    public static String[] getTianGanArray(int encoding) {
        // For simplicity, UTF-8 encoding is used for all
        return tiangan;
    }

    /**
     * Gets array of Dizhi characters based on encoding
     * @param encoding Encoding type ('a', 'g', 'b', 'u')
     * @return Array of Dizhi strings
     */
    public static String[] getDiZhiArray(int encoding) {
        // For simplicity, UTF-8 encoding is used for all
        return dizhi;
    }

    /**
     * Gets array of miscellaneous characters based on encoding
     * @param encoding Encoding type ('a', 'g', 'b', 'u')
     * @return Array of miscellaneous character strings
     */
    public static String[] getMiscCharArray(int encoding) {
        // For simplicity, UTF-8 encoding is used for all
        return miscchar;
    }

    /**
     * Gets array of Jieqi (solar term) names based on encoding
     * @param encoding Encoding type ('a', 'g', 'b', 'u')
     * @return Array of Jieqi strings
     */
    public static String[] getJieQiArray(int encoding) {
        // For simplicity, UTF-8 encoding is used for all
        return jieqi;
    }
}
