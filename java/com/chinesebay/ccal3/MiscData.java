package com.chinesebay.ccal3;

public class MiscData {

    // Direct translation of the U8 (UTF-8) arrays from htmlmonth.cpp
    public static final String[] U8_JIEQI = {
            "\u5c0f\u5bd2", /* XH - Lesser Cold */
            "\u5927\u5bd2", /* DH - Greater Cold */
            "\u7acb\u6625", /* LC - Start of Spring */
            "\u96e8\u6c34", /* YS - Rain Water */
            "\u60ca\u86f0", /* JZ - Awakening of Insects */
            "\u6625\u5206", /* CF - Spring Equinox */
            "\u6e05\u660e", /* QM - Clear and Bright */
            "\u8c37\u96e8", /* GY - Grain Rain */
            "\u7acb\u590f", /* LX - Start of Summer */
            "\u5c0f\u6ee1", /* XM - Lesser Fullness */
            "\u8292\u79cd", /* MZ - Grain in Ear */
            "\u590f\u81f3", /* XZ - Summer Solstice */
            "\u5c0f\u6691", /* XS - Lesser Heat */
            "\u5927\u6691", /* DS - Greater Heat */
            "\u7acb\u79cb", /* LQ - Start of Autumn */
            "\u5904\u6691", /* CS - End of Heat */
            "\u767d\u9732", /* BL - White Dew */
            "\u79cb\u5206", /* QF - Autumn Equinox */
            "\u5bd2\u9732", /* HL - Cold Dew */
            "\u971c\u964d", /* SJ - Frost's Descent */
            "\u7acb\u51ac", /* LD - Start of Winter */
            "\u5c0f\u96ea", /* XX - Lesser Snow */
            "\u5927\u96ea", /* DX - Greater Snow */
            "\u51ac\u81f3"  /* DZ - Winter Solstice */
    };

    public static final String[] U8_MISC_CHAR = {
            "\u521d",   /* 0: Chu */
            "\u4e00",   /* 1: Yi */
            "\u4e8c",   /* 2: Er */
            "\u4e09",   /* 3: San */
            "\u56db",   /* 4: Si */
            "\u4e94",   /* 5: Wu */
            "\u516d",   /* 6: Liu */
            "\u4e03",   /* 7: Qi */
            "\u516b",   /* 8: Ba */
            "\u4e5d",   /* 9: Jiu */
            "\u5341",   /* 10: Shi(Ten) */
            "\u5eff",   /* 11: Nian(Twenty) */
            "\u6b63",   /* 12: Zheng */
            "\u95f0",   /* 13: Run */
            "\u6708",   /* 14: Yue */
            "\u65e5",   /* 15: Ri */
            "\u5e74",   /* 16: Nian(Year) */
            "\u5927",   /* 17: Da */
            "\u5c0f",   /* 18: Xiao */
            "\uff0c",   /* 19: (comma) */
            "  ",     /* 20: (blank) */
            "\u59cb"    /* 21: Shi(Start) */
    };

    public static final String[] U8_TIAN_GAN = {
            "\u7532", "\u4e59", "\u4e19", "\u4e01",
            "\u620a", "\u5df1", "\u5e9a", "\u8f9b",
            "\u58ec", "\u7678"
    };

    public static final String[] U8_DI_ZHI = {
            "\u5b50", "\u4e11", "\u5bc5", "\u536f",
            "\u8fb0", "\u5df3", "\u5348", "\u672a",
            "\u7533", "\u9149", "\u620c", "\u4ea5"
    };

    private static final String[] B5_JIEQI = U8_JIEQI.clone();
    private static final String[] B5_MISC_CHAR = U8_MISC_CHAR.clone();
    private static final String[] GB_MISC_CHAR = U8_MISC_CHAR.clone();
    private static boolean simplified = true;

