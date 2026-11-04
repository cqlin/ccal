/*
 * Copyright (c) 2000-2012, by Zhuo Meng (zxm8@case.edu).
 * All rights reserved.
 *
 * Distributed under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.chinesebay.ccal3;

import java.util.ArrayList;

import com.chinesebay.ccal3.SolarTerm.SolarTermResult;

/**
 * Determines the lunar month numbers for a given year
 * 
 */
public class LunarYear {

	public static class LunarYearResult {
		public double leapMonth; // The return value from the original C++ function
		public ArrayList<Double> vterms;
		public double lastnew;
		public double lastmon;
		public ArrayList<Double> vmoons;
		public ArrayList<Double> vmonth;
		public double nextnew;
	}

	/**
	 * Inputs: jdws, Time of winter solstice of given year vmoons, vector of new
	 * moon times since day after winter solstice of previous year Returns: true --
	 * normal year, false -- leap year
	 */
	private static boolean isNormalYear(double jdws, ArrayList<Double> vmoons) {
		int i = 0, nmonth = 0;
		double jend = jdws + 0.5;
		jend = (int) (jend) + 0.5;
		while (i < vmoons.size() && vmoons.get(i++) < jend)
			nmonth++;
		assert (nmonth == 12 || nmonth == 13);
		if (nmonth == 12) /* Normal year */
			return true;
		return false;
	}

	/**
	 * Inputs: jstart, time of new moon for the starting of the month jend, time of
	 * new moon for the starting of the next month vterms, vector of solarterms of
	 * the given year Returns: true -- a Zhongqi is in, false -- no Zhongqi
	 */
	private static boolean isZhongQiInMonth(double jstart, double jend, ArrayList<Double> vterms) {
		jstart -= 0.5;
		jstart = (int) (jstart) + 0.5;
		jend -= 0.5;
		jend = (int) (jend) + 0.5;
		int i;
		for (i = 1; i < vterms.size(); i += 2) {
			if (vterms.get(i) >= jstart && vterms.get(i) < jend)
				return true;
			if (vterms.get(i) >= jend)
				return false;
		}
		return false;
	}

	/**
	 * Inputs: vjds, vector of julian dates to have the hour portion trimmed
	 * Outputs: vjds, vector of julian dates with the hour portion trimmed
	 */
	private static void trimHour(ArrayList<Double> vjds) {
		int i, t;
		for (i = 0; i < vjds.size(); i++) {
			t = (int) (vjds.get(i) + 0.5);
			vjds.set(i, (double) t);
		}
	}

