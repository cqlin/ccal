package com.chinesebay.ccal;

public class CcalData {
    public static String[] jieqi = {"XH", "DH", "LC", "YS", "JZ", "CF", "QM", "GY",
            "LX", "XM", "MZ", "XZ", "XS", "DS", "LQ", "CS",
            "BL", "QF", "HL", "SJ", "LD", "XX", "DX", "DZ"};
    public static String[] monnames = {"January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"};
    public static String[] daynames = {"Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"};
    public static String[][] daynamesGB = {{"Sun  \u65e5"}, {"Mon  \u4e00"}, {"Tue  \u4e8c"},
            {"Wed  \u4e09"}, {"Thu  \u56db"}, {"Fri  \u4e94"},
            {"Sat  \u516d"}};
    public static String[][] daynamesB5 = {{"Sun  \u65e5"}, {"Mon  \u4e00"}, {"Tue  \u4e8c"},
            {"Wed  \u4e09"}, {"Thu  \u56db"}, {"Fri  \u4e94"},
            {"Sat  \u516d"}}; // Note: This uses GB characters, original Big5 might be different. Placeholder for now.
    public static String[][] daynamesU8 = {{"Sun  \u65e5"}, {"Mon  \u4e00"},
            {"Tue  \u4e8c"}, {"Wed  \u4e09"},
            {"Thu  \u56db"}, {"Fri  \u4e94"},
            {"Sat  \u516d"}};
    public static short[] daysinmonth = {31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31};
    public static String[][] tiangan = {{"Jia"}, {"Yi"}, {"Bing"}, {"Ding"}, {"Wu"},
            {"Ji"}, {"Geng"}, {"Xin"}, {"Ren"}, {"Gui"}};
    public static String[][] dizhi = {{"Zi"}, {"Chou"}, {"Yin"}, {"Mou"}, {"Chen"}, {"Si"},
            {"Wu"}, {"Wei"}, {"Shen"}, {"You"}, {"Xu"}, {"Hai"}};

