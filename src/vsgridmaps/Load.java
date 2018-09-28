package vsgridmaps;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Load {

    public static ProblemInstance instance(String f) {
        return instance(new File(f));
    }

    public static ProblemInstance instance(File f) {

        try (Scanner scan = new Scanner(f)) {
            return instance(scan);

        } catch (IOException ex) {
            Logger.getLogger(Load.class.getName()).log(Level.SEVERE, null, ex);

            return null;
        }
    }

    public static ProblemInstance instance(Scanner scan) {
        ProblemInstance inst = new ProblemInstance();
        inst.setInstancenumber(scan.nextInt());

        inst.setMinx(scan.nextInt());
        inst.setMaxx(scan.nextInt());
        inst.setMiny(scan.nextInt());
        inst.setMaxy(scan.nextInt());

        WeightedPointSet wps = new WeightedPointSet();
        int n = scan.nextInt();
        int i = 0;
        while (n > 0) {
            wps.add(new WeightedPoint(scan.nextDouble(), scan.nextDouble(), scan.nextInt(), i));
            n--;
            i++;
        }
        inst.setPointset(wps);

        return inst;
    }

    public static int solution(String s, ProblemInstance inst) {
        return solution(new File(s), inst);
    }

    public static int solution(File f, ProblemInstance inst) {
        try (Scanner scan = new Scanner(f)) {

            int grp = scan.nextInt();
            int k = scan.nextInt();
            if (k != inst.getInstancenumber()) {
                System.err.println("Not the same instance!");
                return -1;
            }

            for (WeightedPoint wp : inst.getPointset()) {
                wp.setAssigned(scan.nextDouble(), scan.nextDouble());
            }

            return grp;
        } catch (IOException ex) {
            Logger.getLogger(Load.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
}
