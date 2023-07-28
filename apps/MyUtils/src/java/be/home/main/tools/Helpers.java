package be.home.main.tools;

import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.common.utils.StringUtils;
import be.home.domain.model.MP3TagUtils;
import be.home.domain.model.MezzmoUtils;
import be.home.domain.model.service.MP3FramePattern;
import be.home.domain.model.service.MP3Service;
import be.home.mezzmo.domain.bo.ComposerBO;
import be.home.mezzmo.domain.enums.MP3CleanupType;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.VersionTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.json.AlbumError;
import be.home.model.json.MP3Settings;
import org.apache.logging.log4j.Logger;

import org.springframework.dao.EmptyResultDataAccessException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ghyssee on 09/05/2023
 */
public class Helpers extends BatchJobV2 {

    private static final Logger log = getMainLog(Helpers.class);
    public static MezzmoServiceImpl mezzmoService = null;
    public static ArrayList<String> composers = new ArrayList<String>() {
        {
            /* when these words are found in composer tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Composer */

            add("Bob Seger");
            add("Clark Datchler");
            add("Phil Cunningham");
            add("Jimmy Pursey");
            add("Mike Shinoda");
            add("Concetta Kirschner");
            add("J. Allsop");
            add("Jamie Masefield");
            add("Air Traffic");
            add("Nicolette");
            add("Antonina Armato");
            add("Benga");
            add("Theo Dewitte");
            add("Brad Paisley");
            add("Rihanna");
            add("Madeline Noyes");
            add("Sarah McTaggart");
            add("Cesar Sampson");
            add("Alex Papaconstantinou");
            add("Stav Beger");

            //add("");


        }
    };


    public static ArrayList<String> customTags = new ArrayList<String>() {
        {
            /* is used for cleanup of Custom TXXX Tags + Custom Comment Tags
               + Private Tags. ex. TXXX:MUSICBRAINZ, ...
             */
            // add("");

            add("^DISCOGS(.*)");
            add("^SIZE");
            add("^nos(.*)");
            add("^XMP");
            add("^gapless_playback");
            add("(.*)SUVI(.*)");
            add("^Software");
            add("^Interpr.tes");
            add("^Composer(.*)");
            add("^Copyright message");
            add("^TSO2");
            add("^name");
            add("^creation_time");
            add("Originator");
            add("OrigReference");
            add("^WM/Track");
            add("^WMFSDKVersion");
            add("^WMFSDKNeeded");
            add("^DeviceConformanceTemplate");
            add("^IsVBR");
            add("^Upload(.*)");

        }
    };

    public static ArrayList<String> globalCleanupWords = new ArrayList<String>() {
        {
            /* checked globals */
            // add("(.*)(.*)");
            add("(.*)www.simplemp3s.com(.*)");
            add("(.*)Patjess Place Music(.*)");
            add("(.*)Www.Clubkings.Prv.Pl(.*)");
            add("(.*)Www.Im1-Music.Tk(.*)");
            add("(.*)vip4u.de.vu(.*)");
            add("(.*)redthreat.wordpress.com(.*)");
            add("(.*)elchikokevo.blogspot.com(.*)");
            add("(.*)Hopsmp3thing.Com(.*)");
            add("(.*)vip4u.de.vu(.*)");
            add("(.*)www.Vaylo.com(.*)");
            add("(.*)jonnyaliblog.blogspot.com(.*)");
            add("(.*)www.dailyrnb4u.blogspot.com(.*)");
            add("(.*)www.exclusive\\-music\\-dj.com(.*)");
            add("(.*)DailyTunez.com(.*)");
            add("(.*)WWW.iM1MUSIC.NET(.*)");
            add("^NoFS$");
            add("(.*)EkzclusiveMusikz.co.nr(.*)");
            add("(.*)Hellboy(.*)");
            add("(.*)GoodMusicAllDay.com(.*)");
            add("(.*)www.beatz.bz(.*)");
            add("(.*)jams.to(.*)");
            add("(.*)www.NewJams.net(.*)");
            add("(.*)www.clubomba.com(.*)");
            add("(.*)www.dreamplace.biz(.*)");
            add("(.*)WwW.ElVacilonMusical.CoM(.*)");
            add("(.*)www.dvdvideosoft.com(.*)");
            add("(.*)www.klubowamuza.eu(.*)");
            add("(.*)DailyMusic.ru(.*)");
            add("(.*)www.mp3-box.com(.*)");
            add("^Ajm[0-9]{1,4}(.*)");
            add("(.*)vrijemp3.ws(.*)");
            add("(.*)mp3lio.com(.*)");
            add("(.*)Kingdom-Leaks.com(.*)");
            add("(.*)Pryda Recordings(.*)");
            add("(.*)beatport.com(.*)");
            add("^Prime Music(.*)");
            add("(.*)formulenl(.*)");
            add("(.*)myzuka.org(.*)");
            add("(.*)www.Mr\\-Music.Info(.*)");
            add("(.*)TakTaraneh.Com(.*)");
            add("^IPAUTA$");
            add("(.*)Mp3Fun.IN(.*)");
            add("(.*)Street\\-VibezmOsiK(.*)");

        }
    };

