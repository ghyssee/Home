package be.home.main.mezzmo;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.enums.MP3Tag;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.*;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import com.mpatric.mp3agic.*;
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
import java.util.List;
import java.util.Map;

public class MP3TagChecker extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;

    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(MP3TagChecker.class);
    public AlbumError albumErrors = (AlbumError) JSONUtils.openJSONWithCode(Constants.JSON.ALBUMERRORS, AlbumError.class);
    public String SUBST_A = "H:\\Shared\\Mijn Muziek\\Eric\\iPod\\";
    //public String SUBST_B = "R:\\My Music\\iPod\\";
    public String SUBST_B = "C:\\My Data\\tmp\\Java\\MP3Processor\\Album\\";


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
        processErrors();
        //String file = "C:\\My Data\\tmp\\Java\\MP3Processor\\ToTest\\05 Netsky feat. Digital Farm Animals - Work It Out.mp3";
        //file = "C:\\My Data\\tmp\\Java\\MP3Processor\\ToTest\\test.mp3";
        //readMP3File(file);

    }

    private ID3v2 getId3v2Tag(Mp3File mp3File) {
        if ( !mp3File.hasId3v2Tag()){
            return new ID3v24Tag();
        }
        ID3v2 id3v2 = mp3File.getId3v2Tag();
        if (id3v2 instanceof ID3v24Tag){
            return id3v2;
        }
        ID3v2 id3v2Tag = new ID3v24Tag();
        Map<String, ID3v2FrameSet> map = mp3File.getId3v2Tag().getFrameSets();
        for (ID3v2FrameSet set : map.values()) {
            ID3v2FrameSet newFrame = new ID3v2FrameSet(set.getId());
            for (ID3v2Frame f : set.getFrames()) {
                newFrame.addFrame(f);
            }
            id3v2Tag.getFrameSets().put(set.getId(), newFrame);
        }
        return id3v2Tag;
    }



    private void readMP3File(String file) {
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = getId3v2Tag(mp3file);
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

            System.out.println("Track: " + item.getFileTO().getTrack());
            System.out.println("Artist: " + item.getFileArtistTO().getArtist());
            System.out.println("Title: " + item.getFileTO().getTitle());
            System.out.println(StringUtils.repeat('=', 100));
            processSong(item, list.size(), maxDisc);
        }

    }

    private void processSong(MGOFileAlbumCompositeTO comp, int nrOfTracks, int maxDisc){
        File file = new File(relativizeFile((comp.getFileTO().getFile())));
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
        item.setBasePath(FilenameUtils.getFullPath(file));
        this.albumErrors.items.add(item);
    }

    private void checkMP3Info(MGOFileAlbumCompositeTO comp, File file, int nrOfTracks, int maxDisc){
        Mp3FileExt mp3file = null;
        try {
            mp3file = new Mp3FileExt(file.getAbsolutePath());
            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
                checkTrack(id3v2Tag.getTrack(), comp, nrOfTracks, maxDisc);
                checkArtist(comp, id3v2Tag.getArtist());
                checkTitle(comp, id3v2Tag.getTitle());
                checkFilename(comp, nrOfTracks, maxDisc);

                System.out.println(StringUtils.repeat('=', 100));
                System.out.println("MP3 Tag Info");
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
            log.error(e);
        } catch (UnsupportedTagException e) {
            log.error(e);
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
            log.error(e);
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
            comp.getFileTO().setTrack(Integer.valueOf(mp3Track));
            log.warn("Track does not match: " + "mp3: " + mp3Track + " / Formatted: " + track);
            /* example : Track is 1 => update to 01
                only update the mp3 tag, DB stores it as int
             */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TRACK.name(), mp3Track, track);
            ok = false;
        }
        else if (Integer.parseInt(track) != comp.getFileTO().getTrack().intValue()){
            log.warn("Track does not match: " + "DB: " + comp.getFileTO().getTrack() + " / Formatted: " + track);
            /* example : Track is 1 => update to 01
                only update the mp3 tag, DB stores it as int
             */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TRACK.name(), String.valueOf(comp.getFileTO().getTrack()), track);
            comp.getFileTO().setTrack(Integer.valueOf(track));
            ok = false;
        }
        return ok;
    }

    private boolean checkArtist(MGOFileAlbumCompositeTO comp, String mp3Artist){
        String artist = MP3Helper.getInstance().prettifyArtist(mp3Artist);
        boolean ok = true;
        if (!artist.equals(mp3Artist)){
            log.warn("Artist does not match: " + "mp3: " + mp3Artist + " / Formatted: " + artist);
            /* update mp3 + DB */
            comp.getFileArtistTO().setArtist(artist);
            addItem(comp.getFileArtistTO().getID(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ARTIST.name(), mp3Artist, artist);
            ok = false;
        }
        else if (!artist.equals(comp.getFileArtistTO().getArtist())){
            log.warn("Artist does not match: " + "DB: " + comp.getFileArtistTO().getArtist() + " / Formatted: " + artist);
            /* update mp3 + DB */
            addItem(comp.getFileArtistTO().getID(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ARTIST.name(), comp.getFileArtistTO().getArtist(), artist);
            comp.getFileArtistTO().setArtist(artist);
            ok = false;
        }
        return ok;
    }

    private boolean checkTitle(MGOFileAlbumCompositeTO comp, String mp3Title){
        boolean ok = true;
        String title = MP3Helper.getInstance().prettifySong(mp3Title);
        if (!title.equals(mp3Title)){
            log.warn("Title does not match: " + "mp3: " + mp3Title + " / Formatted: " + title);
            /* update mp3 + DB */
            comp.getFileTO().setTitle(title);
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TITLE.name(), mp3Title, title);
            ok = false;
        }
        else if (!title.equals( comp.getFileTO().getTitle())){
            log.warn("Title does not match: " + "DB: " + comp.getFileTO().getTitle() + " / Formatted: " + title);
            /* update mp3 + DB */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TITLE.name(), comp.getFileTO().getTitle(), title);
            comp.getFileTO().setTitle(title);
            ok = false;
        }
        return ok;
    }

    private String getFileTitle(String file){
        Path tmp = Paths.get(file);
        String filename = tmp.getFileName().toString();
        filename = FilenameUtils.removeExtension(filename);
        return filename;

    }

    private boolean checkFilename(MGOFileAlbumCompositeTO comp, int nrOfTracks, int nrOfCds) throws UnsupportedEncodingException {
        boolean ok = true;
        comp.getFileTO().setFile(comp.getFileTO().getFile().replace(SUBST_B, SUBST_A));
        Path tmp = Paths.get(comp.getFileTO().getFile());
        String filenameFromDB = tmp.getFileName().toString();
        System.out.println("filenameDB = " + filenameFromDB);
        int lengthTrack = String.valueOf(nrOfTracks).length() + (nrOfCds > 0 ? String.valueOf(nrOfCds).length() : 0);
        String track = StringUtils.leftPad(String.valueOf(comp.getFileTO().getTrack()), lengthTrack, '0');
        String filename = track + " " + comp.getFileArtistTO().getArtist() + " - " + comp.getFileTO().getTitle() + ".mp3";
        filename = stripFilename(filename);
        if (!filenameFromDB.equals(filename)){
            String newFile = tmp.getParent().toString() + File.separator + filename;
            //comp.getFileTO().setFile(newFile);
            log.warn("Filename does not match: " + "filenameFromDB: " + filenameFromDB + " / Formatted: " + filename);
            /* rename file to new file
               update MGOFile.file with the new file
            */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    "FILE", comp.getFileTO().getFile(), newFile);
            ok = false;
            /*
            filename = filename + "+$hort²/\\";
            System.out.println("Before Stripped: " + filename);
            System.out.println("Stripped: " + stripFilename(filename));*/
        }
        return ok;
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
            JSONUtils.writeJsonFileWithCode(this.albumErrors, Constants.JSON.ALBUMERRORS);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void renameFile(AlbumError.Item item){
       if (FileUtils.renameFile(relativizeFile(item.oldValue), relativizeFile(item.newValue))) {
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

    public String relativizeFile(String file){
        return file.replace(SUBST_A, SUBST_B);
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
        String sortTitle = replaceFirstWordAndPlaceAtEnd(title, "The");
        sortTitle = replaceFirstWordAndPlaceAtEnd(sortTitle, "A");
        sortTitle = replaceFirstWordAndPlaceAtEnd(sortTitle, "An");

        return sortTitle;
    }

    private String replaceFirstWordAndPlaceAtEnd(String title, String word){
        word += " ";
        String pattern = "^" + word + "(.+)";
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
            updateMP3(item);
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
        String file = relativizeFile(item.getFile());
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = getId3v2Tag(mp3file);

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
                Path path = Paths.get(originalFile);
                Files.delete(path);
                FileUtils.renameFile(newFile, originalFile);
                item.setDone(true);
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            }
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
        strippedFilename = strippedFilename.replaceAll("[úùûüµ]", "u");
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
        strippedFilename = strippedFilename.replace("Ch!pz", "Chipz");
        strippedFilename = strippedFilename.replace("M:ck", "Mick");
        strippedFilename = strippedFilename.replace("$hort", "Short");
        strippedFilename = strippedFilename.replace("+", "&");
        strippedFilename = strippedFilename.replace("$ign", "Sign");
        strippedFilename = strippedFilename.replace("^", "&");
        strippedFilename = strippedFilename.replace("P!nk", "Pink");
        strippedFilename = strippedFilename.replace("$", "s");
        strippedFilename = strippedFilename.replace("/\\", "&");
        strippedFilename = strippedFilename.replaceAll("[^\"&()\\[\\],'. a-zA-Z0-9.-]", "");

        return strippedFilename;
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

