import com.chinesebay.ccal2.*;

public class TestTT2UT {
    public static void main(String[] args) {
        // Test TT2UT for a date in 2026
        double jd_tt = 2461075.0;  // TT for Feb 3, 2026
        double jd_ut = TT2UT.tt2ut(jd_tt);
        
        System.out.printf("JD TT:  %.6f\n", jd_tt);
        System.out.printf("JD UT:  %.6f\n", jd_ut);
        System.out.printf("Difference: %.6f days\n", jd_tt - jd_ut);
        System.out.printf("Difference: %.6f seconds\n", (jd_tt - jd_ut) * 86400);
    }
}