    public static ArrayList<MP3FramePattern> frameCleanups = new ArrayList<MP3FramePattern>() {
        {
            /* COMM */
            //add(new MP3FramePattern("COMM", ""));
            //add(new MP3FramePattern("TIT1", ""));
            //add(new MP3FramePattern("TIT3", ""));
            //add(new MP3FramePattern("TPE4", ""));
            //add(new MP3FramePattern("TKEY", ""));
            //add(new MP3FramePattern("WXXX", ""));
            //add(new MP3FramePattern("TFLT", ""));
            //add(new MP3FramePattern("TENC", ""));
            //add(new MP3FramePattern("TPUB", ""));
            //add(new MP3FramePattern("TCOM", ""));
            //add(new MP3FramePattern("TCOP", ""));
            //add(new MP3FramePattern("TSSE", ""));
            //add(new MP3FramePattern("TMED", ""));
            //add(new MP3FramePattern("USLT", ""));
            //add(new MP3FramePattern("TOWN", ""));
            //add(new MP3FramePattern("TOPE", ""));
            //add(new MP3FramePattern("TSRN", ""));
            add(new MP3FramePattern("COMM", "^ESCAPE$"));
            add(new MP3FramePattern("TENC", "^By Normantorres\\-Aka 47"));
            add(new MP3FramePattern("TPUB", "^Hip\\-O$"));
            add(new MP3FramePattern("TIT1", "^Master$"));
            add(new MP3FramePattern("TCOM", "^\\- LiR Style(.*)"));
            add(new MP3FramePattern("TCOM", "^TEAM OSC"));
            add(new MP3FramePattern("TPUB", "^143$"));
            add(new MP3FramePattern("TCOM", "^Bsaii"));
            add(new MP3FramePattern("COMM", "^The Regulators(.*)"));
            add(new MP3FramePattern("USLT", "^eck eck(.*)"));
            add(new MP3FramePattern("TEXT", "^John Dahlbäck(.*)"));
            add(new MP3FramePattern("TIT1", "^Eurovision(.*)"));
            add(new MP3FramePattern("TENC", "^Tunebite(.*)"));
            add(new MP3FramePattern("TIT1", "^La Face(.*)"));
            add(new MP3FramePattern("TCOM", "^Polow Da Don(.*)"));
            add(new MP3FramePattern("TEXT", "^Miguel Wiels(.*)"));
            add(new MP3FramePattern("TPUB", "^Great Stuff(.*)"));
            add(new MP3FramePattern("TENC", "^Jon$"));
            add(new MP3FramePattern("TENC", "^320 Kpbs"));
            add(new MP3FramePattern("USLT", "^Title\\: Foreign affair(.*)"));
            add(new MP3FramePattern("SYLT", "(.*)"));
            add(new MP3FramePattern("TENC", "^320 Kpbs"));
            add(new MP3FramePattern("TSSE", "^Polish people are slaves(.*)"));
            add(new MP3FramePattern("TIT3", "^Radio Edit"));
            add(new MP3FramePattern("TIT3", "^HITT$"));
            add(new MP3FramePattern("TCOM", "^Play & Win"));
            add(new MP3FramePattern("TPUB", "^Chris Rifflen"));
            add(new MP3FramePattern("TPUB", "^Roc-A-Fella(.*)"));
            add(new MP3FramePattern("TKEY", "^C$"));
            add(new MP3FramePattern("TPUB", "^Hollywood$"));
            add(new MP3FramePattern("TPUB", "^Chartbreaker"));
            add(new MP3FramePattern("COMM", "^\\:\\-\\)$"));
            add(new MP3FramePattern("COMM", "^Top Radio Stars"));
            add(new MP3FramePattern("TPUB", "^Doorn \\(?Spin?nin'?\\)?"));
            add(new MP3FramePattern("COMM", "^Fuck The Fakers"));
            add(new MP3FramePattern("TCOM", "^Nrj"));
            add(new MP3FramePattern("COMM", "^Maxx"));
            add(new MP3FramePattern("TSSE", "^MP3"));
            add(new MP3FramePattern("TCOP", "^Frimusic"));
            add(new MP3FramePattern("TPUB", "^[0-9]{1,2}$"));
            add(new MP3FramePattern("TENC", "^[0-9]{1,2}$"));
            add(new MP3FramePattern("WOAR", "^[0-9]{1,2}$"));
            add(new MP3FramePattern("TIT3", "^[0-9]{1,2}$"));
            add(new MP3FramePattern("TMED", "^Music"));
            add(new MP3FramePattern("TPUB", "^Hollandse Hits"));
            add(new MP3FramePattern("UFID", "(.*)track(.*)"));
            add(new MP3FramePattern("TMED", "^G$"));
            add(new MP3FramePattern("WXXX", "^ng"));
            add(new MP3FramePattern("TCOP", "^Silverwing 2018"));
            add(new MP3FramePattern("COMM", "^Fifa Coca cola(.*)"));

        }
    };

