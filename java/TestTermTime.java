import com.chinesebay.ccal2.*;

public class TestTermTime {
    public static void main(String[] args) {
        short year = 2026;
        
        // LC is term #2, which is in February (month 2)
        // dstart/dend calculation from solarterm():
        int month = 2;  // month = j/2 + 1 = 2/2 + 1 = 2
        int day = 0;    // day = (j % 2) * 14 = 0
        int dstart = (int) Novas.julianDate(year, (short)month, (short)(1 + day), 12.0);
        int dend = (int) Novas.julianDate(year, (short)month, (short)(11 + day), 12.0);
        double angle = ((2 + 1) % 24) * 15.0;  // 45 degrees
        
        System.out.println("Calculating LC for 2026:");
        System.out.println("dstart: " + dstart);
        System.out.println("dend: " + dend);
        System.out.println("angle: " + angle);
        
        // This calls the internal termtime which does the iterative calculation
        // We can't call it directly, but we know:
        double offs = 120.0 / 360.0;
        System.out.println("offs: " + offs);
        
        // The Java result was:
        System.out.println("\nJava result: 2461075.308395");
        System.out.println("Java result - offs: " + (2461075.308395 - offs));
        
        // The C++ result was:
        System.out.println("\nC++ result: 2461075.667884");
        System.out.println("C++ result - offs: " + (2461075.667884 - offs));
        
        System.out.println("\nDifference: " + (2461075.667884 - 2461075.308395) + " days");
        System.out.println("This suggests termtime() itself returns different values");
    }
}
