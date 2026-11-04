Open calendar-matched.htm in a browser. It embeds calendar.css, so it works as
a single file, with no JavaScript or build step.

The reference PDF is 2027; the supplied HTML is 2026. This example deliberately
preserves the HTML year, every date, weekday position and lunar label.

The source has one continuous table. restyle.py groups its rows into twelve
month tables and wraps the Gregorian number and lunar label in separate spans.
It preserves month-heading text as accessible headings and captions. The CSS
then supplies the four-column layout, month watermarks, typography, separators,
weekend colors and A4 print sizing.

To repeat the transformation with another ccal-generated file:
    python restyle.py SOURCE.htm OUTPUT.htm

For browser printing: choose A4, portrait, 100% scale, no margins, disable
browser headers/footers, and enable background graphics. The result should be
one page. Screen view keeps the same paper proportions and scrolls horizontally
on narrow windows.

Fonts affect exact fidelity. Bookman Old Style (or URW Bookman) is preferred
for dates; Times New Roman for the year and month watermarks; SimSun, Songti SC,
or Noto Serif CJK SC for Chinese. Install one of these Chinese fonts if your
system does not have Chinese glyph coverage. The original PDF draws Chinese
characters as vector outlines, which CSS text cannot reproduce pixel for pixel
without the matching original font.
