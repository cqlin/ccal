package com.chinesebay.ccal2;

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

/**
 * YearCache.java
 *
 * STUB: This is a placeholder for the year caching optimization.
 * The full implementation would include pre-computed solar term and moon phase
 * data for years 1900-2100 to speed up calendar calculations.
 *
 * For now, caching is disabled and all calculations are done on-demand.
 * To implement full caching, port the large data arrays from yearcache.cpp
 * (allterms[], allmoons[], allmonth[]).
 */
public class YearCache {
    // Currently caching is disabled
    private static final boolean USE_CACHE = false;

    /**
     * Checks if a year is in the cached range
     * @param year The year to check
     * @return true if cached data is available (currently always false)
     */
    public static boolean isInRange(short year) {
        return false; // Caching disabled
    }

    /**
     * Retrieves cached year data
     * This is a stub - full implementation would load from pre-computed arrays
     */
    public static void getYear(short year, java.util.ArrayList<Double> vterms,
                               double[] lastnew, double[] lastmon,
                               java.util.ArrayList<Double> vmoons,
                               java.util.ArrayList<Double> vmonth,
                               double[] nextnew) {
        throw new UnsupportedOperationException("Year caching not implemented - use LunarYear.lunaryear() instead");
    }
}
