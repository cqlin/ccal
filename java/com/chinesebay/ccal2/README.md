# CCal Java Conversion

This directory contains the Java conversion of the ccal (Chinese Calendar) program originally written in C++ by Zhuo Meng.

## Overview

CCal is a program that computes and displays Chinese calendar information, including:
- Solar terms (24 Jieqi) based on the Sun's ecliptic longitude
- Lunar month boundaries (new moons)
- Leap month calculations
- Traditional Chinese calendar dates
- Stem-Branch (Tiangan-Dizhi) year designations

The conversion follows the principle: **"convert straight from C to Java, don't use external libraries"** - all astronomical calculations are implemented from scratch in pure Java.

## Converted Modules

### Core Astronomical Library (from novas folder)
- **NovasConstants.java** - Physical and astronomical constants
- **Novas.java** - Main NOVAS library with celestial mechanics calculations
  - IAU 2006 precession model
  - IAU 2000A nutation model (678 periodic terms)
  - Light aberration corrections
  - Time scale conversions (TDB ↔ TT)
  - Julian date calculations
- **NutationData.java** - IAU 2000A nutation data (7,710 coefficients)
- **SolarSystem.java** - Sun-Earth ephemeris using 50-term Bretagnon series
- **ETiltResult.java** - Result structure for Earth tilt calculations
- **TDB2TTResult.java** - Result structure for time conversions
- **CalDate.java** - Calendar date structure

### Chinese Calendar Calculation
- **MoonUtil.java** - Moon calculation utilities (degree/radian conversions)
- **MoonPhase.java** - Moon phase calculations using Jean Meeus algorithms
- **TT2UT.java** - Terrestrial Time to Universal Time converter (NASA polynomial)
- **SolarTerm.java** - Computes 24 solar terms for a given year
  - Uses iterative regula falsi method to find exact solar longitude moments
  - Adjusts for Beijing timezone (UTC+8 before 1928, UTC+8 after)
- **MPhases.java** - Moon phase wrapper for finding phases in a date range
- **LunarYear.java** - Main lunar calendar logic
  - Determines normal vs leap years (12 vs 13 months)
  - Identifies leap months using Zhongqi (major solar term) rules
  - Computes month numbers and boundaries

### Output Formatters
- **HtmlMonth.java** - HTML calendar output with Chinese characters (UTF-8)
  - Supports simplified and traditional Chinese characters
  - Converts lunar dates to Chinese character representation
- **PSMonth.java** - PostScript output (STUB - not fully implemented)

### Optimization (Optional)
- **YearCache.java** - Precomputed data cache (STUB - for years 1900-2100)
  - Not implemented; all calculations done on-demand
  - Can be enhanced by porting large data arrays from yearcache.cpp

### Main Program
- **CCal.java** - Main entry point
  - Command-line interface
  - Displays solar terms and lunar months for a given year
  - Optionally shows Gregorian calendar with `-m` flag

## Compilation

All files can be compiled with:
```bash
cd java2
javac *.java
```

For files with Chinese characters (HtmlMonth.java):
```bash
javac -encoding UTF-8 HtmlMonth.java
```

## Usage

Basic usage:
```bash
java CCal [year]
```

Examples:
```bash
# Display Chinese calendar for current year
java CCal

# Display Chinese calendar for 2024
java CCal 2024

# Display with monthly Gregorian calendars
java CCal 2024 -m
```

## Technical Details

### Coordinate Systems
- **Heliocentric**: Sun-centered coordinates
- **Barycentric**: Solar system barycenter-centered
- **Geocentric**: Earth-centered coordinates

### Time Scales
- **TDB** (Barycentric Dynamical Time): Used for solar system dynamics
- **TT** (Terrestrial Time): Atomic time scale
- **UT** (Universal Time): Based on Earth's rotation
- **Julian Date**: Continuous day count from 4713 BC

### Precision
- Nutation model: IAU 2000A with 678 terms (microarcsecond precision)
- Solar ephemeris: 50-term series (accurate within arcseconds over centuries)
- Time conversion: NASA polynomials (accurate 1645-2150+)
- Solar term calculation: Iterative refinement to 1-second precision

