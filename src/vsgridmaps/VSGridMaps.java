package vsgridmaps;

import java.io.IOException;

public class VSGridMaps {

    public static void main(String[] args) throws IOException {
        System.out.println("Program starting");
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
                    boolean succeeded = inst.solveLP();
                    if (succeeded) {
                        saver.addSolution(inst, 8);
                        System.out.println("Solved instance");
                    } else {
                        System.out.println("Instance failed to solve");
                    }
                }

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
