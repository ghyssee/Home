package be.home.main.tools;

import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.domain.model.reconciliation.CODAFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 10/02/2017.
 */
public class LogReader extends BatchJobV2 {

    private static final Logger log = getMainLog(be.home.main.tools.AboMulChecker.class);
    public CODAFile codaMCFile = new CODAFile();
    public CODAFile codaVCFile = new CODAFile();
    public static final String MC_ACCOUNT = "BE96363030803705";
    public static final String VC_ACCOUNT = "BE34363194218490";
    public enum CODAType {
        MC, VC, UNKNOWN;
    }

    public static void main(String args[]) {
        LogReader instance = new LogReader();
        instance.start();

    }

    @Override
    public void run() {

    }

    public void startX(){
        File file = new File("C:\\Temp\\logs\\jboss_run.log.20211129.080000");
        //      processSingleFile(file);
        String ROOT = "C:\\Temp\\coda-isabel\\all";
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.contains("started fire for engineCode ")) {
                    System.out.println(line);
                }
                else if (line.contains("Automatic Match")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void start(){
        String ROOT = "C:\\Temp\\logs";
        FileVisitor<Path> fileProcessor = new ProcessFile();
        try {
            Files.walkFileTree(Paths.get(ROOT), fileProcessor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override public FileVisitResult visitFile(
                Path aFile, BasicFileAttributes aAttrs
        ) throws IOException {
            File file = aFile.toFile();
            String name = file.getName();
            System.out.println("Processing file: " + file.getAbsolutePath());
            readLog(file);
            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) throws IOException {
            //System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }

    private void readLog(File file){
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.contains("started fire for engineCode ")) {
                    System.out.println(line);
                }
                else if (line.contains("Automatic Match")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
