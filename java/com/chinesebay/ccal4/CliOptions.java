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
            throw new UsageException(null);
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
            if (!argument.startsWith("-")) {
                int value = parseOperand(argument);
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
            // ProcessArg in ccal.cpp selects options by their first letter.
            if (argument.length() < 2) {
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
            throw new UsageException("ccal: Invalid month value: month 1-12.", false);
        }
        if (year < 1645 || year > 7000) {
            throw new UsageException("ccal: Invalid year value: year 1645-7000.", false);
        }
        return new CliOptions(year, month, singleMonth, outputMode, encoding, traditionalChinese);
    }

    private static int parseOperand(String argument) {
        // Match atoi followed by assignment to C++'s short year/month fields.
        int index = 0;
        while (index < argument.length()) {
            char value = argument.charAt(index);
            if (value != ' ' && (value < '\t' || value > '\r'))
                break;
            index++;
        }
        boolean negative = index < argument.length() && argument.charAt(index) == '-';
        if (index < argument.length() && (argument.charAt(index) == '+' || negative))
            index++;
        int value = 0;
        while (index < argument.length()) {
            char digit = argument.charAt(index++);
            if (digit < '0' || digit > '9')
                break;
            value = value * 10 + digit - '0';
        }
        return (short) (negative ? -value : value);
    }

    boolean traditionalChinese() {
        return traditionalChinese;
    }

    static final class UsageException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
        final boolean showUsage;

        UsageException(String message) {
            this(message, true);
        }

        UsageException(String message, boolean showUsage) {
            super(message);
            this.showUsage = showUsage;
        }
    }
}
