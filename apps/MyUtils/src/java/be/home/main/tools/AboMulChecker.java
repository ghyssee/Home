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
        String ROOT = "C:\\Temp\\awl";
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
            if (name.startsWith("A")) {
                processAboMul(file);
            }
            else if (name.startsWith(".MLTL")){
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
                if (line.substring(0,6).startsWith("1")){ // 110903
                    //System.out.println("TPPN: " + line.substring(1470,1473));
                    try {
                        Integer tppn = new Integer(line.substring(1470, 1474));
                        if (tppn == 3795){
                            System.out.println(file.getAbsolutePath() + ":" + line);
                        }
                        if (!tppns.contains(tppn)) {
                            tppns.add(tppn);
                        }
                        findId(line);
                    }
                    catch (NumberFormatException ex){

                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findId(String line){
        if (line.substring(1364, 1378).startsWith("ATM01464")){
            //System.out.println("TTW FOUND : " + line);
            if (line.substring(489, 495).equals("004109")){
                System.out.println("Transaction number FOUND : " + line);
                if (line.substring(410, 424).equals("20180810002942")){
                    System.out.println("Transaction date FOUND : " + line.substring(410, 424));
                }
            }
        }
    }

    private void processAwl(File file){
        System.out.println("Processing file: " + file.getAbsolutePath());
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            for (String line : lines){
                if (line.substring(0,6).startsWith("1")){ // 112803
                    //System.out.println("TPPN: " + line.substring(1470,1473));
                    try {
                        Integer tppn = new Integer(line.substring(129, 133));
                        if (!line.substring(143, 145).equals("01")) {
                            //System.out.println("Lines with Code 01: " + line);
                        } else if (!tppns.contains(tppn)) {
                            tppns.add(tppn);
                        }
                    }
                    catch (NumberFormatException ex) {

                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