	/**
	 * @param year
	 * @return
	 */
	public static LunarYearResult lunaryear(short year) {

		LunarYearResult result = new LunarYearResult();
		ArrayList<Double> vterms = new ArrayList<Double>();
		double lastnew = 0;
		double lastmon = 0;
		ArrayList<Double> vmoons = new ArrayList<Double>();
		ArrayList<Double> vmonth = new ArrayList<Double>();
		double nextnew = 0;

		// #ifdef USE_YEARCACHE
		// /* Use cache if in range */
		// if (YearCache::IsInRange(year)) /* Cached year */
		// {
		// YearCache::GetYear(year, vterms, lastnew, lastmon, vmoons, vmonth,
		// nextnew);
		// }
		// else
		// #endif
		{
			/* Determine solar terms */
			SolarTermResult stResult = SolarTerm.solarterm(year);
			double jdpws = stResult.jdpws;
			vterms = stResult.vjdterms;

			/* Determine new moons since day after previous winter solstice */
			double jstart = jdpws + 0.5;
			jstart = (int) (jstart) + 0.5;
			double jend = Novas.julian_date(year, (short)12, (short)31, 23.9999);
			vmoons = MPhases.mphases(jstart, jend, 0);

			/* Determine the month numbers */
			// vmonth.resize(vmoons.size());
			for (int i = 0; i < vmoons.size(); i++) {
				vmonth.add(0.0);
			}

			int i;
			if (isNormalYear(vterms.get(23), vmoons)) /* Normal year */
			{
				int n = 12;
				for (i = 0; i < vmoons.size(); i++) {
					vmonth.set(i, (double) n);
					if (n == 12)
						n = 0;
					n++;
				}
				if (n == 1) /* Involves next lunar year */
				{
					SolarTermResult stResultNext = SolarTerm.solarterm((short) (year + 1));
					double jdnwsp = stResultNext.jdpws;
					ArrayList<Double> vjdnterms = stResultNext.vjdterms;
					
					ArrayList<Double> vnmoons;
					jstart = jdnwsp + 0.5;
					jstart = (int) (jstart) + 0.5;
					jend = Novas.julian_date((short)(year + 1), (short)12, (short)31, 23.999);
					vnmoons = MPhases.mphases(jstart, jend, 0);
					if (!isNormalYear(vjdnterms.get(23), vnmoons)) {
						/* Check if a zhongqi falls inside the month */
						if (!isZhongQiInMonth(vnmoons.get(0), vnmoons.get(1), vjdnterms))
							vmonth.set(vmonth.size() - 1, 11.5);
					}
				}
			} else /* Leap year */
			{
				boolean bleaped = false;
				int n = 11;
				for (i = 0; i < vmoons.size() - 1; i++) {
					/* Check if a zhongqi falls inside the month */
					if (bleaped || isZhongQiInMonth(vmoons.get(i), vmoons.get(i + 1), vterms))
						vmonth.set(i, (double) ++n);
					else {
						if (n == 0)
							vmonth.set(i, 12.5);
						else
							vmonth.set(i, n + 0.5);
						bleaped = true;
					}
					if (n == 12)
						n = 0;
				}
				vmonth.set(vmonth.size() - 1, (double) (n + 1));
			}
			if (vmoons.get(0) < Novas.julian_date(year, (short)1, (short)1, 0.0)) {
				lastnew = Math.floor(vmoons.get(0) + 0.5);
				lastmon = vmonth.get(0);
				vmoons.remove(0);
				vmonth.remove(0);
			} else /* Need to find the last new moon for previous year */
			{
				ArrayList<Double> vlastnew;
				vlastnew = MPhases.mphases(vmoons.get(0) - 35.0, vmoons.get(0) - 25.0, 0);
				trimHour(vlastnew);
				lastnew = vlastnew.get(vlastnew.size() - 1);
				lastmon = 11.0;
			}
			/* Need to find the first new moon for next year */
			ArrayList<Double> vnextnew;
			vnextnew = MPhases.mphases(vmoons.get(vmoons.size() - 1) + 25.0, vmoons.get(vmoons.size() - 1) + 35.0, 0);
			trimHour(vnextnew);
			nextnew = vnextnew.get(vnextnew.size() - 1);
			/* Convert to whole day numbers */
			trimHour(vmoons);
			trimHour(vterms);
			/* Apply correction from DE405 */
			if (year == 1774)
				vterms.set(2, vterms.get(2) + 1);
			if (year == 1878)
				vterms.set(8, vterms.get(8) + 1);
			if (year == 1951)
				vterms.set(23, vterms.get(23) + 1);
		}

		result.vterms = vterms;
		result.lastnew = lastnew;
		result.lastmon = lastmon;
		result.vmoons = vmoons;
		result.vmonth = vmonth;
		result.nextnew = nextnew;
		
		/* Scan for leap month and return the calendar month */
		if ((int) (lastmon + 0.9) != (int) (lastmon)) /* lastmon is a leap month */
		{
			if (Novas.julian_date(year, (short)1, (short)1, 12.0) < vmoons.get(0)) /* runs into new year */
			{
				result.leapMonth = 0.5;
				return result;
			}
		}
		short monnum = 2;
		int i;
		for (i = 0; i < vmoons.size(); i++) {
			if ((int) (vmonth.get(i).doubleValue() + 0.9) != (int) vmonth.get(i).doubleValue()) /* found leap month */
			{
				double jdfirst;
				while ((jdfirst = Novas.julian_date(year, monnum, (short)1, 12.0)) < vmoons.get(i) && monnum <= 12)
					monnum++;
				if (monnum == 13)
				{
					result.leapMonth = 12.0;
					return result;
				}
				/* See if leap month runs into next month */
				if (i != vmoons.size() - 1 && jdfirst < vmoons.get(i + 1))
				{
					result.leapMonth = monnum - 0.5; /* Yes */
					return result;
				}
				else
				{
					result.leapMonth = monnum - 1.0; /* No */
					return result;
				}
			}
		}
		result.leapMonth = 0.0;
		return result;
	}
}
