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

            add("Robbie van Leeuwen");

            add("Nicole Millar");
            add("Adam Hyde");
            add("Reuben Styles");
            add("Sam La More");
            add("C.? Edward Halstead");
            add("E.? Matthews");
            add("F.? Taylor");
            add("Mark Knight");
            add("Axel Hedfors");
            add("Alex Van Den Hoef");
            add("Patrik Berger");
            add("J.? Allen");
            add("Sebastian Lundberg");
            add("E.? Simons");
            add("Mikah Freeman");
            add("Nick Littlemore");
            add("Patrick Bruyndonx");
            add("Martin Garrix");
            add("Jay Hardway");
            add("A.? Hynne");
            add("Will Heard");
            add("Ray Slijngaard");
            add("Joel Fletcher Allen");
            add("Will Sparks");
            add("Kam Denny");
            add("D.? Romers");
            add("Paul Brandoli");
            add("Darren Foreman");
            add("Cara Salimando");
            add("Luke Calleja");
            add("N.? Pelusi");
            add("T.? Honeywill");
            add("Sander van Doorn");
            add("Michael Chard");
            add("T.? Olsen");
            add("Zoe Johnston");
            add("Jono Grant");
            add("Z. Stucchi");
            add("S.? Cogo");
            add("Keith Richards");
            add("Mick Jagger");
            add("Ian Gillian");
            add("Roger Waters");
            add("Roger Taylor");
            add("Fleetwood Mac");
            add("Alex Van Halen");
            add("Bert Russell");
            add("Willie Mitchell");
            add("Amanda Somerville");
            add("Michael Gore");
            add("Curtis Hudson");
            add("Mick Jagger");
            add("John Farrar");
            add("Gavin Sutherland");
            add("Joe Jackson");
            add("Men At Work");
            add("Astor Piazzolla");
            add("");
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
            add("^Google/OriginalClientId(.*)");
            add("^ZuneCollectionID(.*)");
            add("^ZuneMediaID(.*)");
            add("^ZuneAlbumMediaID(.*)");
            add("^ENSEMBLE(.*)");
            add("^TRAKTOR4(.*)");
            add("^GadiField(.*)");
            add("^COMPILATION(.*)");
            add("^PUBLISHER(.*)");
            add("^ID3-TagIT(.*)");

        }
    };

    public static ArrayList<String> globalCleanupWords = new ArrayList<String>() {
        {
            /* checked globals */
            // add("(.*)(.*)");
            add("(.*)www.simplemp3s.com(.*)");
            add("(.*)4Z5At(.*)");
            add("(.*)www.lyricsplugin.com(.*)");
            add("(.*)Mp3Developments.Com(.*)");
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

            add(new MP3FramePattern("TIT1", "^Oldies"));
            add(new MP3FramePattern("TSSE", "^Audiog"));
            add(new MP3FramePattern("TPUB", "^Fiction$"));
            add(new MP3FramePattern("TPUB", "^Wb \\(Warner\\)"));
            add(new MP3FramePattern("TPUB", "^Demon$"));
            add(new MP3FramePattern("USLT", "^credits:(.*)"));
            add(new MP3FramePattern("TOFN", "^Billy Joel(.*)"));
            add(new MP3FramePattern("TIT1", "^Help!(.*)"));
            add(new MP3FramePattern("TIT1", "^Fleetwood Mac(.*)"));

            add(new MP3FramePattern("TOFN", "^David Bowie(.*)"));
            add(new MP3FramePattern("USLT", "^\\[00:00.00\\](.*)"));
            add(new MP3FramePattern("TPUB", "^Col$"));
            add(new MP3FramePattern("TPUB", "^Eva$"));
            add(new MP3FramePattern("TPUB", "^Wea$"));
            add(new MP3FramePattern("TPUB", "^London$"));
            add(new MP3FramePattern("TCOM", "^Neworder$"));
            add(new MP3FramePattern("TCOM", "^Young, Young, Scott$"));
            add(new MP3FramePattern("TPUB", "^Wea/Warner$"));
            add(new MP3FramePattern("TENC", "^\\(Oan\\)$"));
            add(new MP3FramePattern("USLT", "^Soulsister - Alle 40 Goed(.*)"));
            add(new MP3FramePattern("TPUB", "^Wea/Warner$"));
            add(new MP3FramePattern("TPUB", "^Mgm$"));
            add(new MP3FramePattern("TENC", "^(.*Demonoid.Me(.*)"));
            add(new MP3FramePattern("COMM", "^=?\\[[0-9]{1,10}]\\]"));
            add(new MP3FramePattern("TENC", "^Allmusic555"));
            add(new MP3FramePattern("TPUB", "^Funky A$"));
            add(new MP3FramePattern("TENC", "^Balg$"));
            add(new MP3FramePattern("TOFN", "^Joe Cocker(.*)"));
            add(new MP3FramePattern("TOFN", "^Stevie Wonder(.*)"));
            add(new MP3FramePattern("TDLY", "^0$"));
            add(new MP3FramePattern("TCOM", "^G, Moore"));
            add(new MP3FramePattern("TKEY", "^\\*S\\*$"));
            add(new MP3FramePattern("TMED", "^\\(CD/DD\\)$"));
            add(new MP3FramePattern("TPUB", "^100 Hits(.*)"));
            add(new MP3FramePattern("TPUB", "^Volcano(.*)"));
            add(new MP3FramePattern("TPUB", "^Tiv44(.*)"));
            add(new MP3FramePattern("TPUB", "^Ministry Of Sound Uk(.*)"));
            add(new MP3FramePattern("TKEY", "^Kwissie$"));
            add(new MP3FramePattern("USLT", "^xcxc$"));
            add(new MP3FramePattern("TPUB", "^R\\.E\\.M\\.?(.*)"));
            add(new MP3FramePattern("TPUB", "^Bmg$"));
            add(new MP3FramePattern("TOFN", "^Robert Plant(.*)"));



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
            add(new MP3FramePattern("TCOP", "(.*)Digital Distribution Ukraine(.*)"));
            add(new MP3FramePattern("USLT", "^I want to break free(.*)"));
            add(new MP3FramePattern("USLT", "^I was bruised and battered and I(.*)"));



        }
    };
    public static ArrayList<String> publishers = new ArrayList<String>() {
        {
            /* when these words are found in publishers tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Publisher */
            //add("^(.*)");
            add("^Orchard(.*)");
            add("^Abkco(.*)");
            add("^Madacy(.*)");
            add("^Double Play(.*)");
            add("^Divine Recordings(.*)");
            add("^Frontiers Records(.*)");
            add("^Bmg Special Product(.*)");
            add("^Utv Records(.*)");
            add("^Warner Elektra Atlantic Corp(.*)");
            add("^Time Life(.*)");
            add("^Warner Brothers(.*)");
            add("^Cbs Records(.*)");

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
