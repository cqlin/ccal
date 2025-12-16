package com.chinesebay.ccal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static com.chinesebay.ccal.CcalData.*;
import static com.chinesebay.ccal.HtmlMonth.*;
import static com.chinesebay.ccal.LunarYear.lunaryear;
import static com.chinesebay.ccal.Novas.julian_date;
import static com.chinesebay.ccal.PsMonth.PrintClosingPS;
import static com.chinesebay.ccal.PsMonth.PrintHeaderPS;
import static com.chinesebay.ccal.PsMonth.Number2MonthPS; // Uncommented
import static com.chinesebay.ccal.PsMonth.Number2DayPS; // Uncommented
// import static com.chinesebay.ccal.PsMonth.PSbigmchar; // Not directly imported, accessed via PsMonth.
// import static com.chinesebay.ccal.PsMonth.PStiangan; // Not directly imported, accessed via PsMonth.
// import static com.chinesebay.ccal.PsMonth.PSdizhi; // Not directly imported, accessed via PsMonth.
import static com.chinesebay.ccal.Verstr.versionstr;
public class Ccal {
	private static class ProcessArgResult {
		short year;
		short month;
		int pmode;
		boolean bSingle;
		int nEncoding;
	}
	public static void main(String[] args) {
		Calendar now = Calendar.getInstance();
		ProcessArgResult result = new ProcessArgResult();
		result.year = (short) now.get(Calendar.YEAR);
		result.month = (short) (now.get(Calendar.MONTH) + 1);
		if (!ProcessArg(args, result)) {
			System.out.printf("ccal version %s: Displays Chinese calendar (Gregorian with Chinese dates).\n",
					versionstr);
			System.out.println("Usage: ccal [-t|-p|-x] [-g|-b] [-u] [[<month>] <year>].");
			System.out.println("\t-t:\tGenerates HTML table output.");
			System.out.println("\t-p:\tGenerates encapsulated PostScript output.");
			System.out.println("\t-x:\tGenerates XML output.");
			System.out.println("\t-g:\tGenerates simplified Chinese output.");
			System.out.println("\t-b:\tGenerates traditional Chinese output.");
			System.out.println("\t-u:\tUses UTF-8 rather than GB or Big5 for Chinese output.");
			System.exit(1);
		}
		if (result.month < 1 || result.month > 12) {
			System.out.println("ccal: Invalid month value: month 1-12.");
			System.exit(1);
		}
		if (result.year < 1645 || result.year > 7000) {
			System.out.println("ccal: Invalid year value: year 1645-7000.");
			System.exit(1);
		}
		if (IsLeapYear(result.year))
			daysinmonth[1] = 29;
		List<Double> vterms = new ArrayList<>();
		List<Double> vmoons = new ArrayList<>();
		List<Double> vmonth = new ArrayList<>();
		MutableDouble lastnew = new MutableDouble(0);
		MutableDouble lastmon = new MutableDouble(0);
		MutableDouble nextnew = new MutableDouble(0);
		double lmon = lunaryear(result.year, vterms, lastnew, lastmon, vmoons, vmonth, nextnew);
		boolean bIsSim = (result.nEncoding == 'g');
		String titlestr;
		if (result.pmode == 3) {
			System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			System.out.printf("<ccal:year value=\"%d\" xmlns:ccal=\"http://ccal.chinesebay.com/ccal/\">\n",
					result.year);
		}
		if (result.bSingle) {
			titlestr = String.format("%s %d", monnames[result.month - 1], result.year);
			if (result.pmode == 1)
				PrintHeaderHTML(titlestr, result.month, result.year, result.nEncoding);
			else if (result.pmode == 2)
				PrintHeaderPS(titlestr, bIsSim, false);
			PrintMonth(result.year, result.month, vterms, lastnew.value, lastmon.value, vmoons, vmonth, nextnew.value,
					result.pmode, result.bSingle, result.nEncoding,
					((short) (lmon) == result.month || (short) (lmon + 0.9) == result.month));
		} else {
			titlestr = String.format("Year %d", result.year);
			if (result.pmode == 1)
				PrintHeaderHTML(titlestr, 0, result.year, result.nEncoding);
			else if (result.pmode == 2)
				PrintHeaderPS(titlestr, bIsSim, (lmon != 0.0));
			for (short i = 1; i <= 12; i++)
				PrintMonth(result.year, i, vterms, lastnew.value, lastmon.value, vmoons, vmonth, nextnew.value,
						result.pmode, result.bSingle, result.nEncoding, false);
		}
		if (result.pmode == 1)
			PrintClosingHTML();
		else if (result.pmode == 2)
			PrintClosingPS();
		else if (result.pmode == 3)
			System.out.println("</ccal:year>");
	}
	private static boolean ProcessArg(String[] argv, ProcessArgResult result) {
		if (argv.length > 5)
			return false;
		result.pmode = 0;
		result.bSingle = true;
		result.nEncoding = 'a';
		boolean bIsUTF8 = false;
		if (argv.length == 0)
			return false;
		int nParam = 0;
		for (int i = 0; i < argv.length; i++) {
			if (argv[i].charAt(0) != '-') {
				if (nParam == 0) {
					result.year = Short.parseShort(argv[i]);
					result.bSingle = false;
					nParam++;
				} else if (nParam == 1) {
					result.month = result.year;
					result.year = Short.parseShort(argv[i]);
					result.bSingle = true;
					nParam++;
				} else {
					System.out.println("ccal: Too many parameters.");
					return false;
				}
			} else if (argv[i].charAt(1) == 'x')
				result.pmode = 3;
			else if (argv[i].charAt(1) == 'p')
				result.pmode = 2;
			else if (argv[i].charAt(1) == 't')
				result.pmode = 1;
			else if (argv[i].charAt(1) == 'g')
				result.nEncoding = 'g';
			else if (argv[i].charAt(1) == 'b')
				result.nEncoding = 'b';
			else if (argv[i].charAt(1) == 'u')
				bIsUTF8 = true;
			else {
				System.out.println("ccal: Unrecognized option.");
				return false;
			}
		}
		if (result.pmode != 2) {
			if (bIsUTF8 || result.pmode == 3) {
				if (result.nEncoding == 'b')
					SetU8Characters(false);
				result.nEncoding = 'u';
			}
		}
		if (result.pmode != 0) {
			if (result.nEncoding == 'a')
				result.nEncoding = 'g';
		}
		return true;
	}
	private static boolean IsLeapYear(short year) {
		if (year / 100 * 100 != year) /* Non century year */ {
			return year / 4 * 4 == year;
		} else {
			return year / 400 * 400 == year;
		}
	}
	private static void GetMonthNumber(double mnumber, short[] month, char[] leap) {
		month[0] = (short) mnumber;
		if (mnumber - month[0] == 0.0)
			leap[0] = 0;
		else
			leap[0] = 'R';
	}
	private static void PrintMonthNumber(double mnumber) {
		short[] month = new short[1];
		char[] leap = new char[2];
		GetMonthNumber(mnumber, month, leap);
		System.out.printf(" [%2d]Y%1s ", month[0], " ");
	}
	private static void PrintMonth(short year, short month, List<Double> vterms, double lastnew, double lastmon,
			List<Double> vmoons, List<Double> vmonth, double nextnew, int pmode, boolean bSingle, int nEncoding,
			boolean bNeedsRun) {
		String[][] CHtiangan = CcalData.GBtiangan; // Initialized CHtiangan with a default from CcalData
		String[][] CHdizhi = CcalData.GBdizhi; // Initialized with a default
		String[][] CHmiscchar = CcalData.GBmiscchar; // Initialized CHmiscchar with a default
		String[][] CHjieqi = CcalData.GBjieqi; // Initialized CHjieqi with a default
		String[][] daynamesCH = CcalData.daynamesGB; // Changed type and initialized with a default
		boolean bIsSim = (nEncoding == 'g');
		if (nEncoding == 'u') {
			CHtiangan = CcalData.U8tiangan;
			CHdizhi = CcalData.U8dizhi; // Uncommented
			CHmiscchar = CcalData.U8miscchar;
			CHjieqi = CcalData.U8jieqi;
			daynamesCH = CcalData.daynamesU8; // Corrected assignment
		} else if (nEncoding == 'g') {
			CHtiangan = CcalData.GBtiangan;
			CHdizhi = CcalData.GBdizhi; // Uncommented
			CHmiscchar = CcalData.GBmiscchar;
			CHjieqi = CcalData.GBjieqi;
			daynamesCH = CcalData.daynamesGB; // Corrected assignment
		} else if (nEncoding == 'b') {
			CHtiangan = CcalData.B5tiangan;
			CHdizhi = CcalData.B5dizhi; // Uncommented
			CHmiscchar = CcalData.B5miscchar;
			CHjieqi = CcalData.B5jieqi;
			daynamesCH = CcalData.daynamesB5; // Corrected assignment
		}
		int nCHchars = CHmiscchar[14][0].length();
		String space1 = "&#160;";
		String space2 = " ";
		String sp;
		if (pmode == 1)
			sp = space1;
		else
			sp = space2;
		double jdcnt = julian_date(year, month, (short) 1, 12.0);
		double jdnext;
		if (month < 12)
			jdnext = julian_date(year, (short) (month + 1), (short) 1, 12.0);
		else
			jdnext = julian_date((short) (year + 1), (short) 1, (short) 1, 12.0);
		int termcnt = (month - 1) * 2;
		if (vterms.get(termcnt) < jdcnt)
			termcnt++;
		int moncnt = 0;
		while (moncnt < vmoons.size() && vmoons.get(moncnt) < jdcnt)
			moncnt++;
		boolean sameday = false;
		int ldcnt, dcnt = 1;
		if (month != 1)
			ldcnt = (int) (jdcnt - vmoons.get(moncnt - 1) + 1);
		else
			ldcnt = (int) (jdcnt - lastnew + 1);
		if (jdcnt == vmoons.get(moncnt))
			ldcnt = 1;
		int dofw = ((int) jdcnt + 1) % 7;
		int nWeeks = 5;
		if ((dofw > 4 && daysinmonth[month - 1] == 31) || (dofw > 5 && daysinmonth[month - 1] == 30))
			nWeeks = 6;
		short cyear = (short) ((year - 1984) % 60);
		if (cyear < 0)
			cyear += 60;
		int tiancnt = cyear % 10;
		int dicnt = cyear % 12;
		short[] cmonth = new short[1];
		char[] leap = new char[2];
		String monthhead = "", cmonname = "";
		int nstartlm = (int) (vmoons.get(moncnt) - jdcnt + 1);
		int ndayslm;
		if (moncnt < vmoons.size() - 1)
			ndayslm = (int) (vmoons.get(moncnt + 1) - vmoons.get(moncnt));
		else
			ndayslm = (int) (nextnew - vmoons.get(vmoons.size() - 1));
		GetMonthNumber(vmonth.get(moncnt), cmonth, leap);
		char[] cmonnameArr = new char[100];
		if ((pmode == 0 && nEncoding != 'a') || pmode == 1 || pmode == 3)
			Number2MonthCH(vmonth.get(moncnt), nstartlm, ndayslm, nEncoding, cmonnameArr);
		else if (pmode == 2) { // Added braces for consistency
			Number2MonthPS(vmonth.get(moncnt), nstartlm, ndayslm, !bSingle, cmonnameArr); // Uncommented
		}
		cmonname = new String(cmonnameArr).trim();
		// January is special if lunar New Year is in February
		if (month == 1 && cmonth[0] != 1) {
			int tiancnt0 = (cyear + 59) % 10;
			int dicnt0 = (cyear + 59) % 12;
			if (vmoons.get(moncnt + 1) < jdnext) // Two lunar months in one month
			{
				short[] cmonth1 = new short[1];
				char[] leap1 = new char[2];
				int nstartlm1 = (int) (vmoons.get(moncnt + 1) - jdcnt + 1);
				int ndayslm1;
				char[] cmonname1Arr = new char[100];
				String cmonname1;
				if (moncnt < vmoons.size() - 2)
					ndayslm1 = (int) (vmoons.get(moncnt + 2) - vmoons.get(moncnt + 1));
				else
					ndayslm1 = (int) (nextnew - vmoons.get(vmoons.size() - 1));
				if (pmode == 0 && nEncoding == 'a') {
					GetMonthNumber(vmonth.get(moncnt + 1), cmonth1, leap1);
					monthhead = String.format("%s %d (Year %s%s, Month %s%d%c S%d, Year %s%s, Month %s%d%c S%d)",
							monnames[month - 1], year, CcalData.tiangan[tiancnt0][0], CcalData.dizhi[dicnt0][0],
							new String(leap).trim(), cmonth[0], (ndayslm == 30) ? 'D' : 'X', nstartlm,
							CcalData.tiangan[tiancnt][0], CcalData.dizhi[dicnt][0],
							new String(leap1).trim(), cmonth1[0], (ndayslm1 == 30) ? 'D' : 'X', nstartlm1);
				} else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
					Number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1, nEncoding, cmonname1Arr);
					cmonname1 = new String(cmonname1Arr).trim();
					monthhead = String.format("%s %d%s%s%s%s%s%s%s%s%s%s%s",
							monnames[month - 1], year, sp, sp, CHtiangan[tiancnt0][0], CHdizhi[dicnt0][0],
							CHmiscchar[16][0], cmonname, CHmiscchar[19][0], CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0], CHmiscchar[16][0], cmonname1);
				} else if (pmode == 3) {
					Number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1, nEncoding, cmonname1Arr);
					cmonname1 = new String(cmonname1Arr).trim();
					monthhead = String.format("%s%s%s%s%s%s%s%s%s",
							CHtiangan[tiancnt0][0], CHdizhi[dicnt0][0],
							CHmiscchar[16][0], cmonname, CHmiscchar[19][0], CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0], CHmiscchar[16][0], cmonname1);
				} else if (bSingle) {
					Number2MonthPS(vmonth.get(moncnt + 1), nstartlm1, ndayslm1, !bSingle, cmonname1Arr); // Uncommented
					cmonname1 = new String(cmonname1Arr).trim();
					monthhead = String.format("20 1 SF (%s %d) S\n/fsc 21 def gsave ptc\n" +
							"%s%s%s%s%sgrestore",
							monnames[month - 1], year, PsMonth.PSbigmchar[20][0], PsMonth.PStiangan[tiancnt0][0],
							PsMonth.PSdizhi[dicnt0][0], PsMonth.PSbigmchar[16][0], cmonname, PsMonth.PSbigmchar[19][0],
							PsMonth.PStiangan[tiancnt][0], PsMonth.PSdizhi[dicnt][0], PsMonth.PSbigmchar[16][0],
							cmonname1);
				}
			}
			else {
				if (pmode == 0 && nEncoding == 'a') {
					monthhead = String.format("%s %d (Year %s%s, Month %s%d%c S%d)",
							monnames[month - 1], year, CcalData.tiangan[tiancnt0][0], CcalData.dizhi[dicnt0][0],
							new String(leap).trim(), cmonth[0], (ndayslm == 30) ? 'D' : 'X', nstartlm);
				} else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
					monthhead = String.format("%s %d%s%s%s%s%s%s",
							monnames[month - 1], year, sp, sp, CHtiangan[tiancnt0][0],
							CHdizhi[dicnt0][0], CHmiscchar[16][0], cmonname);
				} else if (pmode == 3) {
					monthhead = String.format("%s%s%s%s",
							CHtiangan[tiancnt0][0],
							CHdizhi[dicnt0][0], CHmiscchar[16][0], cmonname);
				} else if (bSingle) {
					monthhead = String.format("20 1 SF (%s %d) S\n/fsc 21 def gsave ptc\n" +
							"%s%s%s%s%sgrestore",
							monnames[month - 1], year, PsMonth.PSbigmchar[20][0], PsMonth.PStiangan[tiancnt0][0],
							PsMonth.PSdizhi[dicnt0][0], PsMonth.PSbigmchar[16][0], cmonname);
				}
			}
		}
		else {
			if (moncnt < vmoons.size() - 1 && vmoons.get(moncnt + 1) < jdnext)
			/* Two lunar months in one month */ {
				short[] cmonth1 = new short[1];
				char[] leap1 = new char[2];
				int nstartlm1 = (int) (vmoons.get(moncnt + 1) - jdcnt + 1);
				int ndayslm1;
				char[] cmonname1Arr = new char[100];
				String cmonname1;
				if (moncnt < vmoons.size() - 2)
					ndayslm1 = (int) (vmoons.get(moncnt + 2) - vmoons.get(moncnt + 1));
				else
					ndayslm1 = (int) (nextnew - vmoons.get(vmoons.size() - 1));
				if (pmode == 0 && nEncoding == 'a') {
					GetMonthNumber(vmonth.get(moncnt + 1), cmonth1, leap1);
					monthhead = String.format("%s %d (Year %s%s, Month %s%d%c S%d, %s%d%c S%d)",
							monnames[month - 1], year, CcalData.tiangan[tiancnt][0], CcalData.dizhi[dicnt][0],
							new String(leap).trim(), cmonth[0], (ndayslm == 30) ? 'D' : 'X', nstartlm,
							new String(leap1).trim(), cmonth1[0], (ndayslm1 == 30) ? 'D' : 'X', nstartlm1);
				} else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
					Number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1, nEncoding, cmonname1Arr);
					cmonname1 = new String(cmonname1Arr).trim();
					monthhead = String.format("%s %d%s%s%s%s%s%s%s%s", monnames[month - 1], year, sp, sp,
							CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0],
							CHmiscchar[16][0], cmonname, CHmiscchar[19][0], cmonname1);
				} else if (pmode == 3) {
					Number2MonthCH(vmonth.get(moncnt + 1), nstartlm1, ndayslm1, nEncoding, cmonname1Arr);
					cmonname1 = new String(cmonname1Arr).trim();
					monthhead = String.format("%s%s%s%s%s%s",
							CHtiangan[tiancnt][0], CHdizhi[dicnt][0],
							CHmiscchar[16][0], cmonname, CHmiscchar[19][0], cmonname1);
				} else if (bSingle) {
					Number2MonthPS(vmonth.get(moncnt + 1), nstartlm1, ndayslm1, !bSingle, cmonname1Arr); // Uncommented
					cmonname1 = new String(cmonname1Arr).trim();
					monthhead = String.format("20 1 SF (%s %d) S\n/fsc 21 def gsave ptc\n" +
							"%s%s%s%s%s%s%sgrestore",
							monnames[month - 1], year, PsMonth.PSbigmchar[20][0], PsMonth.PStiangan[tiancnt][0],
							PsMonth.PSdizhi[dicnt][0], PsMonth.PSbigmchar[16][0], cmonname, PsMonth.PSbigmchar[19][0],
							cmonname1);
				}
			}
			else if (month == 2 && vmoons.get(moncnt) >= jdnext)
			/* No new moon in February */ {
				ndayslm = (int) (vmoons.get(moncnt) - vmoons.get(moncnt - 1));
				if (pmode == 0 && nEncoding == 'a') {
					GetMonthNumber(vmonth.get(moncnt - 1), cmonth, leap);
					monthhead = String.format("%s %d (Year %s%s, Month %s%d%c)",
							monnames[month - 1], year, CcalData.tiangan[tiancnt][0], CcalData.dizhi[dicnt][0],
							new String(leap).trim(), cmonth[0], (ndayslm == 30) ? 'D' : 'X');
				} else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
					Number2MonthCH(vmonth.get(moncnt - 1), nstartlm, ndayslm, nEncoding, cmonnameArr); // Uncommented
					cmonname = new String(cmonnameArr).trim();
					int p = cmonname.indexOf(CHmiscchar[14][0]) + 2 * nCHchars;
					cmonname = cmonname.substring(0, p);
					monthhead = String.format("%s %d%s%s%s%s%s%s", monnames[month - 1], year, sp, sp,
							CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0],
							CHmiscchar[16][0], cmonname);
				} else if (pmode == 3) {
					Number2MonthCH(vmonth.get(moncnt - 1), nstartlm, ndayslm, nEncoding, cmonnameArr); // Uncommented
					cmonname = new String(cmonnameArr).trim();
					int p = cmonname.indexOf(CHmiscchar[14][0]) + 2 * nCHchars;
					cmonname = cmonname.substring(0, p);
					monthhead = String.format("%s%s%s%s",
							CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0],
							CHmiscchar[16][0], cmonname);
				} else if (bSingle) {
					Number2MonthPS(vmonth.get(moncnt - 1), nstartlm, ndayslm, !bSingle, cmonnameArr); // Uncommented
					cmonname = new String(cmonnameArr).trim();
					int p = 0; /* cmonname.indexOf(PSbigmchar[14][0]) + 8; */ // Commented out
					cmonname = cmonname.substring(0, p);
					monthhead = String.format("20 1 SF (%s %d) S\n/fsc 21 def gsave ptc\n" +
							"%s%s%s%s%sgrestore",
							monnames[month - 1], year, PsMonth.PSbigmchar[20][0], PsMonth.PStiangan[tiancnt][0],
							PsMonth.PSdizhi[dicnt][0], PsMonth.PSbigmchar[16][0], cmonname);
				}
			}
			else {
				if (pmode == 0 && nEncoding == 'a') {
					monthhead = String.format("%s %d (Year %s%s, Month %s%d%c S%d)",
							monnames[month - 1], year, CcalData.tiangan[tiancnt][0], CcalData.dizhi[dicnt][0],
							new String(leap).trim(), cmonth[0], (ndayslm == 30) ? 'D' : 'X', nstartlm);
				} else if ((pmode == 0 && nEncoding != 'a') || pmode == 1) {
					monthhead = String.format("%s %d%s%s%s%s%s%s", monnames[month - 1], year, sp, sp,
							CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0],
							CHmiscchar[16][0], cmonname);
				} else if (pmode == 3) {
					monthhead = String.format("%s%s%s%s",
							CHtiangan[tiancnt][0],
							CHdizhi[dicnt][0],
							CHmiscchar[16][0], cmonname);
				} else if (bSingle) {
					monthhead = String.format("20 1 SF (%s %d) S\n/fsc 21 def gsave ptc\n" +
							"%s%s%s%s%sgrestore",
							monnames[month - 1], year, PsMonth.PSbigmchar[20][0], PsMonth.PStiangan[tiancnt][0],
							PsMonth.PSdizhi[dicnt][0], PsMonth.PSbigmchar[16][0], cmonname);
				}
			}
		}
		int nmove;
		if (pmode == 0) {
			int nHeadLen = monthhead.length();
			if (nEncoding == 'u') {
				int nasc = 0;
				for (int i = 0; i < nHeadLen; i++)
					if ((byte) (monthhead.charAt(i)) < 0x80)
						nasc++;
				nHeadLen = (nHeadLen - nasc) * 2 / 3 + nasc;
			}
			nmove = (68 - nHeadLen) / 2;
			if (nmove < 0)
				nmove = 0;
			for (int i = 0; i < nmove; i++)
				System.out.print(" ");
			System.out.println(monthhead);
		} else if (pmode == 1) {
			System.out.println("<tr>\n<th colspan=\"7\" width=\"100%\">");
			System.out.printf("%s</th>\n</tr>\n", monthhead);
		} else if (pmode == 3) {
			System.out.printf("<ccal:month value=\"%d\" name=\"%s\" cname=\"%s\">\n",
					month, monnames[month - 1], monthhead);
		} else if (bSingle) {
			PsMonth.PrintHeaderMonthPS(monthhead, month, true, bIsSim, bNeedsRun, nWeeks); // Uncommented
			int p1 = monthhead.indexOf("(");
			int p2 = monthhead.indexOf(")");
			int nlen = p2 - p1 - 1;
			int nheadlen = monthhead.length();
			if (nheadlen - nlen - 66 > 56)
				nmove = (4200 - 78 * (nlen + (nheadlen - nlen - 86) / 2 + 3)) / 2;
			else if (nheadlen - nlen - 66 > 30)
				nmove = (4200 - 78 * (nlen + (nheadlen - nlen - 66) / 2 + 3)) / 2;
			else
				nmove = (4200 - 78 * (nlen + (nheadlen - nlen - 46) / 2 + 2)) / 2;
			if (nmove < 0)
				nmove = 0;
			System.out.printf("%d 2475 m\n", nmove);
			System.out.println(monthhead);
		} else {
			System.out.printf("%% %s %d\n", monnames[month - 1], year);
			int posx = (month - 1) % 4 * 140;
			int posy = 3450 - (month - 1) / 4 * 1150 - 173;
			System.out.printf("%d lpts %d m gsave ct\n",
					posx, posy);
			System.out.printf("%% Month number\n%d lpts %d m gsave\n100 1 SF 1 0.9 1 K",
					((month < 10) ? 35 : 10), -750);
			System.out.printf(" (%d) S grestore\n", month);
		}
		/* Day of week */
		String dayshort;
		if (pmode == 0) {
			for (int i = 0; i < 7; i++) {
				if (nEncoding != 'u')
					System.out.printf("%s   ", daynamesCH[i][0]); // daynamesCH is String[][]
				else
					System.out.printf("%s   ", daynamesCH[i][0]); // daynamesCH is String[][]
			}
			System.out.println();
		} else if (pmode == 1) {
			System.out.println("<tr align=\"center\">");
			for (int i = 0; i < 7; i++) {
				dayshort = CcalData.daynames[i].substring(0, 3);
				if (i == 0)
					System.out.printf("<td width=\"15%%\"><font color=\"#FF0000\">%s %s</font></td>\n", dayshort,
							CHmiscchar[15][0]);
				else if (i == 6)
					System.out.printf("<td width=\"15%%\"><font color=\"#00E600\">%s %s</font></td>\n", dayshort,
							CHmiscchar[i][0]);
				else
					System.out.printf("<td width=\"14%%\">%s %s</td>\n", dayshort, CHmiscchar[i][0]);
			}
			System.out.println("</tr>");
		} else if (pmode == 2) {
			if (bSingle) {
				System.out.println("% Day names\n14 1 SF\n/fsc 15 def");
				for (int i = 0; i < 7; i++) {
					dayshort = CcalData.daynames[i].substring(0, 3);
					if (i == 0)
						System.out.printf("gsave 1 0 0 K\n100 2270 m (%s) S\n400 2270 m " +
								"gsave ptc %s grestore\ngrestore\n", dayshort, PsMonth.PSbigmchar[15][0]);
					else if (i == 6)
						System.out.printf("gsave 0 0.8 0 K\n3700 2270 m (%s) S\n4000 2270 m " +
								"gsave ptc %s grestore\ngrestore\n", dayshort, PsMonth.PSbigmchar[i][0]);
					else {
						int posx = i * 600 + 100;
						System.out.printf("%d 2270 m (%s) S\n%d 2270 m gsave ptc " +
								"%s grestore\n", posx, dayshort,
								(posx + 300), PsMonth.PSbigmchar[i][0]);
					}
				}
				System.out.println("% Days\n17 1 SF\n/fsc 7.5 def");
			} else {
				System.out.println("% Week heading\n0 0 m Ymh");
				System.out.println("% Days\n9 0 SF");
			}
		}
		final String ANSI_REV = "\u001b[7m";
		final String ANSI_NORMAL = "\u001b[0m";
		Calendar today = Calendar.getInstance();
		char[] cdaynameArr = new char[21];
		String cdayname = "";
		for (int w = 0; w < nWeeks; w++) {
			if (pmode == 1) {
				System.out.println("<tr align=\"center\">");
			}
			if (pmode == 3) {
				System.out.println("<ccal:week>");
			}
			for (int i = 0; i < 7; i++) {
				if (pmode == 1) {
					if (i == 0)
						System.out.print("<td width=\"15%\"><font color=\"#FF0000\">");
					else if (i == 6)
						System.out.print("<td width=\"15%\"><font color=\"#00E600\">");
					else
						System.out.print("<td width=\"14%\">");
				}
				if (pmode == 3) {
					System.out.print("<ccal:day ");
				}
				if (dcnt > daysinmonth[month - 1]) {
					if (pmode == 1) {
						if (i == 0 || i == 6)
							System.out.print("&#160;</font></td>\n");
						else
							System.out.print("&#160;</td>\n");
						continue;
					}
					if (pmode == 3) {
						System.out.print("/>\n");
						continue;
					}
					break;
				}
				if (w == 0) {
					if (i < dofw) {
						if (pmode == 0)
							System.out.print("          ");
						else if (pmode == 1) {
							if (i == 0 || i == 6)
								System.out.print("&#160;</font></td>\n");
							else
								System.out.print("&#160;</td>\n");
						} else if (pmode == 3) {
							System.out.print("/>\n");
						}
						continue;
					}
				}
				if (dcnt == 1 || ldcnt == 1) {
					int mcnt_ = moncnt;
					if (ldcnt != 1)
						mcnt_--;
					if (mcnt_ >= 0)
						GetMonthNumber(vmonth.get(mcnt_), cmonth, leap);
					else
						GetMonthNumber(lastmon, cmonth, leap);
					if (nEncoding != 'a') {
						if (mcnt_ >= 0)
							Number2MonthCH(vmonth.get(mcnt_), 1, 30, nEncoding, cmonnameArr);
						else
							Number2MonthCH(lastmon, 1, 30, nEncoding, cmonnameArr);
						cmonname = new String(cmonnameArr).trim();
						int p = cmonname.indexOf(CHmiscchar[14][0]) + nCHchars;
						cmonname = cmonname.substring(0, p);
					}
				}
				if (today.get(Calendar.YEAR) == year && (today.get(Calendar.MONTH) + 1) == month
						&& today.get(Calendar.DAY_OF_MONTH) == dcnt) {
					if (pmode == 0)
						System.out.print(ANSI_REV);
				}
				if (pmode == 0 || pmode == 1)
					System.out.printf("%2d", dcnt);
				else if (pmode == 3) {
					System.out.printf("value=\"%d\" cmonth=\"%d\" leap=\"%s\" cdate=\"%d\" ", dcnt, cmonth[0],
							new String(leap).trim(), ldcnt);
					Number2DayCH(ldcnt, nEncoding, cdaynameArr);
					cdayname = new String(cdaynameArr).trim();
				} else if (bSingle) {
					int posx = i * 600 + 50;
					int posy = 1875 - w * 375 + 220;
					if (nWeeks == 5)
						posy = 1800 - w * 450 + 295;
					if (i == 0)
						System.out.print("gsave 1 0 0 K\n");
					else if (i == 6)
						System.out.print("gsave 0 0.8 0 K\n");
					System.out.printf("%d %d m (%2d) S\ngsave 8 0 SF ", posx, posy, dcnt);
					posx += 170;
					if (dcnt == 1 || ldcnt == 1) {
						if (leap[0] == 'R')
							System.out.printf("%d %d m (%dR.%d) S grestore\n%d %d m", (posx + 5), (posy + 56),
									cmonth[0], ldcnt, posx, (posy - 2));
						else
							System.out.printf("%d %d m (%d.%d) S grestore\n%d %d m", (posx + 5), (posy + 56), cmonth[0],
									ldcnt, posx, (posy - 2));
					} else
						System.out.printf("%d %d m (%2d) S grestore\n%d %d m", (posx + 5), (posy + 56), ldcnt, posx,
								(posy - 2));
					System.out.print(" gsave ptc");
				} else {
					int posx = i * 19;
					int posy = -w * 19 - 14;
					if (i == 0)
						System.out.print("gsave 1 0 0 K\n");
					else if (i == 6)
						System.out.print("gsave 0 0.8 0 K\n");
					System.out.printf("%d %d moveto (%2d) S\n", posx, posy, dcnt);
					posy -= 7;
					System.out.printf("%d %d moveto gsave ptc", posx, posy);
				}
				if (!sameday && (termcnt >= vterms.size() || jdcnt != vterms.get(termcnt))
						&& (moncnt >= vmoons.size() || jdcnt != vmoons.get(moncnt))) {
					if (pmode == 0) {
						if (nEncoding == 'a')
							System.out.printf(" [%2d]   ", ldcnt);
						else {
							Number2DayCH(ldcnt, nEncoding, cdaynameArr);
							cdayname = new String(cdaynameArr).trim();
							System.out.printf(" %s   ", cdayname);
						}
					} else if (pmode == 1) {
						if (dcnt == 1)
							System.out.printf(" %s", cmonname);
						else
							System.out.print(" ");
						Number2DayCH(ldcnt, nEncoding, cdaynameArr);
						cdayname = new String(cdaynameArr).trim();
						System.out.print(cdayname);
						if (i == 0 || i == 6)
							System.out.print("</font>");
						if (cdayname.length() == 2 * nCHchars && dcnt != 1)
							System.out.print("&#160;&#160;</td>\n");
						else
							System.out.print("</td>\n");
					} else if (pmode == 2 && bSingle) {
						if (dcnt == 1) { // This if block needs to be self-contained
							if (moncnt > 0) {
								Number2MonthPS(vmonth.get(moncnt - 1), 1, 30, true, cmonnameArr); // Uncommented
							} else {
								Number2MonthPS(lastmon, 1, 30, true, cmonnameArr); // Uncommented
							}
							cmonname = new String(cmonnameArr).trim();
							System.out.printf(" %s", cmonname);
						} else {
							System.out.print(" ");
						}
						Number2DayPS(ldcnt, cdaynameArr); // Uncommented
						cdayname = new String(cdaynameArr).trim();
						System.out.printf("%s \n", cdayname);
						if (i == 0 || i == 6)
							System.out.print("grestore\n");
						System.out.print("grestore\n");
					} else if (pmode == 2) {
						Number2DayPS(ldcnt, cdaynameArr); // Uncommented
						cdayname = new String(cdaynameArr).trim();
						if (cdayname.length() > 8)
							System.out.printf(" grestore -%d 0 rmoveto gsave ptc", ((cdayname.length() - 8) / 4 * 3));
						System.out.printf(" %s \n", cdayname);
						if (i == 0 || i == 6)
							System.out.print("grestore\n");
						System.out.print("grestore\n");
					}
				} else if (sameday) {
					if (pmode == 0) {
						if (nEncoding == 'a')
							PrintMonthNumber(vmonth.get(moncnt++));
						else {
							Number2MonthCH(vmonth.get(moncnt++), 1, 30, nEncoding, cmonnameArr);
							cmonname = new String(cmonnameArr).trim();
							int p = cmonname.indexOf(CHmiscchar[14][0]) + nCHchars;
							cmonname = cmonname.substring(0, p);
							if (cmonname.length() <= 3 * nCHchars)
								System.out.print(" ");
							System.out.print(cmonname);
							if (cmonname.length() == 2 * nCHchars)
								System.out.print("   ");
							if (cmonname.length() == 3 * nCHchars)
								System.out.print(" ");
						}
					} else if (pmode == 1) {
						Number2DayCH(ldcnt, nEncoding, cdaynameArr);
						cdayname = new String(cdaynameArr).trim();
						System.out.printf(" %s", cdayname);
						if (i == 0 || i == 6)
							System.out.print("</font>");
						if (cdayname.length() == 2 * nCHchars)
							System.out.print("&#160;&#160;</td>\n");
						else
							System.out.print("</td>\n");
					} else if (pmode == 2) { // This else if is fine
						Number2DayPS(ldcnt, cdaynameArr); // Uncommented
						cdayname = new String(cdaynameArr).trim();
						System.out.printf(" %s \n", cdayname);
						if (i == 0 || i == 6)
							System.out.print("grestore\n");
						System.out.print("grestore\n");
					}
					sameday = false;
				} else if (termcnt < vterms.size() && jdcnt == vterms.get(termcnt)) {
					if (moncnt < vmoons.size() && jdcnt == vmoons.get(moncnt))
						sameday = true;
					if (pmode == 0) {
						if (nEncoding == 'a')
							System.out.printf(" [%s]   ", jieqi[termcnt++]);
						else {
							System.out.printf(" %s   ", CHjieqi[termcnt++][0]);
						}
					} else if (pmode == 1) {
						if (sameday)
							System.out.printf(" %s", cmonname);
						else
							System.out.print(" ");
						System.out.printf("%s", CHjieqi[termcnt++][0]);
						if (i == 0 || i == 6)
							System.out.print("</font>");
						if (!sameday)
							System.out.print("&#160;&#160;</td>\n");
						else
							System.out.print("</td>\n");
					} else if (pmode == 3) {
						cdayname = CHjieqi[termcnt++][0];
					} else if (pmode == 2) {
						if (sameday) {
							Number2MonthPS(vmonth.get(moncnt++), 1, 30, true, cmonnameArr); // Uncommented
							cmonname = new String(cmonnameArr).trim();
							if (!bSingle)
								System.out.print(" grestore -6 0 rmoveto gsave ptc");
							if (!bSingle && cmonname.length() > 8) {
								double fac = 16.0 / (cmonname.length() + 8.0);
								System.out.printf(" gsave %f 1 scale %s", fac, cmonname);
							} else
								System.out.printf(" %s ", cmonname);
						} else
							System.out.print(" ");
						System.out.printf("%s\n", PsMonth.PSjieqi[termcnt++][0]); // Corrected usage
						if (sameday && !bSingle && cmonname.length() > 8)
							System.out.print("grestore\n");
						if (i == 0 || i == 6)
							System.out.print("grestore\n");
						System.out.print("grestore\n");
					}
				} else {
					if (pmode == 0) {
						if (nEncoding == 'a')
							PrintMonthNumber(vmonth.get(moncnt++));
						else {
							Number2MonthCH(vmonth.get(moncnt++), 1, 30, nEncoding, cmonnameArr);
							cmonname = new String(cmonnameArr).trim();
							int p = cmonname.indexOf(CHmiscchar[14][0]) + nCHchars;
							cmonname = cmonname.substring(0, p);
							if (cmonname.length() <= 3 * nCHchars)
								System.out.print(" ");
							System.out.print(cmonname);
							if (cmonname.length() == 2 * nCHchars)
								System.out.print("   ");
							if (cmonname.length() == 3 * nCHchars)
								System.out.print(" ");
						}
					} else if (pmode == 1 || pmode == 3) {
						Number2MonthCH(vmonth.get(moncnt++), 1, 30, nEncoding, cmonnameArr);
						cmonname = new String(cmonnameArr).trim();
						int p = cmonname.indexOf(CHmiscchar[14][0]) + nCHchars;
						cmonname = cmonname.substring(0, p);
						if (pmode == 1) {
							System.out.printf(" %s", cmonname);
							if (i == 0 || i == 6)
								System.out.print("</font>");
							if (cmonname.length() == 2 * nCHchars)
								System.out.print("&#160;&#160;</td>\n");
							else
								System.out.print("</td>\n");
						}
					} else if (pmode == 2 && bSingle) {
						Number2MonthPS(vmonth.get(moncnt++), 1, 30, true, cmonnameArr); // Uncommented
						cmonname = new String(cmonnameArr).trim();
						System.out.printf(" %s \n", cmonname);
						if (i == 0 || i == 6)
							System.out.print("grestore\n");
						System.out.print("grestore\n");
					} else if (pmode == 2) { // This else if is fine
						Number2MonthPS(vmonth.get(moncnt++), 1, 30, true, cmonnameArr); // Uncommented
						cmonname = new String(cmonnameArr).trim();
						if (cmonname.length() > 8)
							System.out.printf(" grestore -%d 0 rmoveto gsave ptc", ((cmonname.length() - 7) / 4 * 3));
						System.out.printf(" %s \n", cmonname);
						if (i == 0 || i == 6)
							System.out.print("grestore\n");
						System.out.print("grestore\n");
					}
				}
				if (pmode == 3)
					System.out.printf("cmonthname=\"%s\" cdatename=\"%s\" />\n", cmonname, cdayname);
				if (today.get(Calendar.YEAR) == year && (today.get(Calendar.MONTH) + 1) == month
						&& today.get(Calendar.DAY_OF_MONTH) == dcnt) {
					if (pmode == 0)
						System.out.print(ANSI_NORMAL);
				}
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
		if (pmode == 2 && !bSingle) {
			System.out.println("grestore");
		} else if (pmode == 3) {
			System.out.println("</ccal:month>");
		}
	}
}