### Chinese Calendar Rules
1. **Month Numbering**: Starts from month 11 (containing winter solstice)
2. **Leap Month**: A month without a Zhongqi (major solar term)
3. **Year Boundary**: Contains winter solstice (Dongzhi)
4. **Normal Year**: 12 lunar months between consecutive winter solstices
5. **Leap Year**: 13 lunar months between consecutive winter solstices

### Conversion Patterns (C++ → Java)
- C pointers (`double *pos`) → Java arrays (`double[] pos`) or return objects
- C structs → Java classes
- C static variables → Java static fields
- C functions → Java static methods
- C vectors (`vdouble`) → Java ArrayList<Double>
- C string manipulation → Java StringBuilder/String

## Limitations and Future Work

### Not Fully Implemented
1. **YearCache** - Precomputed data for fast lookups (1900-2100)
   - Current: All calculations done on-demand
   - Enhancement: Port 1193 lines of cached data from yearcache.cpp

2. **PSMonth** - PostScript calendar output
   - Current: Stub implementation
   - Enhancement: Port 1113 lines including embedded font definitions

3. **Advanced CCal Features** - Full command-line program
   - Current: Simplified version with basic output
   - Missing: Multiple output formats (ASCII, HTML table, XML)
   - Missing: Single month vs whole year modes
   - Missing: GB2312/BIG5 encoding support (only UTF-8)
   - Enhancement: Port full ccal.cpp (1125 lines)

### Performance
- First calculation for a year takes several seconds (computing 24 solar terms)
- Subsequent queries would benefit from implementing YearCache
- Consider memoization for frequently-queried years

## Files Overview

| File | Lines | Description |
|------|-------|-------------|
| **NovasConstants.java** | 76 | Physical constants |
| **Novas.java** | 684 | Main NOVAS library (13 methods) |
| **NutationData.java** | 1,371 | IAU 2000A nutation model data |
| **SolarSystem.java** | 476 | Solar ephemeris calculations |
| **CalDate.java** | 15 | Date structure |
| **ETiltResult.java** | ~20 | Earth tilt result structure |
| **TDB2TTResult.java** | ~15 | Time conversion result |
| **MoonUtil.java** | 33 | Moon utilities |
| **MoonPhase.java** | 209 | Moon phase algorithms |
| **TT2UT.java** | 119 | Time scale conversion |
| **SolarTerm.java** | ~193 | 24 solar terms calculator |
| **MPhases.java** | ~75 | Moon phase wrapper |
| **LunarYear.java** | ~235 | Lunar calendar logic |
| **YearCache.java** | ~55 | Cache stub |
| **HtmlMonth.java** | ~225 | HTML output formatter |
| **PSMonth.java** | ~60 | PostScript stub |
| **CCal.java** | ~210 | Main program |
| **Total** | **~4,070** | All files |

## References

1. **NOVAS** (Naval Observatory Vector Astrometry Software)
   - Version: C Edition 3.1
   - Source: U.S. Naval Observatory
   - URL: http://aa.usno.navy.mil/software/novas/

2. **Astronomical Algorithms** by Jean Meeus
   - Used for moon phase calculations (Chapter 47)

3. **Bretagnon & Simon Solar Theory**
   - Planetary Programs and Tables (1986)
   - 50-term solar ephemeris series

4. **IAU Standards**
   - IAU 2006 Precession (P03 model)
   - IAU 2000A Nutation (678-term model)
   - IERS Conventions (2003)

5. **Chinese Calendar System**
   - Traditional lunisolar calendar rules
   - Solar term definitions based on ecliptic longitude

## License

Copyright (c) 2000-2012, by Zhuo Meng (zxm8@case.edu)

Distributed under the terms of the GNU Lesser General Public License (LGPL) version 2 or later.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

## Version

- Original C++ ccal: Version 2.5.3 (and earlier)
- Java conversion: Version 3.0-java
- Conversion date: December 2025
- Java version required: Java 8 or later

## Author

Original C++ program: Zhuo Meng (zxm8@case.edu)
Java conversion: Direct translation from C++ to Java, maintaining all algorithms
