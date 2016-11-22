package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.CSVUtils;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.WinUtils;
import be.home.domain.model.Synchronizer;
import be.home.mezzmo.domain.model.*;
import be.home.mezzmo.domain.service.IPodServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.MP3Settings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

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
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        Map <String, MGOFileAlbumCompositeTO> map = getMezzmoService().getMaxDisc();

        export(mp3Settings.mezzmo.export.getiPod(), "iPodDB.PlayCount", map);
        synchronize(mp3Settings.mezzmo.export.getiPod(), "iPodMezzmoSynced", map);
    }

    public void synchronize(String base, String filename, Map <String, MGOFileAlbumCompositeTO> map) {

        List <MGOFileAlbumCompositeTO> list = getIPodService().getListPlayCount();
        Synchronizer synchronizer = new Synchronizer(list, map);
        try {
            synchronizer.synchronize(base, filename);
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public void export(String base, String filename, Map <String, MGOFileAlbumCompositeTO> map){

        List <MGOFileAlbumCompositeTO> list = getIPodService().getListPlayCount();
        CSVUtils csvUtils = new CSVUtils();
        CSVPrinter csvFilePrinter = null;
        File exportFile = new File(base + File.separator + filename + "." + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".csv");
        String fields[] = {"FileTitle", "PlayCount", "File", "DateLastPlayed", "Album"};

        try {
            csvFilePrinter = csvUtils.initialize(exportFile, fields);
            writeToExportFile(list, csvFilePrinter, map);
        } catch (FileNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } finally {
            csvUtils.close(csvFilePrinter);
        }

    }

    public void writeToExportFile( List<MGOFileAlbumCompositeTO> list, CSVPrinter csvFilePrinter, Map <String, MGOFileAlbumCompositeTO> map) throws IOException {
        for (MGOFileAlbumCompositeTO comp : list ){
            List record = new ArrayList();
            record.add(Synchronizer.getFileTitle(map, comp));
            record.add(comp.getFileTO().getPlayCount());
            record.add("");
            record.add(SQLiteUtils.convertDateToString(comp.getFileTO().getDateLastPlayed()));
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

