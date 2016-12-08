package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.AlbumError;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileAlbumTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

        export("", "MezzmoDB.PlayCount.V12.csv");

    }

    public void export(String base, String fileName){

        TransferObject to = new TransferObject();
        albumErrors.items = new ArrayList<>();
        //base = ""
        MGOFileAlbumTO albumTO = new MGOFileAlbumTO();
        albumTO.setName("Ultratop 50 20160102 02 Januari 2016");
        List<MGOFileAlbumCompositeTO> listAlbums = getMezzmoService().getAlbums(albumTO, new TransferObject());
        for (MGOFileAlbumCompositeTO comp : listAlbums){
            System.out.println("AlbumID: " + comp.getFileAlbumTO().getId());
            System.out.println("Album: " + comp.getFileAlbumTO().getName());
            processAlbum(comp);
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
        for (MGOFileAlbumCompositeTO item : list){

            /* temp replace */
            String file = item.getFileTO().getFile();
            file = file.replace("H:\\Shared\\Mijn Muziek\\Eric\\iPod\\", "C:\\My Data\\tmp\\Java\\MP3Processor\\Album\\");
            item.getFileTO().setFile(file);
            //file.replace()

            System.out.println("Track: " + item.getFileTO().getTrack());
            System.out.println("Artist: " + item.getFileArtistTO().getArtist());
            System.out.println("Title: " + item.getFileTO().getTitle());
            processSong(item, list.size(), maxDisc);
        }

    }

    private void processSong(MGOFileAlbumCompositeTO comp, int nrOfTracks, int maxDisc){
        File file = new File(comp.getFileTO().getFile());
        if (file.exists()){
            checkMP3Info(comp, file, nrOfTracks, maxDisc);
        }
        else {
            log.warn("File Not Found: " + file.getAbsolutePath());
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    "FILE_NOT_EXIST", "",
                    "");

        }

    }

    private  void addItem(Long id, String file, String album, String type, String oldValue, String newValue){
        AlbumError.Item item = new AlbumError().new Item();
        item.setId(id);
        item.setFile(file);
        item.setType(type);
        item.setOldValue(oldValue);
        item.setNewValue(newValue);
        this.albumErrors.items.add(item);
    }

    private void checkMP3Info(MGOFileAlbumCompositeTO comp, File file, int nrOfTracks, int maxDisc){
        Mp3FileExt mp3file = null;
        try {
            mp3file = new Mp3FileExt(file.getAbsolutePath());
            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
                //checkTrack(id3v2Tag.getTrack(), comp.getFileTO().getTrack(), 0);
                checkTrack(id3v2Tag.getTrack(), comp, nrOfTracks, maxDisc);
                checkArtist(comp, id3v2Tag.getArtist());
                checkTitle(comp, id3v2Tag.getTitle());
                checkFilename(comp, nrOfTracks, maxDisc);

                System.out.println("Track: " + id3v2Tag.getTrack());
                System.out.println("Artist: " + id3v2Tag.getArtist());
                System.out.println("Title: " + id3v2Tag.getTitle());
                System.out.println(StringUtils.repeat('=', 100));
            }
            else {
                log.warn("No id3v2Tag Info found for file: " + file.getAbsolutePath());
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
    }

    private int getMaxDisc(List<MGOFileAlbumCompositeTO> list){
        int maxDisc = 0;
        for (MGOFileAlbumCompositeTO comp : list){
            maxDisc = Math.max(maxDisc, comp.getFileTO().getDisc());
        }
        return maxDisc;
    }

    private boolean checkTrack(String mp3Track, MGOFileAlbumCompositeTO comp, int nrOfTracks, int nrOfCds){
        //System.out.println("Track: " + id3v2Tag.getTrack());
        boolean ok = true;
        int lengthTrack = String.valueOf(nrOfTracks).length();
        String track = StringUtils.leftPad(mp3Track, lengthTrack, '0');
        if (!track.equals(mp3Track)){
            log.warn("Track does not match: " + "mp3: " + mp3Track + " / Formatted: " + track);
            /* example : Track is 1 => update to 01
                only update the mp3 tag, DB stores it as int
             */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    "TRACK", mp3Track, track);
            ok = false;
        }
        return ok;
    }

    private boolean checkArtist(MGOFileAlbumCompositeTO comp, String mp3Artist){
        String artist = MP3Helper.getInstance().prettifyArtist(mp3Artist);
        boolean ok = true;
        if (!artist.equals(comp.getFileArtistTO().getArtist())){
            log.warn("Artist does not match: " + "mp3: " + mp3Artist + " / Formatted: " + artist);
            /* update mp3 + DB */
            comp.getFileArtistTO().setArtist(artist);
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    "ARTIST", mp3Artist, artist);
            ok = false;
        }
        return ok;
    }

    private boolean checkTitle(MGOFileAlbumCompositeTO comp, String mp3Title){
        boolean ok = true;
        String title = MP3Helper.getInstance().prettifySong(mp3Title);
        if (!title.equals(comp.getFileTO().getTitle())){
            log.warn("Title does not match: " + "mp3: " + mp3Title + " / Formatted: " + title);
            /* update mp3 + DB */
            comp.getFileTO().setTitle(title);
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    "TITLE", mp3Title, title);
            ok = false;
        }
        return ok;
    }

    private boolean checkFilename(MGOFileAlbumCompositeTO comp, int nrOfTracks, int nrOfCds) throws UnsupportedEncodingException {
        boolean ok = true;
        Path tmp = Paths.get(comp.getFileTO().getFile());
        String filenameFromDB = tmp.getFileName().toString();
        System.out.println("filenameDB = " + filenameFromDB);
        int lengthTrack = String.valueOf(nrOfTracks).length() + (nrOfCds > 0 ? String.valueOf(nrOfCds).length() : 0);
        String track = StringUtils.leftPad(String.valueOf(comp.getFileTO().getTrack()), lengthTrack, '0');
        String filename = track + " " + comp.getFileArtistTO().getArtist() + " - " + comp.getFileTO().getTitle() + ".mp3";
        filename = stripFilename(filename);
        if (!filenameFromDB.equals(filename)){
            String newFile = tmp.getParent().toString() + File.separator + filename;
            comp.getFileTO().setFile(newFile);
            log.warn("Filename does not match: " + "filenameFromDB: " + filenameFromDB + " / Formatted: " + filename);
            /* rename file to new file
               update MGOFile.file with the new file
            */
            try {
                FileUtils.renameFile(comp.getFileTO().getFile(), newFile);
            } catch (IOException e) {
                log.error(e);
            }
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    "FILENAME", filenameFromDB, filename);
            ok = false;
            /*
            filename = filename + "+$hort²/\\";
            System.out.println("Before Stripped: " + filename);
            System.out.println("Stripped: " + stripFilename(filename));*/
        }
        return ok;
    }



    public String stripFilename(String filename){
        String strippedFilename = filename;
        strippedFilename = strippedFilename.replaceAll("<3", "Love");
        strippedFilename = strippedFilename.replaceAll("\\*\\*", "uc");
        strippedFilename = strippedFilename.replaceAll("[áàâäåã]", "a");
        strippedFilename = strippedFilename.replaceAll("[ÁÀÂÄÅÃ]", "A");
        strippedFilename = strippedFilename.replaceAll("[éèêë]", "e");
        strippedFilename = strippedFilename.replaceAll("[ÉÈÊË]", "E");
        strippedFilename = strippedFilename.replaceAll("[íìîï]", "i");
        strippedFilename = strippedFilename.replaceAll("[ÍÌÎÏ]", "I");
        strippedFilename = strippedFilename.replaceAll("[óòôöõø°]", "o");
        strippedFilename = strippedFilename.replaceAll("[ÓÒÔÖÕØ]", "O");
        strippedFilename = strippedFilename.replaceAll("[úùûü]", "u");
        strippedFilename = strippedFilename.replaceAll("[ÚÙÛÜ]", "U");
        strippedFilename = strippedFilename.replaceAll("[ýÿ]", "y");
        strippedFilename = strippedFilename.replaceAll("[Ý]", "Y");
        strippedFilename = strippedFilename.replace("/", "&");
        strippedFilename = strippedFilename.replace("æ", "ae");
        strippedFilename = strippedFilename.replace("Æ", "AE");
        strippedFilename = strippedFilename.replace("ñ", "n");
        strippedFilename = strippedFilename.replace("Ñ", "N");
        strippedFilename = strippedFilename.replace("@", "At");
        strippedFilename = strippedFilename.replace("ç", "c");
        strippedFilename = strippedFilename.replace("Ç", "C");
        strippedFilename = strippedFilename.replace("Λ", "&");
        strippedFilename = strippedFilename.replace("ß", "ss");
        strippedFilename = strippedFilename.replace("²", "2");
        strippedFilename = strippedFilename.replace("³", "3");
        strippedFilename = strippedFilename.replace("³", "3");
        strippedFilename = strippedFilename.replace("³", "3");
        strippedFilename = strippedFilename.replace("Ch!pz", "Chipz");
        strippedFilename = strippedFilename.replace("M:ck", "Mick");
        strippedFilename = strippedFilename.replace("$hort", "Short");
        strippedFilename = strippedFilename.replace("+", "&");
        strippedFilename = strippedFilename.replace("$ign", "Sign");
        strippedFilename = strippedFilename.replace("^", "&");
        strippedFilename = strippedFilename.replace("P!nk", "Pink");
        strippedFilename = strippedFilename.replace("$", "s");
        strippedFilename = strippedFilename.replace("/\\", "&");
        strippedFilename = strippedFilename.replaceAll("[^\"&' a-zA-Z0-9.-]", "");

        return strippedFilename;
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

