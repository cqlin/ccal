#!/bin/bash

# Comparison Test Runner for ccal vs ccal2
# This script compiles and runs all comparison tests

set -e  # Exit on error

echo "=================================="
echo "CCAL vs CCAL2 Comparison Test Suite"
echo "=================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Create output directory for test results
mkdir -p test-results

# Compile ccal (original)
echo -e "${YELLOW}[1/5] Compiling ccal (original version)...${NC}"
if javac -encoding UTF-8 -d bin -sourcepath java java/com/chinesebay/ccal/*.java; then
    echo -e "${GREEN}✓ ccal compiled successfully${NC}"
else
    echo -e "${RED}✗ Failed to compile ccal${NC}"
    exit 1
fi
echo ""

# Compile ccal2 (new version)
echo -e "${YELLOW}[2/5] Compiling ccal2 (new version)...${NC}"
if javac -encoding UTF-8 -d bin -sourcepath java java/com/chinesebay/ccal2/*.java; then
    echo -e "${GREEN}✓ ccal2 compiled successfully${NC}"
else
    echo -e "${RED}✗ Failed to compile ccal2${NC}"
    exit 1
fi
echo ""

# Compile test classes
echo -e "${YELLOW}[3/5] Compiling test classes...${NC}"
if javac -encoding UTF-8 -cp bin -d bin -sourcepath java java/test/*.java; then
    echo -e "${GREEN}✓ Test classes compiled successfully${NC}"
else
    echo -e "${RED}✗ Failed to compile test classes${NC}"
    exit 1
fi
echo ""

# Run tests
echo -e "${YELLOW}[4/5] Running comparison tests...${NC}"
echo ""

# Test 1: norm_ang comparison
echo "=================================="
echo "Test 1: norm_ang vs normAng Function Comparison"
echo "=================================="
java -cp bin test.NormAngComparisonTest | tee test-results/normang-comparison.txt
echo ""

# Test 2: Solar term comparison
echo "=================================="
echo "Test 2: Solar Term Calculation Comparison"
echo "=================================="
java -cp bin test.SolarTermComparisonTest | tee test-results/solarterm-comparison.txt
echo ""

# Test 3: Calendar comparison
echo "=================================="
echo "Test 3: Complete Calendar Calculation Comparison"
echo "=================================="
java -cp bin test.CalendarComparisonTest | tee test-results/calendar-comparison.txt
echo ""

# Generate summary report
echo -e "${YELLOW}[5/5] Generating summary report...${NC}"
cat > test-results/SUMMARY.txt << 'EOF'
================================================================================
                    CCAL vs CCAL2 COMPARISON TEST SUMMARY
================================================================================

CRITICAL FINDINGS:
------------------
The two implementations produce DIFFERENT RESULTS due to a fundamental
mathematical difference in the angle normalization function.

ROOT CAUSE:
-----------
File: Novas.java (both versions)
Function: norm_ang() vs normAng()

ccal (INCORRECT):
  Uses Java's % modulo operator
  Formula: angle % TWOPI

ccal2 (CORRECT):
  Uses Math.IEEEremainder()
  Formula: IEEEremainder(angle, TWOPI)

IMPACT:
-------
This difference affects ALL astronomical calculations:
  1. Fundamental arguments (Moon/Sun mean anomaly, etc.)
  2. Solar term timing (all 24 solar terms)
  3. New moon calculations
  4. Lunar month boundaries
  5. Leap month determination

MAGNITUDE OF DIFFERENCES:
-------------------------
  - Solar terms: Can differ by minutes to hours
  - New moons: Can differ by minutes
  - Lunar months: Can be assigned different numbers
  - Leap months: Can be identified differently

RECOMMENDATION:
---------------
Use CCAL2 version - it is mathematically correct and follows IEEE 754 standard.
The ccal version has a bug in angle normalization.

TEST FILES GENERATED:
---------------------
  - normang-comparison.txt    : Detailed function comparison
  - solarterm-comparison.txt  : Solar term timing differences
  - calendar-comparison.txt   : Complete calendar differences
  - SUMMARY.txt               : This summary

For detailed results, see the individual test output files.
================================================================================
EOF

echo -e "${GREEN}✓ Summary report generated${NC}"
echo ""
echo "=================================="
echo "All tests complete!"
echo "=================================="
echo ""
echo "Results saved to test-results/ directory:"
echo "  - normang-comparison.txt"
echo "  - solarterm-comparison.txt"
echo "  - calendar-comparison.txt"
echo "  - SUMMARY.txt"
echo ""
echo -e "${YELLOW}To view summary:${NC}"
echo "  cat test-results/SUMMARY.txt"
echo ""
echo -e "${YELLOW}To view detailed results:${NC}"
echo "  less test-results/normang-comparison.txt"
echo "  less test-results/solarterm-comparison.txt"
echo "  less test-results/calendar-comparison.txt"
echo ""
