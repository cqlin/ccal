# ccal2 vs ccal.exe comparison

JDK: EXE4J_JAVA_HOME = C:\era\OpenJDK\jdk-25.0.2.
Compiled current ccal2 sources into this temporary directory; no repository source changes.
Reference executable: C:\era\Code\ccal\ccal.exe.
Compared stdout after CRLF-to-LF normalization, and exit codes.

## ASCII annual output, every year 1900-2100 inclusive
- 201 years tested.
- 191 complete outputs match exactly.
- Daily calendar cells match for all 201 years.
- 10 outputs differ only in February headers: 1900, 1911, 1938, 1957, 1976, 1995, 2014, 2033, 2071, 2090.
- ccal2 lacks the C++ special case for February with no new moon.
- Example February 2033: executable says Month 1X; Java says Month 2D S29, incorrectly advertising a February 29 in a non-leap year.
- The December 2033 leap month markers match.

## Additional ASCII annual samples
1645, 1700, 1800, 1899, 2101, 2200, 2400, 3000, 7000: exact matches.

## Other formats (February 2024)
- UTF-8 simplified/traditional text: same calendar content after removing whitespace, but header centering and lunar-month cell padding differ.
- HTML with -t -u: same content after removing whitespace, nonbreaking-space padding, and generator metadata; not an exact output match.
- XML with -x: numeric lunar date fields match on all 29 days, but Java emits empty cdatename on 27 days, including lunar New Year.
- PostScript with -p: executable exits 0; Java exits 1 with UnsupportedOperationException at PSMonth.getPSBigMChar (PSMonth.java:1080).
- GB2312/Big5 byte output also differs; CLI encoding flags do not explicitly configure Java System.out.
- Initial Windows pipe runs produced question marks for Chinese characters; UTF-8 tests were repeated with -Dstdout.encoding=UTF-8 to separate encoding from renderer differences.

## Scope
These are rendered-calendar comparisons, not verification of sub-day astronomical timestamps, nor an exhaustive test of 1645-7000.
Sourcegraph search repo:ccal file:ccal2 CCal found no repositories/results; local sources and executable were used.
Existing run-comparison-tests.bat compares Java ccal with Java ccal2, not the native executable.

## Recommended design (not implemented)
1. Port the C++ no-new-moon February header branch into CCal.printMonth.
2. Populate XML cdatename for regular lunar days and month starts.
3. Use Java character/display widths, not C++ encoded-byte widths, for padding.
4. Complete PostScript stub methods or explicitly reject unsupported output.
5. Configure output encoding to match selected CLI format.
6. Add native-executable regression comparisons for these cases after approval and creation of a feature branch.

To make **`ccal2` match `ccal.exe`**, port the missing C++ rendering behavior rather than changing astronomical calculations.

### Recommended changes

1. **Fix February headers — `CCal.java`**
   Port the special branch from `ccal.cpp`:
   ```java
   else if (month == 2 && vmoons.get(moncnt) >= jdnext)
   ```
   Use the preceding lunar month (`moncnt - 1`) and omit the start-date marker. This fixes all 10 observed annual ASCII mismatches.

2. **Populate XML day names — `CCal.java`**
   Set `cdatename` using `HtmlMonth.number2DayCH(ldcnt)` for ordinary days and lunar-month starts. Preserve solar-term names on solar-term days and reproduce C++ handling when events coincide.

3. **Correct Chinese text and HTML padding**
   Replace calculations copied from C++ byte lengths with Java character/display-width calculations. Match C++ header centering, cell spacing, and HTML `&#160;` padding. Restore generator metadata if exact output equality is required.

4. **Complete PostScript support — `PSMonth.java`**
   Port the remaining stub methods, including `getPSBigMChar()`, from `psmonth.cpp`. Then compare the complete generated output, accounting separately for dynamic metadata.

5. **Set output encoding explicitly**
   Configure `System.out` once after argument parsing:
   - `-u` or XML → UTF-8
   - `-g` → GB2312
   - `-b` → Big5

   Ensure HTML’s declared charset matches the actual encoding.

### Verification

Repeat the **1900–2100** sweep and add regression cases for February without a new moon, coincident solar terms/new moons, XML, Chinese encodings, HTML, and PostScript.

**I recommend leaving the astronomical algorithms unchanged initially:** the tested ASCII daily cells already match.

No changes made yet. Please review this approach. Before implementation, create and switch to a feature branch—the repository was on `master` during testing.