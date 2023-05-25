package be.home.domain.model.service;

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
    public boolean PUBLISHER_EXCLUSION_LIST = true;

    // if enabled, Chapter frames will be deleted with cleanup procedure
    public boolean CLEAN_CHAPTER = true;

    public String GLOBAL_FRAME = "*";

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
            add("^TCMP");
            add("^Facebook");
            add("^Skype");
            add("^Vbox7.com");
            add("^Youtube.com");
            add("^ERLPYAAGNIR_FERENEECL_UONDSE");
            add("^YMP3HASH");
            add("^TAGGER");
            add("^WM/MEDIAPRIMARYCLASSID");
            add("^WM/ENCODINGTIME");
            add("^TYER");
            add("^GN/ExtD(.*)");
            add("^FILEOWNER");
            add("^WEBSITE");
            add("^DESCRIPTION");
            add("^SCRIPT");
            add("^originalyear");
            add("^Acoustid(.*)");

            add("^Aan ?Geboden ?Door");
            add("^AccurateRip(.*)");
            add("^album(.*)");
            add("^AMGID");
            add("^(.*)artist(.*)");
            add("^BARCODE");
            add("^Catalog(.*)");
            add("^CDDB(.*)");
            add("^comments?");
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
            add("^ID3v1 Comment");
            add("^Disc");
            add("^Part of a set");
            add("^ISRC");
            add("Tool Name");
            add("^Tool Version");
            add("^author");
            add("^Intensity");
            add("^COMMANDS");
            add("^WE SUCCEED WHERE(.*)");
            add("^BAND");
            add("^DISCTOTAL");

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
            add("^mailto:uits@7digital.com(.*)");
            add("^mailto:uits-info@umusic.com(.*)");

            /* end private frame owners */

        }
    };

    public ArrayList<String> globalCleanupWords = new ArrayList<String>() {
        {
            /* checked globals */

        }
    };

    public ArrayList<MP3FramePattern> cleanupFrameWords = new ArrayList<MP3FramePattern>() {
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
            add(new MP3FramePattern("TMED", ""));
            //add(new MP3FramePattern("USLT", ""));
            //add(new MP3FramePattern("TOWN", ""));
            //add(new MP3FramePattern("TOPE", ""));
            //add(new MP3FramePattern("TSRN", ""));
        }
    };

    public ArrayList<String> globalExcludeWords = new ArrayList<String>() {
        {
            /* when these words are found in one of the non standard tags, it's not considered as a warning */
            /* also used for comments that should not be deleted an not shown as warning */

       }
    };

    public static ArrayList<MP3FramePattern> framePatterns = new ArrayList<MP3FramePattern>() {
        {
            /* when these words are found in publishers tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */
            //add(new MP3FramePattern("", ""));

        }
    };
    public static ArrayList<String> publishers = new ArrayList<String>() {
        {
            /* when these words are found in publishers tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Publisher */
            add("^Quality Music");
            add("^Eruption Records");
            add("^Varèse Sarabande");
            add("^Manteca");
            add("^Sbme Special Mkts");
            add("^PMP/RAL");
            add("^Razor & Tie");
            add("^Phantom Import Distribution");
        }
    };
    public static ArrayList<String> composers = new ArrayList<String>() {
        {
            /* when these words are found in composer tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Composer */
            add("Antonio Stith");
             //add("");


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
    public void commit() throws MP3Exception;
    public void cleanupTag(String frameId);

    public boolean isWarning();


    public boolean isSave();

    public String getFile();

}
