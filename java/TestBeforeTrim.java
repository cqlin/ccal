import com.chinesebay.ccal2.*;
import java.util.ArrayList;

public class TestBeforeTrim {
    public static void main(String[] args) {
        short year = 2026;
        
        // Get raw solar term values (before trimming)
        SolarTerm.SolarTermResult st = SolarTerm.solarterm(year);
        ArrayList<Double> vterms = st.vjdterms;
        
        System.out.println("Solar terms for 2026 BEFORE trim (after fmod fix):");
        System.out.println("LC (index 2): " + vterms.get(2));
        System.out.println("QM (index 6): " + vterms.get(6));
        System.out.println("XM (index 9): " + vterms.get(9));
        
        System.out.println("\nExpected C++ values:");
        System.out.println("LC: 2461075.667884");
        System.out.println("QM: 2461135.610954");
        System.out.println("XM: 2461181.859019");
    }
}
