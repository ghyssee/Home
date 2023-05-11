package be.home.domain.model.service;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;

import java.util.ArrayList;

public interface MP3Service {

    public String getArtist();
    public String getTitle();
    public String getTrack();
    public String getDisc();
    public String getAlbum();
    public String getAlbumArtist();
    public String getYear();
    public String getComment();
    public int getRating();
    public String getRatingAsString();
    public boolean isCompilation();
    public String getUrl();
    public String getEncoder();
    public String getKey();
    public String getLyrics();
    public String getAudioSourceUrl();
    public String getAudiofileUrl();
    public String getArtistUrl();
    public String getCommercialUrl();
    public String getPaymentUrl();
    public String getPublisherUrl();
    public String getRadiostationUrl();
    public String getDiscTotal();
    public String getGenre();
    public String getBPM();
    public ArrayList<String> getWarnings();

    public boolean REMOVE_LENGTH_TAG = true;
    // sometimes, a TLEN tag is added to contain the length of the song
    // But we get that duration on another way, so we remove this tag
    public boolean REMOVE_EMPTY_COMMENT = false;

    // remove TSO2 + TSOA tags if found when analyzing mp3
    public boolean REMOVE_SORT_TAGS = true;

    // if enabled, it will check in a composers.json file if the composer is in there, so that it's excluded
    public boolean COMPOSER_EXCLUSION_LIST = true;

    //
    public boolean KEEP_DISC_TOTAL = false;

    public ArrayList<String> customTags = new ArrayList<String>() {
        {
            /* is used for cleanup of Custom TXXX Tags + Custom Comment Tags
               + Private Tags. ex. TXXX:MUSICBRAINZ, ...
             */

            add("^DISCOGS(.*)");
            /* Music Brainz Custom tags */
            add("^MUSICBRAINZ(.*)");
            add("^WAVELIST(.*)");
            add("^RECORD LABEL(.)");

            add("^Aan ?Geboden ?Door");
            add("^AccurateRip(.*)");
            add("^album(.*)");
            add("^AMGID");
            add("^(.*)artist(.*)");
            add("^BARCODE");
            add("^Catalog(.*)");
            add("^CDDB(.*)");
            add("^comment");
            add("^compatible_brands");
            add("^Content Rating");
            add("^COUNTRY");
            add("^COVER(.*)");
            add("^Credits");
            add("^(.*)date");
            add("DISCID");
            add("^FeeAgency");
            add("^fBPM(.*)");
            add("^framerate");
            add("^GN_ExtData");
            add("^HISTORY");
            add("^INFO");
            add("^ITUNES(.*)");
            add("^iTunMOVI");
            add("^iTunNORM");
            add("^iTunPGAP");
            add("^iTunSMPB");
            add("^major_brand");
            add("^Media(.*)");
            add("^minor_version");
            add("^MusicMatch(.*)");
            add("^NOTES");
            add("^Overlay");
            add("^Play Gap");
            add("^PMEDIA");
            add("^Provider");
            add("^Purchase Date");
            add("^PZTagEditor(.*)");
            add("^RATING");
            add("^replaygain(.*)");
            add("^(.*)Release(.*)");
            add("^Rip date");
            add("^Ripping tool");
            add("^SETSUBTITLE");
            add("^SongRights");
            add("^SongType");
            add("^Source(.*)");
            add("^Supplier");
            add("^TOTALTRACKS");
            add("^TPW");
            add("^Track(.*)");
            add("^UPC");
            add("^UPLOADER");
            add("^User defined text information");
            add("^Work");
            add("^XFade");
            add("^ZN");
            add("Link van(.*)");

            add("^Engineer");
            add("^Encoder");
            add("^ENCODED?(.*)");
            add("^WEBSTORE");
            add("^ENCODINGTIME");
            add("^Year");
            add("^Language");
            add("^Related");
            add("^Style");
            add("^Tagging time");
            add("^PLine");
            add("^CT_GAPLESS_DATA");
            add("^last_played_timestamp");
            add("^added_timestamp");
            add("^play_count");
            add("^first_played_timestamp");
            add("^Mp3gain(.*)");
            add("^EpisodeID");
            add("^audiodata(.*)");
            add("^canseekontime");
            add("^pmsg");
            add("^EpisodeID");
            add("^purl");
            add("^starttime");
            add("^totaldata(.*)");
            add("^totalduration");
            add("^totaldisc(.*)");
            add("^totaltrack(.*)");
            add("^videodata(.*)");
            add("^width");
            add("^duration");
            add("^height");
            add("^bytelength");
            add("^sourcedata");
            add("^ORGANIZATION");
            add("^T?V?EPISODE(.*)");
            add("^Key");
            add("^OrigDate");
            add("^OrigTime");
            add("^TimeReference");
            add("^Language(.*)");
            add("^EnergyLevel");
            add("^PERFORMER");
            add("^RIPPER");
            add("^SPDY");
            add("^LABEL");
            add("^EXPLICIT");
            add("^PLine");
            add("^MUSICMATCH_MOOD");
            add("^TITLE");
            add("^Songs-DB_Preference");
            add("^LABELNO");

            /* private frames owners */
            add("^Google/StoreId(.*)");
            add("^Google/StoreLabelCode(.*)");
            add("^Google/UITS(.*)");
            add("^WM/Mood(.*)");
            add("^WM/UniqueFileIdentifier(.*)");
            add("WM/MediaClassPrimaryID(.*)");
            add("WM/MediaClassSecondaryID(.*)");
            add("http\\://www.cdtag.com(.*)");
            add("^PeakValue");
            add("^AverageLevel");
            add("^WM/Provider(.*)");
            add("^WM/WMContentID(.*)");
            add("^WM/WMCollectionID(.*)");
            add("^WM/WMCollectionGroupID(.*)");
            add("^www.amazon.com(.*)");

            /* end private frame owners */

        }
    };

