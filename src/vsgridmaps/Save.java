package vsgridmaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Save {

    public static void instance(String f, ProblemInstance inst) {
        instance(new File(f), inst);
    }

    public static void instance(File f, ProblemInstance inst) {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(f))) {

            write.write("" + inst.getInstancenumber());
            write.newLine();

            write.write(inst.getMinx() + " " + inst.getMaxx() + " " + inst.getMiny() + " " + inst.getMaxy());
            write.newLine();

            write.write("" + inst.getPointset().size());
            write.newLine();

            for (WeightedPoint wp : inst.getPointset()) {
                write.write(wp.getX() + " " + wp.getY() + " " + wp.getWeight());
                write.newLine();
            }

        } catch (IOException ex) {
            Logger.getLogger(Save.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void solution(String f, ProblemInstance inst, int groupnumber) {
        solution(new File(f), inst, groupnumber);
    }

    public static void solution(File f, ProblemInstance inst, int groupnumber) {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(f))) {

            write.write(groupnumber + "");
            write.newLine();

            write.write(inst.getInstancenumber());
            write.newLine();

            for (WeightedPoint wp : inst.getPointset()) {
                write.write(wp.getAssigned_x() + " " + wp.getAssigned_y());
                write.newLine();
            }

        } catch (IOException ex) {
            Logger.getLogger(Save.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String solution(ProblemInstance inst, int groupnumber) {
        String s = groupnumber + "\n";
        s += inst.getInstancenumber() + "\n";

        for (WeightedPoint wp : inst.getPointset()) {
            s += wp.getAssigned_x() + " " + wp.getAssigned_y() + "\n";
        }
        return s;
    }
}
