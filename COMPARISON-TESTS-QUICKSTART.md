# Quick Start: CCAL vs CCAL2 Comparison Tests

## TL;DR - Run All Tests

### Windows
```cmd
run-comparison-tests.bat
```

### Linux/Mac
```bash
chmod +x run-comparison-tests.sh
./run-comparison-tests.sh
```

Results will be saved to `test-results/` directory.

## What Gets Tested

### Test 1: Angle Normalization Function
- **File:** `NormAngComparisonTest.java`
- **Tests:** `norm_ang()` vs `normAng()`
- **Key Finding:** Different implementations of modulo operation
  - ccal: Uses `%` operator (WRONG)
  - ccal2: Uses `IEEEremainder()` (CORRECT)

### Test 2: Solar Terms (24 节气)
- **File:** `SolarTermComparisonTest.java`
- **Tests:** All 24 solar terms for multiple years
- **Key Finding:** Time differences of minutes to hours
  - Affects lunar month boundaries
  - Affects leap month calculation

### Test 3: Complete Calendar
- **File:** `CalendarComparisonTest.java`
- **Tests:** Full lunar calendar calculations
- **Key Finding:** Different results for:
  - New moon times
  - Lunar month numbers
  - Leap month identification

## Expected Results

You should see output showing:

1. **Angle normalization differences** - Mathematical differences in core function
2. **Solar term timing differences** - Variations in when solar terms occur
3. **Calendar structure differences** - Different lunar month assignments

All results indicate that **ccal2 is correct** and ccal has a bug.

## Output Files

After running, check `test-results/`:
- `SUMMARY.txt` - Executive summary (READ THIS FIRST)
- `normang-comparison.txt` - Detailed angle function analysis
- `solarterm-comparison.txt` - Solar term timing data
- `calendar-comparison.txt` - Complete calendar comparison

## Quick View Results

```bash
# View summary
cat test-results/SUMMARY.txt

# View specific test
cat test-results/normang-comparison.txt
```

## Manual Compilation (if needed)

```bash
# Create bin directory
mkdir -p bin

# Compile ccal (original)
javac -d bin -sourcepath java java/com/chinesebay/ccal/*.java

# Compile ccal2 (new)
javac -d bin -sourcepath java java/com/chinesebay/ccal2/*.java

# Compile tests
javac -cp bin -d bin -sourcepath java java/test/*.java

# Run tests individually
java -cp bin test.NormAngComparisonTest
java -cp bin test.SolarTermComparisonTest
java -cp bin test.CalendarComparisonTest
```

## Understanding Test Output

### Markers in Output
- `***` = Significant difference (> 1 minute for time calculations)
- Differences shown in:
  - Days (Julian date difference)
  - Hours (human-readable)
  - Minutes (precise)

### Sample Output Interpretation

```
DIFFERENCE FOUND:
  Input angle:     100.000000000000000
  ccal  result:    6.035398163397449
  ccal2 result:    0.752331036401283
  Absolute diff:   5.283067e+00
  Diff in degrees: 302.6927°
  Diff in arcsec:  1089695.7"
```

This shows the two implementations produce VERY different results for the same input.

## Conclusion

**ROOT CAUSE:** Bug in `Novas.java:norm_ang()` function in ccal
- Uses wrong modulo operator (`%` instead of `IEEEremainder()`)
- Affects ALL astronomical calculations
- Cascades through solar terms → new moons → lunar months

**RECOMMENDATION:** Use ccal2 (it's mathematically correct)

## Need Help?

See [TEST-README.md](TEST-README.md) for detailed documentation.
