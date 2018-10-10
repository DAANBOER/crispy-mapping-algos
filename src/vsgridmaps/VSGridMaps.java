package vsgridmaps;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class VSGridMaps {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Not enough arguments provided");
            System.exit(1);
        }

        if (args[0].equals("-check") && args.length >= 3) {
            ProblemInstance inst = Load.instance(args[1]);
            Load.solution(args[2], inst);

            System.out.println("Solution is valid: " + inst.isValid());
            System.out.println("Distortion: " + inst.computeScore());

        } else if (args[0].equals("-batch") && args.length >= 3) {

            ZipLoader loader = new ZipLoader(args[1]);
            ZipSaver saver = new ZipSaver(args[2]);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    loader.close();
                    saver.close();
                    System.out.println("Zip file probably saved correctly");
                } catch (IOException e) {
                    System.out.println("There was an error saving the zip file");
                }
            }));

            try {


                for (ProblemInstance inst : loader.iterateInstances()) {
                    // TODO: run some solver on instance

                    // NB: if your solver changes the order of the points in the
                    // problem instance, make sure to revert before saving!
                    // Alternatively, you can also create a new list in your solver
                    // and work on that one...

                    // TODO: uncomment and fill in your groupnumber
                    //saver.addSolution(inst, groupnumber);

                    sleep(5000);
                    saver.addSolution(inst, 8);
                    System.out.println("Solved instance");
                }

            } catch (InterruptedException e) {
                System.out.println("Program was interrupted");
            } finally {
                loader.close();
                saver.close();
            }


        } else if (args[0].equals("-run") && args.length >= 2 ) {
        	ProblemInstance inst = Load.instance(args[1]);
        	
        	//Do something with the instance
        	
        }
        

    }

}
