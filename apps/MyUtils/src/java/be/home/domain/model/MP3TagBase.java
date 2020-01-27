package be.home.domain.model;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.enums.MP3Tag;
import be.home.common.exceptions.ApplicationException;
import be.home.common.main.BatchJobV2;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.*;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.json.AlbumError;
import be.home.model.json.MP3Settings;
import be.home.model.json.SongCorrections;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 28/02/2017.
 */
public abstract class MP3TagBase extends BatchJobV2 {

    public MP3TagUtils mp3TagUtils;
    private static final Logger log = getMainLog(MP3TagBase.class);
    public static MezzmoServiceImpl mezzmoService = null;

    public void displayStatus(MGOFileAlbumCompositeTO comp, String outputFile, String template,
                              int nrAlbumsToCheck, int totalAlbumsToCheck,
                              int nr, int total, int maxNumberOfErrors) throws IOException {
        VelocityUtils vu = new VelocityUtils();

        VelocityContext context = new VelocityContext();
        context.put("album", comp.getFileAlbumTO().getName());
        context.put("progress", nrAlbumsToCheck);
        context.put("total", totalAlbumsToCheck);
        context.put("subProgress", nr);
        context.put("subTotal", total);
        context.put("numberOfErrors", mp3TagUtils.getErrorList().size());
        context.put("maxNumberOfErrors", maxNumberOfErrors);
        context.put("esc", new EscapeTool());
        context.put("refresh", 2);
        vu.makeFile(template, outputFile, context);
    }

    protected boolean maxItemsReached(int maxErrors){
        if (mp3TagUtils.getErrorList().size() >= maxErrors && maxErrors != 0){
            return true;
        }
        return false;
    }

    protected void saveUpdateSongInfo(SongCorrections songCorrections){
        String info = "Saving the Song Corrections...";
        log.info(info);
        try {
            JSONUtils.writeJsonFileWithCode(songCorrections, Constants.JSON.SONGCORRECTIONS);
        } catch (IOException e) {
            throw new ApplicationException("Problem writing the Song Corrections JSON file", e);
        }
        log.info(info + " Done");
    }

    protected void saveErrors(){
        String info = "Saving the Album Errors...";
        log.info(info);
        try {
            JSONUtils.writeJsonFileWithCode(mp3TagUtils.getAlbumError(), Constants.JSON.ALBUMERRORS);
        } catch (IOException e) {
            throw new ApplicationException("Problem writing the AlbumErrors JSON file", e);
        }
        log.info(info + " Done");
    }

    protected int getMaxDisc(List<MGOFileAlbumCompositeTO> list){
        int maxDisc = 0;
        for (MGOFileAlbumCompositeTO comp : list){
            maxDisc = Math.max(maxDisc, comp.getFileTO().getDisc());
        }
        return maxDisc;
    }

