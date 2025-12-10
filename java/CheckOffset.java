public class CheckOffset {
    public static void main(String[] args) {
        double offs = 120.0 / 360.0;
        System.out.println("offs = " + offs + " days");
        System.out.println("offs = " + (offs * 24) + " hours");
        System.out.println();
        
        // The differences we saw:
        System.out.println("Observed differences:");
        System.out.println("LC:  0.359489 days = " + (0.359489 * 24) + " hours");
        System.out.println("QM:  0.372425 days = " + (0.372425 * 24) + " hours");
        System.out.println("XM:  0.382852 days = " + (0.382852 * 24) + " hours");
        System.out.println("Expected: 8 hours (120/360)");
    }
}
