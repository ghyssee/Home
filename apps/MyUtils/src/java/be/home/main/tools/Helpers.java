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
            add("J\\.?C\\.? Fogerty");
            add("Gouldman");
            add("Guns N'? Roses");
            add("John Marascalco");
            add("E\\.? E\\.? Garcia");
            add("Andrew Wright");
            add("Calvin Lewis");
            add("Don Mclean");
            add("Paul Roberts");
            add("Lord Jaffah");
            add("Bob Marley");
            add("Curtis Mayfield");
            add("Mac Davis");
            add("Dewey Bunnell");
            add("Keith Richards");
            add("Bias Boshell");
            add("Tito Puente");
            add("Bruce Woolley");
            add("Simon Darlow");
            add("Steve Lipson");
            add("Trevor Horn");
            add("Peter Frampton");
            add("Herb Alpert");
            add("Lou Adler");
            add("Sam Cooke");
            add("Atrocity");
            add("Bob Thiele");
            add("George David Weiss");
            add("Donald Roeser");
            add("Ronald Bell");
            add("George Harrison");
            add("Donald Fagen");
            add("The B-52'?S");
            add("Ray Davies");
            add("Chris Lowe");
            add("Jimmy Destri");
            add("Ernst Jansz");
            add("George Harrison");
            add("Carl Palmer");
            add("Chris Foreman");
            add("Mick Jagger");
            add("Keith Richards");
            add("Eddie Hardin");
            add("Chris Hayes");
            add("Matt Aitken");
            add("Peter Green");
            add("Eddy Grant");
            add("Dave Kaffinetti");
            add("Ferdi Bolland");
            add("Roger Waters");
            add("Amanda Somerville");
            add("Curtis Hudson");
            add("Bert Russell");
            add("Dean Pitchford");
            add("Enrique García");
            add("Tennant");
            add("Peter Brown");
            add("J.? Leyers");
            add("Mark White");
            add("^Salvatore Stellita");
            add("Michael Cleveland");
            add("Linda Creed");
            add("Stars On 45");
            add("B. ?De Groot");
            add("Jörgen Elofsson");
            add("Juan Luis Guerra");
            add("Lloyd Cole");
            add("Corey Randone");
            add("Nick Lowe");
            add("John Beck");
            add("Robert Ponger");
            add("illy Preston");
            add("Claudio Guidetti");
            add("De Kreuners");
            add("De Mens");
            add("Wim De Craene");
            add("H.? Van Loenhout");
            add("Neil Young");
            add("Angus Young");
            add("Lindsey Buckingham");


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
            add("^ZuneAlbumArtistMediaID(.*)");
            add("^ZuneUserEditedFields(.*)");
            add("^ENSEMBLE(.*)");
            add("^TRAKTOR4(.*)");
            add("^GadiField(.*)");
            add("^COMPILATION(.*)");
            add("^PUBLISHER(.*)");
            add("^ID3-TagIT(.*)");
            add("^VENUE(.*)");
            add("^DISCOGRAPHY(.*)");
            add("^key_end(.*)");
            add("^tag_(.*)");
            add("^time");
            add("^bpm_start(.*)");
            add("^bpm_accuracy(.*)");
            add("^style_01_degree(.*)");
            add("^key_start(.*)");
            add("^style_01(.*)");
            add("^bpm_end(.*)");
            add("^beat_intensity(.*)");
            add("^key_accuracy(.*)");
            add("^MusicMagic(.*)");
            add("^RATE(.*)");
            add("^EIGENAAR VAN DE TAAK(.*)");
            add("^ALLMUSICURL");
            add("^LYRICSURL");
            add("^ORIGALBUM");
            add("^origyear");
            add("^PRODUCER");
            add("^REMASTERED");
            add("^Fred Fairbrass");
            add("^Dennis Locorriere");
            add("^Andy Morris");
            add("^EXCLUSIVO");
            add("^RETIRADO(.*)");
            add("^SEARCH");
            add("^TraktorID");
            add("^TraktorPeakDB");
            add("^TraktorPerceivedDB");
            add("^TraktorLastPlayed");


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
            add("(.*)www.m-ex.me(.*)");
            add("(.*)www.Farskids.com(.*)");
            add("(.*)Demonoid.Me(.*)");
            add("(.*)Oddzod(.*)");
            add("(.*)www.videolan.org(.*)");
            add("(.*)\\[2156\\] Pl");
            add("^Kwissie(.*)");
            add("(.*)bitsarah.com(.*)");



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
            add(new MP3FramePattern("TMED", "^cd album(.*)"));
            add(new MP3FramePattern("TPUB", "^100 Hits(.*)"));
            add(new MP3FramePattern("TPUB", "^Volcano(.*)"));
            add(new MP3FramePattern("TPUB", "^Tiv44(.*)"));
            add(new MP3FramePattern("TPUB", "^Ministry Of Sound Uk(.*)"));
            add(new MP3FramePattern("USLT", "^xcxc$"));
            add(new MP3FramePattern("TPUB", "^R\\.E\\.M\\.?(.*)"));
            add(new MP3FramePattern("TPUB", "^Bmg$"));
            add(new MP3FramePattern("TOFN", "^Robert Plant(.*)"));
            add(new MP3FramePattern("TPUB", "^Fantasy$"));
            add(new MP3FramePattern("TOFN", "^Yazoo(.*)"));
            add(new MP3FramePattern("USLT", "^© ¤ @$"));
            add(new MP3FramePattern("TCOP", "^Uncutlady$"));
            add(new MP3FramePattern("TENC", "^Fray$"));
            add(new MP3FramePattern("TENC", "^Smaikls®$"));
            add(new MP3FramePattern("TPUB", "^Umvd Import(.*)"));
            add(new MP3FramePattern("TIT1", "^GBR$"));
            add(new MP3FramePattern("TIT1", "^2001 remaster$"));
            add(new MP3FramePattern("TPUB", "^Col$"));
            add(new MP3FramePattern("TPUB", "^Ztt(.*)"));
            add(new MP3FramePattern("TENC", "^Vachek(.*)"));
            add(new MP3FramePattern("TENC", "^Fairstars Audio Converter(.*)"));
            add(new MP3FramePattern("TENC", "^Klm$"));
            add(new MP3FramePattern("TPUB", "^Drakkar$"));
            add(new MP3FramePattern("TPUB", "^Reprise$"));
            add(new MP3FramePattern("TCOP", "^2009 © Top(.*)"));
            add(new MP3FramePattern("TENC", "^Kwissie$"));
            add(new MP3FramePattern("TOPE", "^Original Uk Single(.*)"));
            add(new MP3FramePattern("TENC", "(.*)A Die Hard F&Tp Fan(.*)"));
            add(new MP3FramePattern("TIT1", "^2005 Japanese remaster(.*)"));
            add(new MP3FramePattern("TCOM", "^Rfm$"));
            add(new MP3FramePattern("TPUB", "^Dark Horse$"));
            add(new MP3FramePattern("TPUB", "^Polydor ?/ ?Pgd$"));
            add(new MP3FramePattern("COMM", "(.*)64120B18(.*)"));
            add(new MP3FramePattern("TPUB", "^Stefmir$"));
            add(new MP3FramePattern("TCOM", "^Macmanus$"));
            add(new MP3FramePattern("TMED", "^Divers Artistes$"));
            add(new MP3FramePattern("TMED", "^Vinyl LP, Album$"));
            add(new MP3FramePattern("TPUB", "^Track Record$"));
            add(new MP3FramePattern("TPUB", "^Toshiba$"));
            add(new MP3FramePattern("TOFN", "^Zucchero Fornaciari(.*)"));
            add(new MP3FramePattern("TOFN", "^Bette Midler(.*)"));
            add(new MP3FramePattern("TCOP", "^1990 \\[Pias\\] Recordings(.*)"));
            add(new MP3FramePattern("TOFN", "^Buoys(.*)"));
            add(new MP3FramePattern("TPUB", "^Octone$"));
            add(new MP3FramePattern("TOFN", "^Bob Seger & the Silver Bullet Band(.*)"));
            add(new MP3FramePattern("TCOP", "^2009 © Top01(.*)"));
            add(new MP3FramePattern("TPUB", "^Utv/A & M"));
            add(new MP3FramePattern("COMM", "^7+FE6F8EB5F9850142DF99F2D489F9786F+192068"));
            add(new MP3FramePattern("TCOM", "^Burnette - Burnette - Burlison(.*)"));
            add(new MP3FramePattern("TPUB", "^Varese$"));
            add(new MP3FramePattern("TIT1", "^Alternative(.*))"));
            add(new MP3FramePattern("TPUB", "(.*)Decca(.*)"));
            add(new MP3FramePattern("TIT1", "^11B A$"));
            add(new MP3FramePattern("TKEY", "^A#?$"));
            add(new MP3FramePattern("TCOM", "^X$"));
            add(new MP3FramePattern("TOFN", "^Joe Jackson(.*)"));
            add(new MP3FramePattern("TOWN", "^AlexN(.*)"));

            add(new MP3FramePattern("TOFN", "^David Bowie(.*)"));
            add(new MP3FramePattern("TPUB", "^Fiction$"));
            add(new MP3FramePattern("TOFN", "^Billy Joel(.*)"));
            add(new MP3FramePattern("USLT", "(.*)werbe pieth gmbh(.*)"));
            add(new MP3FramePattern("TKEY", "^J\\*\\S\\*$"));
            add(new MP3FramePattern("COMM", "^J150FCD12$"));
            add(new MP3FramePattern("TPUB", "^Readers Digest(.*)"));
            add(new MP3FramePattern("TCOM", "^Studio Brussel(.*)"));
            add(new MP3FramePattern("TENC", "^Emg$"));
            add(new MP3FramePattern("TENC", "(.*)Aerius(.*)"));
            add(new MP3FramePattern("TOFN", "^Charles & Eddie(.*)"));

            add(new MP3FramePattern("TIT1", "^[0-9]{4,4} remaster(.*)"));
            add(new MP3FramePattern("TOFN", "^Elton John(.*)"));
            add(new MP3FramePattern("TOFN", "^Hans de Booij(.*)"));
            add(new MP3FramePattern("TPUB", "^Karen$"));
            add(new MP3FramePattern("COMM", "^32874"));
            add(new MP3FramePattern("TPUB", "^Vmp$"));
            add(new MP3FramePattern("COMM", "^J0545-0458-0420-0421-0414-0576(.*)"));
            add(new MP3FramePattern("TPUB", "^Arcade$"));
            add(new MP3FramePattern("TENC", "^Loen Entertainment(.*)";
            add(new MP3FramePattern("TENC", "(.*)Pepe(.*)"));
            add(new MP3FramePattern("TOAL", "^I'll Always Be There(.*)"));
            add(new MP3FramePattern("TPUB", "^Thump$"));

            add(new MP3FramePattern("COMM", "^20+D56FDB4FECF8306AF4F3EA66D5E2A5C0+10637666(.*)"));
            add(new MP3FramePattern("TOWN", "^junos(.*)"));
            add(new MP3FramePattern("TENC", "^Ninety(.*)"));
            add(new MP3FramePattern("COMM", "^The B.B.& Q. Band(.*)"));
            add(new MP3FramePattern("COMM", "^J20+217DD34BC0A8BD7A416374F79FA28D98+11857460(.*)"));
            add(new MP3FramePattern("COMM", "^25+73684C9F1EE0CC39E175D4D26E9E62B6+9954605(.*)"));
            add(new MP3FramePattern("TOFN", "^Louis Neefs(.*)"));
            add(new MP3FramePattern("TIT1", "^e Komplete Kleinkunstkollektie(.*)"));
            add(new MP3FramePattern("TCOMM", "^Young, Young, Scott(.*)"));
            add(new MP3FramePattern("COMM", "^11743(.*)"));
            add(new MP3FramePattern("TPUB", "^Epc$"));
            add(new MP3FramePattern("COMM", "^m$"));
            add(new MP3FramePattern("TKEY", "^Em$"));
            add(new MP3FramePattern("COMM", "^Mellow$"));
            add(new MP3FramePattern("COMM", "^The most comprehensive antho";

            add(new MP3FramePattern("TENC", "^Lt$";
            add(new MP3FramePattern("TPUB", "^Sire$";
            add(new MP3FramePattern("TENC", "(.*)mvclub(.*))";


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
            add(new MP3FramePattern("USLT", "^Open m'n ogen(.*)"));
            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));
            add(new MP3FramePattern("TENC", "(.*)TSound Forge(.*)"));
            add(new MP3FramePattern("USLT", "^Got a wife and kids in Baltimore(.*)"));

            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));
            add(new MP3FramePattern("TSSE", "(.*)libFLAC.*)"));
            add(new MP3FramePattern("USLT", "^When I need you(.*)"));
            add(new MP3FramePattern("USLT", "^Swaying room as the music starts(.*)"));
            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));
            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));
            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));
            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));
            add(new MP3FramePattern("USLT", "(.*)The power of love is a curious thing(.*)"));







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
            add("^Griffin Music(.*)");
            add("^Time Life(.*)");
            add("^Indigo Film Srl(.*)");
            add("^Faria Records(.*)");
            add("^Wounded Bird Records(.*)");
            add("^Union Square Music(.*)");
            add("^East West(.*)");
            add("^Castle Music(.*)");
            add("^Mca Records Ltd(.*)");
            add("^Blue Horizon(.*)");
            add("^Frontiers Records(.*)");
            add("^Woodstock Records(.*)");
            add("^Jci Associated Labels(.*)");
            add("^Sundazed(.*)");
            add("^Experience Hendrix(.*)");
            add("^Wm Benelux - Belgium(.*)");
            add("^Double Play(.*)");
            add("^Indent Series(.*)");
            add("^Elite Music Group(.*)");
            add("^Ark 21(.*)");
            add("^Mute Records(.*)");
            add("^Phantom Sound & Vision(.*)");
            add("^Bmg Special Products(.*)");
            add("^Prism Entertainment(.*)");
            add("^Mushroom Records(.*)");


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
