package com.chinesebay.ccal2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

/**
 * Compares the Java CLI with the original, runnable C++ executable.
 * Uses only the JDK. Run from the repository root after compiling ccal2 and
 * this class into the same output directory:
 * java -cp <classes> com.chinesebay.ccal2.NativeComparisonTest [--full] [--ccal|--ccal3|--ccal4]
 * --full also compares every ASCII annual calendar from 1900 through 2100.
 * --ccal selects com.chinesebay.ccal.Ccal instead of ccal2.
 * --ccal3 selects com.chinesebay.ccal3.Ccal instead of ccal2.
 * --ccal4 selects com.chinesebay.ccal4.CalendarMain instead of ccal2.
 */
public final class NativeComparisonTest {
    private static final int[] HEADER_YEARS = {
        1900, 1911, 1938, 1957, 1976, 1995, 2014, 2033, 2071, 2090
    };

    private NativeComparisonTest() {
    }

    public static void main(String[] args) throws Exception {
        boolean full = Arrays.asList(args).contains("--full");
        boolean original = Arrays.asList(args).contains("--ccal");
        boolean third = Arrays.asList(args).contains("--ccal3");
        boolean fourth = Arrays.asList(args).contains("--ccal4");
        for (String arg : args)
            if (!"--full".equals(arg) && !"--ccal".equals(arg) && !"--ccal3".equals(arg)
                    && !"--ccal4".equals(arg))
                throw new IllegalArgumentException("Usage: NativeComparisonTest [--full] [--ccal|--ccal3|--ccal4]");
        if ((original ? 1 : 0) + (third ? 1 : 0) + (fourth ? 1 : 0) > 1)
            throw new IllegalArgumentException("Select only one implementation");
        String mainClass = fourth ? "com.chinesebay.ccal4.CalendarMain" : third ? "com.chinesebay.ccal3.Ccal"
            : original ? "com.chinesebay.ccal.Ccal" : CCal.class.getName();
        Path executable = Paths.get("ccal.exe").toAbsolutePath();
        if (!Files.isRegularFile(executable))
            throw new IllegalStateException("Run from the repository root containing ccal.exe");

        List<String[]> cases = new ArrayList<>();
        if (full) {
            for (int year = 1900; year <= 2100; year++)
                cases.add(new String[] {Integer.toString(year)});
        } else {
            for (int year : HEADER_YEARS)
                cases.add(new String[] {Integer.toString(year)});
        }
        for (int year : new int[] {1645, 1700, 1800, 1899, 2024, 2026, 2101, 2200, 2400, 3000, 7000})
            cases.add(new String[] {Integer.toString(year)});

        String[][] formats = {
            {"-u"}, {"-g"}, {"-b"}, {"-b", "-u"},
            {"-t", "-u"}, {"-t", "-g"}, {"-t", "-b"}, {"-t", "-b", "-u"},
            {"-x"}, {"-x", "-b"}, {"-p"}, {"-p", "-b"}
        };
        // Two new moons/year labels, leap day, no February new moon, leap 11th
        // month, and an ordinary leap month. Annual cases exercise event overlaps.
        String[][] months = {{"1", "2023"}, {"2", "2024"}, {"2", "2033"}, {"12", "2033"}, {"8", "2025"}};
        for (String[] format : formats) {
            for (String[] month : months)
                cases.add(concat(format, month));
            cases.add(concat(format, new String[] {"2033"}));
        }
        if (original || third || fourth) {
            cases.add(new String[0]);
            if (!full)
                cases.add(new String[] {"2100"});
        }

        Path temporary = Files.createTempDirectory("ccal2-native-regression-");
        int failures = 0;
        try {
            for (String[] calendarArgs : cases) {
                List<String> nativeCommand = new ArrayList<>();
                nativeCommand.add(executable.toString());
                nativeCommand.addAll(Arrays.asList(calendarArgs));
                List<String> javaCommand = new ArrayList<>();
                javaCommand.add(Paths.get(System.getProperty("java.home"), "bin", "java.exe").toString());
                javaCommand.add("-cp");
                javaCommand.add(System.getProperty("java.class.path"));
                javaCommand.add(mainClass);
                javaCommand.addAll(Arrays.asList(calendarArgs));

                Result nativeResult = run(nativeCommand, temporary);
                Result javaResult = run(javaCommand, temporary);
                boolean postScript = Arrays.asList(calendarArgs).contains("-p");
                String expected = normalize(nativeResult.output, postScript);
                String actual = normalize(javaResult.output, postScript);
                // HTML intentionally has a new layout; compare every nonempty date cell,
                // in order, rather than requiring the obsolete native markup.
                if (Arrays.asList(calendarArgs).contains("-t")) {
                    if (!actual.contains("href=\"calendar.css\"") || actual.contains("style=")
                            || actual.contains("<style") || actual.contains("<script") || actual.contains("<font"))
                        throw new AssertionError("HTML must use external styling without inline scripts/styles");
                    boolean single = actual.contains("class=\"sheet single-month\"");
                    if (!single && !actual.contains("class=\"sheet annual\""))
                        throw new AssertionError("HTML is missing its calendar layout class");
                    int monthsInHtml = actual.split("<section class=\"month\"", -1).length - 1;
                    if (monthsInHtml != (single ? 1 : 12)
                            || actual.split("data-month=\"", -1).length - 1 != monthsInHtml)
                        throw new AssertionError("HTML must render a table and watermark for each month");
                    expected = htmlDates(expected);
                    actual = htmlDates(actual);
                }
                if (nativeResult.exit != 0 || javaResult.exit != 0
                        || !Arrays.equals(nativeResult.error, javaResult.error) || !expected.equals(actual)) {
                    failures++;
                    System.err.println("FAIL " + Arrays.toString(calendarArgs)
                        + " (native exit " + nativeResult.exit + ", Java exit " + javaResult.exit + ")");
                    int offset = 0;
                    while (offset < Math.min(expected.length(), actual.length())
                            && expected.charAt(offset) == actual.charAt(offset))
                        offset++;
                    System.err.println("First differing output byte: " + offset);
                    if (javaResult.error.length != 0)
                        System.err.println(new String(javaResult.error, StandardCharsets.UTF_8));
                }
            }
        } finally {
            Files.deleteIfExists(temporary.resolve("stdout"));
            Files.deleteIfExists(temporary.resolve("stderr"));
            Files.delete(temporary);
        }
        System.out.println(mainClass + ": " + (cases.size() - failures) + "/" + cases.size()
            + " comparisons passed (HTML date content; other formats exact).");
        if (failures != 0)
            throw new AssertionError(failures + " comparisons failed");
    }

