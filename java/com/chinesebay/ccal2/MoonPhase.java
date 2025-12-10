package com.chinesebay.ccal2;

/*
   Copyright 1996, Christopher Osburn, Lunar Outreach Services,

   As part of the ccal program by Zhuo Meng, this version is
   distributed under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   calculate phase of the moon per Meeus Ch. 47
   updated with parameters from Ch. 49 of 2005 printing of 2nd Edition
*/

/**
 * Calculate phase of the moon
 */
public class MoonPhase {
    /**
     * Calculate moon phase by lunation
     * @param lun phase parameter - number of lunations since New Moon of 2000 January 6
     * @param phi phase parameter - 0=New, 1=First Qtr, 2=Full, 3=Last Qtr
     * @return Apparent JD of the needed phase
     */
    public static double moonphasebylunation(int lun, int phi) {
        double k = lun + phi / 4.0;
        return moonphase(k, phi);
    }

    /**
     * Calculate moon phase
     * @param k lunation number
     * @param phi phase parameter - 0=New, 1=First Qtr, 2=Full, 3=Last Qtr
     * @return Apparent JD of the needed phase
     */
    public static double moonphase(double k, int phi) {
        double T = k / 1236.85; // (47.3)

        // First approximation
        double JDE = 2451550.09766 + (29.530588861 * k) // (47.1)
                + T * T * (0.00015437 + T * (-0.000000150 + 0.00000000073 * T));

        // Correction parameters
        double E = 1.0 + T * (-0.002516 + -0.0000074 * T); // (45.6)
        double M = 2.5534 + 29.10535670 * k // (47.4)
                + T * T * (-0.0000014 + -0.00000011 * T);
        double M1 = 201.5643 + 385.81693528 * k // (47.5)
                + T * T * (0.0107582 + T * (0.00001238 + -0.000000058 * T));
        double F = 160.7108 + 390.67050284 * k // (47.6)
                + T * T * (-0.0016118 * T * (-0.00000227 + 0.000000011 * T));
        double O = 124.7746 - 1.56375588 * k // (47.7)
                + T * T * (0.0020672 + 0.00000215 * T);

        // Planetary arguments
        double[] A = new double[15];
        A[0] = 0; // unused
        A[1] = 299.77 + 0.107408 * k - 0.009173 * T * T;
        A[2] = 251.88 + 0.016321 * k;
        A[3] = 251.83 + 26.651886 * k;
        A[4] = 349.42 + 36.412478 * k;
        A[5] = 84.66 + 18.206239 * k;
        A[6] = 141.74 + 53.303771 * k;
        A[7] = 207.14 + 2.453732 * k;
        A[8] = 154.84 + 7.306860 * k;
        A[9] = 34.52 + 27.261239 * k;
        A[10] = 207.19 + 0.121824 * k;
        A[11] = 291.34 + 1.844379 * k;
        A[12] = 161.72 + 24.198154 * k;
        A[13] = 239.56 + 25.513099 * k;
        A[14] = 331.55 + 3.592518 * k;

        // Convert to radians
        M = MoonUtil.torad(M);
        M1 = MoonUtil.torad(M1);
        F = MoonUtil.torad(F);
        O = MoonUtil.torad(O);

        // Convert planetary arguments to radians
        for (int i = 1; i <= 14; i++) {
            A[i] = MoonUtil.torad(A[i]);
        }

        double W = 0.0;

        // Apply corrections based on phase
        switch (phi) {
            case 0: // New Moon
                JDE = JDE
                        - 0.40720 * Math.sin(M1)
                        + 0.17241 * E * Math.sin(M)
                        + 0.01608 * Math.sin(2.0 * M1)
                        + 0.01039 * Math.sin(2.0 * F)
                        + 0.00739 * E * Math.sin(M1 - M)
                        - 0.00514 * E * Math.sin(M1 + M)
                        + 0.00208 * E * E * Math.sin(2.0 * M)
                        - 0.00111 * Math.sin(M1 - 2.0 * F)
                        - 0.00057 * Math.sin(M1 + 2.0 * F)
                        + 0.00056 * E * Math.sin(2.0 * M1 + M)
                        - 0.00042 * Math.sin(3.0 * M1)
                        + 0.00042 * E * Math.sin(M + 2.0 * F)
                        + 0.00038 * E * Math.sin(M - 2.0 * F)
                        - 0.00024 * E * Math.sin(2.0 * M1 - M)
                        - 0.00017 * Math.sin(O)
                        - 0.00007 * Math.sin(M1 + 2.0 * M)
                        + 0.00004 * Math.sin(2.0 * M1 - 2.0 * F)
                        + 0.00004 * Math.sin(3.0 * M)
                        + 0.00003 * Math.sin(M1 + M - 2.0 * F)
                        + 0.00003 * Math.sin(2.0 * M1 + 2.0 * F)
                        - 0.00003 * Math.sin(M1 + M + 2.0 * F)
                        + 0.00003 * Math.sin(M1 - M + 2.0 * F)
                        - 0.00002 * Math.sin(M1 - M - 2.0 * F)
                        - 0.00002 * Math.sin(3.0 * M1 + M)
                        + 0.00002 * Math.sin(4.0 * M1);
                break;

            case 2: // Full Moon
                JDE = JDE
                        - 0.40614 * Math.sin(M1)
                        + 0.17302 * E * Math.sin(M)
                        + 0.01614 * Math.sin(2.0 * M1)
                        + 0.01043 * Math.sin(2.0 * F)
                        + 0.00734 * E * Math.sin(M1 - M)
                        - 0.00515 * E * Math.sin(M1 + M)
                        + 0.00209 * E * E * Math.sin(2.0 * M)
                        - 0.00111 * Math.sin(M1 - 2.0 * F)
                        - 0.00057 * Math.sin(M1 + 2.0 * F)
                        + 0.00056 * E * Math.sin(2.0 * M1 + M)
                        - 0.00042 * Math.sin(3.0 * M1)
                        + 0.00042 * E * Math.sin(M + 2.0 * F)
                        + 0.00038 * E * Math.sin(M - 2.0 * F)
                        - 0.00024 * E * Math.sin(2.0 * M1 - M)
                        - 0.00017 * Math.sin(O)
                        - 0.00007 * Math.sin(M1 + 2.0 * M)
                        + 0.00004 * Math.sin(2.0 * M1 - 2.0 * F)
                        + 0.00004 * Math.sin(3.0 * M)
                        + 0.00003 * Math.sin(M1 + M - 2.0 * F)
                        + 0.00003 * Math.sin(2.0 * M1 + 2.0 * F)
                        - 0.00003 * Math.sin(M1 + M + 2.0 * F)
                        + 0.00003 * Math.sin(M1 - M + 2.0 * F)
                        - 0.00002 * Math.sin(M1 - M - 2.0 * F)
                        - 0.00002 * Math.sin(3.0 * M1 + M)
                        + 0.00002 * Math.sin(4.0 * M1);
                break;

            case 1: // First Quarter
            case 3: // Last Quarter
                JDE = JDE
                        - 0.62801 * Math.sin(M1)
                        + 0.17172 * E * Math.sin(M)
                        - 0.01183 * E * Math.sin(M1 + M)
                        + 0.00862 * Math.sin(2.0 * M1)
                        + 0.00804 * Math.sin(2.0 * F)
                        + 0.00454 * E * Math.sin(M1 - M)
                        + 0.00204 * E * E * Math.sin(2.0 * M)
                        - 0.00180 * Math.sin(M1 - 2.0 * F)
                        - 0.00070 * Math.sin(M1 + 2.0 * F)
                        - 0.00040 * Math.sin(3.0 * M1)
                        - 0.00034 * E * Math.sin(2.0 * M1 - M)
                        + 0.00032 * E * Math.sin(M + 2.0 * F)
                        + 0.00032 * E * Math.sin(M - 2.0 * F)
                        - 0.00028 * E * E * Math.sin(M1 + 2.0 * M)
                        + 0.00027 * E * Math.sin(2.0 * M1 + M)
                        - 0.00017 * Math.sin(O)
                        - 0.00005 * Math.sin(M1 - M - 2.0 * F)
                        + 0.00004 * Math.sin(2.0 * M1 + 2.0 * F)
                        - 0.00004 * Math.sin(M1 + M + 2.0 * F)
                        + 0.00004 * Math.sin(M1 - 2.0 * M)
                        + 0.00003 * Math.sin(M1 + M - 2.0 * F)
                        + 0.00003 * Math.sin(3.0 * M)
                        + 0.00002 * Math.sin(2.0 * M1 - 2.0 * F)
                        + 0.00002 * Math.sin(M1 - M + 2.0 * F)
                        - 0.00002 * Math.sin(3.0 * M1 + M);

                W = 0.00306
                        - 0.00038 * E * Math.cos(M)
                        + 0.00026 * Math.cos(M1)
                        - 0.00002 * Math.cos(M1 - M)
                        + 0.00002 * Math.cos(M1 + M)
                        + 0.00002 * Math.cos(2.0 * F);
                if (phi == 3) {
                    W = -W;
                }
                JDE += W;
                break;

            default:
                System.err.println("The Moon has exploded!");
                System.exit(1);
                break;
        }

        // Final corrections
        JDE = JDE
                + 0.000325 * Math.sin(A[1])
                + 0.000165 * Math.sin(A[2])
                + 0.000164 * Math.sin(A[3])
                + 0.000126 * Math.sin(A[4])
                + 0.000110 * Math.sin(A[5])
                + 0.000062 * Math.sin(A[6])
                + 0.000060 * Math.sin(A[7])
                + 0.000056 * Math.sin(A[8])
                + 0.000047 * Math.sin(A[9])
                + 0.000042 * Math.sin(A[10])
                + 0.000040 * Math.sin(A[11])
                + 0.000037 * Math.sin(A[12])
                + 0.000035 * Math.sin(A[13])
                + 0.000023 * Math.sin(A[14]);

        return JDE;
    }
}
