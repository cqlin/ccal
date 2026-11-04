package com.chinesebay.ccal4;

/** Chinese labels and numeric names used by text, HTML, and XML renderers. */
final class ChineseText {
    static final String[] STEMS = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    static final String[] BRANCHES = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    static final String[] ROMAN_STEMS = {"Jia", "Yi", "Bing", "Ding", "Wu", "Ji", "Geng", "Xin", "Ren", "Gui"};
    static final String[] ROMAN_BRANCHES = {"Zi", "Chou", "Yin", "Mou", "Chen", "Si", "Wu", "Wei", "Shen", "You", "Xu", "Hai"};
    static final String[] ASCII_SOLAR_TERMS = {"XH", "DH", "LC", "YS", "JZ", "CF", "QM", "GY",
            "LX", "XM", "MZ", "XZ", "XS", "DS", "LQ", "CS", "BL", "QF", "HL", "SJ", "LD", "XX", "DX", "DZ"};
    static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    static final String[] DAY_NAMES = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private static final String[] NUMBERS = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
    private static final String[] SIMPLIFIED_TERMS = {"小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
            "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};
    private static final String[] TRADITIONAL_TERMS = {"小寒", "大寒", "立春", "雨水", "驚蟄", "春分", "清明", "穀雨",
            "立夏", "小滿", "芒種", "夏至", "小暑", "大暑", "立秋", "處暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};

    private final boolean traditional;

    ChineseText(boolean traditional) {
        this.traditional = traditional;
    }

    String solarTerm(int index) {
        return (traditional ? TRADITIONAL_TERMS : SIMPLIFIED_TERMS)[index];
    }

    String leap() {
        return traditional ? "閏" : "闰";
    }

    String month() {
        return "月";
    }

    String day() {
        return "日";
    }

    String year() {
        return "年";
    }

    String big() {
        return "大";
    }

    String small() {
        return "小";
    }

    String start() {
        return "始";
    }

    String stemBranch(int gregorianYear) {
        int cycle = CalendarMath.floorMod(gregorianYear - 1984, 60);
        return STEMS[cycle % 10] + BRANCHES[cycle % 12];
    }

    String romanStemBranch(int gregorianYear) {
        int cycle = CalendarMath.floorMod(gregorianYear - 1984, 60);
        return ROMAN_STEMS[cycle % 10] + ROMAN_BRANCHES[cycle % 12];
    }

    String lunarMonth(double lunarMonth, int startDay, int days) {
        int number = (int) Math.floor(lunarMonth);
        StringBuilder result = new StringBuilder();
        if (LunarYear.isLeapMonth(lunarMonth)) {
            result.append(leap());
        }
        if (number > 10) {
            result.append("十");
            number -= 10;
        }
        if (number == 1 && result.length() == 0) {
            result.append("正");
        } else {
            result.append(NUMBERS[number]);
        }
        result.append(month()).append(days == 30 ? big() : small())
                .append(startDay).append(day()).append(start());
        return result.toString();
    }

    String lunarMonthShort(double lunarMonth) {
        int number = (int) Math.floor(lunarMonth);
        StringBuilder result = new StringBuilder();
        if (LunarYear.isLeapMonth(lunarMonth)) {
            result.append(leap());
        }
        if (number > 10) {
            result.append("十");
            number -= 10;
        }
        if (number == 1 && result.length() == 0) {
            result.append("正");
        } else {
            result.append(NUMBERS[number]);
        }
        return result.append(month()).toString();
    }

    String lunarDay(int lunarDay) {
        if (lunarDay < 1 || lunarDay > 30) {
            throw new IllegalArgumentException("lunar day must be between 1 and 30");
        }
        if (lunarDay <= 10) {
            return "初" + NUMBERS[lunarDay];
        }
        if (lunarDay < 20) {
            return "十" + NUMBERS[lunarDay - 10];
        }
        if (lunarDay == 20) {
            return "二十";
        }
        if (lunarDay < 30) {
            return "廿" + NUMBERS[lunarDay - 20];
        }
        return "三十";
    }
}
