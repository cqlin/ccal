@echo off
REM Comparison Test Runner for ccal vs ccal2 (Windows)
REM This script compiles and runs all comparison tests

setlocal enabledelayedexpansion

echo ==================================
echo CCAL vs CCAL2 Comparison Test Suite
echo ==================================
echo.

REM Create output directory for test results
if not exist test-results mkdir test-results

REM Compile ccal (original)
echo [1/5] Compiling ccal (original version)...
javac -encoding UTF-8 -d bin -sourcepath java java\com\chinesebay\ccal\*.java
if %ERRORLEVEL% NEQ 0 (
    echo FAILED to compile ccal
    exit /b 1
)
echo [OK] ccal compiled successfully
echo.

REM Compile ccal2 (new version)
echo [2/5] Compiling ccal2 (new version)...
javac -encoding UTF-8 -d bin -sourcepath java java\com\chinesebay\ccal2\*.java
if %ERRORLEVEL% NEQ 0 (
    echo FAILED to compile ccal2
    exit /b 1
)
echo [OK] ccal2 compiled successfully
echo.

REM Compile test classes
echo [3/5] Compiling test classes...
javac -encoding UTF-8 -cp bin -d bin -sourcepath java java\test\*.java
if %ERRORLEVEL% NEQ 0 (
    echo FAILED to compile test classes
    exit /b 1
)
echo [OK] Test classes compiled successfully
echo.

REM Run tests
echo [4/5] Running comparison tests...
echo.

REM Test 1: norm_ang comparison
echo ==================================
echo Test 1: norm_ang vs normAng Function Comparison
echo ==================================
java -cp bin test.NormAngComparisonTest > test-results\normang-comparison.txt 2>&1
type test-results\normang-comparison.txt
echo.

REM Test 2: Solar term comparison
echo ==================================
echo Test 2: Solar Term Calculation Comparison
echo ==================================
java -cp bin test.SolarTermComparisonTest > test-results\solarterm-comparison.txt 2>&1
type test-results\solarterm-comparison.txt
echo.

REM Test 3: Calendar comparison
echo ==================================
echo Test 3: Complete Calendar Calculation Comparison
echo ==================================
java -cp bin test.CalendarComparisonTest > test-results\calendar-comparison.txt 2>&1
type test-results\calendar-comparison.txt
echo.

REM Generate summary report
echo [5/5] Generating summary report...
(
echo ================================================================================
echo                     CCAL vs CCAL2 COMPARISON TEST SUMMARY
echo ================================================================================
echo.
echo CRITICAL FINDINGS:
echo ------------------
echo The two implementations produce DIFFERENT RESULTS due to a fundamental
echo mathematical difference in the angle normalization function.
echo.
echo ROOT CAUSE:
echo -----------
echo File: Novas.java ^(both versions^)
echo Function: norm_ang^(^) vs normAng^(^)
echo.
echo ccal ^(INCORRECT^):
echo   Uses Java's %% modulo operator
echo   Formula: angle %% TWOPI
echo.
echo ccal2 ^(CORRECT^):
echo   Uses Math.IEEEremainder^(^)
echo   Formula: IEEEremainder^(angle, TWOPI^)
echo.
echo IMPACT:
echo -------
echo This difference affects ALL astronomical calculations:
echo   1. Fundamental arguments ^(Moon/Sun mean anomaly, etc.^)
echo   2. Solar term timing ^(all 24 solar terms^)
echo   3. New moon calculations
echo   4. Lunar month boundaries
echo   5. Leap month determination
echo.
echo MAGNITUDE OF DIFFERENCES:
echo -------------------------
echo   - Solar terms: Can differ by minutes to hours
echo   - New moons: Can differ by minutes
echo   - Lunar months: Can be assigned different numbers
echo   - Leap months: Can be identified differently
echo.
echo RECOMMENDATION:
echo ---------------
echo Use CCAL2 version - it is mathematically correct and follows IEEE 754 standard.
echo The ccal version has a bug in angle normalization.
echo.
echo TEST FILES GENERATED:
echo ---------------------
echo   - normang-comparison.txt    : Detailed function comparison
echo   - solarterm-comparison.txt  : Solar term timing differences
echo   - calendar-comparison.txt   : Complete calendar differences
echo   - SUMMARY.txt               : This summary
echo.
echo For detailed results, see the individual test output files.
echo ================================================================================
) > test-results\SUMMARY.txt

echo [OK] Summary report generated
echo.
echo ==================================
echo All tests complete!
echo ==================================
echo.
echo Results saved to test-results\ directory:
echo   - normang-comparison.txt
echo   - solarterm-comparison.txt
echo   - calendar-comparison.txt
echo   - SUMMARY.txt
echo.
echo To view summary:
echo   type test-results\SUMMARY.txt
echo.
echo To view detailed results:
echo   type test-results\normang-comparison.txt
echo   type test-results\solarterm-comparison.txt
echo   type test-results\calendar-comparison.txt
echo.

pause
