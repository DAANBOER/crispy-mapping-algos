package vsgridmaps;

import java.io.IOException;

public class VSGridMaps {

    public static void main(String[] args) throws IOException {
        if (args[0].equals("-check") && args.length >= 3) {
            ProblemInstance inst = Load.instance(args[1]);
            Load.solution(args[2], inst);

            System.out.println("Solution is valid: " + inst.isValid());
            System.out.println("Distortion: " + inst.computeScore());

        } else if (args[0].equals("-batch") && args.length >= 3) {

            ZipLoader loader = new ZipLoader(args[1]);
            ZipSaver saver = new ZipSaver(args[2]);

            for (ProblemInstance inst : loader.iterateInstances()) {
                // TODO: run some solver on instance                

                // NB: if your solver changes the order of the points in the 
                // problem instance, make sure to revert before saving!
                // Alternatively, you can also create a new list in your solver 
                // and work on that one...
                
                // TODO: uncomment and fill in your groupnumber
                //saver.addSolution(inst, groupnumber);
            }

            // NB: if you interrupt the program, the saver doesn't close and 
            // your zipfile with solutions won't be made properly
            // You may want to find a fix for that... (or don't save into a zip)
            loader.close();
            saver.close();

        } else if (args[0].equals("-run") && args.length >= 2 ) {
        	ProblemInstance inst = Load.instance(args[1]);
        	
        	inst.solveGreedily();
        	
        }
        

    }

}
