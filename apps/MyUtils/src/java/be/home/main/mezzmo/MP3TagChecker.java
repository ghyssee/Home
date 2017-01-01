package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.enums.MP3Tag;
import be.home.common.exceptions.ApplicationException;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.*;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.MP3TagUtils;
import be.home.mezzmo.domain.model.*;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import com.mpatric.mp3agic.*;
import javafx.application.Application;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MP3TagChecker extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(MP3TagChecker.class);
    public AlbumError albumErrors = (AlbumError) JSONUtils.openJSONWithCode(Constants.JSON.ALBUMERRORS, AlbumError.class);

    public static void main(String args[]) {

        MP3TagChecker instance = new MP3TagChecker();
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
        final String batchJob = "MP3TagChecker";

        //System.out.println(stripFilename("Can't Feel"));
        //export();
        int nr = processErrors();
        log.info("Nr Of Errors processed: " + nr);
        /*
        String file = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\MNM Big Hits Best Of 2015\\05 Netsky feat. Digital Farm Animals - Work It Out.mp3";
        file = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\MNM Big Hits Best Of 2015\\105 Christine & The Queens - Christine.mp3";
        String fileOK = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\MNM Big Hits Best Of 2015\\209 Dotan - Hungry.mp3";
        String fileOK2 = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\MNM Big Hits Best Of 2015\\105 Christine & The Queens - Christine.mp3";
        String newFile = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\test.mp3";
        ID3v2 id3v2 = readMP3File(fileOK2);
        readMP3File2(newFile, id3v2);
        */

    }

    private ID3v2 readMP3File(String file) {
        Mp3File mp3file = null;
        ID3v2 id3v2Tag = null;
        try {
            mp3file = new Mp3File(file);
            id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
            String artist = "Axwell Λ Ingrosso";
            //id3v2Tag.setArtist(artist);
            //mp3file.removeId3v2Tag();
            //mp3file.removeCustomTag();
            mp3file.setId3v2Tag(id3v2Tag);
            String newFile = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\test.mp3";
            mp3file.save(newFile);
            log.info("test");
        } catch (Exception e) {
            LogUtils.logError(log, e);
        }
        return id3v2Tag;
    }


    private void readMP3File2(String file, ID3v2 testID) {
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
            String artist = "Axwell Λ Ingrosso";
            String album = "test";
            id3v2Tag.setArtist(artist);
            //id3v2Tag.setAlbum(album);
            //mp3file.setId3v2Tag(id3v2Tag);
            //String newFile = "C:\\My Data\\tmp\\Java\\MP3Processor\\_test\\test.mp3";
            //mp3file.save(newFile);
        } catch (Exception e) {
            LogUtils.logError(log, e);
        }
    }

    public void export(){

        TransferObject to = new TransferObject();
        albumErrors.items = new ArrayList<>();
        //base = ""
        MGOFileAlbumTO albumTO = new MGOFileAlbumTO();
        File file = new File(Setup.getInstance().getFullPath(Constants.FILE.ALBUMS_TO_CHECK));
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8);
            for (String line : lines){
                if (StringUtils.isBlank(line)){
                    break;
                }
                albumTO.setName(line.trim());
                List<MGOFileAlbumCompositeTO> listAlbums = getMezzmoService().getAlbums(albumTO, new TransferObject());
                if (listAlbums == null || listAlbums.size() == 0){
                    log.warn("No Album(s) found!!!");
                }
                else {
                    for (MGOFileAlbumCompositeTO comp : listAlbums) {
                        log.info("AlbumID: " + comp.getFileAlbumTO().getId());
                        log.info("Album: " + comp.getFileAlbumTO().getName());
                        processAlbum(comp);
                    }
                }
            }
            try {
                JSONUtils.writeJsonFileWithCode(this.albumErrors, Constants.JSON.ALBUMERRORS);
            } catch (IOException e) {
                LogUtils.logError(log, e, "Problem writing the AlbumErrors JSON file");

            }
        } catch (IOException e) {
            LogUtils.logError(log, e, "Problem opening file " + file.getAbsolutePath());
        }
    }

    private void processAlbum(MGOFileAlbumCompositeTO comp){
        MGOFileAlbumCompositeTO search = new MGOFileAlbumCompositeTO();
        search.getFileAlbumTO().setId(comp.getFileAlbumTO().getId());
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().findSongsByAlbum(search);
        log.info("Album: " + comp.getFileAlbumTO().getName());
        int maxDisc = getMaxDisc(list);
        log.info("Max Disc: " + maxDisc);
        MP3TagUtils tagUtils = new MP3TagUtils(this.albumErrors);
        for (MGOFileAlbumCompositeTO item : list){

            log.info("Track: " + item.getFileTO().getTrack());
            log.info("Artist: " + item.getFileArtistTO().getArtist());
            log.info("Title: " + item.getFileTO().getTitle());
            log.info(StringUtils.repeat('=', 100));
            tagUtils.processSong(item, list.size(), maxDisc);
        }

    }

    private int getMaxDisc(List<MGOFileAlbumCompositeTO> list){
        int maxDisc = 0;
        for (MGOFileAlbumCompositeTO comp : list){
            maxDisc = Math.max(maxDisc, comp.getFileTO().getDisc());
        }
        return maxDisc;
    }

    private int processErrors(){
        int errorsProcessed = 0;
        log.info("Processing Errors");
        if (this.albumErrors.items.size() == 0){
            log.info("Nothing To Process");
            return 0;
        }
        for (AlbumError.Item item : this.albumErrors.items){
            if (!item.isDone() && item.isProcess()) {
                errorsProcessed++;
                log.info("Processing Id " + item.id + " / Type = " + item.type);
                switch (MP3Tag.valueOf(item.getType())) {
                    case FILE:
                        renameFile(item);
                        break;
                    case FILETITLE:
                        updateFileTitle(item);
                        break;
                    case ARTIST:
                        updateArtist(item);
                        break;
                    case TITLE:
                        updateSong(item);
                        break;
                    case ALBUM:
                        updateAlbum(item);
                        break;
                    case TRACK:
                        updateTrack(item);
                        break;
                    case DISC:
                        updateDisc(item);
                    default:
                        log.error("Id: " + item.getId() + " / Unknwon Type");
                }
            }
        }
        try {
            flushAlbumErrors();
        } catch (IOException e) {
            LogUtils.logError(log, e, "There was a problem flushing the Album Errors File");
        }
        return errorsProcessed;
    }

    private void flushAlbumErrors() throws IOException {
        List <AlbumError.Item> filteredAlbumErrors = new ArrayList <> ();
        String logAlbum = Setup.getInstance().getFullPath(Constants.FILE.ALBUM_LOG);
        logAlbum = logAlbum.replace("<DATE>", DateUtils.formatYYYYMMDD(new Date()));
        MyFileWriter albumLogger = new MyFileWriter(logAlbum, MyFileWriter.APPEND);
        for (AlbumError.Item item : this.albumErrors.items){
            if (item.isDone()) {
                albumLogger.append("ID: " + item.getId());
                albumLogger.append("File: " + item.getFile());
                albumLogger.append("Type: " + item.getType());
                albumLogger.append("Old Value: " + item.getOldValue());
                albumLogger.append("New Value: " + item.getNewValue());
                albumLogger.append(StringUtils.repeat('=', 200));
            }
            else {
                filteredAlbumErrors.add(item);
            }
        }
        albumLogger.close();
        this.albumErrors.items = filteredAlbumErrors;
        JSONUtils.writeJsonFileWithCode(this.albumErrors, Constants.JSON.ALBUMERRORS);
        }

    private void renameFile(AlbumError.Item item){
       if (FileUtils.renameFile(MP3TagUtils.relativizeFile(item.oldValue), MP3TagUtils.relativizeFile(item.newValue))) {
           MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
           comp.getFileTO().setId(item.getId());
           comp.getFileTO().setFile(item.getNewValue());
           comp.getFileTO().setFileTitle(MP3TagUtils.getFileTitle(item.getNewValue()));
           try {
               int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
               if (nr > 0) {
                   log.info("File updated: " + "Id: " + item.getId() +
                           " / New File: " + item.getNewValue() + " / " + nr + " record(s)");
                   item.setDone(true);
               }
           } catch (SQLException e) {
               LogUtils.logError(log, e);
           }
       }
    }

    private void updateSong(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setTitle(item.getNewValue());
        comp.getFileTO().setSortTitle(getSetSortTitle(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("Title updated: " + "Id: " + item.getId() +
                        " / New Title: " + item.getNewValue() + " / " + nr + " record(s)");
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private void updateDisc(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setDisc(Integer.parseInt(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("Disc updated: " + "Id: " + item.getId() +
                        " / New Disc: " + item.getNewValue() + " / " + nr + " record(s)");
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private void updateAlbum(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileAlbumTO().setId(item.getId());
        comp.getFileAlbumTO().setName(item.getNewValue());
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("Album updated: " + "Id: " + item.getId() +
                        " / New Album: " + item.getNewValue() + " / " + nr + " record(s)");
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private void updateFileTitle(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setFileTitle(item.getNewValue());
        comp.getFileTO().setSortTitle(getSetSortTitle(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("FileTitle updated: " + "Id: " + item.getId() +
                        " / New FileTitle: " + item.getNewValue() + " / " + nr + " record(s)");
                item.setDone(true);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private void updateTrack(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setTrack(Integer.parseInt(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("Track updated: " + "Id: " + item.getId() +
                        " / New Track: " + item.getNewValue() + " / " + nr + " record(s)");
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private String getSetSortTitle(String title){
        String sortTitle = moveFirstWordAtEnd(title, "The");
        sortTitle = moveFirstWordAtEnd(sortTitle, "A");
        sortTitle = moveFirstWordAtEnd(sortTitle, "An");

        return sortTitle;
    }

    private String moveFirstWordAtEnd(String title, String word){
        String pattern = "^" + word + " " + "(.+)";
        String sortTitle = title.replaceAll(pattern, "$1, " + word);
        return sortTitle;

    }


    private void updateArtist(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getFileId());
        comp.getFileArtistTO().setID(item.getId());
        comp.getFileArtistTO().setArtist(item.getNewValue());
        try {
            int nr = getMezzmoService().updateArtist(comp);
            if (nr > 0) {
                log.info("Artitst updated: " + "Id: " + item.getId() +
                        " / New Artist: " + item.getNewValue() + " / " + nr + " record(s)");
                //item.setDone(true);
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private void updateArtistOld(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileArtistTO().setID(item.getId());
        comp.getFileArtistTO().setArtist(item.getNewValue());
        MGOFileArtistTO artist = null;
        try {
            artist = getMezzmoService().findArtist(comp.getFileArtistTO());
        }
        catch (EmptyResultDataAccessException e){
            // artist not found;
        }
        if (artist != null) {
            System.out.println("artist found");
            Result result = getMezzmoService().updateLinkFileArtist(comp.getFileArtistTO(), artist.getID());
            log.info("Nr Of Links updated: " + result.getNr1());
            /* update the found artist (because of case insensitive constraint on
               field artist, so update it to be sure it is prettified (feat. => Feat.)
             */
            int nr = 0;
            try {
                comp.getFileArtistTO().setID(artist.getID());
                nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
                if (nr > 0) {
                    log.info("Artitst updated: " + "Id: " + item.getId() +
                            " / New Artist: " + item.getNewValue() + " / " + nr + " record(s)");
                    //item.setDone(true);
                    updateMP3(item);
                }
            } catch (SQLException e) {
                LogUtils.logError(log, e);
            }
            //System.out.println("Nr Of Old Artists deleted: " + result.getNr2());
        }
        else {
            try {
                int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
                if (nr > 0) {
                    log.info("Artitst updated: " + "Id: " + item.getId() +
                            " / New Artist: " + item.getNewValue() + " / " + nr + " record(s)");
                    //item.setDone(true);
                    updateMP3(item);
                }
            } catch (SQLException e) {
                LogUtils.logError(log, e);
            }
        }
    }

    private void updateMP3(AlbumError.Item item) {
        String file = MP3TagUtils.relativizeFile(item.getFile());
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);

            boolean update = false;
            switch (MP3Tag.valueOf(item.getType())){
                case ALBUM:
                    if (item.getNewValue().equals(id3v2Tag.getAlbum())) {
                        log.info("No Update needed for " + item.getFile());
                        log.info("MP3 Album Info: " + id3v2Tag.getAlbum());
                        log.info("Update Info: " + item.getType());
                        item.setDone(true);
                    }
                    else {
                        update = true;
                        id3v2Tag.setAlbum(item.getNewValue());
                    }
                    break;
                case ARTIST :
                    if (item.getNewValue().equals(id3v2Tag.getArtist())) {
                        log.info("No Update needed for " + item.getFile());
                        log.info("MP3 Artist Info: " + id3v2Tag.getArtist());
                        log.info("Update Info: " + item.getType());
                        item.setDone(true);
                    }
                    else {
                        update = true;
                        id3v2Tag.setArtist(item.getNewValue());
                    }
                    break;
                case TITLE:
                    if (item.getNewValue().equals(id3v2Tag.getTitle())) {
                        log.info("No Update needed for " + item.getFile());
                        log.info("MP3 Title Info: " + id3v2Tag.getTitle());
                        log.info("Update Info: " + item.getType());
                        item.setDone(true);
                    }
                    else {
                        id3v2Tag.setTitle(item.getNewValue());
                        update = true;
                    }
                    break;
                case DISC:
                        id3v2Tag.setPartOfSet(item.getNewValue());
                        update = true;
                        break;
                case TRACK:
                    id3v2Tag.setTrack(item.getNewValue());
                    update = true;
                    break;
            }
            if (update) {
                mp3file.setId3v2Tag(id3v2Tag);
                String originalFile = file;
                String newFile = originalFile + ".NEW";
                mp3file.save(newFile);
                Path path = Paths.get(newFile);
                if (Files.exists(path)) {
                    path = Paths.get(originalFile);
                    Files.delete(path);
                    FileUtils.renameFile(newFile, originalFile);
                    item.setDone(true);
                }
                else {
                    String errorMsg = "There was a problem when updating file " + originalFile;
                    log.error(errorMsg);
                }
            }
        } catch (Exception e) {
            LogUtils.logError(log, e);
        }
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