    public ArrayList<String> globalExcludeWords = new ArrayList<String>() {
        {
            /* when these words are found in one of the non standard tags, it's not considered as a warning */
            /* also used for comments that should not be deleted an not shown as warning */

        }
    };

    public static ArrayList<MP3FramePattern> frameExclusions = new ArrayList<MP3FramePattern>() {
        {
            /* when these words are found in publishers tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */
            //add(new MP3FramePattern("", ""));
            add(new MP3FramePattern("TPUB", "^Ariola(.*)"));

            //add(new MP3FramePattern("TCOP", "^(.*)"));
            //add(new MP3FramePattern("USLT", "^(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Lost & Cie(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Paradise Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Studio 100(.*)"));
            add(new MP3FramePattern("TCOP", "^Mostiko Belgium(.*)"));
            add(new MP3FramePattern("TENC", "^Mp3 To Wave Converter Plus(.*)"));
            add(new MP3FramePattern("TENC", "^Free Mp3 Wma Converter(.*)"));
            add(new MP3FramePattern("TENC", "^Play MPE Player(.*)"));
            add(new MP3FramePattern("USLT", "^Can we pretend that airplanes(.*)"));
            add(new MP3FramePattern("WXXX", "^www.eurovision.tv(.*)"));
            add(new MP3FramePattern("WXXX", "^www.edshaeeran.com"));
            add(new MP3FramePattern("TSSE", "^FFmbc(.*)"));
            add(new MP3FramePattern("TSSE", "^To MP3 Converter Free(.*)"));
            add(new MP3FramePattern("TSSE", "^Logic Pro(.*)"));
            add(new MP3FramePattern("USLT", "^You can call me artist(.*)"));
            add(new MP3FramePattern("COMM", "^Engelse cover van Zeil je voor het eerst(.*)"));


        }
    };
    public static ArrayList<String> publishers = new ArrayList<String>() {
        {
            /* when these words are found in publishers tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Publisher */
            //add("^(.*)");
             add("^RSM 22(.*)");
            add("^Blue Moon(.*)");
            add("^Poker Flat(.*)");
            add("^K7(.*)");
            add("^Accurate(.*)");
            add("^Central Station(.*)");
            add("^Superstar Recordings(.*)");
            add("^Wagram(.*)");
            add("^Homerun(.*)");
            add("(.*)Studio 100(.*)");
            add("^Bmg/Ariola(.*)");
            add("^LaFace(.*)");
            add("^Rebel Rock(.*)");
            add("^Musicor(.*)");
            add("^Homerun Records(.*)");
            add("^Sbme Special Mkts(.*)");
            add("^Sinuz Recordings(.*)");
            add("^Mercury(.*)");
            add("^Vertigo(.*)");
            add("(.*)Universal Music)(.*)");
            add("^Republic(.*)");
            add("(.*)Starwatch Entertainment(.*)");
            add("(.*)Smash The House(.*)");
            add("^Mosaert");
            add("^BLNK Music(.*)");
            add("^Pias");
            add("^Bruce Springsteen(.*)");

         }
    };

