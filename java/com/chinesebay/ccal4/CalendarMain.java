package com.chinesebay.ccal4;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/** Command-line entry point for the plain-Java ccal conversion. */
public final class CalendarMain {
    public static final String VERSION = "2.5.3-java";

    private CalendarMain() {
    }

    public static void main(String[] arguments) {
        try {
            LocalDate today = LocalDate.now();
            CliOptions options = CliOptions.parse(arguments, today);
            String output = CalendarRenderer.render(options, today);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out, outputCharset(options)), true);
            writer.print(output);
            writer.flush();
        } catch (CliOptions.UsageException exception) {
            System.err.println(exception.getMessage());
            printUsage(System.err);
            System.exit(1);
        } catch (RuntimeException exception) {
            System.err.println("ccal: " + exception.getMessage());
            System.exit(1);
        }
    }

    private static Charset outputCharset(CliOptions options) {
        if (options.outputMode == CliOptions.OutputMode.XML || options.encoding == 'u') {
            return StandardCharsets.UTF_8;
        }
        if (options.encoding == 'g') {
            return Charset.forName("GB2312");
        }
        if (options.encoding == 'b') {
            return Charset.forName("Big5");
        }
        return StandardCharsets.US_ASCII;
    }

    private static void printUsage(java.io.PrintStream stream) {
        stream.println("ccal version " + VERSION + ": Displays Chinese calendar (Gregorian with Chinese dates).");
        stream.println("Usage: ccal [-t|-p|-x] [-g|-b] [-u] [[<month>] <year>].");
        stream.println("\t-t:\tGenerates HTML table output.");
        stream.println("\t-p:\tGenerates Encapsulated PostScript output.");
        stream.println("\t-x:\tGenerates XML output.");
        stream.println("\t-g:\tGenerates simplified Chinese output.");
        stream.println("\t-b:\tGenerates traditional Chinese output.");
        stream.println("\t-u:\tUses UTF-8 rather than GB or Big5 for Chinese output.");
    }
}