    protected void flushAlbumErrors() throws IOException {
        List <AlbumError.Item> filteredAlbumErrors = new ArrayList<>();
        String logAlbum = Setup.getInstance().getFullPath(Constants.FILE.ALBUM_LOG);
        logAlbum = logAlbum.replace("<DATE>", DateUtils.formatYYYYMMDD(new Date()));
        MyFileWriter albumLogger = new MyFileWriter(logAlbum, MyFileWriter.APPEND);
        for (AlbumError.Item item : mp3TagUtils.getErrorList()){
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
        mp3TagUtils.setErrorList(filteredAlbumErrors);
    }

    protected String getSetSortTitle(String title){
        String sortTitle = moveFirstWordAtEnd(title, "The");
        sortTitle = moveFirstWordAtEnd(sortTitle, "A");
        sortTitle = moveFirstWordAtEnd(sortTitle, "An");

        return sortTitle;
    }

    protected String moveFirstWordAtEnd(String title, String word){
        String pattern = "^" + word + " " + "(.+)";
        String sortTitle = title.replaceAll(pattern, "$1, " + word);
        return sortTitle;

    }

    protected void updateFileTitle(AlbumError.Item item)  {
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setFileTitle(item.getNewValue());
        comp.getFileTO().setSortTitle(getSetSortTitle(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("FileTitle updated: " + "Id: " + item.getId() +
                        " / New FileTitle: " + item.getNewValue() + " / " + nr + " record(s)");
                setDone(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        } catch (IOException e) {
            LogUtils.logError(log, e);
        }
    }

    protected void updateSong(AlbumError.Item item){
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

    protected void updateDisc(AlbumError.Item item){
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

    protected void updateDuration(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setDuration(Integer.parseInt(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("Duration updated: " + "Id: " + item.getId() +
                        " / New Duration: " + item.getNewValue() + " / " + nr + " record(s)");
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    protected void updateRating(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getId());
        comp.getFileTO().setRanking(Integer.parseInt(item.getNewValue()));
        try {
            int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
            if (nr > 0) {
                log.info("Rating updated: " + "Id: " + item.getId() +
                        " / New Rating: " + item.getNewValue() + " / " + nr + " record(s)");
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    protected void updateAlbum(AlbumError.Item item){

        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getFileId());
        comp.getFileAlbumTO().setId(item.getId());
        comp.getFileAlbumTO().setName(item.getOldValue());
        try {
            int nr = getMezzmoService().updateAlbum(comp, item.newValue);
            if (nr > 0) {
                log.info("Album updated: " + "Id: " + item.getId() +
                        " / New Album: " + item.getNewValue() + " / " + nr + " record(s)");
                //item.setDone(true);
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }

    }

    protected void updateTrack(AlbumError.Item item){
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

    protected void updateArtist(AlbumError.Item item){
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

    protected void updateAlbumArtist(AlbumError.Item item){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(item.getFileId());
        comp.getAlbumArtistTO().setId(item.getId());
        comp.getAlbumArtistTO().setName(item.getNewValue());
        try {
            int nr = getMezzmoService().updateAlbumArtist(comp);
            if (nr > 0) {
                log.info("Album Artist updated: " + "Id: " + item.getId() +
                        " / New Album Artist: " + item.getNewValue() + " / " + nr + " record(s)");
                //item.setDone(true);
                updateMP3(item);
            }
        } catch (SQLException e) {
            LogUtils.logError(log, e);
        }
    }

    private void updateMP3(AlbumError.Item item) {
        String file = this.mp3TagUtils.relativizeFile(item.getFile());
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
                        setDone(item);
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
                        setDone(item);
                    }
                    else {
                        update = true;
                        id3v2Tag.setArtist(item.getNewValue());
                    }
                    break;
                case ALBUMARTIST :
                    if (item.getNewValue().equals(id3v2Tag.getAlbumArtist())) {
                        log.info("No Update needed for " + item.getFile());
                        log.info("MP3 Album Artist Info: " + id3v2Tag.getAlbumArtist());
                        log.info("Update Info: " + item.getType());
                        setDone(item);
                    }
                    else {
                        update = true;
                        id3v2Tag.setAlbumArtist(item.getNewValue());
                    }
                    break;
                case TITLE:
                    if (item.getNewValue().equals(id3v2Tag.getTitle())) {
                        log.info("No Update needed for " + item.getFile());
                        log.info("MP3 Title Info: " + id3v2Tag.getTitle());
                        log.info("Update Info: " + item.getType());
                        setDone(item);
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
                case DURATION:
                    // nothing to do
                    break;
                case RATING:
                    // nothing to do
                    break;
                case TRACK:
                    if (item.getNewValue().equals(id3v2Tag.getTrack())) {
                        log.info("No Update needed for " + item.getFile());
                        log.info("MP3 Track Info: " + id3v2Tag.getTrack());
                        log.info("Update Info: " + item.getType());
                        setDone(item);
                    }
                    else {
                        id3v2Tag.setTrack(item.getNewValue());
                        update = true;
                    }
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
                    setDone(item);
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

    protected void setDone(AlbumError.Item item) throws IOException {
        item.setDone(true);
        boolean done = true;
        if (item.update){
            // check if other errors for same id need to be processed
            for (AlbumError.Item errorItem : this.mp3TagUtils.getErrorList()){
                if (!errorItem.isDone() && item.fileId.equals(errorItem.fileId)){
                    done = false;
                    break;
                }
            }
            if (done) {
                // remove it from the JSON update file
                SongCorrections songCorrections = (SongCorrections) JSONUtils.openJSONWithCode(Constants.JSON.SONGCORRECTIONS, SongCorrections.class);
                for (SongCorrections.Item songItem : songCorrections.items) {
                    if (songItem.fileId.equals(item.fileId) && !songItem.done) {
                        songItem.done = true;
                        saveUpdateSongInfo(songCorrections);
                        break;
                    }
                }
            }
        }
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

}
