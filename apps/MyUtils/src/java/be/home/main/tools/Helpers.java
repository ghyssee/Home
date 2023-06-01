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

            /* Composer */add("Bob Seger");
            add("Clark Datchler");
            add("Gary Clark");
            add("Kevin Cronin");
            add("Martin L.? Gore");
            add("Jimmy Webb");
            add("Marillion");
            add("Dennis DeYoung");
            add("David Coverdale");
            add("Richard Scher");
            add("M.? Vidal");
            add("O.? Kels");
            add("Gary Kemp");
            add("Bryan Ferry");
            add("Per Gessle");
            add("Jay Rifkin");
            add("Freddy Curci");
            add("Peter Lord");
            add("Warren Allen Brooks");
            add("Anders Eljas");
            add("Julie Gold");
            add("Claus Lessmann");
            add("Mick Jones");
            add("Joey Tempest");
            add("Robert White Johnson");
            add("Barry Mann");
            add("Chris Isaak");
            add("Justin Hayward");
            add("Alex North");
            add("Carole King");
            add("Tom Scholz");
            add("Ben Margulies");
            add("Christopher Neil");
            add("Dani Klein");
            add("Chris Rea");
            add("Timmy T.?");
            add("Alannah Myles");
            add("Mae Moore");
            add("Eric Kaz");
            add("Michael Bolton");
            add("C.? DeRouge");
            add("Cole Porter");
            add("Ben Margulies");
            add("Mariah Carey");
            add("Rod Stewart");
            add("Carole Bayer Sager");
            add("Per Gessle");
            add("Curtis Stigers");
            add("Jeff Lynne");
            add("Linda Mccartney");
            add("Paul Mccartney");
            add("R.? Matuschek");
            add("Keith Washington");
            add("Amy Grant");
            add("Jon Secada");
            add("Randy Goodrum");
            add("Clive Davis");
            add("Alex Call");
            add("Jimmy Page");
            add("Bill Leverty");
            add("Per Gessle");
            add("Cat Stevens");
            add("Jean Guiot");
            add("C.? Hine");
            add("Mick Leeson");
            add("Don Henley");
            add("Claus Lessmann");
            add("Walter Afanasieff");
            add("Scotty Wiseman");
            add("L.? Teijo");
            add("Darius Rucker");
            add("Seal");
            add("Mick Hucknall");
            add("Bob Robert");
            add("John Bristol");
            add("Amanda McBroom");
            add("Mark Batson");
            add("Bosson");
            add("Harley Streten");
            add("Ansley Ford");
            add("Matthias Richter");
            add("Enzio Latumaelissa");
            add("William Grigahcine");
            add("W.? Hector");
            add("L.? Daniels");
            add("Nils van Zandt");
            add("T.? Helsloot");
            add("Dan Stein");
            add("Hadrien Federiconi");
            add("Florence Welch");
            add("Danny Corten");
            add("A.? van den Hoef");
            add("Kiesa Rae Ellestad");
            add("Gavin Koolman");
            add("Mario Goossens");
            add("Alain Macklovitch");
            add("Jef Martens");
            add("Tadjmc");
            add("Salahdine Ibnou Kacemi");
            add("Sebastien Devaud");
            add("Malia Civetz");
            add("Michael Di Scala");
            add("M.? Colella");
            add("Edith Wayne");
            add("Sananda Maitreya");
            add("M.? Roelandt");
            add("Sandro Cavazza");
            add("Henri PFR");
            add("Thom Bell");
            add("Paul Fisher");
            add("Guilherme Boratto");
            add("Aaron Sheldon Espe");
            add("M.? D'elisio");
            add("Dominic Matheson");
            add("Luke Mcdermott");
            add("U.? Ozcan");
            add("Maxim Neslany");
            add("Zedd");
            add("Katy Perry");
            add("Martijn Garritsen");
            add("Ivan Gough");
            add("Selena Gomez");
            add("Alvaro Tauchert Soler");
            add("Sean Paul");
            add("21 Savage");
            add("Le SIDE");
            add("Bryan du Chatenier");
            add("Delaney Alberto");
            add("Iliass Takditi");
            add("James Hetfield");
            add("James Hetfield");
            add("J.? Lennon");
            add("Kendrick Duckworth");
            add("Lana Del Rey");
            add("Alessia Caracciolo");
            add("Ashley Frangipane");
            add("Rodney Clawson");
            add("Sam Hunt");
            add("Usher Raymond");
            add("Brantley Gilbert");
            add("Marcos Masis");

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
            add("^MTB.Backup");
            add("^WAVEFORMATEXTENSIBLE_CHANNEL_MASK");
            add("^HEADER");
            add("^purchaseaccount");
            add("^lyrics");

        }
    };

    public static ArrayList<String> globalCleanupWords = new ArrayList<String>() {
        {
            /* checked globals */
            // add("(.*)(.*)");
            add("(.*)www.simplemp3s.com(.*)");
            add("(.*)doornroosje@past2present(.*)");


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
            add(new MP3FramePattern("TCOM", "^Traditional$"));
            add(new MP3FramePattern("TMED", "(.*)Unofficial Release(.*)"));
            add(new MP3FramePattern("TCOP", "^Happy Music."));
            add(new MP3FramePattern("TPUB", "^Copyright Control"));
            add(new MP3FramePattern("TCOM", "^mu$"));
            add(new MP3FramePattern("TEXT", "^Gestuurd"));
            add(new MP3FramePattern("TPUB", "^Is Dat Belangrijk Dan"));
            add(new MP3FramePattern("TPUB", "^Mute$"));
            add(new MP3FramePattern("TPUB", "^SMM$"));
            add(new MP3FramePattern("COMM", "^FLAC 1.2.1"));
            add(new MP3FramePattern("TPUB", "^Gut$"));
            add(new MP3FramePattern("COMM", "^Ultimate Pop Theme"));
            add(new MP3FramePattern("USLT", "^Pop Grand Theme$"));




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
            add(new MP3FramePattern("TCOP", "(.*)Play Two(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Defected Records(.*)"));
            add(new MP3FramePattern("USLT", "^What are all these words(.*)"));
            add(new MP3FramePattern("USLT", "^Got ready for the night and(.*)"));
            add(new MP3FramePattern("USLT", "^Oh oh ah, Seysey(.*)"));
            add(new MP3FramePattern("USLT", "^Every now and then,(.*)"));
            add(new MP3FramePattern("USLT", "^One, one, one, one, one(.*)"));
            add(new MP3FramePattern("USLT", "^I, I've been up all week(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Amaterasu(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Purple Money(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Sound4label(.*)"));
            add(new MP3FramePattern("TCOP", "^KYZ Records(.*)"));
            add(new MP3FramePattern("TCOP", "^Digitraxx(.*)"));
            add(new MP3FramePattern("USLT", "^Let me tell you 'bout a boy \\(girl\\) I know(.*)"));
            add(new MP3FramePattern("TCOP", "^Musical Freedom Label(.*)"));
            add(new MP3FramePattern("COMM", "^Four Weddings And A Funeral$"));
            add(new MP3FramePattern("COMM", "^Philadelphia$"));
            add(new MP3FramePattern("COMM", "^The Mirror Has Two Faces$"));
            add(new MP3FramePattern("COMM", "^Free Willy$"));
            add(new MP3FramePattern("COMM", "^Romeo & Juliet$"));
            add(new MP3FramePattern("COMM", "^Top Gun$"));
            add(new MP3FramePattern("COMM", "^Ghost$"));
            add(new MP3FramePattern("COMM", "^The Mask Of Zorro$"));
            add(new MP3FramePattern("COMM", "^Abeltje$"));
            add(new MP3FramePattern("COMM", "^$Aladin"));
            add(new MP3FramePattern("COMM", "^Forrest Gump$"));
            add(new MP3FramePattern("COMM", "^The Graduate$"));
            add(new MP3FramePattern("COMM", "^An Officer And A Gentleman$"));
            add(new MP3FramePattern("COMM", "^Arthur$"));
            add(new MP3FramePattern("COMM", "^Love Jones$"));
            add(new MP3FramePattern("COMM", "^Simon Birch$"));
            add(new MP3FramePattern("COMM", "^Hercules$"));
            add(new MP3FramePattern("COMM", "^Titanic$"));
            add(new MP3FramePattern("USLT", "^Ayy, I've been fuckin' hoes and poppin' pillies(.*)"));
            add(new MP3FramePattern("USLT", "^I really need you(.*)"));
            add(new MP3FramePattern("USLT", "^Si el ritmo te lleva a mover la cabeza ya empezamos como es(.*)"));
            add(new MP3FramePattern("USLT", "^Sorry I ain’t got no money(.*)"));
            add(new MP3FramePattern("USLT", "^Love you in real life(.*)"));
            add(new MP3FramePattern("USLT", "^You ain't even missing me baby(.*)"));
            add(new MP3FramePattern("USLT", "\\(Turn on\\)(.*)"));
            add(new MP3FramePattern("USLT", "^When I met you in the summer(.*)"));
            add(new MP3FramePattern("USLT", "^You don't get them girls loose loose(.*)"));

        }
    };
    public static ArrayList<String> publishers = new ArrayList<String>() {
        {
            /* when these words are found in publishers tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Publisher */
             add("^RSM 22(.*)");
            add("^Crimson");
            add("^Top Notch(.*)");
            add("^Polydor Group");
            add("^Toshiba Emi(.*)");
            add("^Hospital Records(.*)");
            add("(.*)Entertainment One U.?S.?(.*)");
            add("(.*)Cash Money(.*)");
            add("(.*)Hollywood Records(.*)");
            add("^ARS Entertainment(.*)");


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
