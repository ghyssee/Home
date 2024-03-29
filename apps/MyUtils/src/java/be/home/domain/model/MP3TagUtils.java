package be.home.domain.model;

import be.home.common.constants.Constants;
import be.home.common.database.sqlbuilder.Comparator;
import be.home.common.database.sqlbuilder.Type;
import be.home.common.enums.MP3Tag;
import be.home.common.mp3.MP3Utils;
import be.home.common.database.sqlbuilder.SQLBuilder;
import be.home.common.utils.LogUtils;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.mezzmo.domain.dao.definition.MGOFileColumns;
import be.home.mezzmo.domain.dao.definition.TablesEnum;
import be.home.model.json.AlbumError;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.model.json.MP3Settings;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Gebruiker on 17/12/2016.
 */
public class MP3TagUtils {

    private AlbumError albumErrors;
    private List <AlbumError.Item> songErrors = new ArrayList();
    // indicates if it is an update and not a default check
    public boolean update = false;

    private MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath;
    private static final Logger log = LogManager.getLogger();
    public static final String SUBST_A1 = "H:\\Shared\\Mijn Muziek\\Eric\\iPod\\";
    public static final String SUBST_B1 = "T:\\My Music\\iPod\\";
    public static final String SUBST_A = "H:\\Shared\\Mijn Muziek\\";
    public static final String SUBST_B = "O:\\Shared\\Mijn Muziek\\";
    //public static final String SUBST_B = "C:\\My Data\\tmp\\Java\\MP3Processor\\Album\\";
    public static final String HTML_BREAK = "<br>";
    private static long idCounter = 0;

    private MP3TagUtils (){

    }

    public static synchronized long createID()
    {
        return idCounter++;
    }

    public MP3TagUtils(AlbumError albumErrors, MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath){

        this.albumErrors = albumErrors;
        this.relativePath = relativePath;
        this.update = update;

    }

    public void enableUpdateMode(){
        this.update = true;
    }

    public void clearErrorList(){
        this.albumErrors.items = new ArrayList<AlbumError.Item>();
    }

    public List<AlbumError.Item> getErrorList(){
        return this.albumErrors.items;
    }

    public void setErrorList(List<AlbumError.Item> list){
        this.albumErrors.items = list;
    }

    public AlbumError getAlbumError(){
        return this.albumErrors;
    }


