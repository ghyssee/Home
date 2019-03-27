package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.CSVUtils;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.LogUtils;
import be.home.domain.model.Synchronizer;
import be.home.mezzmo.domain.model.*;
import be.home.mezzmo.domain.service.IPodServiceImpl;
import be.home.mezzmo.domain.service.MediaMonkeyServiceImpl;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import be.home.model.json.MP3Settings;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class SynchronizeIPodPlayCount extends BatchJobV2{

    public static IPodServiceImpl iPodService = null;
    public static MezzmoServiceImpl mezzmoService = null;
    public static MediaMonkeyServiceImpl mediaMonkeyService = null;

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(SynchronizeIPodPlayCount.class);

    public static void main(String args[]) {

        SynchronizeIPodPlayCount instance = new SynchronizeIPodPlayCount();
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

        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        Map <String, MGOFileAlbumCompositeTO> map = getMezzmoService().getMaxDisc();

        export(mp3Settings.mezzmo.export.getiPod(), "iPodDB.PlayCount", map);
        synchronize(mp3Settings.mezzmo.export.getiPod(), "iPodMezzmoSynced", map, mp3Settings.mezzmo.synchronizePlaycount);
    }

    public void synchronize(String base, String filename, Map <String, MGOFileAlbumCompositeTO> map, boolean sync) {

        List <MGOFileAlbumCompositeTO> list = getIPodService().getListPlayCount();
        Synchronizer synchronizer = new Synchronizer(list, map, sync);
        try {
            synchronizer.synchronizeIPodWithMezzmo(base, filename);
        } catch (Exception e) {
            LogUtils.logError(log, e);
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
            LogUtils.logError(log, e);
        } catch (IOException e) {
            LogUtils.logError(log, e);
        } catch (Exception e) {
            LogUtils.logError(log, e);
        } finally {
            csvUtils.close(csvFilePrinter);
        }

    }

    public void writeToExportFile( List<MGOFileAlbumCompositeTO> list, CSVPrinter csvFilePrinter, Map <String, MGOFileAlbumCompositeTO> map) throws IOException {
        for (MGOFileAlbumCompositeTO comp : list ){
            List record = new ArrayList();
            record.add(getMezzmoService().constructFileTitle(map, comp));
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

    public static MediaMonkeyServiceImpl getMediaMonkeyService(){

        if (mediaMonkeyService == null) {
            return MediaMonkeyServiceImpl.getInstance();
        }
        return mediaMonkeyService;
    }

}