    // extern variables
    public static String[][] GBjieqi = {
        {"\u5c0f\u5bd2"}, {"\u5927\u5bd2"}, {"\u7acb\u6625"}, {"\u96e8\u6c34"}, {"\u60ca\u86f0"}, {"\u6625\u5206"}, {"\u6e05\u660e"}, {"\u8c37\u96e8"},
        {"\u7acb\u590f"}, {"\u5c0f\u6ee1"}, {"\u8292\u79cd"}, {"\u590f\u81f3"}, {"\u5c0f\u6691"}, {"\u5927\u6691"}, {"\u7acb\u79cb"}, {"\u5904\u6691"},
        {"\u767d\u9732"}, {"\u79cb\u5206"}, {"\u5bd2\u9732"}, {"\u971c\u964d"}, {"\u7acb\u51ac"}, {"\u5c0f\u96ea"}, {"\u5927\u96ea"}, {"\u51ac\u81f3"}
    };
    public static String[][] B5jieqi = {
        {"\u5c0f\u5bd2"}, {"\u5927\u5bd2"}, {"\u7acb\u6625"}, {"\u96e8\u6c34"}, {"\u9a5a\u87c4"}, {"\u6625\u5206"}, {"\u6e05\u660e"}, {"\u7a40\u96e8"},
        {"\u7acb\u590f"}, {"\u5c0f\u6eff"}, {"\u8292\u7a2e"}, {"\u590f\u81f3"}, {"\u5c0f\u6691"}, {"\u5927\u6691"}, {"\u7acb\u79cb"}, {"\u8655\u6691"},
        {"\u767d\u9732"}, {"\u79cb\u5206"}, {"\u5bd2\u9732"}, {"\u971c\u964d"}, {"\u7acb\u51ac"}, {"\u5c0f\u96ea"}, {"\u5927\u96ea"}, {"\u51ac\u81f3"}
    };
    public static String[][] U8jieqi = {
        {"\u5c0f\u5bd2"}, {"\u5927\u5bd2"}, {"\u7acb\u6625"}, {"\u96e8\u6c34"}, {"\u60ca\u86f0"}, {"\u6625\u5206"}, {"\u6e05\u660e"}, {"\u8c37\u96e8"},
        {"\u7acb\u590f"}, {"\u5c0f\u6ee1"}, {"\u8292\u79cd"}, {"\u590f\u81f3"}, {"\u5c0f\u6691"}, {"\u5927\u6691"}, {"\u7acb\u79cb"}, {"\u5904\u6691"},
        {"\u767d\u9732"}, {"\u79cb\u5206"}, {"\u5bd2\u9732"}, {"\u971c\u964d"}, {"\u7acb\u51ac"}, {"\u5c0f\u96ea"}, {"\u5927\u96ea"}, {"\u51ac\u81f3"}
    };
    public static String[][] GBmiscchar = {
        {"\u521d"}, {"\u4e00"}, {"\u4e8c"}, {"\u4e09"}, {"\u56db"}, {"\u4e94"}, {"\u516d"}, {"\u4e03"}, {"\u516b"}, {"\u4e5d"}, {"\u5341"},
        {"\u5eff"}, {"\u6b63"}, {"\u95f0"}, {"\u6708"}, {"\u65e5"}, {"\u5e74"}, {"\u5927"}, {"\u5c0f"}, {"\uff0c"}, {"\u3000"}, {"\u59cb"}
    };
    public static String[][] B5miscchar = {
        {"\u521d"}, {"\u4e00"}, {"\u4e8c"}, {"\u4e09"}, {"\u56db"}, {"\u4e94"}, {"\u516d"}, {"\u4e03"}, {"\u516b"}, {"\u4e5d"}, {"\u5341"},
        {"\u5eff"}, {"\u6b63"}, {"\u958f"}, {"\u6708"}, {"\u65e5"}, {"\u5e74"}, {"\u5927"}, {"\u5c0f"}, {"\uff0c"}, {"\u3000"}, {"\u59cb"}
    };
    public static String[][] U8miscchar = {
        {"\u521d"}, {"\u4e00"}, {"\u4e8c"}, {"\u4e09"}, {"\u56db"}, {"\u4e94"}, {"\u516d"}, {"\u4e03"}, {"\u516b"}, {"\u4e5d"}, {"\u5341"},
        {"\u5eff"}, {"\u6b63"}, {"\u95f0"}, {"\u6708"}, {"\u65e5"}, {"\u5e74"}, {"\u5927"}, {"\u5c0f"}, {"\uff0c"}, {"  "}, {"\u59cb"}
    };
    public static String[][] PSbigmchar = {
        {"    "}, {"n11 "}, {"n12 "}, {"n13 "}, {"n14 "}, {"n15 "},
        {"n16 "}, {"n17 "}, {"n18 "}, {"n19 "}, {"n1a "}, {"    "},
        {"m12 "}, {"m13 "}, {"m14 "}, {"m15 "}, {"m16 "}, {"m17 "},
        {"m18 "}, {"m19 "}, {"m1a "}, {"m1b "}
    };
    public static String[][] GBtiangan = {
        {"\u7532"}, {"\u4e59"}, {"\u4e19"}, {"\u4e01"}, {"\u620a"}, {"\u5df1"}, {"\u5e9a"}, {"\u8f9b"}, {"\u58ec"}, {"\u7678"}
    };
    public static String[][] B5tiangan = {
        {"\u7532"}, {"\u4e59"}, {"\u4e19"}, {"\u4e01"}, {"\u620a"}, {"\u5df1"}, {"\u5e9a"}, {"\u8f9b"}, {"\u58ec"}, {"\u7678"}
    };
    public static String[][] U8tiangan = {
        {"\u7532"}, {"\u4e59"}, {"\u4e19"}, {"\u4e01"}, {"\u620a"}, {"\u5df1"}, {"\u5e9a"}, {"\u8f9b"}, {"\u58ec"}, {"\u7678"}
    };
    public static String[][] GBdizhi = {
        {"\u5b50"}, {"\u4e11"}, {"\u5bc5"}, {"\u536f"}, {"\u8fb0"}, {"\u5df3"}, {"\u5348"}, {"\u672a"}, {"\u7533"}, {"\u9149"}, {"\u620c"}, {"\u4ea5"}
    };
    public static String[][] B5dizhi = {
        {"\u5b50"}, {"\u4e11"}, {"\u5bc5"}, {"\u536f"}, {"\u8fb0"}, {"\u5df3"}, {"\u5348"}, {"\u672a"}, {"\u7533"}, {"\u9149"}, {"\u620c"}, {"\u4ea5"}
    };
    public static String[][] U8dizhi = {
        {"\u5b50"}, {"\u4e11"}, {"\u5bc5"}, {"\u536f"}, {"\u8fb0"}, {"\u5df3"}, {"\u5348"}, {"\u672a"}, {"\u7533"}, {"\u9149"}, {"\u620c"}, {"\u4ea5"}
    };

    public static String[][] U8DiffSim = {
        {"\u60ca"}, {"\u86f0"}, {"\u8c37"}, {"\u6ee1"}, {"\u79cd"}, {"\u5904"}, {"\u95f0"}
    };
    public static String[][] U8DiffTra = {
        {"\u9a5a"}, {"\u87c4"}, {"\u7a40"}, {"\u6eff"}, {"\u7a2e"}, {"\u8655"}, {"\u958f"}
    };
}