import com.chinesebay.ccal2.*;
import java.util.ArrayList;

public class TestRounding {
    public static void main(String[] args) {
        short year = 2026;
        SolarTerm.SolarTermResult st = SolarTerm.solarterm(year);
        ArrayList<Double> vterms = st.vjdterms;
        
        String[] names = {"XH", "DH", "LC", "YS", "JZ", "CF", "QM", "GY",
                         "LX", "XM", "MZ", "XZ", "XS", "DS", "LQ", "CS",
                         "BL", "QF", "HL", "SJ", "LD", "XX", "DX", "DZ"};
        
        System.out.println("Term | JD Frac | Truncate Day | Round Day | C++ Shows");
        System.out.println("-----|---------|--------------|-----------|----------");
        
        // Check the problematic ones
        int[] check = {2, 6, 9, 13, 17, 23}; // LC, QM, XM, DS, QF, DZ
        for (int i : check) {
            double jd = vterms.get(i);
            int intDay = (int)jd;
            int roundDay = (int)Math.round(jd);
            double frac = jd - intDay;
            
            int month = (i / 2) + 1;
            double jdMonthStart = Novas.julianDate(year, (short)month, (short)1, 12.0);
            int truncDay = (int)(jd - jdMonthStart) + 1;
            int roundDayOfMonth = (int)(Math.round(jd) - jdMonthStart) + 1;
            
            System.out.printf("%3s  | %.3f   | %2d           | %2d        | ", 
                names[i], frac, truncDay, roundDayOfMonth);
            
            if (i == 2) System.out.println("4");  // LC
            else if (i == 6) System.out.println("5");  // QM  
            else if (i == 9) System.out.println("21");  // XM
            else if (i == 13) System.out.println("23");  // DS
            else if (i == 17) System.out.println("23");  // QF
            else if (i == 23) System.out.println("22");  // DZ
        }
    }
}
