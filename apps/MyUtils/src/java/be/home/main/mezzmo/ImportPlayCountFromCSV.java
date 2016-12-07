package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileAlbumTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.MP3Settings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ImportPlayCountFromCSV extends BatchJobV2{

    public static final boolean UPDATE = true;
    public static final boolean NO_UPDATE = false;
    public static int updatedRecords = 0;
    public static final String ALBUM_INIT = "0";
    public static final String ALBUM_ENABLE = "1";
    public static final String ALBUM_DISABLE = "2";
    public static String albumStatus = ALBUM_INIT;
    public static MezzmoServiceImpl mezzmoService = null;
    public static final String[] FILE_HEADER_MAPPING = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(ImportPlayCountFromCSV.class);

    public static void main(String args[]) {

        ImportPlayCountFromCSV instance = new ImportPlayCountFromCSV();
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
        final String batchJob = "Mezzmo Synchronisation";
        //log4GE = new Log4GE(config.logDir, config.movies.log);
        //log4GE.start(batchJob);
        //log4GE.info("test");
        //log4GE.addColumn("Status", 20);
        //log4GE.printHeaders();
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        String filename = mp3Settings.mezzmo.importF.base + File.separator + mp3Settings.mezzmo.importF.filename;
        filename = Setup.replaceEnvironmentVariables(filename);
        File file = new File(filename);
        if (file.exists()) {
            processCSV(file, UPDATE);
        }
        else {
            log.error("Import File does not exist: " + file.getAbsolutePath());
        }
    }

    public void processCSV(File file, boolean update) {
        log.info(StringUtils.repeat('*', 100));
        log.info("Processing CSV File: " + file.getAbsolutePath());
        log.info(StringUtils.repeat('*', 100));
        FileReader fileReader = null;
        try {
            FileInputStream stream = new FileInputStream(file);
            final Reader reader = new InputStreamReader(new BOMInputStream(stream), StandardCharsets.UTF_8);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
            CSVParser csvFileParser = new CSVParser(reader, csvFileFormat);
            int counter = 0;
            List<CSVRecord> list = csvFileParser.getRecords();
            if (list != null && list.size() > 1) {
                List<MGOFileAlbumCompositeTO> updateList = new ArrayList<MGOFileAlbumCompositeTO>();
                List<MGOFileAlbumCompositeTO> errorList = new ArrayList<MGOFileAlbumCompositeTO>();
                for (CSVRecord csvRecord : list.subList(1, list.size())) {
                    counter++;
                    //System.out.println("FileTitle: " + csvRecord.get("FileTitle"));
                    //System.out.println("PlayCount: " + csvRecord.get("PlayCount"));
                    checkAlbumStatus(csvRecord);
                    String album = null;
                    if (albumStatus.equals( ALBUM_ENABLE)){
                        album = csvRecord.get("Album");
                    }
                    updateList.addAll(getListOfMP3FilesToUpdate(csvRecord.get("FileTitle"), csvRecord.get("File"), csvRecord.get("PlayCount"), album, csvRecord.get("DateLastPlayed"), errorList));
                }
                log.info("Nr Of Records in CSV File: " + counter);
                log.info("Base:" + file.getParent());
                listErrors(file.getParent(), errorList, csvFileFormat);
                listPlayCounts(updateList);
                if (update) {
                    updatePlayCounts(updateList);
                }
            } else {
                log.error("Problem reading CSV file or file only contains header");
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("TOTAL Nr of Records updated: " + updatedRecords);
    }

    private void checkAlbumStatus(CSVRecord csvRecord){
        if (albumStatus.equals(ALBUM_INIT))
            try {
                String test = csvRecord.get("Album");
                albumStatus = ALBUM_ENABLE;
                log.info("Album = " + test);
            }
            catch (IllegalArgumentException ex) {
                log.error(ex.getMessage());
                albumStatus = ALBUM_DISABLE;
            }

    }

    public void getFileAlbums() {
        /*
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getFileAlbum("Ultratop 50 2015%");
        for (MGOFileAlbumCompositeTO comp : list) {
            MGOFileTO fileTO = comp.getFileTO();
            MGOFileAlbumTO fileAlbumTO = comp.getFileAlbumTO();
            System.out.println("file = " + fileTO.getFile());
            System.out.println("fileTitle = " + fileTO.getFileTitle());
            System.out.println("fileAlbum = " + fileAlbumTO.getName());
            System.out.println("playCount = " + fileTO.getPlayCount());
            System.out.println("ranking = " + fileTO.getRanking());
            System.out.println("DateLastPlayed = " + fileTO.getDateLastPlayed());
        }*/
    }

    public List<MGOFileAlbumCompositeTO> getListOfMP3FilesToUpdate(String fileID, String file, String playCount, String album, String dateLastPlayed, List<MGOFileAlbumCompositeTO> errorList) {

        int iPlayCount = Integer.parseInt(playCount);
        MGOFileAlbumCompositeTO compSearchTO = new MGOFileAlbumCompositeTO();
        compSearchTO.getFileTO().setFileTitle(fileID);
        compSearchTO.getFileAlbumTO().setName(album);
        List<MGOFileTO> list = getMezzmoService().getFiles(compSearchTO);
        List<MGOFileAlbumCompositeTO> updateList = new ArrayList<MGOFileAlbumCompositeTO>();
        MGOFileAlbumCompositeTO fileAlbumCompositeTO = new MGOFileAlbumCompositeTO();
        fileAlbumCompositeTO.getFileTO().setFileTitle(fileID);
        fileAlbumCompositeTO.getFileTO().setFile(file);
        fileAlbumCompositeTO.getFileTO().setPlayCount(iPlayCount);
        fileAlbumCompositeTO.getFileAlbumTO().setName(album);
        Date lastPlayed = SQLiteUtils.convertStringToDate(dateLastPlayed);
        if (StringUtils.isBlank(dateLastPlayed)){
            lastPlayed = new Date();
        }
        fileAlbumCompositeTO.getFileTO().setDateLastPlayed(lastPlayed);


        if (list == null || list.size() == 0) {
            log.error("ERROR: FileID Not Found: " + fileID + " / Album = " + album);
            MGOFileTO errorTO = new MGOFileTO();
            errorList.add(fileAlbumCompositeTO);
        } else {
            for (MGOFileTO fileTO : list) {
                if (fileTO.getPlayCount() < iPlayCount) {
                    log.info("fileID = " + fileID);
                    log.info("CSV PlayCount = " + iPlayCount);
                    log.info("CSV Album = " + album);
                    log.info("DB PlayCount = " + fileTO.getPlayCount());
                    log.info("file = " + fileTO.getFile());
                    log.info("ranking = " + fileTO.getRanking());
                    log.info("====================================================");
                    updateList.add(fileAlbumCompositeTO);
                } else {
                    log.info("FOUND, but playCount DB is equal or bigger: " + fileID +
                            " - DB: " + fileTO.getPlayCount() + " - CSV: " + iPlayCount);
                }
            }
        }
        return updateList;
    }

    public void updatePlayCounts(List<MGOFileAlbumCompositeTO> updateList) {

        log.info("START Updating PlayCounts...");
        log.info("Total: " + updateList.size());
        for (MGOFileAlbumCompositeTO compositeTO : updateList) {
            MGOFileTO fileTo = compositeTO.getFileTO();
            try {
                log.info("UPDATING: " + formatMP3File(compositeTO));
                int rec = getMezzmoService().updatePlayCount(fileTo.getFileTitle(), compositeTO.getFileAlbumTO().getName(), fileTo.getPlayCount(), fileTo.getDateLastPlayed());
                log.info("UPDATED: " + formatMP3File(compositeTO));
                log.info("Nr Of Records updated: " + rec);
                updatedRecords += rec;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        log.info("FINISHED Updating PlayCounts...");
    }

    private String formatMP3File(MGOFileAlbumCompositeTO compositeTO){
        MGOFileTO fileTo = compositeTO.getFileTO();
        MGOFileAlbumTO albumTO = compositeTO.getFileAlbumTO();
        return fileTo.getFileTitle() + " - " + "PlayCount: " + fileTo.getPlayCount() + " - " +
                albumTO.getName();
    }

    public void listErrors(String basePath, List<MGOFileAlbumCompositeTO> updateList, CSVFormat csvFileFormat) {

        if (updateList != null && updateList.size() > 0) {
            FileWriter fileWriter = null;
            CSVPrinter csvFilePrinter = null;
            log.info("List Of ERRORS Found");
            log.info(StringUtils.repeat('=', 100));
            log.info("Total: " + updateList.size());
            try {
                String filename = basePath + File.separator + "MP3Songs.Errors." + DateFormatUtils.format(new Date(), "yyyyMMdd.HHmm") + ".csv";
                fileWriter = new FileWriter(filename);
                csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
                for (MGOFileAlbumCompositeTO fileAlbumCompositeTO : updateList) {
                    List dataRecord = new ArrayList();
                    dataRecord.add(fileAlbumCompositeTO.getFileTO().getFileTitle());
                    dataRecord.add(fileAlbumCompositeTO.getFileTO().getPlayCount());
                    dataRecord.add(fileAlbumCompositeTO.getFileTO().getFile());
                    dataRecord.add(fileAlbumCompositeTO.getFileTO().getDateLastPlayed().getTime());
                    dataRecord.add(fileAlbumCompositeTO.getFileAlbumTO().getName());
                    log.info("File: " + fileAlbumCompositeTO.getFileTO().getFileTitle());
                    log.info("PlayCount: " + fileAlbumCompositeTO.getFileTO().getPlayCount());
                    log.info("Album: " + fileAlbumCompositeTO.getFileAlbumTO().getName());
                    log.info(StringUtils.repeat('=', 100));
                    csvFilePrinter.printRecord(dataRecord);


                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (fileWriter != null){
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                        csvFilePrinter.close();
                    } catch (IOException e) {
                        log.error("Error while flushing/closing fileWriter/csvPrinter !!!");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void listPlayCounts(List<MGOFileAlbumCompositeTO> updateList) {

        log.info("List Of MP3 Files To Update...");
        log.info("Total: " + updateList.size());
        for (MGOFileAlbumCompositeTO compositeTO : updateList) {
            log.info(formatMP3File(compositeTO));
        }
    }

        public static MezzmoServiceImpl getMezzmoService(){

            if (mezzmoService == null) {
                return MezzmoServiceImpl.getInstance();
            }
            return mezzmoService;
    }
}

