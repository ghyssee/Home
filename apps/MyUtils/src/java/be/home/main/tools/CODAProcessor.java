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
import java.util.List;

/**
 * Created by ghyssee on 10/02/2017.
 */
public class CODAProcessor extends BatchJobV2 {

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
        CODAProcessor instance = new CODAProcessor();
        instance.start();

    }

    @Override
    public void run() {

    }

    public void start(){
//        File file = new File("C:\\Temp\\atos\\check\\ABO_MUL.F0017211");
        //      processSingleFile(file);
        String ROOT = "C:\\Temp\\coda-isabel\\6";
        FileVisitor<Path> fileProcessor = new ProcessFile();
        try {
            Files.walkFileTree(Paths.get(ROOT), fileProcessor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(StringUtils.repeat('-', 80));
        List<String> test = codaMCFile.getLines();
        for (String line: test){
            System.out.println((String)line);
        }
        System.out.println(StringUtils.repeat('-', 80));
        test = codaVCFile.getLines();
        for (String line: test){
            System.out.println((String)line);
        }
        try {
            //codaMCFile.make();
            if (codaMCFile.getAccount() != null) {
                codaMCFile.make();
            }
            if (codaVCFile.getAccount() != null) {
                codaVCFile.make();
            }
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
            //System.out.println("Processing file: " + file.getAbsolutePath());
            processCODA(file);
            return FileVisitResult.CONTINUE;
        }

        @Override  public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) throws IOException {
            //System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }
    }


    private void processCODA(File file){
        System.out.println("Processing file: " + file.getAbsolutePath());
        boolean accountMC = false;
        boolean accountVC = false;
        String sign = null;
        String totalAmount;
        BigDecimal calcAmount = new BigDecimal("0");
        BigDecimal divider  = new BigDecimal("1000");
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);

            // MC Account = BE96363030803705
            // VC Account = BE34363194218490
            for (String line : lines){
                if (isHeader(line)){
                    if (isMCAccount(line)){
                        codaType = CODAType.MC;
                        codaMCFile.setAccount(MC_ACCOUNT);
                        codaMCFile.setType("MC");

                    }
                    else if (isVCAccount(line)){
                        codaType = CODAType.VC;
                        codaVCFile.setAccount(VC_ACCOUNT);;
                        codaVCFile.setType("VC");
                    }
                    else {
                        codaType = CODAType.UNKNOWN;
                    }
                    if (codaType == codaType.MC){
                        processHeader(line, codaMCFile);
                        System.out.println("totalHeaderCalcAmount: " + codaMCFile.getTotalHeaderCalcAmount().toString());
                    }
                    else if (codaType == codaType.VC){
                        processHeader(line, codaVCFile);
                        System.out.println("totalHeaderCalcAmount: " + codaVCFile.getTotalHeaderCalcAmount().toString());
                    }
                    else {
                        // Not an MC or VC Account
                        System.out.println("Skipping file " + file.getAbsolutePath());
                        break;
                    }
                }
                else if (isFooter(line)){
                    if (codaType == codaType.MC){
                        processFooter(line, codaMCFile);
                        System.out.println("totalFooterCalcAmount: " + codaMCFile.getTotalFooterCalcAmount().toString());
                    }
                    else if (codaType == codaType.VC){
                        processFooter(line, codaVCFile);
                        System.out.println("totalFooterCalcAmount: " + codaVCFile.getTotalFooterCalcAmount().toString());
                    }
                }
                else if (isDataLine(line)){
                    if (codaType == codaType.MC){
                        processDataLine(line, codaMCFile);
                    }
                    else if (codaType == codaType.VC){
                        processDataLine(line, codaVCFile);
                    }
                    System.out.println(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processDataLine(String line, CODAFile codaFile){
        if(line.startsWith("21")){
            String transactionCode = getTransactionCode(line);
            if (transactionCode.equalsIgnoreCase("00150000")) {
                codaFile.setSkipDataLine(false);
                codaFile.addLine(line);
            }
            else {
                System.out.println("Skipping dataline " + line);
                codaFile.setSkipDataLine(true);
            }
        }
        else if (codaFile.isSkipDataLine()){
            System.out.println("Skipping extra dataline " + line);
        }
        else {
            codaFile.addLine(line);
        }
    }

    private String getTransactionCode(String line){
        String transCode = null;
        if (line.startsWith("21")){
            transCode = line.substring(53,61);
            System.out.println("transactionCode = " + transCode);
        }
        return transCode;
    }

    private boolean isHeader(String line){
        return line.startsWith("1")  ;
    }
    private boolean isFooter(String line){
        return line.startsWith("8")  ;
    }
    private boolean isDataLine(String line){
        return line.startsWith("2")  ;
    }

    private boolean isMCAccount(String line){
        String account =  line.substring(5,21);
        return account.equalsIgnoreCase(MC_ACCOUNT);
    }
    private boolean isVCAccount(String line){
        String account =  line.substring(5,21);
        return account.equalsIgnoreCase(VC_ACCOUNT);
    }

    private void processHeader(String line, CODAFile codaFile){
        String sign = line.substring(42,43);
        String account =  line.substring(5,21);
        System.out.println("Account: " + account);
        System.out.println("sign: " + sign);
        String totalAmount = line.substring(43,58);
        BigDecimal calcAmount = new BigDecimal(totalAmount);
        if (sign.equals("1")){
            codaFile.setTotalHeaderCalcAmount(codaFile.getTotalHeaderCalcAmount().subtract(calcAmount));
        }
        else {
            codaFile.setTotalHeaderCalcAmount(codaFile.getTotalHeaderCalcAmount().add(calcAmount));
        }
        System.out.println("totalAmount: " + totalAmount);
        System.out.println("calcAmount: " + calcAmount.toString());
        return;

    }

    private void processFooter(String line, CODAFile codaFile){
        String sign = line.substring(41,42);
        String account =  line.substring(4,20);
        System.out.println("Account: " + account);
        System.out.println("sign: " + sign);
        String totalAmount = line.substring(42,57);
        BigDecimal calcAmount = new BigDecimal(totalAmount);
        if (sign.equals("1")){
            codaFile.setTotalFooterCalcAmount(codaFile.getTotalFooterCalcAmount().subtract(calcAmount));
        }
        else {
            codaFile.setTotalFooterCalcAmount(codaFile.getTotalFooterCalcAmount().add(calcAmount));
        }
    }

}
