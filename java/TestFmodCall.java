import com.chinesebay.ccal2.*;

public class TestFmodCall {
    public static void main(String[] args) {
        double t = 0.26;  // Approximate for year 2026
        double[] a = new double[5];
        
        System.out.println("Calling fundArgs with t=" + t);
        Novas.fundArgs(t, a);
        
        System.out.println("Fundamental arguments:");
        for (int i = 0; i < 5; i++) {
            System.out.println("a[" + i + "] = " + a[i]);
        }
    }
}
