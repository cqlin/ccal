import com.chinesebay.ccal2.*;
import java.util.ArrayList;

public class Compare2026 {
    public static void main(String[] args) {
        short year = 2026;
        SolarTerm.SolarTermResult st = SolarTerm.solarterm(year);
        ArrayList<Double> vterms = st.vjdterms;
        
        System.out.println("Comparison: C++ vs Java for 2026");
        System.out.println("Term | C++ JD         | Java JD        | Diff");
        System.out.println("-----|----------------|----------------|-------");
        
        double[] cppValues = {
            2461075.667884,  // LC index 2
            2461135.610954,  // QM index 6
            2461181.859019,  // XM index 9
            2461244.634117,  // DS index 13
            2461306.836966,  // QF index 17
            2461396.701534   // DZ index 23
        };
        int[] indices = {2, 6, 9, 13, 17, 23};
        String[] names = {"LC", "QM", "XM", "DS", "QF", "DZ"};
        
        for (int i = 0; i < indices.length; i++) {
            double javaJd = vterms.get(indices[i]);
            double diff = cppValues[i] - javaJd;
            System.out.printf("%3s  | %.6f | %.6f | %.6f\n", 
                names[i], cppValues[i], javaJd, diff);
        }
    }
}
