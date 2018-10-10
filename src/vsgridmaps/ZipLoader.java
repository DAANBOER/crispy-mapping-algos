package vsgridmaps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipLoader {

    private final ZipFile zipfile;

    public ZipLoader(String filename) throws IOException {
        zipfile = new ZipFile(filename);
    }
    
    public Iterable<ProblemInstance> iterateInstances() {
        final Enumeration<? extends ZipEntry> entries = zipfile.entries();
            
        return () -> new Iterator<ProblemInstance>() {
            
            ProblemInstance next = null;
            
            @Override
            public boolean hasNext() {
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".txt")) {
                        try {
                            Scanner scan = new Scanner(new InputStreamReader(zipfile.getInputStream(entry)));
                            next = Load.instance(scan);
                            return true;
                        } catch (IOException ex) {
                            Logger.getLogger(ZipLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
                }
                return false;
            }
            
            @Override
            public ProblemInstance next() {
                ProblemInstance answer;
                if (next != null) {
                    answer = next;
                    next = null;
                } else if (hasNext()) {
                    answer = next;
                    next = null;
                } else {
                    answer = null;
                }
                return answer;
            }
        };
    }
    
    public void close() throws IOException {
        zipfile.close();
    }

}
