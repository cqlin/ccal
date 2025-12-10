import com.chinesebay.ccal2.*;
import java.util.ArrayList;

public class TestAfterTrim {
    public static void main(String[] args) {
        short year = 2026;
        
        // Get lunar year data (which includes trimHour)
        LunarYear.LunarYearResult lunar = LunarYear.lunaryear(year);
        ArrayList<Double> vterms = lunar.vterms;
        
        System.out.println("Solar terms AFTER lunaryear (should be rounded):");
        System.out.println("LC (index 2): " + vterms.get(2));
        System.out.println("QM (index 6): " + vterms.get(6));
        System.out.println("XM (index 9): " + vterms.get(9));
    }
}
