import com.chinesebay.ccal2.*;
import java.util.ArrayList;

public class Test2024Trim {
    public static void main(String[] args) {
        short year = 2024;
        
        // Get lunar year data
        LunarYear.LunarYearResult lunar = LunarYear.lunaryear(year);
        ArrayList<Double> vterms = lunar.vterms;
        
        System.out.println("Solar terms for 2024 AFTER lunaryear:");
        System.out.println("XH (index 0): " + vterms.get(0));
        System.out.println("DH (index 1): " + vterms.get(1));
        System.out.println("LC (index 2): " + vterms.get(2));
    }
}
