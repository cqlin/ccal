package com.chinesebay.ccal4;

import java.time.LocalDate;

/** Parsed command-line options for {@link CalendarMain}. */
final class CliOptions {
    enum OutputMode { ASCII, HTML, POSTSCRIPT, XML }

    final int year;
    final int month;
    final boolean singleMonth;
    final OutputMode outputMode;
    final char encoding;
    final boolean traditionalChinese;

    private CliOptions(int year, int month, boolean singleMonth, OutputMode outputMode, char encoding,
            boolean traditionalChinese) {
        this.year = year;
        this.month = month;
        this.singleMonth = singleMonth;
        this.outputMode = outputMode;
        this.encoding = encoding;
        this.traditionalChinese = traditionalChinese;
    }

    static CliOptions parse(String[] arguments, LocalDate today) {
        if (arguments.length > 5) {
            throw new UsageException("ccal: Too many parameters.");
        }
        int year = today.getYear();
        int month = today.getMonthValue();
        boolean singleMonth = true;
        OutputMode outputMode = OutputMode.ASCII;
        char encoding = 'a';
        boolean traditionalChinese = false;
        boolean utf8 = false;
        int operands = 0;

        for (String argument : arguments) {
            if (!argument.startsWith("-") || argument.length() == 1) {
                int value;
                try {
                    value = Integer.parseInt(argument);
                } catch (NumberFormatException exception) {
                    throw new UsageException("ccal: Invalid numeric value: " + argument);
                }
                if (operands == 0) {
                    year = value;
                    singleMonth = false;
                } else if (operands == 1) {
                    month = year;
                    year = value;
                    singleMonth = true;
                } else {
                    throw new UsageException("ccal: Too many parameters.");
                }
                operands++;
                continue;
            }
            if (argument.length() != 2) {
                throw new UsageException("ccal: Unrecognized option.");
            }
            switch (argument.charAt(1)) {
                case 'x':
                    outputMode = OutputMode.XML;
                    break;
                case 'p':
                    outputMode = OutputMode.POSTSCRIPT;
                    break;
                case 't':
                    outputMode = OutputMode.HTML;
                    break;
                case 'g':
                    encoding = 'g';
                    traditionalChinese = false;
                    break;
                case 'b':
                    encoding = 'b';
                    traditionalChinese = true;
                    break;
                case 'u':
                    utf8 = true;
                    break;
                default:
                    throw new UsageException("ccal: Unrecognized option.");
            }
        }
        if (outputMode != OutputMode.POSTSCRIPT && (utf8 || outputMode == OutputMode.XML)) {
            encoding = 'u';
        }
        if (outputMode != OutputMode.ASCII && encoding == 'a') {
            encoding = 'g';
        }
        if (month < 1 || month > 12) {
            throw new UsageException("ccal: Invalid month value: month 1-12.");
        }
        if (year < 1645 || year > 7000) {
            throw new UsageException("ccal: Invalid year value: year 1645-7000.");
        }
        return new CliOptions(year, month, singleMonth, outputMode, encoding, traditionalChinese);
    }

    boolean traditionalChinese() {
        return traditionalChinese;
    }

    static final class UsageException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        UsageException(String message) {
            super(message);
        }
    }
}
