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

    public static void PrintHeaderHTML(String titlestr, int month, int year, int nEncoding) {
        System.out.print("<html>\n<head>\n<meta http-equiv=\"Content-Type\" ");
        if (nEncoding == 'u')
            System.out.print("content=\"text/html; charset=utf-8\">\n");
        else if (nEncoding == 'g')
            System.out.print("content=\"text/html; charset=gb2312\">\n");
        else
            System.out.print("content=\"text/html; charset=big5\">\n");
        System.out.printf("<meta name=\"GENERATOR\" content=\"ccal-%s by Zhuo Meng, http://thunder.cwru.edu/ccal/\">\n", Verstr.versionstr);
        String[][] miscchar;
        if (nEncoding == 'u')
            miscchar = U8miscchar;
        else if (nEncoding == 'g')
            miscchar = GBmiscchar;
        else
            miscchar = B5miscchar;
        if (month != 0)
            System.out.printf("<title>Chinese Calendar for %s / %d%s%d%s</title>\n", titlestr, year, miscchar[16][0], month, miscchar[14][0]);
        else
            System.out.printf("<title>Chinese Calendar for %s / %d%s</title>\n", titlestr, year, miscchar[16][0]);
        System.out.print("</head>\n<body>\n<center>\n");
        System.out.print("<table border=\"1\" cellspacing=\"1\" width=\"90%\">\n");
    }

    public static void PrintClosingHTML() {
        System.out.print("</table>\n</center>\n</body>\n</html>\n");
    }
}
