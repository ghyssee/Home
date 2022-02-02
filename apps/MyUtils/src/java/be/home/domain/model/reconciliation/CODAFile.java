package be.home.domain.model.reconciliation;

import be.home.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CODAFile {

    private BigDecimal totalHeaderCalcAmount;
    private BigDecimal totalFooterCalcAmount;

    private boolean skipDataLine;

    private String type;

    private String account;

    private List <String> lines = new ArrayList <String> ();

    public CODAFile(){
        this.totalHeaderCalcAmount = new BigDecimal(0);
        this.totalFooterCalcAmount = new BigDecimal(0);
        this.skipDataLine = false;
    }

    public BigDecimal getTotalHeaderCalcAmount() {
        return totalHeaderCalcAmount;
    }

    public void setTotalHeaderCalcAmount(BigDecimal totalHeaderCalcAmount) {
        this.totalHeaderCalcAmount = totalHeaderCalcAmount;
    }

    public BigDecimal getTotalFooterCalcAmount() {
        return totalFooterCalcAmount;
    }

    public void setTotalFooterCalcAmount(BigDecimal totalFooterCalcAmount) {
        this.totalFooterCalcAmount = totalFooterCalcAmount;
    }

    public void addLine(String line){
        lines.add(line);
    }

    public List <String> getLines() {
        return lines;
    }

    public void setLines(List lines) {
        this.lines = lines;
    }

    public void make() throws IOException {
        StringBuilder fileName = new StringBuilder();
        fileName.append("c:/temp/ING_");
        fileName.append(DateUtils.formatDate(new Date(), "yyMMdd"));
        fileName.append("_XX_CODA2_ICMTPF_");
        fileName.append(getType());
        fileName.append("_1.COD");

        PrintWriter pw = new PrintWriter(new FileWriter(fileName.toString()));

        pw.println(makeHeader());
        for (String line: getLines()){
            pw.println(line);
        }
        pw.println(makeFooter());
        pw.close();
    }

    public String makeHeader(){
        String header = "1" + StringUtils.repeat(' ', 127);
        header = replaceStringAt(header, 5, this.getAccount());
        String sign = "0";
        if (totalHeaderCalcAmount.signum() >= 0){
            sign = "0";
        }
        else {
            sign = "1";
        }
        //BigDecimal multiplier  = new BigDecimal("1000");
        String amount = totalHeaderCalcAmount.toString();
        amount = sign + StringUtils.leftPad(amount, 15, "0");
        header = replaceStringAt(header, 42, amount);
        return header;
    }

    public String makeFooter(){
        String footer = "8" + StringUtils.repeat(' ', 127);
        footer = replaceStringAt(footer, 4, this.getAccount());
        String sign = "0";
        if (totalFooterCalcAmount.signum() >= 0){
            sign = "0";
        }
        else {
            sign = "1";
        }
        //BigDecimal multiplier  = new BigDecimal("1000");
        String amount = totalFooterCalcAmount.toString();
        amount = sign + StringUtils.leftPad(amount, 15, "0");
        footer = replaceStringAt(footer, 41, amount);
        return footer;
    }

    public static String replaceStringAt(String s, int pos, String c) {
        int i1 = pos;
        int i2 = pos + c.length();
        return s.substring(0,i1) + c + s.substring(i2);
    }

    public boolean isSkipDataLine() {
        return skipDataLine;
    }

    public void setSkipDataLine(boolean skipDataLine) {
        this.skipDataLine = skipDataLine;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}