    public void processSong(MGOFileAlbumCompositeTO comp, int nrOfTracks, int maxDisc){
        File file = new File(relativizeFile((comp.getFileTO().getFile())));
        if (file.exists()){
            checkMP3Info(comp, file, nrOfTracks, maxDisc);
        }
        else {
            log.warn("File Not Found: " + file.getAbsolutePath());
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.FILENOTFOUND, "",
                    ""
                    );

        }
        this.albumErrors.items.addAll(this.songErrors);
        this.songErrors = new ArrayList();

    }

    private boolean _checkForTitleExceptions(MGOFileAlbumCompositeTO comp, String originalArtist){
        ArtistSongItem item = MP3Helper.getInstance().prettifyRuleArtistSong(originalArtist, comp.getFileTO().getTitle(), true);
        String title = item.getSong();
        boolean ok = true;
        if (!title.equals(comp.getFileTO().getTitle())){
                    addItem(comp.getFileTO().getId(),
                            comp.getFileTO().getId(),
                            comp.getFileTO().getFile(),
                            comp.getFileAlbumTO().getName(),
                            MP3Tag.TITLE, comp.getFileTO().getTitle(), title);
                    comp.getFileTO().setTitle(title);
                    ok = false;
        }
        return ok;
    }

    private boolean checkForExceptions(MGOFileAlbumCompositeTO comp){
        ArtistSongItem item = MP3Helper.getInstance().prettifyRuleArtistSong(comp.getFileArtistTO().getArtist(), comp.getFileTO().getTitle(), true);
        String artist = item.getArtist();
        boolean ok = true;
        if (!artist.equals(comp.getFileArtistTO().getArtist())){
            addItem(comp.getFileTO().getId(),
                    comp.getFileArtistTO().getID(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ARTIST, comp.getFileArtistTO().getArtist(), artist);
            comp.getFileArtistTO().setArtist(artist);
            ok = false;
        }
        String title = item.getSong();
        if (!title.equals(comp.getFileTO().getTitle())){
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TITLE, comp.getFileTO().getTitle(), title);
            comp.getFileTO().setTitle(title);
            ok = false;
        }
        return ok;
    }

    private boolean checkForAlbumExceptions(MGOFileAlbumCompositeTO comp){
        ArtistSongItem item = MP3Helper.getInstance().prettifyRuleArtistSong(comp.getAlbumArtistTO().getName(), comp.getFileAlbumTO().getName(), true, MP3Helper.SONG_ALBUM_TYPE.ALBUM);
        String artist = item.getArtist();
        boolean ok = true;
        if (!artist.equals(comp.getAlbumArtistTO().getName())){
            addItem(comp.getFileTO().getId(),
                    comp.getAlbumArtistTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUMARTIST, comp.getAlbumArtistTO().getName(), artist);
            comp.getAlbumArtistTO().setName(artist);
            ok = false;
        }
        // No Album Title Check For The Moment
        /*
        String title = item.getSong();
        if (!title.equals(comp.getFileTO().getTitle())){
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TITLE, comp.getFileTO().getTitle(), title);
            comp.getFileTO().setTitle(title);
            ok = false;
        }*/

        return ok;
    }

    private boolean _checkForArtistExceptions(MGOFileAlbumCompositeTO comp){
        ArtistSongItem item = MP3Helper.getInstance().prettifyRuleArtistSong(comp.getFileArtistTO().getArtist(), comp.getFileTO().getTitle(), true);
        String artist = item.getArtist();
        boolean ok = true;
        if (!artist.equals(comp.getFileArtistTO().getArtist())){
            addItem(comp.getFileTO().getId(),
                    comp.getFileArtistTO().getID(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ARTIST, comp.getFileArtistTO().getArtist(), artist);
            comp.getFileArtistTO().setArtist(artist);
            ok = false;
        }
        return ok;
    }

    private MP3Service initMP3File(File file) throws MP3Exception {
        MP3Service mp3file = new MP3JAudioTaggerServiceImpl(file.getAbsolutePath());
        return mp3file;
    }

    private void checkMP3Info(MGOFileAlbumCompositeTO comp, File file, int nrOfTracks, int maxDisc){
        MP3Service mp3file = null;
        MP3Utils mp3Utils = new MP3Utils();
        try {
            // mp3file = new Mp3File(file.getAbsolutePath());
            mp3file = initMP3File(file);
            if (mp3file == null){
                log.error("Could not open file: " + file.getAbsolutePath());
                return;
            }
            if (mp3file.hasTag()) {
                //id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
                if (MP3Utils.checkId3v2Tag(mp3file)) {
                    checkTrack(mp3file.getTrack(), comp, nrOfTracks, maxDisc);
                    checkArtist(comp, mp3file.getArtist());
                    checkTitle(comp, mp3file.getTitle());
                    checkForExceptions(comp);
                    checkDuration(comp, mp3file.getDuration());
                    checkRating(comp, mp3file.getRating());
                    if (checkDisc(comp, mp3file.getDisc())) {
                        if (checkFilename(comp, nrOfTracks, maxDisc)) {
                            // if filename is ok, an extra check for filetitle
                            checkFileTitle(comp);
                        }
                    }
                    checkAlbumArtist(comp, mp3file.isCompilation(), mp3file.getAlbumArtist());
                    checkForAlbumExceptions(comp);
                    checkAlbum(comp, mp3file.getAlbum());

                    /*System.out.println(StringUtils.repeat('=', 100));
                    System.out.println("MP3 Tag Info");
                    System.out.println("Track: " + id3v2Tag.getTrack());
                    System.out.println("Artist: " + id3v2Tag.getArtist());
                    System.out.println("Title: " + id3v2Tag.getTitle());
                    System.out.println(StringUtils.repeat('=', 100));*/
                }
                else {
                    addItem(comp.getFileTO().getId(),
                            comp.getFileTO().getId(),
                            comp.getFileTO().getFile(),
                            comp.getFileAlbumTO().getName(),
                            MP3Tag.MP3CHECK,
                            "No ID3v2 Tag Info Found"  + HTML_BREAK +
                            "Manual Intervention Needed For This File",
                            "");
                }
            }
            else {
                log.error("No id3v2Tag Info found for file: " + file.getAbsolutePath());
                return;
            }
        } catch (MP3Exception | UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error(e);
        }
}

    private boolean checkDuration(MGOFileAlbumCompositeTO comp, long duration) {
        int durationFromDB = comp.getFileTO().getDuration();
        long durationFromMP3 = duration;
        boolean ok = true;
        /*
        if (duration >= 1800){
            //ignore check for mp3 longer thang 0,5 hour
            return true;
        }*/
        if (durationFromMP3 < (durationFromDB-1) || durationFromMP3 > (durationFromDB+1)){
            ok = false;
        }
        if (!ok){
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.DURATION, String.valueOf(durationFromDB), String.valueOf(durationFromMP3));
            try {
                comp.getFileTO().setDuration(Math.toIntExact(durationFromMP3));
            }
            catch (NumberFormatException e){
                // nothing to do
            }
        }
        return ok;
    }

    private boolean checkRating(MGOFileAlbumCompositeTO comp, int ratingFromMP3) {
        int ratingFromDB = comp.getFileTO().getRanking();
        MP3Utils mp3Utils = new MP3Utils();
        int stars = ratingFromMP3; // mp3Utils.convertRating(ratingFromMP3);
        boolean ok = true;
        if (stars != ratingFromDB){
            ok = false;
        }
        if (!ok){
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.RATING, String.valueOf(ratingFromDB), String.valueOf(stars));
            comp.getFileTO().setRanking(stars);
        }
        return ok;
    }

    private boolean checkDisc(MGOFileAlbumCompositeTO comp, String disc) {
        int discFromDB = comp.getFileTO().getDisc();
        int discFromMP3 = 0;
        String mp3Disc = StringUtils.isBlank(disc) ? "0": disc;
        boolean ok = true;
        try {
            discFromMP3 = Integer.parseInt(mp3Disc);
            if (discFromMP3 != discFromDB){
                ok = false;
            }
        }
        catch (NumberFormatException e){
            ok = false;
        }
        if (!ok){
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.DISC, String.valueOf(discFromDB), mp3Disc.trim());
            try {
                comp.getFileTO().setDisc(Integer.parseInt(mp3Disc));
            }
            catch (NumberFormatException e){
                // nothing to do
            }
        }
        return ok;
    }

    private  void addItem(Long fileId, Long id, String file, String album, MP3Tag type, String oldValue, String newValue) {
        AlbumError.Item item = new AlbumError().new Item();
        item.setFileId(fileId);
        item.setId(id);
        item.setFile(file);
        item.setType(type.name());
        item.setOldValue(oldValue);
        item.setNewValue(newValue);
        item.setBasePath(FilenameUtils.getFullPath(file));
        item.setUniqueId(createID());
        item.update = update;
        AlbumError.Item errorITem = findSameErrorType(type);
        if (errorITem == null){
            //albumErrors.items.add(item);
            songErrors.add(item);
        }
        else {
            if (newValue.equals(errorITem.getOldValue())){
                removeErrorType(errorITem.getType());

            }
            else {
                errorITem.setNewValue(newValue);
            }
        }
    }

    private void removeErrorType(String type){
        Predicate<AlbumError.Item> predicate = p-> p.getType().equals(type);
        songErrors.removeIf(predicate);
    }

    private AlbumError.Item findSameErrorType(MP3Tag type){
        AlbumError.Item item = null;
        for (AlbumError.Item errorItem : this.songErrors){
            if (errorItem.type.equals(type.name())){
                item = errorItem;
                break;
            }
        }
        return item;
    }

    private int calculateLengthOfTrack(int nrOfTracks, int nrOfCds){
        if (nrOfCds > 1){
            nrOfTracks = Math.max(nrOfTracks / nrOfCds, 10);
        }
        int lengthTrack = String.valueOf(nrOfTracks).length();
        return Math.max(lengthTrack, 2);

    }

    private boolean checkFilename(MGOFileAlbumCompositeTO comp, int nrOfTracks, int nrOfCds) throws UnsupportedEncodingException {
        boolean ok = true;
        comp.getFileTO().setFile(comp.getFileTO().getFile().replace(SUBST_B, SUBST_A));
        Path pathFromDB = Paths.get(comp.getFileTO().getFile());
        String filenameFromDB = pathFromDB.getFileName().toString();
        log.info("filenameDB = " + filenameFromDB);
        int lengthDisc = nrOfCds > 0 ? String.valueOf(nrOfCds).length() : 0;
        int lengthTrack = calculateLengthOfTrack(nrOfTracks, nrOfCds);
        String track = StringUtils.leftPad(String.valueOf(comp.getFileTO().getTrack()), lengthTrack, '0');
        String cd = nrOfCds > 0 ? StringUtils.leftPad(String.valueOf(comp.getFileTO().getDisc()), lengthDisc, '0')
                : "";
        String filename = cd + track + " " + comp.getFileArtistTO().getArtist() + " - " + comp.getFileTO().getTitle() + ".mp3";
        String strFilenameFromDB = pathFromDB.toString();
        filename = MP3Helper.getInstance().stripFilename(filename);
        if (!strFilenameFromDB.startsWith(SUBST_A)){
            // Path is in small letters instead of The Real Path
            int len = SUBST_A.length();
            String tmp = strFilenameFromDB.substring(0, len);
            if (tmp.compareToIgnoreCase(SUBST_A) == 0){
                Path newPathFile = Paths.get(SUBST_A + strFilenameFromDB.substring(len));
                String newFile = newPathFile.getParent().toString() + File.separator + filename;
                addItem(comp.getFileTO().getId(),
                        comp.getFileTO().getId(),
                        comp.getFileTO().getFile(),
                        comp.getFileAlbumTO().getName(),
                        MP3Tag.FILE, comp.getFileTO().getFile(), newFile);
            }

            ok = false;
        }
        else if (!filenameFromDB.equals(filename)){
            String newFile = pathFromDB.getParent().toString() + File.separator + filename;
            //comp.getFileTO().setFile(newFile);
            log.warn("Filename does not match: " + "filenameFromDB: " + filenameFromDB + " / Formatted: " + filename);
            /* rename file to new file
               update Tables.file with the new file
            */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.FILE, comp.getFileTO().getFile(), newFile);
            ok = false;
        }
        else  {
            File file = new File(relativizeFile((comp.getFileTO().getFile())));
            Path path = Paths.get(file.getAbsolutePath());
            try {
                Path realPath = path.toRealPath(LinkOption.NOFOLLOW_LINKS);
                String realFile = realPath.getFileName().toString();
                log.info("Real File: " +realFile);
                if (!realFile.equals(filenameFromDB)) {
                    addItem(comp.getFileTO().getId(),
                            comp.getFileTO().getId(),
                            comp.getFileTO().getFile(),
                            comp.getFileAlbumTO().getName(),
                            MP3Tag.FILE, realPath.toString().replace(SUBST_B, SUBST_A), comp.getFileTO().getFile());
                    ok = false;

                }
            } catch (IOException e) {
                LogUtils.logError(log, e);
            }
        }


        return ok;
    }
    public static String getFileTitle(String file){
        Path tmp = Paths.get(file);
        String filename = tmp.getFileName().toString();
        filename = FilenameUtils.removeExtension(filename);
        return filename;

    }

    private boolean checkFileTitle(MGOFileAlbumCompositeTO comp) throws UnsupportedEncodingException {
        boolean ok = true;
        comp.getFileTO().setFile(comp.getFileTO().getFile().replace(SUBST_B, SUBST_A));
        String filenameFromDB = getFileTitle(comp.getFileTO().getFile());
        if (!filenameFromDB.equals(comp.getFileTO().getFileTitle())){
            log.warn("FileTitle does not match: " + "filenameFromDB: " + filenameFromDB +
                     " / FileTitle: " + comp.getFileTO().getFileTitle());
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.FILETITLE, comp.getFileTO().getFileTitle(), filenameFromDB);
            comp.getFileTO().setFileTitle(filenameFromDB);
            ok = false;
        }
        return ok;
    }

    private boolean checkTrack(String mp3Track, MGOFileAlbumCompositeTO comp, int nrOfTracks, int nrOfCds){
        //System.out.println("Track: " + id3v2Tag.getTrack());
        String track = null;
        int lengthTrack = calculateLengthOfTrack(nrOfTracks, nrOfCds);
        if (mp3Track == null){
            track = StringUtils.leftPad(String.valueOf(comp.getFileTO().getTrack()),
                    lengthTrack, '0');
        }
        else {
            track = StringUtils.leftPad(mp3Track, lengthTrack, '0');
        }
        boolean ok = true;
        try {
            Integer.parseInt(mp3Track);
        }
        catch (NumberFormatException ex){
            ok = false;
            if (!this.update) {
                addItem(comp.getFileTO().getId(),
                        comp.getFileTO().getId(),
                        comp.getFileTO().getFile(),
                        comp.getFileAlbumTO().getName(),
                        MP3Tag.TRACKERROR, mp3Track, null);
            }
            else {
                addItem(comp.getFileTO().getId(),
                        comp.getFileTO().getId(),
                        comp.getFileTO().getFile(),
                        comp.getFileAlbumTO().getName(),
                        MP3Tag.TRACK, null, String.valueOf(comp.getFileTO().getTrack()));
            }
        }
        if (!ok){
           // do nothing
        }
        else if (!this.update && !track.equals(mp3Track)){
            if (mp3Track != null){
                comp.getFileTO().setTrack(Integer.valueOf(mp3Track));
            }
            log.warn("Track does not match: " + "mp3: " + mp3Track + " / Formatted: " + track);
            /* example : Track is 1 => update to 01
                only update the mp3 tag, DB stores it as int
             */
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TRACK, mp3Track, track);
            ok = false;
        }
        else if (Integer.parseInt(track) != comp.getFileTO().getTrack().intValue()){
            log.warn("Track does not match: " + "DB: " + comp.getFileTO().getTrack() + " / Formatted: " + track);
            /* example : Track is 1 => update to 01
                only update the mp3 tag, DB stores it as int
             */
            String oldValue = String.valueOf(comp.getFileTO().getTrack());
            String newValue = track;
            if (this.update){
                oldValue = track;
                newValue = String.valueOf(comp.getFileTO().getTrack());
            }
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TRACK, oldValue, newValue);
            if (!this.update) {
                comp.getFileTO().setTrack(Integer.valueOf(track));
            }
            ok = false;
        }
        return ok;
    }

    private boolean checkAlbum(MGOFileAlbumCompositeTO comp, String mp3Album){
        String album = MP3Helper.getInstance().prettifyAlbum(mp3Album, comp.getAlbumArtistTO().getName());
        boolean ok = true;
        if (!update && !album.equals(mp3Album)){
            log.warn("Album does not match: " + "mp3: " + mp3Album + " / Formatted: " + album);
            /* update mp3 + DB */
            comp.getFileAlbumTO().setName(album);
            addItem(comp.getFileTO().getId(),
                    comp.getFileAlbumTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUM, mp3Album, album);
            ok = false;
        }
        else if (!this.update && !album.equals(comp.getFileAlbumTO().getName())){
            log.warn("Album does not match: " + "formatted: " + mp3Album + " / DB: " + comp.getFileAlbumTO().getName());
            /* update mp3 + DB */
            String oldValue = comp.getFileAlbumTO().getName();
            String newValue = album;
            if (this.update){
                oldValue = album;
                newValue = comp.getFileAlbumTO().getName();
            }
            addItem(comp.getFileTO().getId(),
                    comp.getFileAlbumTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUM, oldValue, newValue);
            if (!this.update) {
                comp.getFileAlbumTO().setName(album);
            }
            ok = false;
        }
        File file = new File(comp.getFileTO().getFile());
        String physicalPath = getAlbumPath(file);
        String strippedPhysicalPath = removeYearFromAlbum(physicalPath);
        //strippedPhysicalPath = MP3Helper.getInstance().stripFilename(strippedPhysicalPath);
        if (!"VARIOUS ARTISTS".equals(comp.getAlbumArtistTO().getName().toUpperCase())){
            album = comp.getAlbumArtistTO().getName() + " - " + album;
        }
        String strippedAlbum = MP3Helper.getInstance().stripFilename(album);
        if (!strippedAlbum.equals(physicalPath) && !strippedAlbum.equals(strippedPhysicalPath)){
            log.warn("Path Album does not match: " + "Formatted: " + strippedAlbum + " / Disc: " + physicalPath);
            String possibleNewFile = file.getParentFile().getParentFile().getAbsolutePath() + File.separator + strippedAlbum + File.separator;
            String oldFile = file.getParentFile().getAbsolutePath() + File.separator;
            addItem(comp.getFileTO().getId(),
                    comp.getFileAlbumTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUMCHECK, getAlbumCheckInfoOld("Disc:", strippedAlbum, "MP3:", album),
                                              getAlbumCheckInfoNew(
                                                      //file.getParentFile().getAbsolutePath(),
                                                      comp.getFileTO().getId(),
                                                      oldFile,
                                                      possibleNewFile));
            ok = false;
        }
        return ok;
    }

    public static String getAlbumPath(File file){
        String path = file.getParentFile().getName();
        /* check if path is a subdivision, ex. 0001 - 0100
           Main Path is than the parent of the parent of the file
         */
        if (path.matches("(.* - )?[0-9]{3,4} ?- ?[0-9]{3,4}")){
            File parent = file.getParentFile().getParentFile();
            if (parent != null) {
                path = parent.getName();
            }
        }
        return path;
    }

    private String getAlbumCheckInfoOld(String line1, String line2, String line3, String line4){
        String value = line1 + HTML_BREAK;
        value += line2 + HTML_BREAK;
        value += line3 + HTML_BREAK;
        value += line4 + HTML_BREAK;
        value += "Manual Intervention Needed. Possible Actions:" + HTML_BREAK;
        value += "1. Rename folder to " + line2 + HTML_BREAK;
        value += "2. Update DB With New Value";
        return value;
    }

    private String getAlbumCheckInfoNew(Long id, String oldValue, String newValue){
        String SQL = new SQLBuilder()
                .update()
                .addTable(TablesEnum.MGOFile)
                .updateColumn(MGOFileColumns.FILE, Type.FUNCTION,
                        "REPLACE(" + MGOFileColumns.FILE.name() +
                              ",'" + oldValue + "','" + newValue + "')")
                //.addCondition(MGOFileColumns.FILE, Comparator.LIKE, find + "%")
                .addCondition(MGOFileColumns.ID, Comparator.EQUALS, id)
                .render();
        return SQL;
    }

    public String removeYearFromAlbum(String text){
        String prettifiedText = text;
        prettifiedText = prettifiedText.replaceAll("\\([0-9]{1,4}\\)$", "");
        return prettifiedText.trim();
    }

    private boolean checkArtist(MGOFileAlbumCompositeTO comp, String mp3Artist){
        String artist = MP3Helper.getInstance().prettifyArtist(mp3Artist);
        boolean ok = true;
        if (!this.update && !artist.equals(mp3Artist)){
            log.warn("Artist does not match: " + "mp3: " + mp3Artist + " / Formatted: " + artist);
            /* update mp3 + DB */
            comp.getFileArtistTO().setArtist(artist);
            addItem(comp.getFileTO().getId(),
                    comp.getFileArtistTO().getID(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ARTIST, mp3Artist, artist);
            ok = false;
        }
        else if (!artist.equals(comp.getFileArtistTO().getArtist())){
            log.warn("Artist does not match: " + "DB: " + comp.getFileArtistTO().getArtist() + " / Formatted: " + artist);
            /* update mp3 + DB */
            String oldValue = comp.getFileArtistTO().getArtist();
            String newValue = artist;
            if (this.update){
                oldValue = artist;
                newValue = comp.getFileArtistTO().getArtist();
            }
            addItem(comp.getFileTO().getId(),
                    comp.getFileArtistTO().getID(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ARTIST, oldValue, newValue);
            if (!this.update) {
                comp.getFileArtistTO().setArtist(artist);
            }
            ok = false;
        }
        return ok;
    }

    private boolean addAlbumArtistError(MGOFileAlbumCompositeTO comp, boolean update, String oldValue, String newValue){
        addItem(comp.getFileTO().getId(),
                comp.getAlbumArtistTO().getId(),
                comp.getFileTO().getFile(),
                comp.getFileAlbumTO().getName(),
                MP3Tag.ALBUMARTIST, oldValue, newValue);
        if (!update) {
            comp.getAlbumArtistTO().setName(newValue);
        }
        return false;
    }

    private boolean checkAlbumArtist(MGOFileAlbumCompositeTO comp, boolean compilation, String mp3AlbumArtist){
        String albumArtist = MP3Helper.getInstance().prettifyArtist(mp3AlbumArtist);
        boolean ok = true;
        if (compilation){
           if (!albumArtist.equals(Constants.AlbumArtist.name)){
               log.warn("Compilation CD: Album Artist must be " + Constants.AlbumArtist.name);
               ok = addAlbumArtistError(comp, update, albumArtist, Constants.AlbumArtist.name);
           }
            else if (!mp3AlbumArtist.equals(Constants.AlbumArtist.name)) {
               log.warn("Compilation CD: Album Artist must be " + Constants.AlbumArtist.name);
               ok = addAlbumArtistError(comp, update, mp3AlbumArtist, Constants.AlbumArtist.name);
           }
        }
        else if (!this.update && !albumArtist.equals(mp3AlbumArtist)){
            log.warn("Album Artist does not match: " + "mp3: " + mp3AlbumArtist + " / Formatted: " + albumArtist);
            ok = addAlbumArtistError(comp, update, mp3AlbumArtist, albumArtist);
        }
        if (ok){
           if (!this.update && !albumArtist.equals(comp.getAlbumArtistTO().getName())) {
               log.warn("Album Artist does not match: " + "DB: " + comp.getAlbumArtistTO().getName() + " / Formatted: " + albumArtist);
            /* update mp3 + DB */
               String oldValue = comp.getAlbumArtistTO().getName();
               String newValue = albumArtist;
               if (this.update) {
                   // updated the value through the update screen, overruling the info from the mp3 file
                   oldValue = albumArtist;
                   newValue = comp.getAlbumArtistTO().getName();
               }
               ok = addAlbumArtistError(comp, update, oldValue, newValue);
           }
        }
        return ok;
    }

    private boolean checkTitle(MGOFileAlbumCompositeTO comp, String mp3Title){
        boolean ok = true;
        String title = MP3Helper.getInstance().prettifySong(mp3Title);
        if (!this.update && !title.equals(mp3Title)){
            log.warn("Title does not match: " + "mp3: " + mp3Title + " / Formatted: " + title);
            /* update mp3 + DB */
            comp.getFileTO().setTitle(title);
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TITLE, mp3Title, title);
            ok = false;
        }
        else if (!title.equals( comp.getFileTO().getTitle())){
            log.warn("Title does not match: " + "DB: " + comp.getFileTO().getTitle() + " / Formatted: " + title);
            /* update mp3 + DB */
            String oldValue = comp.getFileTO().getTitle();
            String newValue = title;
            if (this.update){
                oldValue = title;
                newValue = comp.getFileTO().getTitle();
            }
            addItem(comp.getFileTO().getId(),
                    comp.getFileTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.TITLE, oldValue, newValue);
            if (!this.update) {
                comp.getFileTO().setTitle(title);
            }
            ok = false;
        }
        return ok;
    }

    public MP3Settings.Mezzmo.Mp3Checker.RelativePath getRelativePath() {
        return relativePath;
    }

    public String relativizeFile(String file){

        //String SUBST_1 = "h:\\shared\\mijn muziek\\eric\\ipod\\";
        //file = file.replace(SUBST_A, SUBST_B);
        //file = file.replace(SUBST_1, SUBST_B);
        file = file.replace(this.relativePath.original, this.relativePath.substitute);
        return file;
    }

}