    public static void main(String args[]) throws IOException {


        //SortComposerFile();

        importComposers();
        importPublishers();
        importExclusionLines();
        importCleanupLines();
        importGlobalCleanupLines();
        importCustomTags();

    }




    private static void testiPodDate(){
        Date tmp = SQLiteUtils.convertiPodDateToDate(610643234L);
        System.out.println(tmp);
        tmp = SQLiteUtils.convertiPodDateToDate(573087600L);
        System.out.println(tmp);
        tmp = SQLiteUtils.convertiPodDateToDate(604623600L);
        System.out.println(tmp);
    }

    private static void testVersion(){

        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        String version = mp3Settings.mezzmo.version;
        try {
            VersionTO versionTO = getMezzmoService().findVersion(version);
            System.out.println(versionTO.lastUpdated);
        }
        catch (EmptyResultDataAccessException e){
            int nr = getMezzmoService().addVersion(version);
            log.info("Version: " + version);
        }
    }

    private static void testAlbumArtist(){
        SQLiteJDBC.initialize();
        // update Album Artist With New Name
        MGOFileAlbumCompositeTO comp = testAlbumArtistItem(140342L, 4594L, "Sven Van HeesXX");
        // reset it to the Original Name
        comp = testAlbumArtistItem(comp.getFileTO().getId(), comp.getAlbumArtistTO().getId(), "Sven Van Hees");
        comp = testAlbumArtistItem(comp.getFileTO().getId(), comp.getAlbumArtistTO().getId(), "Various Artists");
        comp = testAlbumArtistItem(comp.getFileTO().getId(), comp.getAlbumArtistTO().getId(), "Sven Van Hees");
        comp = testAlbumArtistItem(comp.getFileTO().getId(), comp.getAlbumArtistTO().getId(), "Sven Van HEES");
        comp = testAlbumArtistItem(comp.getFileTO().getId(), comp.getAlbumArtistTO().getId(), "Sven Van Hees");
    }

