package com.chinesebay.ccal;

import static com.chinesebay.ccal.CcalData.*;

public class HtmlMonth {
    public static void SetU8Characters(boolean bIsSim) {
        if (bIsSim) {
            // C++: strncpy(&(U8jieqi[4][0]), U8DiffSim[0], 3); & strncpy(&(U8jieqi[4][3]), U8DiffSim[1], 3);
            // Result: U8jieqi[4] becomes U8DiffSim[0] + U8DiffSim[1]
            CcalData.U8jieqi[4][0] = CcalData.U8DiffSim[0][0] + CcalData.U8DiffSim[1][0];

            // C++: strncpy(&(U8jieqi[7][0]), U8DiffSim[2], 3);
            // Result: U8jieqi[7] becomes U8DiffSim[2]
            CcalData.U8jieqi[7][0] = CcalData.U8DiffSim[2][0];

            // C++: strncpy(&(U8jieqi[9][3]), U8DiffSim[3], 3);
            // Result: U8jieqi[9]'s second char replaced by U8DiffSim[3]
            CcalData.U8jieqi[9][0] = CcalData.U8jieqi[9][0].substring(0, 1) + CcalData.U8DiffSim[3][0];

            // C++: strncpy(&(U8jieqi[10][3]), U8DiffSim[4], 3);
            // Result: U8jieqi[10]'s second char replaced by U8DiffSim[4]
            CcalData.U8jieqi[10][0] = CcalData.U8jieqi[10][0].substring(0, 1) + CcalData.U8DiffSim[4][0];

            // C++: strncpy(&(U8jieqi[15][0]), U8DiffSim[5], 3);
            // Result: U8jieqi[15] becomes U8DiffSim[5]
            CcalData.U8jieqi[15][0] = CcalData.U8DiffSim[5][0];

            // C++: strncpy(&(U8miscchar[13][0]), U8DiffSim[6], 3);
            // Result: U8miscchar[13] becomes U8DiffSim[6]
            CcalData.U8miscchar[13][0] = CcalData.U8DiffSim[6][0];
        } else {
            // Corresponding changes for U8DiffTra
            CcalData.U8jieqi[4][0] = CcalData.U8DiffTra[0][0] + CcalData.U8DiffTra[1][0];
            CcalData.U8jieqi[7][0] = CcalData.U8DiffTra[2][0];
            CcalData.U8jieqi[9][0] = CcalData.U8jieqi[9][0].substring(0, 1) + CcalData.U8DiffTra[3][0];
            CcalData.U8jieqi[10][0] = CcalData.U8jieqi[10][0].substring(0, 1) + CcalData.U8DiffTra[4][0];
            CcalData.U8jieqi[15][0] = CcalData.U8DiffTra[5][0];
            CcalData.U8miscchar[13][0] = CcalData.U8DiffTra[6][0];
        }
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
        System.out.print("<table border=\"1\" cellspacing=\"1\" width=\"90%%\">\n");
    }

    public static void PrintClosingHTML() {
        System.out.print("</table>\n</center>\n</body>\n</html>\n");
    }
}
