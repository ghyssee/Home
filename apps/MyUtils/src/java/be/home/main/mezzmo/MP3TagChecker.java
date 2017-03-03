package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.enums.MP3Tag;
import be.home.common.exceptions.ApplicationException;
import be.home.common.model.TransferObject;
import be.home.common.utils.*;
import be.home.common.utils.FileUtils;
import be.home.domain.model.MP3TagBase;
import be.home.domain.model.MP3TagUtils;
import be.home.mezzmo.domain.enums.MP3TagCheckerStatus;
import be.home.mezzmo.domain.model.*;
import be.home.model.ConfigTO;
import be.home.model.json.AlbumError;
import be.home.model.json.MP3Settings;
import be.home.model.json.SongCorrections;
import org.apache.commons.io.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.texen.util.FileUtil;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MP3TagChecker extends MP3TagBase {


    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(MP3TagChecker.class);
    private final boolean UPDATE = true;

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

    private MP3Settings.Mezzmo.Mp3Checker.RelativePath getRelativePath (MP3Settings mp3Settings){
        String id = mp3Settings.mezzmo.mp3Checker.currentRelativePath;
        MP3Settings.Mezzmo.Mp3Checker.RelativePath currentRelativePath = null;
        for (MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath : mp3Settings.mezzmo.mp3Checker.relativePaths){
            if (relativePath.id.equals(id)){
                currentRelativePath = relativePath;
            }
        }
        return currentRelativePath;
    }

    @Override
    public void run() {
        final String batchJob = "MP3TagChecker";
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        MP3TagCheckerStatus status = MP3TagCheckerStatus.valueOf(mp3Settings.mezzmo.mp3Checker.status);
        MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath = getRelativePath(mp3Settings);
        AlbumError albumErrors = (AlbumError) JSONUtils.openJSONWithCode(Constants.JSON.ALBUMERRORS, AlbumError.class);
        this.mp3TagUtils = new MP3TagUtils(albumErrors, relativePath);
        if (relativePath == null){
            throw new ApplicationException("Relative Path with id " + mp3Settings.mezzmo.mp3Checker.currentRelativePath + " not found!");
        }
        int nr = 0;
        try {
            switch (status) {
                case SCAN_FULL:
                    export(mp3Settings.mezzmo.mp3Checker);
                    log.info("Nr Of Errors found: " + albumErrors.items.size());
                    break;
                case SCAN_FILES:
                    processFiles();
                    break;
                case FIX_ERRORS:
                    nr = processErrors();
                    log.info("Nr Of Errors processed: " + nr);
                    saveErrors();
                    break;
                case FIX_FILETITLE:
                    nr = processFileTitles();
                    log.info("Nr Of Errors processed: " + nr);
                    saveErrors();
                    break;
                case UPDATE_SONGS:
                    processUpdateSongs();
                    break;
            }
        }
        catch (Exception ex){
            LogUtils.logError(log, ex);
        }
    }

    public void export(MP3Settings.Mezzmo.Mp3Checker mp3checker) throws IOException {

        TransferObject to = new TransferObject();
        this.mp3TagUtils.clearErrorList();
        MGOFileAlbumTO albumTO = new MGOFileAlbumTO();
        MyFileWriter albumsWithoutErrorsFile = new MyFileWriter(Setup.getInstance().getFullPath(Constants.FILE.ALBUMS_WITHOUT_ERRORS), MyFileWriter.NO_APPEND);
        File file = new File(Setup.getInstance().getFullPath(Constants.FILE.ALBUMS_TO_CHECK));
        File excludeFile = new File(Setup.getInstance().getFullPath(Constants.FILE.ALBUMS_TO_EXCLUDE));
        try {
            List<String> lines = FileUtils.getContents(file, StandardCharsets.UTF_8, FileUtils.REMOVE_EMPTY_LINES);
            List<String> excludeAlbums = FileUtils.getContents(excludeFile, StandardCharsets.UTF_8, FileUtils.REMOVE_EMPTY_LINES);
            int nrAlbumsToCheck = 0;
            for (String line : lines){
                nrAlbumsToCheck++;
                if (StringUtils.isBlank(line)){
                    break;
                }
                if (maxItemsReached(mp3checker.maxNumberOfErrors)){
                    break;
                }
                if ((this.mp3TagUtils.getErrorList().size() % 10) == 0){
                    saveErrors();
                }
                albumTO.setName(line.trim());
                List<MGOFileAlbumCompositeTO> listAlbums = getMezzmoService().getAlbumsWithExcludeList(albumTO, excludeAlbums,
                        new TransferObject());
                if (listAlbums == null || listAlbums.size() == 0){
                    log.warn("No Album(s) found!!!");
                }
                else {
                    int nr = 1;
                    String base = Setup.getFullPath(Constants.Path.WEB_MUSIC) + File.separator;
                    String template = "MP3TagChecker.vm";
                    String outputFile = base + "MP3TagChecker.html";
                    boolean exit = false;
                    for (MGOFileAlbumCompositeTO comp : listAlbums) {
                        log.info("AlbumID: " + comp.getFileAlbumTO().getId());
                        log.info("Album: " + comp.getFileAlbumTO().getName());
                        processAlbum(comp, albumsWithoutErrorsFile, mp3checker);
                        if (maxItemsReached(mp3checker.maxNumberOfErrors)){
                            exit = true;
                        }
                        try {
                            displayStatus(comp, outputFile, template, nrAlbumsToCheck, lines.size(),
                                    nr++, listAlbums.size(), mp3checker.maxNumberOfErrors);
                        }
                        catch (IOException e) {
                            // don't break batch because there was a problem writing status page
                            log.warn("Problem Making Status Page");
                        }
                        if (exit){
                            break;
                        }
                    }
                }
            }
            saveErrors();
        } catch (IOException e) {
            LogUtils.logError(log, e, "Problem opening file " + file.getAbsolutePath());
        }
        albumsWithoutErrorsFile.close();
    }

    private void processFiles(){
        log.info("Processing files that are in error again...");
        List <AlbumError.Item> oldItems = this.mp3TagUtils.getErrorList();
        this.mp3TagUtils.clearErrorList();
        List <Long> fileIdList = new ArrayList();
        for (AlbumError.Item item : oldItems) {
            if (!fileIdList.contains(item.fileId)){
                fileIdList.add(new Long(item.fileId));
            }
        }
        for (Long fileId : fileIdList) {
            try {
                MGOFileAlbumCompositeTO comp = getMezzmoService().findFileById(fileId);
                // get list of all files to get the max Disc
                MGOFileAlbumCompositeTO search = new MGOFileAlbumCompositeTO();
                search.getFileAlbumTO().setId(comp.getFileAlbumTO().getId());
                List<MGOFileAlbumCompositeTO> list = getMezzmoService().findSongsByAlbum(search);
                int maxDisc = getMaxDisc(list);
                logItem(comp, maxDisc);
                this.mp3TagUtils.processSong(comp, list.size(), maxDisc);
            }
            catch (IncorrectResultSizeDataAccessException e){
                log.error(e);
                //albumErrors.items.add(item);
            }
        }
        saveErrors();

    }

    private void processAlbum(MGOFileAlbumCompositeTO comp, MyFileWriter albumsWithoutErrorsFile, MP3Settings.Mezzmo.Mp3Checker mp3checker)
            throws IOException {
        MGOFileAlbumCompositeTO search = new MGOFileAlbumCompositeTO();
        search.getFileAlbumTO().setId(comp.getFileAlbumTO().getId());
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().findSongsByAlbum(search);
        log.info("Album: " + comp.getFileAlbumTO().getName());
        int maxDisc = getMaxDisc(list);
        log.info("Max Disc: " + maxDisc);
        int errors = this.mp3TagUtils.getErrorList().size();
        boolean maxReached = false;
        for (MGOFileAlbumCompositeTO item : list){
            logItem(comp, maxDisc);
            this.mp3TagUtils.processSong(item, list.size(), maxDisc);
            if (maxItemsReached(mp3checker.maxNumberOfErrors)){
                maxReached = true;
                break;
            }
        }
        if (errors == this.mp3TagUtils.getErrorList().size() && !maxReached){
            albumsWithoutErrorsFile.append(comp.getFileAlbumTO().getName());
        }
    }

    private int processErrors(){
        int errorsProcessed = 0;
        log.info("Processing Errors");
        if (this.mp3TagUtils.getErrorList().size() == 0){
            log.info("Nothing To Process");
            return 0;
        }
        for (AlbumError.Item item : this.mp3TagUtils.getErrorList()){
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

    private int processFileTitles() {
        int errorsProcessed = 0;
        log.info("Processing Errors");
        if (this.mp3TagUtils.getErrorList().size() == 0) {
            log.info("Nothing To Process");
            return 0;
        }
        for (AlbumError.Item item : this.mp3TagUtils.getErrorList()) {
            if (!item.isDone()) {
                switch (MP3Tag.valueOf(item.getType())) {
                    case FILETITLE:
                        log.info("Processing Id " + item.id + " / Type = " + item.type);
                        errorsProcessed++;
                        updateFileTitle(item);
                        break;
                }
            }
            try {
                flushAlbumErrors();
            } catch (IOException e) {
                LogUtils.logError(log, e, "There was a problem flushing the Album Errors File");
            }
        }
        return errorsProcessed;
    }

    private void processUpdateSongs(){
        SongCorrections songCorrections = (SongCorrections) JSONUtils.openJSONWithCode(Constants.JSON.SONGCORRECTIONS, SongCorrections.class);
        this.mp3TagUtils.enableUpdateMode();
        this.mp3TagUtils.clearErrorList();
        boolean saveUpdateSongFile = false;

        for (SongCorrections.Item item : songCorrections.items) {
            if (item.done){
                continue;
            }
            try {
                MGOFileAlbumCompositeTO comp = getMezzmoService().findFileById(item.fileId);
                comp.getFileTO().setTrack(item.track);
                comp.getFileTO().setTitle(item.title);
                comp.getFileArtistTO().setArtist(item.artist);
                // get list of all files to get the max Disc
                MGOFileAlbumCompositeTO search = new MGOFileAlbumCompositeTO();
                search.getFileAlbumTO().setId(comp.getFileAlbumTO().getId());
                List<MGOFileAlbumCompositeTO> list = getMezzmoService().findSongsByAlbum(search);
                int maxDisc = getMaxDisc(list);
                logItem(comp, maxDisc);
                int nrOfErrors = this.mp3TagUtils.getErrorList().size();
                this.mp3TagUtils.processSong(comp, list.size(), maxDisc);
                if (nrOfErrors == this.mp3TagUtils.getErrorList().size()){
                    // no errors found processing this update
                    item.done = true;
                    saveUpdateSongFile = true;
                    addAlbumToAlbumsToCheck(comp.getFileAlbumTO().getName());
                }
            }
            catch (IncorrectResultSizeDataAccessException e){
                log.error(e);
                //albumErrors.items.add(item);
            }
        }
        saveErrors();
        if (saveUpdateSongFile){
            saveUpdateSongInfo(songCorrections);
        }

    }

    private void addAlbumToAlbumsToCheck(String album){
        String file = Setup.getInstance().getFullPath(Constants.FILE.ALBUMS_TO_CHECK);
        boolean albumFound = false;
        try {
            List<String> lines = FileUtils.getContents(new File(file), StandardCharsets.UTF_8, FileUtils.REMOVE_EMPTY_LINES);
            for (String line : lines){
                if (album.equals(line.trim())){
                    albumFound = true;
                }
            }
            if (!albumFound){
                lines.add(album);
                log.warn("No Error(s) Found for this song. Add album to list of albums to check: " + album);
                FileUtils.writeContents(new File(file), lines);
            }
        } catch (IOException e) {
            log.error("Problem updating file " + file, e);
        }
    }

    private void logItem(MGOFileAlbumCompositeTO comp, int maxDisc){
        log.info("Track: " + comp.getFileTO().getTrack());
        log.info("Artist: " + comp.getFileArtistTO().getArtist());
        log.info("Title: " + comp.getFileTO().getTitle());
        log.info("Max Disc: " + maxDisc);
        log.info(StringUtils.repeat('=', 100));
    }

    private void renameFile(AlbumError.Item item){
       if (FileUtils.renameFile(this.mp3TagUtils.relativizeFile(item.oldValue), this.mp3TagUtils.relativizeFile(item.newValue))) {
           MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
           comp.getFileTO().setId(item.getId());
           comp.getFileTO().setFile(item.getNewValue());
           comp.getFileTO().setFileTitle(MP3TagUtils.getFileTitle(item.getNewValue()));
           try {
               int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
               if (nr > 0) {
                   log.info("File updated: " + "Id: " + item.getId() +
                           " / New File: " + item.getNewValue() + " / " + nr + " record(s)");
                   setDone(item);
               }
           } catch (SQLException e) {
               LogUtils.logError(log, e);
           } catch (IOException e) {
               LogUtils.logError(log, e);
           }
       }
    }


}

