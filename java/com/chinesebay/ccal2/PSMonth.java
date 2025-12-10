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
 * PSMonth.java
 *
 * STUB: This is a placeholder for PostScript calendar output.
 * The full implementation would include embedded Chinese font definitions
 * and PostScript rendering code (1100+ lines).
 *
 * For now, PostScript output is not implemented. Use HtmlMonth for calendar output.
 * To implement full PostScript support, port the font data and rendering
 * code from psmonth.cpp (includes GB, BIG5, and custom font definitions).
 */
public class PSMonth {
    /**
     * Converts month number to PostScript string representation
     * @param month Month number with .5 indicating leap month
     * @param nstart Day of calendar month that starts this lunar month
     * @param ndays Number of days in this lunar month
     * @param bYear Whether to be used in yearly or monthly calendar
     * @return PostScript string for the month
     */
    public static String number2MonthPS(double month, int nstart, int ndays, boolean bYear) {
        throw new UnsupportedOperationException("PostScript output not implemented - use HtmlMonth instead");
    }

    /**
     * Converts day number to PostScript string representation
     * @param nday Day number (1-30)
     * @return PostScript string for the day
     */
    public static String number2DayPS(int nday) {
        throw new UnsupportedOperationException("PostScript output not implemented - use HtmlMonth instead");
    }

    /**
     * Prints PostScript header
     * @param titlestr String to be included in the title
     * @param bIsSim true for simplified characters, false for traditional
     * @param bNeedsRun true if character Run is needed
     */
    public static void printHeaderPS(String titlestr, boolean bIsSim, boolean bNeedsRun) {
        throw new UnsupportedOperationException("PostScript output not implemented - use HtmlMonth instead");
    }

    /**
     * Prints PostScript closing
     */
    public static void printClosingPS() {
        throw new UnsupportedOperationException("PostScript output not implemented - use HtmlMonth instead");
    }

    /**
     * Prints PostScript header for a single month
     * @param monthhead Month header string
     * @param month Month number
     * @param bSingle Whether this is a single month
     * @param bIsSim true for simplified characters
     * @param bNeedsRun true if Run character is needed
     * @param nWeeks Number of weeks in the month
     */
    public static void printHeaderMonthPS(String monthhead, int month, boolean bSingle,
                                         boolean bIsSim, boolean bNeedsRun, int nWeeks) {
        throw new UnsupportedOperationException("PostScript output not implemented - use HtmlMonth instead");
    }

    /**
     * Gets PostScript big miscellaneous character
     * @param index Index of the character
     * @return PostScript string for the character
     */
    public static String getPSBigMChar(int index) {
        throw new UnsupportedOperationException("PostScript output not implemented - use HtmlMonth instead");
    }
}
