package be.home.domain.model;

import be.home.common.enums.MP3Tag;
import be.home.mezzmo.domain.dao.SQLBuilder;
import be.home.mezzmo.domain.dao.jdbc.MGOFileColumns;
import be.home.mezzmo.domain.dao.jdbc.TablesEnum;
import be.home.mezzmo.domain.model.AlbumError;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Gebruiker on 17/12/2016.
 */
public class MP3TagUtils {

    public AlbumError albumErrors;
    private static final Logger log = Logger.getLogger(MP3TagUtils.class);
    public static final String SUBST_A = "H:\\Shared\\Mijn Muziek\\Eric\\iPod\\";
    //public static final String SUBST_B = "R:\\My Music\\iPod\\";
    public static final String SUBST_B = "C:\\My Data\\tmp\\Java\\MP3Processor\\Album\\";
    public static final String HTML_BREAK = "<br>";

    private MP3TagUtils (){

    }

    public MP3TagUtils(AlbumError albumErrors){
        this.albumErrors = albumErrors;
    }


    public void processSong(MGOFileAlbumCompositeTO comp, int nrOfTracks, int maxDisc){
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

    private void checkMP3Info(MGOFileAlbumCompositeTO comp, File file, int nrOfTracks, int maxDisc){
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(file.getAbsolutePath());
            ID3v2 id3v2Tag;
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
                checkTrack(id3v2Tag.getTrack(), comp, nrOfTracks, maxDisc);
                checkArtist(comp, id3v2Tag.getArtist());
                checkTitle(comp, id3v2Tag.getTitle());
                checkAlbum(comp, id3v2Tag.getAlbum());
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


    private  void addItem(Long id, String file, String album, String type, String oldValue, String newValue){
        AlbumError.Item item = new AlbumError().new Item();
        item.setId(id);
        item.setFile(file);
        item.setType(type);
        item.setOldValue(oldValue);
        item.setNewValue(newValue);
        item.setBasePath(FilenameUtils.getFullPath(file));
        albumErrors.items.add(item);
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
               update Tables.file with the new file
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

    private boolean checkAlbum(MGOFileAlbumCompositeTO comp, String mp3Album){
        String album = MP3Helper.getInstance().prettifyAlbum(mp3Album);
        boolean ok = true;
        if (!album.equals(mp3Album)){
            log.warn("Album does not match: " + "mp3: " + mp3Album + " / Formatted: " + album);
            /* update mp3 + DB */
            comp.getFileAlbumTO().setName(album);
            addItem(comp.getFileAlbumTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUM.name(), mp3Album, album);
            ok = false;
        }
        else if (!album.equals(comp.getFileAlbumTO().getName())){
            log.warn("Album does not match: " + "formatted: " + mp3Album + " / DB: " + comp.getFileAlbumTO().getName());
            /* update mp3 + DB */
            addItem(comp.getFileAlbumTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUM.name(), comp.getFileAlbumTO().getName(), album);
            comp.getFileAlbumTO().setName(album);
            ok = false;
        }
        File file = new File(comp.getFileTO().getFile());
        String path = file.getParentFile().getName();
        path = removeYearFromAlbum(path);
        if (!album.equals(path)){
            log.warn("Path Album does not match: " + "Formatted: " + album + " / Disc: " + path);
            String oldFile = file.getParentFile().getParentFile().getAbsolutePath() + File.separator + album + File.separator;
            String possibleNewFile = file.getParentFile().getAbsolutePath() + File.separator;
            addItem(comp.getFileAlbumTO().getId(),
                    comp.getFileTO().getFile(),
                    comp.getFileAlbumTO().getName(),
                    MP3Tag.ALBUMCHECK.name(), getAlbumCheckInfoOld("Disc:", path, "MP3:", album),
                                              getAlbumCheckInfoNew(
                                                      file.getParentFile().getAbsolutePath(),
                                                      oldFile,
                                                      possibleNewFile));
            comp.getFileArtistTO().setArtist(album);
            ok = false;
        }
        return ok;
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

    private String getAlbumCheckInfoNew(String find, String oldValue, String newValue){
        String SQL = new SQLBuilder()
                .update()
                .addTable(TablesEnum.MGOFile)
                .updateColumn(MGOFileColumns.File, SQLBuilder.Type.FUNCTION,
                        "REPLACE(" + MGOFileColumns.File.name() +
                              ",'" + oldValue + "','" + newValue + "')")
                .addCondition(MGOFileColumns.File, SQLBuilder.Comparator.LIKE, find + "%")
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

    public String stripFilename(String filename){
        String strippedFilename = filename;
        strippedFilename = strippedFilename.replaceAll("<3", "Love");
        strippedFilename = strippedFilename.replaceAll("\\*\\*\\*", "uck");
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
        strippedFilename = strippedFilename.replaceAll("[^&()\\[\\],'. a-zA-Z0-9.-]", "");

        return strippedFilename;
    }

    public static String relativizeFile(String file){
        return file.replace(SUBST_A, SUBST_B);
    }

}
