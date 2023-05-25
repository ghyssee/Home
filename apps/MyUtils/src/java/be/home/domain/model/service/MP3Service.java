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
            // add("");

            add("^DISCOGS(.*)");
            add("^VideoKind");
            add("^SL Comment");
        }
    };

    public ArrayList<String> globalCleanupWords = new ArrayList<String>() {
        {
            add("(.*)www.cloud9music.nl(.*)");
            add("(.*)www.newmp3s.net(.*)");
            /* checked globals */
            // add("(.*)(.*)");
            add("(.*)www.newestmp3s.com(.*)");

        }
    };

    public ArrayList<MP3FramePattern> frameCleanups = new ArrayList<MP3FramePattern>() {
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
            add(new MP3FramePattern("USLT", "^Adrenaline MP3s"));
            add(new MP3FramePattern("TIT3", "^RADIO MIX"));
            add(new MP3FramePattern("TIT1", "^Pop$"));
            add(new MP3FramePattern("TMED", "^DIG$"));
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
            add(new MP3FramePattern("USLT", "^Many nights we prayed"));
            add(new MP3FramePattern("USLT", "^Hands Clean"));
            add(new MP3FramePattern("TPUB", "^J\\-Records"));
            add(new MP3FramePattern("TPUB", "^Underground Inc"));
            add(new MP3FramePattern("TPUB", "^A&M Records"));
            add(new MP3FramePattern("TPUB", "^Ariola(.*)"));

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
            add("^Razor & Tie");
            add("^Fania(.*)");

        }
    };
    public static ArrayList<String> composers = new ArrayList<String>() {
        {
            /* when these words are found in composer tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Composer */
            add("Antonio Stith");
             //add("");
            add("Alexander Kronlund");
            add("Robin Carlsson");
            add("Asia Whiteacre");
            add("Philip Jacobs");
            add("John Paul Cooper");
            add("Sophie");
            add("Allan Pineda");
            add("Cathy Dennis");
            add("Steve Booker");
            add("Rub.n Blades");
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
