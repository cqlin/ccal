import com.chinesebay.ccal2.*;
import java.util.ArrayList;

public class CheckDates {
    public static void main(String[] args) {
        short year = 2026;
        
        // LC should be Feb 4, int JD = 2461075 (0.308 means ~7:24am)
        double lcJd = 2461075.308395;
        double feb3 = Novas.julianDate(year, (short)2, (short)3, 12.0);
        double feb4 = Novas.julianDate(year, (short)2, (short)4, 12.0);
        
        System.out.println("LC term: JD = " + lcJd);
        System.out.println("Feb 3 noon: JD = " + feb3 + " -> int = " + (int)feb3);
        System.out.println("Feb 4 noon: JD = " + feb4 + " -> int = " + (int)feb4);
        System.out.println("LC int cast: " + (int)lcJd);
        System.out.println("Feb 3 matches LC? " + ((int)feb3 == (int)lcJd));
        System.out.println("Feb 4 matches LC? " + ((int)feb4 == (int)lcJd));
        System.out.println();
        
        // QM should be Apr 5, int JD = 2461135 (0.238 means ~5:42am)
        double qmJd = 2461135.238529;
        double apr4 = Novas.julianDate(year, (short)4, (short)4, 12.0);
        double apr5 = Novas.julianDate(year, (short)4, (short)5, 12.0);
        
        System.out.println("QM term: JD = " + qmJd);
        System.out.println("Apr 4 noon: JD = " + apr4 + " -> int = " + (int)apr4);
        System.out.println("Apr 5 noon: JD = " + apr5 + " -> int = " + (int)apr5);
        System.out.println("QM int cast: " + (int)qmJd);
        System.out.println("Apr 4 matches QM? " + ((int)apr4 == (int)qmJd));
        System.out.println("Apr 5 matches QM? " + ((int)apr5 == (int)qmJd));
    }
}