    private static String htmlDates(String html) {
        Matcher cells = Pattern.compile("<td\\b[^>]*>(.*?)</td>", Pattern.DOTALL).matcher(html);
        StringBuilder result = new StringBuilder();
        while (cells.find()) {
            String label = cells.group(1).replaceAll("<[^>]*>", "")
                .replace("&#160;", "").replace("&nbsp;", "").replaceAll("[ \\t\\r\\n]", "");
            if (!label.isEmpty() && label.charAt(0) >= '0' && label.charAt(0) <= '9')
                result.append(label).append('\n');
        }
        if (result.length() == 0)
            throw new AssertionError("HTML contains no date cells");
        return result.toString();
    }

    private static String[] concat(String[] first, String[] second) {
        String[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static Result run(List<String> command, Path temporary) throws Exception {
        Path output = temporary.resolve("stdout");
        Path error = temporary.resolve("stderr");
        Process process = new ProcessBuilder(command).redirectOutput(output.toFile())
            .redirectError(error.toFile()).start();
        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            process.waitFor();
            throw new IOException("Timed out: " + command);
        }
        return new Result(process.exitValue(), Files.readAllBytes(output), Files.readAllBytes(error));
    }

    private static String normalize(byte[] output, boolean postScript) {
        // ISO-8859-1 preserves every byte, including GB2312 and Big5 output.
        String text = new String(output, StandardCharsets.ISO_8859_1).replace("\r\n", "\n");
        // Do not mask font definitions, padding, metadata, or calendar differences.
        return postScript ? text.replaceAll("(?m)^%%CreationDate:[^\n]*\n", "") : text;
    }

    private static final class Result {
        final int exit;
        final byte[] output;
        final byte[] error;

        Result(int exit, byte[] output, byte[] error) {
            this.exit = exit;
            this.output = output;
            this.error = error;
        }
    }
}
