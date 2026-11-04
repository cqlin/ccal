package com.chinesebay.ccal;

import static com.chinesebay.ccal.CcalData.*;

public class HtmlMonth {
    public static void SetU8Characters(boolean bIsSim) {
        String[][] diff = bIsSim ? U8DiffSim : U8DiffTra;
        U8jieqi[4][0] = diff[0][0] + diff[1][0];
        U8jieqi[7][0] = diff[2][0] + "\u96e8";
        U8jieqi[9][0] = "\u5c0f" + diff[3][0];
        U8jieqi[10][0] = "\u8292" + diff[4][0];
        U8jieqi[15][0] = diff[5][0] + "\u6691";
        U8miscchar[13][0] = diff[6][0];
    }

    public static void Number2MonthCH(double month, int nstart, int ndays, int nEncoding, char[] monname) {
        String[][] miscchar;
        if (nEncoding == 'u')
            miscchar = U8miscchar;
        else if (nEncoding == 'g')
            miscchar = GBmiscchar;
        else
            miscchar = B5miscchar;

        StringBuilder sb = new StringBuilder();
        int nmonth = (int) month;
        if (month - nmonth == 0.5) /* Leap month */
            sb.append(miscchar[13][0]);
        if (nmonth > 10) {
            sb.append(miscchar[10][0]);
            nmonth -= 10;
        }
        if (nmonth == 1 && sb.length() == 0) /* Zheng month */
            sb.append(miscchar[12][0]);
        else
            sb.append(miscchar[nmonth][0]);
        sb.append(miscchar[14][0]);
        if (ndays == 30)
            sb.append(miscchar[17][0]);
        else
            sb.append(miscchar[18][0]);
        sb.append(nstart);
        sb.append(miscchar[15][0]);
        sb.append(miscchar[21][0]);

        String result = sb.toString();
        result.getChars(0, result.length(), monname, 0);
        monname[result.length()] = 0;
    }

    public static void Number2DayCH(int nday, int nEncoding, char[] dayname) {
        String[][] miscchar;
        if (nEncoding == 'u')
            miscchar = U8miscchar;
        else if (nEncoding == 'g')
            miscchar = GBmiscchar;
        else
            miscchar = B5miscchar;

        StringBuilder sb = new StringBuilder();
        if (nday <= 10)
            sb.append(miscchar[0][0]);
        if (nday > 10 && nday < 20) {
            sb.append(miscchar[10][0]);
            nday -= 10;
        }
        if (nday == 20) {
            sb.append(miscchar[2][0]);
            nday -= 10;
        }
        if (nday > 20 && nday < 30) {
            sb.append(miscchar[11][0]);
            nday -= 20;
        }
        if (nday == 30) {
            sb.append(miscchar[3][0]);
            nday -= 20;
        }
        sb.append(miscchar[nday][0]);

        String result = sb.toString();
        result.getChars(0, result.length(), dayname, 0);
        dayname[result.length()] = 0;
    }

    public static void PrintHeaderHTML(String title, int month, int year, int encoding) {
        String charset = encoding == 'g' ? "gb2312" : encoding == 'b' ? "big5" : "utf-8";
        System.out.println("<!DOCTYPE html>\n<html lang=\"zh\">\n<head>");
        System.out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + charset + "\">");
        System.out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        System.out.println("<link rel=\"stylesheet\" href=\"calendar.css\">");
        System.out.println("<title>Chinese Calendar for " + title + "</title>\n</head>\n<body>");
        System.out.println("<main class=\"sheet " + (month == 0 ? "annual" : "single-month") + "\">");
        System.out.println("<h1 class=\"year\">" + year + "</h1>\n<div class=\"calendar\">");
    }

    public static void PrintClosingHTML() {
        System.out.println("</div>\n</main>\n</body>\n</html>");
    }
}
