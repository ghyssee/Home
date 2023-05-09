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
            add("^[0-9]{1,2}");
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

            /* TIT1 Content group description */
            add("^PMEDIA");

            /* Publisher */
            add("^Tv Various");
            add("^Domino");
            add("^Alliance");
            add("^Collectables");
            add("^UMe");
            add("^UMG Records");


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
            add("Andre Marshall");
            add("Sean Henriques");
            add("Rohan Ashley Fuller");
            add("Henderson//H. Seriki");
            add("O.? Salinas");
            add("H.? Seriki");
            add("J.? Salinas");
            add("M.? Crooms");
            add("Faheem Najm");
            add("Donald Pears");
            add("Shaffer Smith");
            add("Daniel Powter");
            add("Slade");
            add("Billy Mann");
            add("KT Tunstall");
            add("Marti Dodson");
            add("Tyson Ritter");
            add("Monty Powell");
            add("R.? Richard");
            add("Jeffrey Phillips");
            add("Chester Jennings");
            add("M.? S.? Eriksen");
            add("Josh Alexander");
            add("Peter Wallace");
            add("D.? Kulash");
            add("Hinder");
            add("Steve McEwan");
            add("DeAndre Way");
            add("Carlos McKinney");
            add("Stacy Ferguson");
            add("Butch Walker");
            add("Jeff Blue");
            add("Tom Higgenson");
            add("Brian Howes");
            add("Chris Daughtry");
            add("Jason Wade");
            add("Jude Cole");
            add("Tasleema Yasin");
            add("LaShawn Daniels");
            add("Sam Watters");
            add("Sean Kingston");
            add("Louis Biancaniello");
            add("T-Pain");
            add("Chris Brown");
            add("Terius Nash");
            add("Christopher Stewart");
            add("Alicia Keys");
            add("Ryan Tedder");
            add("Martin Johnson");
            add("Hayley Williams");
            add("Josh Farro");
            add("Finger Eleven");
            add("Richard Marx");
            add("Nicola Fasano");
            add("Patrick Gonella");
            add("Daniel Seraphine");
            add("Christopher Henderson");
            add("Jamie Foxx");
            add("S.? Mescudi");
            add("D-Way");
            add("Eriksen");
            add("Benny Blanco");
            add("Julian De Martino");
            add("Katie White");
            add("Joe King");
            add("Ben Wysocki");
            add("Isaac Slade");
            add("Nickelback");
            add("Tom Douglas");
            add("Diane Warren");
            add("Red One");
            add("J.? Skaller");
            add("Aubrey Graham");
            add("Christopher Stewart");
            add("K.? Cossom");
            add("Sly Dunbar");
            add("Carl Young");
            add("Robert Warren");
            add("Ryan Tedder");
            add("Sam Hollander");
            add("Steve McEwan");
            add("Benjamin Levin");
            add("Dave Haywood");
            add("Pebe Sebert");
            add("Joshua Coleman");
            add("Onika Maraj");
            add("J.? Cole");
            add("Dougie Mandagi");
            add("Martin Johnson");
            add("Steve Kipner");
            add("Shane Stevens");
            add("Tom Douglas");
            add("Dave Barnes");
            add("Marc Griffin");
            add("Peter Munters");
            add("Tommy Henriksen");
            add("Nick Bailey");
            add("Toby Gad");
            add("Anita McCloud");
            add("Joel Brentlinger");
            add("RJ Lange");
            add("Dan Pritzker");
            add("Blair Daly");
            add("Savan Kotecha");
            add("Tim Pagnotta");
            add("Ryan Tedder");
            add("Sara Bareilles");
            add("Hayley Williams");
            add("Josh Steely");
            add("Shimon Moore");
            add("Paul Sprangers");
            add("Laura Rogers");
            add("Lydia Rogers");
            add("Mark James");
            add("Jacob Kasher Hindlin");
            add("Benjamin Levin");
            add("Jae Choung");
            add("Virman Coquia");
            add("Ilie Eduard Marian");
            add("Janae Ratliff");
            add("Christopher Gholson");
            add("Milton James");
            add("Tony Scales");
            add("Jordan Witzigreuter");
            add("Robin Ghosh");
            add("Vincent Herbert");
            add("Johnny Hammond");
            add("Shawn Carter");
            add("Dewayne Carter");
            add("Curtis Jackson");
            add("Jason Bowman");
            add("Kris Ivory");
            add("Skyler Gordy");
            add("Shaffer Smith");
            add("Nick Van De Wall");
            add("Eric Judy");
            add("Allen Pineda");
            add("Noel Zancanella");
            add("Colt Ford");
            add("Ben Hayslip");
            add("Rhett Akins");
            add("Jonathan Yip");
            add("Alex Geringas");
            add("Noah Beresin");
            add("Devin Tailes");
            add("Solana Rowe");
            add("Charlton Howard");
            add("Luke Combs");
            add("Daniel Klein");
            add("");
            add("");

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
