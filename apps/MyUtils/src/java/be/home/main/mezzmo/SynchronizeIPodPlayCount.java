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
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class SynchronizeIPodPlayCount extends BatchJobV2{

    public static IPodServiceImpl iPodService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MezzmoPlaylists.class);
    public static final String MP3_PLAYLIST = "H:/Shared/Mijn Muziek/Eric/playlist";
    public static final String[] FILE_HEADER_MAPPING = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};

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

        //export(base, "MezzmoDB.PlayCount.V12.csv");
        Long f = 484936688L;
        if (f != null && f.longValue() != 0) {
            java.util.Date date = new java.util.Date(f);
            System.out.println(date);

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

    public void writeToExportFile( List<MGOFileAlbumCompositeTO> list, CSVPrinter csvFilePrinter) throws IOException {
        //{"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};
        for (MGOFileAlbumCompositeTO comp : list ){
            List record = new ArrayList();
            MGOFileTO fileTO = comp.getFileTO();
            String fileTitle = fileTO.getTrack() + " " + comp.getFileArtistTO().getArtist() + " - " + fileTO.getTitle();
            record.add(fileTitle);
            record.add(comp.getFileTO().getPlayCount());
            record.add("");
            record.add(SQLiteUtils.convertDateToLong(comp.getFileTO().getDateLastPlayed()));
            record.add(comp.getFileAlbumTO().getName());
            csvFilePrinter.printRecord(record);
        }

    }

    public static IPodServiceImpl getIPodService(){

        if (iPodService == null) {
            return IPodServiceImpl.getInstance();
        }
        return iPodService;
    }

}

