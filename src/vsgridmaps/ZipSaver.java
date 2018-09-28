package vsgridmaps;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipSaver {

    private final FileOutputStream fos;
    private final ZipOutputStream zos;

    public ZipSaver(String filename) throws IOException {
        fos = new FileOutputStream(filename);
        zos = new ZipOutputStream(fos);
    }

    public void addSolution(ProblemInstance inst, int groupnumber) throws IOException {
        ZipEntry zipEntry = new ZipEntry(inst.getInstancenumber() + ".txt");
        zos.putNextEntry(zipEntry);
        String result = Save.solution(inst, groupnumber);
        InputStream input = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[1024];
        int length;
        while ((length = input.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
    }

    public void close() throws IOException {
        zos.close();
        fos.close();
    }

}