    private static MGOFileAlbumCompositeTO testAlbumArtistItem(long fileId, long albumArtistId, String name){
        MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
        comp.getFileTO().setId(fileId);
        comp.getAlbumArtistTO().setId(albumArtistId);
        comp.getAlbumArtistTO().setName(name);
        try {
            MezzmoServiceImpl.getInstance().updateAlbumArtist(comp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comp;
    }

    private static void fileNotFound(){
        AlbumError albumErrors = (AlbumError) JSONUtils.openJSONWithCode(Constants.JSON.ALBUMERRORS, AlbumError.class);
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath = MezzmoUtils.getRelativePath(mp3Settings);
        MP3TagUtils tagUtils = new MP3TagUtils(albumErrors, relativePath);

        final String query = "SELECT MGOFile.ID, MGOFileAlbum.Data AS ALBUM, MGOFile.disc, MGOFile.track, MGOFile.playcount, * from MGOFile MGOFILE" + System.lineSeparator() +
                "INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id)" + System.lineSeparator() +
                "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" + System.lineSeparator() +
                "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.id)" + System.lineSeparator() +
                "INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID)" + System.lineSeparator() +
                "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.FileID = MGOFILE.id)" + System.lineSeparator() +
                " where MGOFile.FILE like '%";

        final String delQuery = "DELETE FROM MGOFile WHERE ID=";


        try {
            MyFileWriter myFile = new MyFileWriter("C:\\My Data\\tmp\\Java\\MP3Processor\\Test\\RenameFiles.txt", MyFileWriter.NO_APPEND);
            MyFileWriter myFile2 = new MyFileWriter("C:\\My Data\\tmp\\Java\\MP3Processor\\Test\\CheckDoubles.txt", MyFileWriter.NO_APPEND);
            for (AlbumError.Item item : albumErrors.items){
                if (item.type.equals("FILENOTFOUND")){
                    File file = new File(item.file);
                    String track = file.getName().split(" ",2)[0];
                    String oldFile = tagUtils.relativizeFile(file.getParent() + File.separator + track);
                    myFile.append("ren \"" + oldFile + "*\" \"" + file.getName() + "\"");
                    String relPath = file.getParent();
                    relPath = SQLiteUtils.escape(relPath.replace(tagUtils.getRelativePath().original, "")
                            .replace(tagUtils.getRelativePath().substitute, "")
                    );
                    myFile2.append(query + relPath + File.separator + track + "%'");
                    myFile2.append("");
                    myFile2.append(delQuery + item.fileId);
                    myFile2.append("");

                }
            }
            myFile.close();
            myFile2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void importExclusionLines() throws IOException {

        ComposerBO composerBO = ComposerBO.getInstance();

        for (MP3FramePattern pattern : frameExclusions) {
            if (!StringUtils.isBlank(pattern.getPattern()) && !StringUtils.isBlank(pattern.getFrameId())) {
                composerBO.add(MP3CleanupType.EXCLUDE, pattern.getFrameId(), pattern.getPattern());
            } else {
                log.info("Skipping empty exclusion line: " + pattern.getFrameId() + " - " + pattern.getPattern());
            }
        }
        composerBO.save();
    }

    private static void importCleanupLines() throws IOException {

        ComposerBO composerBO = ComposerBO.getInstance();

        for (MP3FramePattern pattern : frameCleanups) {
            if (!StringUtils.isBlank(pattern.getPattern()) && !StringUtils.isBlank(pattern.getFrameId())) {
                composerBO.add(MP3CleanupType.CLEAN, pattern.getFrameId(), pattern.getPattern());
            } else {
                log.info("Skipping empty cleanup line: " + pattern.getFrameId() + " - " + pattern.getPattern());
            }
            composerBO.save();
        }
    }

    private static void importGlobalCleanupLines() throws IOException {

        ComposerBO composerBO = ComposerBO.getInstance();

        for (String pattern : globalCleanupWords){
            if (!StringUtils.isBlank(pattern)) {
                composerBO.add(MP3CleanupType.CLEAN, MP3Service.GLOBAL_FRAME, pattern);
            }
            else {
                log.info("Skipping empty global cleanup line: " + pattern);
            }
        }
        composerBO.save();
    }

    private static void importComposers() throws IOException {
        //composerFile.composers.

        ComposerBO composerBO = ComposerBO.getInstance();

        for (String composer : composers){
            if (!StringUtils.isBlank(composer)) {
                composerBO.add(MP3CleanupType.EXCLUDE, "TCOM", composer, true);
            }
            else {
                log.info("Skipping empty composer line: " + composer);
            }
        }
        composerBO.save();
    }

    private static void importCustomTags() throws IOException {
        //composerFile.composers.

        ComposerBO composerBO = ComposerBO.getInstance();

        for (String customTag : customTags){
            if (!StringUtils.isBlank(customTag)) {
                composerBO.add(MP3CleanupType.CUSTOM_TAG, MP3Service.GLOBAL_FRAME, customTag);
            }
            else {
                log.info("Skipping empty Custom Tag line: " + customTag);
            }
        }
        composerBO.save();
    }

    private static void importPublishers() throws IOException {

        ComposerBO composerBO = ComposerBO.getInstance();

        for (String publisher : publishers){
            if (!StringUtils.isBlank(publisher)) {
                composerBO.add(MP3CleanupType.EXCLUDE, "TPUB", publisher);
            }
            else {
                    log.info("Skipping empty publisher line: " + publisher);
            }
        }
        composerBO.save();
    }

    private static void SortComposerFile() throws IOException {
        ComposerBO composerBO = ComposerBO.getInstance();
        composerBO.sort();
        composerBO.save();

    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }

    @Override
    public void run() {

    }
}
