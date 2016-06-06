package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportPlayCount extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static final String[] FILE_HEADER_MAPPING = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(Mezzmo.class);

    public static void main(String args[]) {

        ExportPlayCount instance = new ExportPlayCount();
        try {
            config = instance.init();
            SQLiteJDBC.initialize(workingDir);
            instance.run();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        final String batchJob = "Export PlayCount";
        //log4GE = new Log4GE(config.logDir, config.movies.log);
        //log4GE.start(batchJob);
        //log4GE.info("test");
        //log4GE.addColumn("Status", 20);
        //log4GE.printHeaders();

        String base = WinUtils.getOneDrivePath();
        log.info("OneDrive Path: " + base);
        base += "\\Muziek\\Export\\";

        export(base, "MezzmoDB.PlayCount.V12.csv");

    }

    public void export(String base, String fileName) {
        System.out.println(StringUtils.repeat('*',100));
        System.out.println("Processing CSV File: " + fileName);
        System.out.println(StringUtils.repeat('*',100));
        FileReader fileReader = null;
        Writer fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        try {
            File file = new File(base + fileName);
            File exportFile = new File(base + "Export.csv");
            FileInputStream stream = new FileInputStream(file);
            final Reader reader = new InputStreamReader(new BOMInputStream(stream), StandardCharsets.UTF_8);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
            FileOutputStream outputStream = new FileOutputStream(exportFile);
            fileWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            CSVParser csvFileParser = new CSVParser(reader, csvFileFormat);
            int counter = 0;
            List<CSVRecord> list = csvFileParser.getRecords();
            if (list != null && list.size() > 1) {
                for (CSVRecord csvRecord : list.subList(1, list.size())) {
                    counter++;
                    List record = new ArrayList();
                    for (int i=0; i < FILE_HEADER_MAPPING.length; i++){
                        record.add(csvRecord.get(FILE_HEADER_MAPPING[i]));
                    }
                    csvFilePrinter.printRecord(record);
                    System.out.println("FileTitle: " + csvRecord.get("FileTitle"));
                    System.out.println("PlayCount: " + csvRecord.get("PlayCount"));
                }
                System.out.println("Nr Of Records in CSV File: " + counter);
            } else {
                System.err.println("Problem reading CSV file or file only contains header");
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null){
                try {
                    fileWriter.flush();
                    fileWriter.close();
                    if (csvFilePrinter != null){
                        csvFilePrinter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<MGOFileAlbumCompositeTO> getListOfMP3SongsWithPlayCount() {
        return null;
    }


        public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