    public ArrayList<String> cleanupWords = new ArrayList<String>() {
        {
            // used for other FRAMES like PRIVATE, ENCODED BY, COMPOSER,
            // TXXX, MEDIATYPE, URL, ...
            // also used for COMMENT tags for the content (not customized comment descriptors)
            add("^http\\://(.*)");
            add("^D?RJ/SNWTJE");
            add("^drj");
            add("^Team B&J");
            add("^[0-9]{1,2}\\+?\\+?");
            add("^\\. ?\\. ?\\. ?");
            add("^0{7,7}(.*)");
            add("^\\(Radio Edit\\)");
            add("^gortha_ii@ferialaw.com(.*)");

            add("^(.*)DJ Bert(.*)");
            add("(.*)www.mediahuman.com(.*)");
            add("(.*)www.universalmusic.nl(.*)");
            add("^Pop$");
            add("^Fireman$");
            add("^reserved$");

            add("mSm ?. ?[0-9]{1,4} ?Productions BV");
            add("(.*)Salvatoro(.*)");
            add("(.*)Scorpio(.*)");
            add("(.*)www.SongsLover.pk");
            add("(.*)www.MzHipHop.Me");
            add("(.*)www.MustJam.com");
            add("(.*)www.pirates4all.com(.*)");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("(.*)www.musicasparabaixar.org(.*)");
            add("(.*)www.t.me(.*)");

            add("^Digital Media");
            add("(.*)Pirate Shovon(.*)");
            add("^Poor$");
            add("^Good$");
            add("^Fair$");
            add("^Excellent$");
            add("^Very Good$");
            add("^AverageLevel(.*)");
            add("^RUnderground.ru(.*)");
            add("^Warner Bros(.*)");

            /* TENC encoded by */
            add("^allsoundtracks.com(.*)");
            add("^primemusic.ru(.*)");
            add("^(.*)Oldskoolscouse(.*)");
            add("^Oz$");
            add("^Ripped by(.*)");
            add("^UNTOUCHED");

            /* TMED */
            add("^(ANA|DIG|\\(?CD/DD\\)?|CD \\(?Lossless\\)?) >> (.*)");

            /* USLT */
            add("^TRI\\.BE(.*)");

            /* TCOP */
            add("^Òîëüêî(.*)");

            /* WXXX */
            add("^h?ttp://(.*)");
            add("^\\?,O\\?(.*)");
            add("^\u0014C\u0007(.*)");
            add("^www?.virginr(.*)");
            add("^newzbin release");
            add("^www.fb.co(.*)");
            add("^rack:Web Page(.*)");
            add("^(.*)http\\://(.*)");
            add("^Mnm\\.Be");
            add("^B2H & BUG");

            /* TIT1 Content group description */
            add("^PMEDIA");

            /* Publisher */
            add("^Tv Various");
            add("^Domino");
            add("^Alliance");
            add("^Collectables");
            add("^UMe");
            add("^UMG Records");
            add("^ExtremeReleases");


            /* TFLT File type */
            add("^video/mp4");
            add("^audio/mp3");
            add("^audio/x-ms-wma");
            add("^/3");

            /* TSSE */
            add("^JS");
            add("^Audio$");
            add("^MediaMonkey(.*)");
            add("^iCORM");

            /* composer */
            add("(.*)Janis Ian(.*)");
            add("^Batt(.*)");
            add("^Now.? [0-9]{1,3}(.*)");
            add("^islam(.*)");

            /* TOWN */
            add("^(.*drOhimself)(.*)");

            /* COMMENT descriptions */
            add("^00000000(.*)");
            add("^Track(.*)");
            add("^*(.*)Mp3Friends(.*)");
            add("(.*)DJ.lexus(.*)");
            add("^ejdE10");
            add("(.*)www.SongsLover.pk");
            add("(.*)www.MzHipHop.Me");
            add("(.*)www.MustJam.com");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("^\\.$");
            add("Eddie2011");
            add("^DMC$");
            add("^Aaa$");
            add("^(.*)flacless.com(.*)");
            add("^(.*)www.israbox.com");
            add("^(.*)www.updatedmp3s.com(.*)");
            add("(.*)vk.com(.*)");
            add("^DIG$");
            add("(.*)www.torrentazos.com(.*)");
            add("(.*)PeakValue(.*)");
            add("(.*)ftn2Day.Nl(.*)");
            add("^Spread The Love");
            add("^Enjoy!");
        }
    };

