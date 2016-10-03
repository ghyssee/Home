package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.bo.PlaylistBO;
import be.home.mezzmo.domain.model.*;
import be.home.mezzmo.domain.service.IPodServiceImpl;
import be.home.mezzmo.domain.service.MediaMonkeyServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.common.configuration.Setup;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class SynchronizeIPodPlayCount extends BatchJobV2{

    public static IPodServiceImpl iPodService = null;
    public static MezzmoServiceImpl mezzmoService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MezzmoPlaylists.class);
    public static final String MP3_PLAYLIST = "H:/Shared/Mijn Muziek/Eric/playlist";
    public static final String[] FILE_HEADER_MAPPING = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album", "DateLastPlayedText"};

    public static void main(String args[]) {

        SynchronizeIPodPlayCount instance = new SynchronizeIPodPlayCount();
        try {
            config = instance.init();
            SQLiteJDBC.initialize();
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

        String base = WinUtils.getOneDrivePath();
        log.info("OneDrive Path: " + base);
        base += "\\Muziek\\Export\\";

        export(base, "iPodDB.PlayCount.csv");
        synchronize(base, "iPodDB.PlayCount.csv");


    }

    public void synchronize(String base, String filename) {

        List <MGOFileAlbumCompositeTO> list = getIPodService().getListPlayCount();
        //getMezzmoService().updatePlayCount()
        int errors = 0;
        for (MGOFileAlbumCompositeTO comp : list ){
            List record = new ArrayList();
            MGOFileTO fileTO = comp.getFileTO();
            MGOFileTO foundFileTO = getMezzmoService().findByTitleAndAlbum(comp);
            if (foundFileTO == null){
                log.error("Following File Not Found In The Mezzmo DB: " + getFileTitle(comp));
                errors++;
            }
            else {
                int playCount = foundFileTO.getPlayCount() + fileTO.getPlayCount();
                System.out.println("FileTitle: " + getFileTitle(comp));
                System.out.println("Playcount: " + foundFileTO.getPlayCount() + " => " + playCount);
                try {
                    int count = getMezzmoService().updatePlayCount(getFileTitle(comp), comp.getFileAlbumTO().getName(), playCount, comp.getFileTO().getDateLastPlayed());
                    if (count != 1){
                        log.error("Problem updating file " + getFileTitle(comp) + " with playcount " + playCount);
                        errors++;
                        getMezzmoService().updatePlayCount(getFileTitle(comp), comp.getFileAlbumTO().getName(), playCount, comp.getFileTO().getDateLastPlayed());
                    }
                } catch (SQLException e) {
                    log.error("Problem updating file " + getFileTitle(comp) + " with playcount " + playCount);
                    errors++;
                }
            }
        }
        if (errors > 0){
            log.error("Number of errors found: " + errors);
        }

    }



    public void export(String base, String filename){

        List <MGOFileAlbumCompositeTO> list = getIPodService().getListPlayCount();
        Writer fileWriter = null;
        File exportFile = new File(base + "MP3SongsWithPlay." + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".csv");
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(exportFile);
            fileWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

            writeToExportFile(list, csvFilePrinter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        }

    }

    private String formatTrack(MGOFileTO fileTO){
        String track = String.format("%02d", fileTO.getTrack());
        if (fileTO.getDisc() > 0){
            track = String.format("%d", fileTO.getDisc());
        }
        return track;
    }

    private String getFileTitle(MGOFileAlbumCompositeTO comp){
        String fileTitle = formatTrack(comp.getFileTO()) + " " + comp.getFileArtistTO().getArtist() + " - " +
               comp.getFileTO().getTitle();
        return fileTitle;

    }

    public void writeToExportFile( List<MGOFileAlbumCompositeTO> list, CSVPrinter csvFilePrinter) throws IOException {
        //{"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};
        for (MGOFileAlbumCompositeTO comp : list ){
            List record = new ArrayList();
            MGOFileTO fileTO = comp.getFileTO();
            record.add(getFileTitle(comp));
            record.add(comp.getFileTO().getPlayCount());
            record.add("");
            record.add(SQLiteUtils.convertDateToLong(comp.getFileTO().getDateLastPlayed()));
            record.add(comp.getFileAlbumTO().getName());
            String pattern = "dd/MM/yyyy HH:mm:ss";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            record.add(format.format(comp.getFileTO().getDateLastPlayed()));
            csvFilePrinter.printRecord(record);
        }

    }

    public static IPodServiceImpl getIPodService(){

        if (iPodService == null) {
            return IPodServiceImpl.getInstance();
        }
        return iPodService;
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