    static {
        // Character differences in the Big5 and UTF-8 tables in htmlmonth.cpp.
        B5_JIEQI[4] = "\u9a5a\u87c4";
        B5_JIEQI[7] = "\u7a40\u96e8";
        B5_JIEQI[9] = "\u5c0f\u6eff";
        B5_JIEQI[10] = "\u8292\u7a2e";
        B5_JIEQI[15] = "\u8655\u6691";
        B5_MISC_CHAR[13] = "\u958f";
        B5_MISC_CHAR[20] = "\u3000";
        GB_MISC_CHAR[20] = "\u3000";
    }

    public static void setSimplified(boolean value) {
        simplified = value;
    }

    private static boolean traditional(int encoding) {
        return encoding == 'b' || (encoding == 'u' && !simplified);
    }

    public static String[] getTianGanArray(int encoding) {
        return U8_TIAN_GAN;
    }

    public static String[] getDiZhiArray(int encoding) {
        return U8_DI_ZHI;
    }

    public static String[] getJieQiArray(int encoding) {
        return traditional(encoding) ? B5_JIEQI : U8_JIEQI;
    }

    public static String[] getMiscCharArray(int encoding) {
        if (encoding == 'g')
            return GB_MISC_CHAR;
        return traditional(encoding) ? B5_MISC_CHAR : U8_MISC_CHAR;
    }

    /**
     * Corresponds to Number2MonthCH in htmlmonth.cpp
     *
     * @param month     month number with .5 indicating leap month
     * @param nstart    day of calendar month that starts this lunar month
     * @param ndays     number of days in this lunar month
     * @param nEncoding 'g' for GB, 'b' for BIG5, 'u' for UTF-8
     * @return C string containing the Chinese characters for the month.
     */
    public static String numberToMonthCH(double month, int nstart, int ndays, int nEncoding) {
        String[] miscchar = getMiscCharArray(nEncoding);
        StringBuilder monname = new StringBuilder();

        int nmonth = (int) month;
        if (month - nmonth == 0.5) { // Leap month
            monname.append(miscchar[13]);
        }
        if (nmonth > 10) {
            monname.append(miscchar[10]);
            nmonth -= 10;
        }
        if (nmonth == 1 && monname.length() == 0) { // Zheng month
            monname.append(miscchar[12]);
        } else {
            monname.append(miscchar[nmonth]);
        }
        monname.append(miscchar[14]);
        if (ndays == 30) {
            monname.append(miscchar[17]);
        } else {
            monname.append(miscchar[18]);
        }
        monname.append(nstart);
        monname.append(miscchar[15]);
        monname.append(miscchar[21]);
        return monname.toString();
    }


    /**
     * Corresponds to Number2DayCH in htmlmonth.cpp
     *
     * @param nday      day number (1 - 30)
     * @param nEncoding 'g' for GB, 'b' for BIG5, 'u' for UTF-8
     * @return C string containing the Chinese characters for the day.
     */
    public static String numberToDayCH(int nday, int nEncoding) {
        String[] miscchar = getMiscCharArray(nEncoding);
        StringBuilder dayname = new StringBuilder();

        if (nday <= 10) {
            dayname.append(miscchar[0]);
        }
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

    static String number2MonthCH(double month, int start, int days) {
        return numberToMonthCH(month, start, days, 'u');
    }

    static String number2DayCH(int day) {
        return numberToDayCH(day, 'u');
    }

    static void printHeaderHTML(String title, int month, int year, int encoding) {
        String[] miscchar = getMiscCharArray(encoding);
        String charset = encoding == 'g' ? "gb2312" : encoding == 'b' ? "big5" : "utf-8";
        System.out.println("<html>\n<head>");
        System.out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + charset + "\">");
        System.out.println("<meta name=\"GENERATOR\" content=\"ccal-2.5.3 by Zhuo Meng, http://thunder.cwru.edu/ccal/\">");
        System.out.printf("<title>Chinese Calendar for %s / %d%s", title, year, miscchar[16]);
        if (month != 0)
            System.out.printf("%d%s", month, miscchar[14]);
        System.out.println("</title>\n</head>\n<body>\n<center>");
        System.out.println("<table border=\"1\" cellspacing=\"1\" width=\"90%\">");
    }

    static void printClosingHTML() {
        System.out.println("</table>\n</center>\n</body>\n</html>");
    }
}
