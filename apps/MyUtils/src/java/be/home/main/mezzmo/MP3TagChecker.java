package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.enums.MP3Tag;
import be.home.common.exceptions.ApplicationException;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.DateUtils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
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
    public String ALBUM = "Ultratop 50 201212 December 2012";
    public String[] ALBUMS = {
            "Ultratop 50 20140104 04 Januari 2014"
    };



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
        export();
        //processErrors();
        //String file = "C:\\My Data\\tmp\\Java\\MP3Processor\\ToTest\\05 Netsky feat. Digital Farm Animals - Work It Out.mp3";
        //file = "C:\\My Data\\tmp\\Java\\MP3Processor\\ToTest\\test.mp3";
        //readMP3File(file);

    }

    private void readMP3File(String file) {
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
            String artist = "Axwell Λ Ingrosso";
            id3v2Tag.setArtist(artist);
            mp3file.setId3v2Tag(id3v2Tag);
            String originalFile = file;
            String newFile = "C:\\My Data\\tmp\\Java\\MP3Processor\\ToTest\\test2.mp3";
            mp3file.save(newFile);
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public void export(){

        TransferObject to = new TransferObject();
        albumErrors.items = new ArrayList<>();
        //base = ""
        MGOFileAlbumTO albumTO = new MGOFileAlbumTO();
        for (String album : ALBUMS){
            albumTO.setName(album);
            List<MGOFileAlbumCompositeTO> listAlbums = getMezzmoService().getAlbums(albumTO, new TransferObject());
            if (listAlbums == null || listAlbums.size() == 0){
                log.warn("No Album(s) found!!!");
            }
            else {
                for (MGOFileAlbumCompositeTO comp : listAlbums) {
                    System.out.println("AlbumID: " + comp.getFileAlbumTO().getId());
                    System.out.println("Album: " + comp.getFileAlbumTO().getName());
                    processAlbum(comp);
                }
            }
        }
        try {
            JSONUtils.writeJsonFileWithCode(this.albumErrors, Constants.JSON.ALBUMERRORS);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void processAlbum(MGOFileAlbumCompositeTO comp){
        MGOFileAlbumCompositeTO search = new MGOFileAlbumCompositeTO();
        search.getFileAlbumTO().setId(comp.getFileAlbumTO().getId());
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().findSongsByAlbum(search);
        System.out.println("Album: " + comp.getFileAlbumTO().getName());
        int maxDisc = getMaxDisc(list);
        System.out.println("Max Disc: " + maxDisc);
        MP3TagUtils tagUtils = new MP3TagUtils(this.albumErrors);
        for (MGOFileAlbumCompositeTO item : list){

            System.out.println("Track: " + item.getFileTO().getTrack());
            System.out.println("Artist: " + item.getFileArtistTO().getArtist());
            System.out.println("Title: " + item.getFileTO().getTitle());
            System.out.println(StringUtils.repeat('=', 100));
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


    private String getFileTitle(String file){
        Path tmp = Paths.get(file);
        String filename = tmp.getFileName().toString();
        filename = FilenameUtils.removeExtension(filename);
        return filename;

    }

    private void processErrors(){
        log.info("Processing Errors");
        if (this.albumErrors.items.size() == 0){
            log.info("Nothing To Process");
            return;
        }
        for (AlbumError.Item item : this.albumErrors.items){
            if (!item.isDone()) {
                log.info("Processing Id " + item.id + " / Type = " + item.type);
                switch (MP3Tag.valueOf(item.getType())) {
                    case FILE:
                        renameFile(item);
                        break;
                    case ARTIST:
                        updateArtist(item);
                        break;
                    case TITLE:
                        updateSong(item);
                        break;
                    case TRACK:
                        updateTrack(item);
                        break;
                    default:
                        log.error("Id: " + item.getId() + " / Unknwon Type");
                }
            }
        }
        try {
            flushAlbumErrors();
        } catch (IOException e) {
            log.error(e);
        }
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
           comp.getFileTO().setFileTitle(getFileTitle(item.getNewValue()));
           try {
               int nr = getMezzmoService().updateSong(comp, MP3Tag.valueOf(item.getType()));
               if (nr > 0) {
                   log.info("File updated: " + "Id: " + item.getId() +
                           " / New File: " + item.getNewValue() + " / " + nr + " record(s)");
                   item.setDone(true);
               }
           } catch (SQLException e) {
               log.error(e);
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
            log.error(e);
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
            log.error(e);
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
            System.out.println("Nr Of Links updated: " + result.getNr1());
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
                    e.printStackTrace();
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
                log.error(e);
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
                case ARTIST :
                    update = true;
                    id3v2Tag.setArtist(item.getNewValue());
                    break;
                case TITLE:
                    id3v2Tag.setTitle(item.getNewValue());
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
            log.error(e);
            e.printStackTrace();
            }
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

