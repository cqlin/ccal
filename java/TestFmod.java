public class TestFmod {
    public static void main(String[] args) {
        double ASEC360 = 1296000.0;
        double value = 485868.249036 + 2026 * (1717915923.2178);  // Simplified for year 2026
        
        // Java IEEEremainder
        double javaResult = Math.IEEEremainder(value, ASEC360);
        
        // Simulated fmod (remainder with sign of dividend)
        double cppResult = value - Math.floor(value / ASEC360) * ASEC360;
        
        System.out.println("Value: " + value);
        System.out.println("Java IEEEremainder: " + javaResult);
        System.out.println("C++ fmod equivalent: " + cppResult);
        System.out.println("Difference: " + (javaResult - cppResult));
        
        if (Math.abs(javaResult - cppResult) > 1e-6) {
            System.out.println("\n*** SIGNIFICANT DIFFERENCE FOUND! ***");
            System.out.println("This explains the solar term calculation discrepancy!");
        }
    }
}
