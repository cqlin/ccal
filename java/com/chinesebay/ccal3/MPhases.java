/*
   Copyright (c) 2000-2012, by Zhuo Meng (zxm8@case.edu).
   All rights reserved.

   Distributed under the terms of the GNU Lesser General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package com.chinesebay.ccal3;

import java.util.ArrayList;

/**
 * Computes the julian dates (Beijing Time) of a given moon phase (0-3, for new
 * moon to last quarter) between two given time marks
 * 
 */
public class MPhases {
	/**
	 * 
	 * @param tstart
	 *            start of the period in julian date (Beijing Time)
	 * @param tend
	 *            end of the period in julian date (Beijing Time)
	 * @param phase
	 *            the desired phase, 0-3 for new moon to last quarter
	 * @return the julian dates (Beijing Time) of all occurings of the desired
	 *         phase
	 */
	public static ArrayList<Double> mphases(double tstart, double tend, int phase) {
		ArrayList<Double> vjdphases = new ArrayList<Double>();
		double offs;
		if (tstart < 2425245) /* Before 1928 */
			offs = (116.0 + 25.0 / 60.0) / 360.0;
		else
			offs = 120.0 / 360.0;
		/* Find the lunation number of the first given phase after tstart */
		double period = 29.53058853;
		int lun = (int) ((tstart - offs - 2451550.09765 - phase * 7.375) / period);
		double jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
		while (jd1 - tstart > 29) {
			lun--;
			jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
		}
		while (tstart > jd1) {
			lun++;
			jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
		}
		/* Compute subsequent phases until after tend */
		jd1 = TT2UT.tt2ut(jd1);
		vjdphases.add(jd1);
		while (jd1 < tend - 29) {
			lun++;
			jd1 = MoonPhase.moonphasebylunation(lun, phase) + offs;
			jd1 = TT2UT.tt2ut(jd1);
			if (jd1 < tend)
				vjdphases.add(jd1);
		}
		return vjdphases;
	}

}
