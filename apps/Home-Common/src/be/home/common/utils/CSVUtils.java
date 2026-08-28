package be.home.common.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by ghyssee on 22/11/2016.
 */
public class CSVUtils {
    Writer writer = null;

    public CSVPrinter initialize(File file, String[] fields) throws IOException {
        return this.initialize(file, fields, ';');
    }

    public CSVPrinter initialize(File file, String[] fields, char delimiter) throws IOException {
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(file);
        this.writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(delimiter);
        if (fields != null) {
            csvFileFormat = csvFileFormat.withHeader(fields);
        }
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

    public InputStreamReader newReader(final Path path) throws IOException {

        return new InputStreamReader(new BOMInputStream(Files.newInputStream(path)),
                StandardCharsets.UTF_8);

    }

    public Iterable<CSVRecord> getRecords(InputStreamReader in, String... header) throws IOException {

        Iterable<CSVRecord> records = CSVFormat.RFC4180.builder()
                .setHeader(header)
                .setDelimiter(';')
                .setSkipHeaderRecord(true)
                .build()
                .parse(in);
        return records;
    }

    public CsvToBean<Object> readOpenCSV(String file, Map<String, String> map, HeaderColumnNameTranslateMappingStrategy h, Class clazz) throws FileNotFoundException {

        File csvFile = new File(file);

        Reader reader = new BufferedReader(new FileReader(csvFile));

        h.setType(clazz);
        h.setColumnMapping(map);

        CsvToBean<Object> csvReader = new CsvToBeanBuilder(reader)
                .withType(clazz)
                .withSeparator(';')
                .withSkipLines(0)
                .withIgnoreLeadingWhiteSpace(true)
                .withIgnoreEmptyLine(true)
                .withMappingStrategy(h)
                .build();
        return csvReader;

    }
}
