package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.CSVUtils;
import be.home.common.utils.DateUtils;
import be.home.common.utils.LogUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportPlayCount extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static final String[] FILE_HEADER_MAPPING = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(ExportPlayCount.class);

    public static void main(String args[]) {

        ExportPlayCount instance = new ExportPlayCount();
        try {
            config = instance.init();
            instance.run();
        }
        catch (FileNotFoundException e){
            LogUtils.logError(log, e);
        } catch (IOException e) {
            LogUtils.logError(log, e);
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

    public void export(String base, String fileName){
        TransferObject to = new TransferObject();

        CSVUtils csvUtils = new CSVUtils();
        CSVPrinter csvFilePrinter = null;
        File exportFile = new File(base + "MP3SongsWithPlay." + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".csv");
        String fields[] = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};

        try {
            csvFilePrinter = csvUtils.initialize(exportFile, fields);
            do {
                List<MGOFileAlbumCompositeTO> list = getMezzmoService().getMP3FilesWithPlayCount(to);
                log.info("Index = " + to.getIndex());
                writeToExportFile(list, csvFilePrinter);
            }
            while (!to.isEndOfList());
        } catch (FileNotFoundException e) {
            LogUtils.logError(log, e);
        } catch (IOException e) {
            LogUtils.logError(log, e);
        } finally {
            csvUtils.close(csvFilePrinter);
        }
    }

    public void writeToExportFile( List<MGOFileAlbumCompositeTO> list, CSVPrinter csvFilePrinter) throws IOException {
        //{"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};
        for (MGOFileAlbumCompositeTO comp : list ){
            List record = new ArrayList();
            record.add(comp.getFileTO().getFileTitle());
            record.add(comp.getFileTO().getPlayCount());
            record.add(comp.getFileTO().getFile());
            record.add(SQLiteUtils.convertDateToString(comp.getFileTO().getDateLastPlayed()));
            record.add(comp.getFileAlbumTO().getName());
            csvFilePrinter.printRecord(record);
        }

    }

        public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

