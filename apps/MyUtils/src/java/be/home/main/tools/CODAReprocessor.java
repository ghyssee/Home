package be.home.main.tools;

import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.domain.model.reconciliation.CODAFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 10/02/2017.
 */
public class CODAReprocessor extends BatchJobV2 {

    private static final Logger log = getMainLog(be.home.main.tools.AboMulChecker.class);
    public CODAFile codaMCFile = new CODAFile();
    public CODAFile codaVCFile = new CODAFile();
    public static final String MC_ACCOUNT = "BE96363030803705";
    public static final String VC_ACCOUNT = "BE34363194218490";
    public enum CODAType {
        MC, VC, UNKNOWN;
    }
    public CODAType codaType;


    public static void main(String args[]) {
        CODAReprocessor instance = new CODAReprocessor();
        instance.start();

    }

    @Override
    public void run() {

    }

    public void start() {
//        File file = new File("C:\\Temp\\atos\\check\\ABO_MUL.F0017211");
        //      processSingleFile(file);
        File file = new File("c:\\Temp\\elavon\\CODA_32000695_230714020604.dat");
        processCODA(file);
    }



    private void processCODA(File file){
        System.out.println("Processing file: " + file.getAbsolutePath());
        File newFile = new File(file.getAbsolutePath() + ".NEW");
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            List<String> newLines = new ArrayList<>();


            for (String line : lines){
                if (isDataLine(line)){
                    String transactionId = line.substring(95,110);
                    if (transactionId.startsWith("0")){
                        System.out.println("Old TransactionId: " + transactionId);
                        StringBuilder newTransactionId = new StringBuilder(transactionId);
                        newTransactionId.delete(8,9);
                        newTransactionId.insert(13, "9");
                        System.out.println("New TransactionId: " + newTransactionId);
                        StringBuilder string = new StringBuilder(line);
                        string.replace(95,110, newTransactionId.toString());
                        System.out.println("Old Line: " + line);
                        System.out.println("New Line: " + string);
                        newLines.add(string.toString());
                    }
                    else {
                        newLines.add(line);
                    }
                }
                else {
                    newLines.add(line);
                }
            }
            FileUtils.writeContents(newFile, newLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isDataLine(String line){
        return line.startsWith("2")  ;
    }




}
