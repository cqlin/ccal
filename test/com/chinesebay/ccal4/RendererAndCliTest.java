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

        Assertions.throwsUsage(new Assertions.ThrowingRunnable() {
            @Override
            public void run() {
                CliOptions.parse(new String[] {"13", "2024"}, fixedDate);
            }
        }, "month validation");
        Assertions.throwsUsage(new Assertions.ThrowingRunnable() {
            @Override
            public void run() {
                CliOptions.parse(new String[] {"-unknown"}, fixedDate);
            }
        }, "option validation");
    }
}
