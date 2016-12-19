package be.home.common.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by ghyssee on 22/11/2016.
 */
public class CSVUtils {
    Writer writer = null;

    public CSVPrinter initialize(File file, String[] fields) throws IOException {
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(file);
        this.writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(fields);
        csvFilePrinter = new CSVPrinter(this.writer, csvFileFormat);
        return csvFilePrinter;
    }

    public void close(CSVPrinter csvPrinter) {
        if (this.writer != null) {
            try {
                this.writer.flush();
                this.writer.close();
                if (csvPrinter != null) {
                    csvPrinter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
