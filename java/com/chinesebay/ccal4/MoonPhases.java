package com.chinesebay.ccal4;

/** Meeus lunar-phase calculations ported from moonphase.cpp. */
final class MoonPhases {
    private MoonPhases() {
    }

    static double phaseByLunation(int lunation, int phase) {
        return phase(lunation + phase / 4.0, phase);
    }

    static double phase(double k, int phase) {
        double t = k / 1236.85;
        double jde = 2451550.09766 + 29.530588861 * k
                + t * t * (0.00015437 + t * (-0.000000150 + 0.00000000073 * t));
        double eccentricity = 1.0 + t * (-0.002516 - 0.0000074 * t);
        double meanAnomalySun = radians(2.5534 + 29.10535670 * k
                + t * t * (-0.0000014 - 0.00000011 * t));
        double meanAnomalyMoon = radians(201.5643 + 385.81693528 * k
                + t * t * (0.0107582 + t * (0.00001238 - 0.000000058 * t)));
        double argumentLatitude = radians(160.7108 + 390.67050284 * k
                + t * t * (-0.0016118 * t * (-0.00000227 + 0.000000011 * t)));
        double ascendingNode = radians(124.7746 - 1.56375588 * k
                + t * t * (0.0020672 + 0.00000215 * t));
        double[] arguments = new double[15];
        arguments[1] = radians(299.77 + 0.107408 * k - 0.009173 * t * t);
        arguments[2] = radians(251.88 + 0.016321 * k);
        arguments[3] = radians(251.83 + 26.651886 * k);
        arguments[4] = radians(349.42 + 36.412478 * k);
        arguments[5] = radians(84.66 + 18.206239 * k);
        arguments[6] = radians(141.74 + 53.303771 * k);
        arguments[7] = radians(207.14 + 2.453732 * k);
        arguments[8] = radians(154.84 + 7.306860 * k);
        arguments[9] = radians(34.52 + 27.261239 * k);
        arguments[10] = radians(207.19 + 0.121824 * k);
        arguments[11] = radians(291.34 + 1.844379 * k);
        arguments[12] = radians(161.72 + 24.198154 * k);
        arguments[13] = radians(239.56 + 25.513099 * k);
        arguments[14] = radians(331.55 + 3.592518 * k);

        if (phase == 0 || phase == 2) {
            double primary = phase == 0 ? 0.40720 : 0.40614;
            double sun = phase == 0 ? 0.17241 : 0.17302;
            double moon2 = phase == 0 ? 0.01608 : 0.01614;
            double latitude2 = phase == 0 ? 0.01039 : 0.01043;
            double minus = phase == 0 ? 0.00739 : 0.00734;
            double plus = phase == 0 ? 0.00514 : 0.00515;
            double sun2 = phase == 0 ? 0.00208 : 0.00209;
            jde = jde
                    - primary * Math.sin(meanAnomalyMoon)
                    + sun * eccentricity * Math.sin(meanAnomalySun)
                    + moon2 * Math.sin(2.0 * meanAnomalyMoon)
                    + latitude2 * Math.sin(2.0 * argumentLatitude)
                    + minus * eccentricity * Math.sin(meanAnomalyMoon - meanAnomalySun)
                    - plus * eccentricity * Math.sin(meanAnomalyMoon + meanAnomalySun)
                    + sun2 * eccentricity * eccentricity * Math.sin(2.0 * meanAnomalySun)
                    - 0.00111 * Math.sin(meanAnomalyMoon - 2.0 * argumentLatitude)
                    - 0.00057 * Math.sin(meanAnomalyMoon + 2.0 * argumentLatitude)
                    + 0.00056 * eccentricity * Math.sin(2.0 * meanAnomalyMoon + meanAnomalySun)
                    - 0.00042 * Math.sin(3.0 * meanAnomalyMoon)
                    + 0.00042 * eccentricity * Math.sin(meanAnomalySun + 2.0 * argumentLatitude)
                    + 0.00038 * eccentricity * Math.sin(meanAnomalySun - 2.0 * argumentLatitude)
                    - 0.00024 * eccentricity * Math.sin(2.0 * meanAnomalyMoon - meanAnomalySun)
                    - 0.00017 * Math.sin(ascendingNode)
                    - 0.00007 * Math.sin(meanAnomalyMoon + 2.0 * meanAnomalySun)
                    + 0.00004 * Math.sin(2.0 * meanAnomalyMoon - 2.0 * argumentLatitude)
                    + 0.00004 * Math.sin(3.0 * meanAnomalySun)
                    + 0.00003 * Math.sin(meanAnomalyMoon + meanAnomalySun - 2.0 * argumentLatitude)
                    + 0.00003 * Math.sin(2.0 * meanAnomalyMoon + 2.0 * argumentLatitude)
                    - 0.00003 * Math.sin(meanAnomalyMoon + meanAnomalySun + 2.0 * argumentLatitude)
                    + 0.00003 * Math.sin(meanAnomalyMoon - meanAnomalySun + 2.0 * argumentLatitude)
                    - 0.00002 * Math.sin(meanAnomalyMoon - meanAnomalySun - 2.0 * argumentLatitude)
                    - 0.00002 * Math.sin(3.0 * meanAnomalyMoon + meanAnomalySun)
                    + 0.00002 * Math.sin(4.0 * meanAnomalyMoon);
        } else if (phase == 1 || phase == 3) {
            jde = jde
                    - 0.62801 * Math.sin(meanAnomalyMoon)
                    + 0.17172 * eccentricity * Math.sin(meanAnomalySun)
                    - 0.01183 * eccentricity * Math.sin(meanAnomalyMoon + meanAnomalySun)
                    + 0.00862 * Math.sin(2.0 * meanAnomalyMoon)
                    + 0.00804 * Math.sin(2.0 * argumentLatitude)
                    + 0.00454 * eccentricity * Math.sin(meanAnomalyMoon - meanAnomalySun)
                    + 0.00204 * eccentricity * eccentricity * Math.sin(2.0 * meanAnomalySun)
                    - 0.00180 * Math.sin(meanAnomalyMoon - 2.0 * argumentLatitude)
                    - 0.00070 * Math.sin(meanAnomalyMoon + 2.0 * argumentLatitude)
                    - 0.00040 * Math.sin(3.0 * meanAnomalyMoon)
                    - 0.00034 * eccentricity * Math.sin(2.0 * meanAnomalyMoon - meanAnomalySun)
                    + 0.00032 * eccentricity * Math.sin(meanAnomalySun + 2.0 * argumentLatitude)
                    + 0.00032 * eccentricity * Math.sin(meanAnomalySun - 2.0 * argumentLatitude)
                    - 0.00028 * eccentricity * eccentricity * Math.sin(meanAnomalyMoon + 2.0 * meanAnomalySun)
                    + 0.00027 * eccentricity * Math.sin(2.0 * meanAnomalyMoon + meanAnomalySun)
                    - 0.00017 * Math.sin(ascendingNode)
                    - 0.00005 * Math.sin(meanAnomalyMoon - meanAnomalySun - 2.0 * argumentLatitude)
                    + 0.00004 * Math.sin(2.0 * meanAnomalyMoon + 2.0 * argumentLatitude)
                    - 0.00004 * Math.sin(meanAnomalyMoon + meanAnomalySun + 2.0 * argumentLatitude)
                    + 0.00004 * Math.sin(meanAnomalyMoon - 2.0 * meanAnomalySun)
                    + 0.00003 * Math.sin(meanAnomalyMoon + meanAnomalySun - 2.0 * argumentLatitude)
                    + 0.00003 * Math.sin(3.0 * meanAnomalySun)
                    + 0.00002 * Math.sin(2.0 * meanAnomalyMoon - 2.0 * argumentLatitude)
                    + 0.00002 * Math.sin(meanAnomalyMoon - meanAnomalySun + 2.0 * argumentLatitude)
                    - 0.00002 * Math.sin(3.0 * meanAnomalyMoon + meanAnomalySun);
            double w = 0.00306 - 0.00038 * eccentricity * Math.cos(meanAnomalySun)
                    + 0.00026 * Math.cos(meanAnomalyMoon)
                    - 0.00002 * Math.cos(meanAnomalyMoon - meanAnomalySun)
                    + 0.00002 * Math.cos(meanAnomalyMoon + meanAnomalySun)
                    + 0.00002 * Math.cos(2.0 * argumentLatitude);
            jde += phase == 3 ? -w : w;
        } else {
            throw new IllegalArgumentException("phase must be between 0 and 3");
        }

        for (int i = 1; i <= 14; i++) {
            double[] coefficients = {0.0, 0.000325, 0.000165, 0.000164, 0.000126,
                    0.000110, 0.000062, 0.000060, 0.000056, 0.000047, 0.000042,
                    0.000040, 0.000037, 0.000035, 0.000023};
            jde += coefficients[i] * Math.sin(arguments[i]);
        }
        return jde;
    }

    private static double radians(double degrees) {
        return Math.toRadians(CalendarMath.normalizeDegrees(degrees));
    }
}
