package be.home.main.tools;

import be.home.common.main.BatchJobV2;
import be.home.common.utils.CSVUtils;
import be.home.common.utils.FileUtils;
import be.home.domain.model.reconciliation.CODAFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 10/02/2017.
 */
public class DoubleRemover extends BatchJobV2 {

    private static final Logger log = getMainLog(be.home.main.tools.DoubleRemover.class);
    public CODAFile codaMCFile = new CODAFile();
    public CODAFile codaVCFile = new CODAFile();
    public static final String MC_ACCOUNT = "BE96363030803705";
    public static final String VC_ACCOUNT = "BE34363194218490";
    public List<String> doubles = new ArrayList();
    public enum CODAType {
        MC, VC, UNKNOWN;
    }

    public static void main(String args[]) {
        DoubleRemover instance = new DoubleRemover();
        instance.start();

    }

    @Override
    public void run() {

    }

    public void start(){
        String ROOT = "C:\\Temp\\WALL-E";
        this.doubles = filterDoubles();
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
           String filename = aFile.getFileName().toString();
           if (filename.equalsIgnoreCase("doubles.txt")){
               System.out.println("Skipping file " + filename);
           }
           else {
               File file = aFile.toFile();
               String name = file.getName();
               System.out.println("Processing file: " + file.getAbsolutePath());
               parseFile(file);
           }
            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) throws IOException {
            //System.out.println("Processing directory:" + aDir);

            return FileVisitResult.CONTINUE;
        }
    }

    private void parseFile(File file){

        try {
            final String ID = "transactionId";
            String[] FILE_HEADER_MAPPING = {ID, "uniqueId", "txDate", "amount", "status"};
            // b6f3d1c1-a822-4a11-9832-080335173d39|524947353|031121|25.38|completed

            String filename = file.getName();
            String base = FilenameUtils.removeExtension(filename);
            String extension = FilenameUtils.getExtension(filename);
            String newFile = "C:/temp/out" + File.separator + base + "_V2" + "." + extension;

            CSVUtils csvUtils = new CSVUtils();
            org.apache.commons.csv.CSVPrinter csvFilePrinter = null;
            csvFilePrinter = csvUtils.initialize(new File(newFile), null, '|');

            FileInputStream stream = new FileInputStream(file);
            final Reader reader = new InputStreamReader(new BOMInputStream(stream), StandardCharsets.UTF_8);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING).withDelimiter('|');
            CSVParser csvFileParser = new CSVParser(reader, csvFileFormat);
            List<CSVRecord> list = csvFileParser.getRecords();
            for (CSVRecord csvRecord : list) {
                if (!isDouble(csvRecord.get(ID))){
                    csvFilePrinter.printRecord(csvRecord);
                }
                else {
                    System.out.println("Dobule found: " + csvRecord.get(ID));
                }
            }
            csvFileParser.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isDouble(String id){
        for (String value: this.doubles){
            if (id.equalsIgnoreCase(value)){
                return true;
            }

        }
        return false;

    }

    private List<String> filterDoubles(){
        File doubleFile = new File( "C:\\Temp\\WALL-E\\doubles.txt");
        List<String> doubles = null;
        List filteredDoubles = new ArrayList();
        try {
            doubles = FileUtils.getContents(doubleFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String doubleLine : doubles){
            Matcher matcher = Pattern.compile("[0-9]{8,12}//(.*?)/(.*)$").matcher(doubleLine);
            while (matcher.find()) {
                if (matcher.groupCount() >= 2) {
                    filteredDoubles.add(matcher.group(1));
                }
            }
        }
        return filteredDoubles;
    }


}