    public ArrayList<String> excludeWords = new ArrayList<String>() {
        {
            /* when these words are found in one of the non standard tags, it's not considered as a warning */
            /* also used for comments that should not be deleted an not shown as warning */
            add("^Telstar");
            add("^Copyright(.*)");
            add("^Credits(.*)");
            add("^(.*)Sony Music Entertainment(.*)");
            add("^Kontor Records");
            add("^Lavf5(.*)");
            add("^iTunes(.*)");
            add("^Exact Audio Copy(.*)");
            add("(.*)Ashampoo Music(.*)");
            add("^Polydor");
            add("^Audiograbber(.*)");
            add("^lame(.*)");
            add("^Judith");
            add("^RCA(.*)");
            add("^Island(.*)");
            add("^Tag&Rename(.*)");
            add("(.*)FreeRIP(.*)");
            add("^Sony(.*)");
            add("^Syco Music (.*)");
            add("^Epic(.*)");
            add("(.*)Concept(.*)");
            add("(.*)Regoli Music(.*)");
            add("^Columbia(.*)");
            add("^Interscope(.*)");
            add("^(.*)Big Machine Records(.*)");
            add("^Universal(.*)");
            add("^Arista(.*)");

            /* USLT Unsychronized lyric */
            add("^I got my driver's license last week(.*)");
            add("^You have the bravest heart(.*)");
            add("^I know you told me not to ask where you have been(.*)");
            add("^I still regret I turned my back on you(.*)");
            add("^But somethin' 'bout it still feels strange(.*)");
            add("^Da, da, da, da, da, da(.*)");
            add("^Prisoner, prisoner(.*)");
            add("^I've been going through some things(.*)");
            add("^Good day in my mind, safe to take a step out(.*)");
            add("^Loyalty over royalty(.*)");
            add("^A 40 HP Johnson(.*)");
            add("^You're just like me only happy(.*)");
            add("^Hmm \\(If swagg did it, it's depressing\\)(.*)");
            add("^Is there something I don’t know(.*)");
            add("^Such a perfect day(.*)");



            /* end */

            add("(.*)eurovision(.*)");
            add("^Now!");
            add("(.*)Them[a|e] song(.*)");
            add("^Official(.*)");
            add("(.*)Liefde voor Muziek(.*)");

            /* TOPE */
            add("^(.*)Samuel Barber(.*)");
            add("^(.*) The Knack(.*)");

            /* TSSE */
            add("(.*)-b=\"[0-9]{1,3}\" -freq=\"[0-9]{1,6}\"");

            /* publisher */
            add("^Now!? Music(.*)");
            add("^EMI(.*)");
            add("^Photo Finish Records(.*)");

            /* encode by */
            add("^Online Media Technologies(.*)");
            add("^NetStream AudioLab(.*)");
       }
    };

