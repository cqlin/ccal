import com.chinesebay.ccal2.*;

public class CheckJulian {
    public static void main(String[] args) {
        short year = 2026;
        
        System.out.println("Understanding Julian Dates:");
        System.out.println("Feb 3, 2026 at midnight (0h): " + Novas.julianDate(year, (short)2, (short)3, 0.0));
        System.out.println("Feb 3, 2026 at noon (12h):    " + Novas.julianDate(year, (short)2, (short)3, 12.0));
        System.out.println("Feb 4, 2026 at midnight (0h): " + Novas.julianDate(year, (short)2, (short)4, 0.0));
        System.out.println("Feb 4, 2026 at noon (12h):    " + Novas.julianDate(year, (short)2, (short)4, 12.0));
        System.out.println();
        System.out.println("LC term at: 2461075.308395 (~7:24am)");
        System.out.println("This is 0.308 days after JD 2461075.0");
        System.out.println("0.308 * 24 = " + (0.308 * 24) + " hours = ~7:24am");
        System.out.println();
        System.out.println("So LC happens on the morning of Feb 3 (7:24am)");
        System.out.println("But C++ shows it on Feb 4!");
        System.out.println();
        System.out.println("The issue: JD 2461075.0 is Feb 3 at NOON, not midnight");
        System.out.println("So JD 2461075.308 is Feb 3 at ~7:24pm (19:24), NOT 7:24am!");
        System.out.println("Wait, let me recalculate: 0.308 days FROM NOON...");
        System.out.println("NOON + (0.308 * 24) hours = 12 + 7.39 = 19:24 (7:24pm)");
        System.out.println();
        System.out.println("Actually, JD starts at noon! So:");
        System.out.println("JD 2461075.0 = Feb 3 at NOON");
        System.out.println("JD 2461075.308 = Feb 3 at NOON + 7.4 hours = Feb 3 at 19:24 (7:24pm)");
        System.out.println("JD 2461076.0 = Feb 4 at NOON");
    }
}
