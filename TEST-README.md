# CCAL vs CCAL2 Comparison Tests

This directory contains comprehensive test suites to compare the results between the original `ccal` and the new `ccal2` implementations.

## Quick Start

### Windows
```cmd
run-comparison-tests.bat
```

### Linux/Mac
```bash
./run-comparison-tests.sh
```

## Test Files

### 1. NormAngComparisonTest.java
**Purpose:** Compares the `norm_ang()` vs `normAng()` function implementations.

**What it tests:**
- Angle normalization with various input values
- Edge cases near 2π boundaries
- Large astronomical values
- Negative angles

**Key Finding:** The two functions produce different results due to:
- `ccal`: Uses Java's `%` operator (incorrect for IEEE 754)
- `ccal2`: Uses `Math.IEEEremainder()` (correct, IEEE 754 compliant)

### 2. SolarTermComparisonTest.java
**Purpose:** Compares solar term (节气/Jieqi) calculations for multiple years.

**What it tests:**
- Previous year's winter solstice timing
- All 24 solar terms for test years
- Time differences in Julian days, hours, and minutes

**Key Finding:** Solar terms can differ by minutes to hours, affecting:
- Lunar month determination
- Leap month calculation
- Calendar structure

### 3. CalendarComparisonTest.java
**Purpose:** Compares complete Chinese lunar calendar calculations.

**What it tests:**
- Leap month identification
- New moon timing (all new moons in a year)
- Lunar month numbering
- Overall calendar structure

**Key Finding:** Differences in solar terms cascade into:
- Different new moon times
- Different lunar month boundaries
- Different leap month determination

## Test Output

All test results are saved to `test-results/` directory:

```
test-results/
├── normang-comparison.txt      # Detailed angle function comparison
├── solarterm-comparison.txt    # Solar term timing analysis
├── calendar-comparison.txt     # Complete calendar comparison
└── SUMMARY.txt                 # Executive summary of findings
```

## Understanding the Results

### Difference Markers
- `***` marks significant differences (> 1 minute for time calculations)
- Differences are shown in:
  - Days (Julian date difference)
  - Hours (more readable for small differences)
  - Minutes (for very precise comparisons)

### Solar Term Names
The 24 solar terms tested:
1. 小寒 (XH) - Minor Cold
2. 大寒 (DH) - Major Cold
3. 立春 (LC) - Start of Spring
4. 雨水 (YS) - Rain Water
5. 惊蛰 (JZ) - Awakening of Insects
6. 春分 (CF) - Spring Equinox
7. 清明 (QM) - Pure Brightness
8. 谷雨 (GY) - Grain Rain
9. 立夏 (LX) - Start of Summer
10. 小满 (XM) - Grain Buds
11. 芒种 (MZ) - Grain in Ear
12. 夏至 (XZ) - Summer Solstice
13. 小暑 (XS) - Minor Heat
14. 大暑 (DS) - Major Heat
15. 立秋 (LQ) - Start of Autumn
16. 处暑 (CS) - End of Heat
17. 白露 (BL) - White Dew
18. 秋分 (QF) - Autumn Equinox
19. 寒露 (HL) - Cold Dew
20. 霜降 (SJ) - Descent of Frost
21. 立冬 (LD) - Start of Winter
22. 小雪 (XX) - Minor Snow
23. 大雪 (DX) - Major Snow
24. 冬至 (DZ) - Winter Solstice

## Root Cause Analysis

### The Bug in ccal

**Location:** `java/com/chinesebay/ccal/Novas.java:462`

```java
public static double norm_ang(double angle) {
    double a;
    a = angle % Novascon.TWOPI;  // ← BUG: Wrong modulo operator
    if (a < 0.0)
        a += Novascon.TWOPI;
    return (a);
}
```

### The Fix in ccal2

**Location:** `java/com/chinesebay/ccal2/Novas.java:693`

```java
public static double normAng(double angle) {
    double a;
    a = IEEEremainder(angle, NovasConstants.TWOPI);  // ← CORRECT
    if (a < 0.0) {
        a += NovasConstants.TWOPI;
    }
    return a;
}
```

### Why This Matters

The Java `%` operator and `Math.IEEEremainder()` produce different results:

| Input | % TWOPI | IEEEremainder(x, TWOPI) |
|-------|---------|-------------------------|
| 10.0  | Different | Different |
| Large values | Can accumulate errors | IEEE 754 compliant |

For astronomical calculations requiring extreme precision:
- Even tiny angle differences (microradians) accumulate
- These affect trigonometric functions (sin, cos)
- Which affect all planetary position calculations
- Leading to different solar term and moon phase times

## Recommendation

**Use ccal2** - It is mathematically correct and follows the IEEE 754 standard for floating-point arithmetic, which is critical for astronomical calculations.

The original `ccal` implementation has a subtle but significant bug in angle normalization that affects all calendar calculations.

## Manual Testing

To test specific years or scenarios:

```bash
# Compile both versions
javac -d bin -sourcepath java java/com/chinesebay/ccal/*.java
javac -d bin -sourcepath java java/com/chinesebay/ccal2/*.java
javac -cp bin -d bin -sourcepath java java/test/*.java

# Run specific test
java -cp bin test.NormAngComparisonTest
java -cp bin test.SolarTermComparisonTest
java -cp bin test.CalendarComparisonTest
```

## Further Reading

- IEEE 754 Standard for Floating-Point Arithmetic
- NOVAS (Naval Observatory Vector Astrometry Software) documentation
- Chinese Calendar Algorithm documentation
- [Java Math.IEEEremainder documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#IEEEremainder-double-double-)
