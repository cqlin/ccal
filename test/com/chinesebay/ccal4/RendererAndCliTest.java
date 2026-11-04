package com.chinesebay.ccal4;

import java.time.LocalDate;

final class RendererAndCliTest {
    private RendererAndCliTest() {
    }

    static void run() {
        LocalDate fixedDate = LocalDate.of(2024, 1, 1);
        CliOptions xml = CliOptions.parse(new String[] {"-x", "1", "2024"}, fixedDate);
        Assertions.equalsInt(2024, xml.year, "XML year argument");
        Assertions.equalsInt(1, xml.month, "XML month argument");
        Assertions.isTrue(xml.singleMonth, "month/year form requests one month");
        Assertions.equalsString("u", Character.toString(xml.encoding), "XML forces UTF-8");
        String xmlOutput = CalendarRenderer.render(xml, fixedDate);
        Assertions.contains(xmlOutput, "<ccal:month value=\"1\" name=\"January\" cname=\"癸卯年十二月大11日始\">",
                "January 2024 XML header regression");
        Assertions.contains(xmlOutput,
                "<ccal:day value=\"10\" cmonth=\"11\" leap=\"\" cdate=\"29\"",
                "January 10 lunar-date regression");
        Assertions.contains(xmlOutput,
                "<ccal:day value=\"11\" cmonth=\"12\" leap=\"\" cdate=\"1\"",
                "January 11 new-moon regression");

        CliOptions traditionalXml = CliOptions.parse(new String[] {"-x", "-b", "12", "2033"}, fixedDate);
        String traditionalOutput = CalendarRenderer.render(traditionalXml, fixedDate);
        Assertions.contains(traditionalOutput, "癸丑年閏十一月小22日始",
                "traditional XML retains traditional leap character");

        CliOptions postScript = CliOptions.parse(new String[] {"-p", "1", "2024"}, fixedDate);
        Assertions.contains(CalendarRenderer.render(postScript, fixedDate), "%!PS-Adobe-3.0 EPSF-3.0",
                "PostScript has an EPS header");

        String nativePostScript = CalendarRenderer.render(postScript, fixedDate);
        Assertions.contains(nativePostScript, "%%BoundingBox: 47 397 554 714",
                "monthly EPS uses the native page layout");
        Assertions.contains(nativePostScript, "/n01 {{<~", "EPS embeds native Chinese glyph definitions");
        Assertions.contains(nativePostScript, "%%Creator: ccal-2.5.3 by Zhuo Meng",
                "EPS retains the native creator metadata");

        String july = render(new String[] {"7", "2024"}, fixedDate);
        Assertions.contains(july, " 7 [ 6]Y ", "month marker deferred after July 6 solar term/new moon");
        String january2033 = render(new String[] {"1", "2033"}, fixedDate);
        Assertions.contains(january2033,
                "January 2033 (Year RenZi, Month 12D S1, Year GuiChou, Month 1X S31)",
                "January with two new moons includes both stem-branch years");
        String february2033 = render(new String[] {"2", "2033"}, fixedDate);
        Assertions.contains(february2033, "February 2033 (Year GuiChou, Month 1X)",
                "February without a new moon omits the start marker");
        Assertions.contains(render(new String[] {"9", "1900"}, fixedDate), "Month R8X S24",
                "ASCII headers put R before the leap-month number");
        String chinese = render(new String[] {"-u", "2", "2024"}, fixedDate);
        Assertions.contains(chinese, "Sun  \u65e5   Mon  \u4e00   ", "native Chinese weekday spacing");
        Assertions.contains(chinese, "10 \u6b63\u6708   ", "native Chinese month-cell padding");
        String html = render(new String[] {"-t", "-u", "2", "2024"}, fixedDate);
        Assertions.contains(html, "<table border=\"1\" cellspacing=\"1\" width=\"90%\">",
                "HTML preserves the native markup");
        Assertions.contains(html, "<tr align=\"right\">", "HTML calendar rows retain native alignment");
        Assertions.contains(render(new String[] {"-t", "-b", "2", "2024"}, fixedDate), "charset=big5",
                "HTML declares the actual output encoding");
        if (System.getProperty("os.name").startsWith("Windows")) {
            String today = render(new String[] {"1", "2024"}, fixedDate);
            Assertions.isTrue(!today.contains("\u001b[7m"), "Windows output has no ANSI highlighting");
        }

        Assertions.throwsUsage(new Assertions.ThrowingRunnable() {
            @Override
            public void run() {
                CliOptions.parse(new String[] {"13", "2024"}, fixedDate);
            }
        }, "month validation");
        Assertions.throwsUsage(new Assertions.ThrowingRunnable() {
            @Override
            public void run() {
                CliOptions.parse(new String[] {"-z"}, fixedDate);
            }
        }, "option validation");
        Assertions.equalsString("u", Character.toString(CliOptions.parse(
                new String[] {"-unknown", "2", "2024"}, fixedDate).encoding),
                "native option parsing uses the first option letter");
        Assertions.equalsInt(2024, CliOptions.parse(new String[] {"2024suffix"}, fixedDate).year,
                "numeric operands retain native atoi prefix parsing");
    }

    private static String render(String[] arguments, LocalDate today) {
        return CalendarRenderer.render(CliOptions.parse(arguments, today), today);
    }
}