    public static ArrayList<String> composers = new ArrayList<String>() {
        {
            /* when these words are found in one of the non standard tags, it's not considered as a warning */
            /* also used for comments that should not be deleted an not shown as warning */

            /* Composer */
            add("Antonio Stith");
            add("Steve Aoki");
            add("Ummet Ozcan");
            add("A.? Bosse");
            add("Sarah Aarons");
            add("J.? Reeves");
            add("D.? Reynolds");
            add("Sam Butler");
            add("D.? Zwaaneveld");
            add("R.? Plasschaert");
            add("Gers Pardoel");
            add("M.? Thivaios");
            add("F.? Laman");
            add("V. Hanao");
            add("Gerald Gillum");
            add("Willem Ardui");
            add("Troye Sivan Mellet");
            add("Alisa Ueno");
            add("Laura Groeseneken");
            add("Nathaniel Rateliff");
            add("G.? Tuinfort");
            add("Luca Pecoraro");
            add("A.? Bouchenak");
            add("A.? Irwin");
            add("Dimitri Thivaios");
            add("L.? Wolfs");
            add("Milo Meskens");
            add("M.? Zoffoli");
            add("J.? Willemsen");
            add("Ma.tre Gims");
            add("Julia Michaels");
            add("H.? Salonen");
            add("Nathan Evans");
            add("Montero Hill");
            add("Joris Van Rossem");
            add("Jeffrey Goldford");
            add("Alex Callier");
            add("Haris Alagic");
            add("Zoe Wees");
            add("Mathieu Terryn");
            add("Ine Tiolants");
            add("Natalie Hemby");
            add("Dimitri Thivaios");
            add("Olivia Trappeniers");
            add("Amy Allen");
            add("James Crawford");
            add("Tyron Hapi");
            add("Robin Schulz");
            add("Max Colombie");
            add("Armen Paul");
            add("Meskerem Mees");
            add("Pommelien Thijs");
            add("Sam de Jong");
            add("Anson Long-Seabra");
            add("Jonathan Vandenbroeck");
            add("Kali Uchis");
            add("urora Aksnes");
            add("Magnus Skylstad");
            add("Cristina Chiluiza");
            add("Marco Masis");
            add("Kygo");
            add("Dimitri Thivaios");
            add("Michael Thivaios");
            add("Olivia Rodrigo");
            add("Tijs Verwest");
            add("Montero Hill");
            add("Alagwu Michael Chigozie");
            add("Stefan van Leijsen");
            add("Felix de Laet");
            add("Valentin Brunel");
            add("Olivia Trappeniers");
            add("Mathieu Terryn");
            add("Alex Callier");
            add("James Crawford");
            add("Bob Gaudio");
            add("Norma Jean Martine");
            add("Brock Korsan");
            add("Tino Schmidt");
            add("Isab.l Usher");
            add("Linus Wiklund");
            add("Jaap Reesema");
            add("Alvaro Soler");
            add("George Sheppard");
            add("Robin Schulz");
            add("Haris Alagic");
            add("Nathan Evans");
            add("Joris Van Rossem");
            add("Montero Hill");
            add("Dave Evans");
            add("Armen Paul");
            add("Jeffrey Goldford");
            add("Shane Codd");
            add("Laura Tesoro");
            add("Shawn Charles");
            add("Shawn Mendes");
            add("Tyron Hapi");
            add("Mart Hoogkamer");
            add("Amy Allen");
        }
    };

    public int getDuration();

    public boolean hasTag();

    public void analyze();

    public  void setArtist(String artist) throws MP3Exception;
    public void setTitle(String title) throws MP3Exception;
    public void setTrack(String track) throws MP3Exception;
    public void setDisc(String disc) throws MP3Exception;
    public void setAlbum(String album) throws MP3Exception;
    public void setAlbumArtist(String albumArtist) throws MP3Exception;
    public void setYear(String year) throws MP3Exception;
    public void setComment(String comment) throws MP3Exception;
    public void setRating(int Rating) throws MP3Exception;
    public void setCompilation(boolean compilation) throws MP3Exception;
    public void setDiscTotal(String discTotal) throws MP3Exception;
    public void setGenre(String grenre) throws MP3Exception;
    public void setBPM(String bpm) throws MP3Exception;
    public void cleanupTags();
    public void clearAlbumImage();

    public boolean isCleanable(String value);

    public void commit() throws MP3Exception;
    public void cleanupTag(String frameId);

    public boolean isWarning();


    public boolean isSave();

    public String getFile();

    public Tag getTag();

    public void setTag(Tag tag);
}
