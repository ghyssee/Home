package be.home.main.tools;

import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 10/02/2017.
 */
public class AboMulChecker extends BatchJobV2 {

        private static final Logger log = getMainLog(be.home.main.tools.AboMulChecker.class);
        public List<Integer> tppns = new ArrayList();

        public static void main(String args[]) {
            AboMulChecker instance = new AboMulChecker();
            instance.start();

        }


    @Override
    public void run() {

    }

    public void start(){
//        File file = new File("C:\\Temp\\atos\\check\\ABO_MUL.F0017211");
  //      processSingleFile(file);
        String ROOT = "C:\\Temp\\atos\\check";
        FileVisitor<Path> fileProcessor = new ProcessFile();
        try {
            Files.walkFileTree(Paths.get(ROOT), fileProcessor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Integer tppn : tppns){
            System.out.println("TPPN: " + tppn);
        }
    }

    private final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override public FileVisitResult visitFile(
                Path aFile, BasicFileAttributes aAttrs
        ) throws IOException {
            File file = aFile.toFile();
            String name = file.getName();
            if (name.startsWith("ABO_MUL")) {
                processAboMul(file);
            }
            else if (name.endsWith(".MSK")){
                processAwl(file);
            }
            //System.out.println("Processing file:" + fileName);
            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) throws IOException {
            //System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }


    private void processAboMul(File file){
        System.out.println("Processing file: " + file.getAbsolutePath());
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            for (String line : lines){
                if (line.substring(0,6).equals("110903")){
                    //System.out.println("TPPN: " + line.substring(1470,1473));
                    Integer tppn = new Integer(line.substring(1470,1474));
                    if (!tppns.contains(tppn)){
                        tppns.add(tppn);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAwl(File file){
        System.out.println("Processing file: " + file.getAbsolutePath());
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            for (String line : lines){
                if (line.substring(0,6).equals("112803")){
                    //System.out.println("TPPN: " + line.substring(1470,1473));
                    Integer tppn = new Integer(line.substring(129,133));
                    if (!line.substring(143,145).equals("01")){
                        System.out.println("Lines with Code 01: " + line);
                    }
                    else if (!tppns.contains(tppn)){
                        tppns.add(tppn);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
