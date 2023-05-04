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
    public ArrayList<String> getWarnings();

    public boolean REMOVE_LENGTH_TAG = true;
    // sometimes, a TLEN tag is added to contain the length of the song
    // But we get that duration on another way, so we remove this tag
    public boolean REMOVE_EMPTY_COMMENT = false;

    public ArrayList<String> customTags = new ArrayList<String>() {
        {
            add("^DISCOGS(.*)");
            /* Music Brainz Custom tags */
            add("^MUSICBRAINZ(.*)");

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
        }
    };

    public ArrayList<String> cleanupWords = new ArrayList<String>() {
        {
            add("^RJ/SNWTJE");
            add("mSm ?. ?[0-9]{1,4} ?Productions BV");
            add("(.*)Salvatoro(.*)");
            add("(.*)Scorpio(.*)");
            add("(.*)www.SongsLover.pk");
            add("(.*)www.MzHipHop.Me");
            add("(.*)www.MustJam.com");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("^Digital Media");
            add("(.*)Pirate Shovon(.*)");
            add("^Poor$");
            add("^Good$");
            add("^Fair$");
            add("^Excellent$");
            add("^Very Good$");

            /* COMMENT descriptions */
            add("(.*)www.SongsLover.pk");
            add("(.*)www.MzHipHop.Me");
            add("(.*)www.MustJam.com");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("^\\.$");
            add("Eddie2011");
            add("^DMC$");
            add("^Aaa$");
            add("^(.*)www.israbox.com");
            add("^(.*)www.updatedmp3s.com(.*)");
            add("^http\\://(.*)");
            add("(.*)MediaClassPrimaryID(.*)");
            add("(.*)MediaClassSecondaryID(.*)");
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
            /* when these words are found in one of the non standard tagss, it's not considered as a warning */
            add("^Telstar");
            add("^Copyright(.*)");
            add("^Credits(.*)");
            add("^Sony Music Entertainment");
            add("^Lavf5(.*)");
            add("^iTunes(.*)");
            add("(.*)Ashampoo Music(.*");
            add("^Polydor");
            add("^Audiograbber(.*)");
            add("^lame(.*)");
            add("^Judith");
            add("^RCA(.*)");
            add("^Island(.*)");
            add("(.*)Ella Yelich-O'connor(.*)");
            add("(.*)Jack Antonoff(.*)");
            add("(.*)Joel Little(.*)");
            add("^Tag&Rename(.*)");
            add("(.*)FreeRIP(.*)");
            add("^Sony(.*)");
            add("^Syco Music (.*)");
            add("^Epic(.*)");
            add("(.*)James Harris(.*)");
            add("(.*)Michael Jackson(.*)");
            add("(.*)Terry Lewis(.*)");
            add("(.*)Rod Temperton(.*)");
            add("(.*)Concept(.*)");
            add("(.*)R\\.? Kelly(.*)");
            add("(.*)Regoli Music(.*)");
            add("^Columbia(.*)");
            add("^Interscope(.*)");
            add("^Big Machine Records(.*)");
            add("^Universal(.*)");
            add("^Arista(.*)");
            add("^Big Machine Records(.*)");
            add("(.*)Curtis Mayfields(.*)");
            add("(.*)Toni Braxton(.*)");
            add("(.*)Ivan Mathias(.*)");
            add("(.*)Pharrell Williams(.*)");
            add("(.*)Keri Lewis(.*)");
            add("(.*)Babyface(.*)");
            add("(.*)R\\.?(egie?)? Penxten(.*)");
            add("(.*)Thomas Gunther(.*)");
            add("(.*)Wim Claes(.*)");
            add("(.*)T\\.? Reznikov(.*)");
            add("(.*)M\\.? Delgado(.*)");
            add("(.*)T\\.? Reznikov(.*)");
            add("(.*)L\\.? Powell(.*)");
            add("(.*)G\\.? Verhulst(.*)");
            add("(.*)D\\.? Verbiest(.*)");
            add("(.*)Peter Vanlaet(.*)");
            add("(.*)Herman Van Molle(.*)");
            add("(.*)K\\.? MacColl(.*)");
            add("(.*)T\\.? Dice(.*)");
            add("(.*)B\\.? Koning(.*)");
            add("(.*)K\\.? MacColl(.*)");
            add("(.*)K\\.? MacColl(.*)");
            add("(.*)Taylor Swift(.*");
            add("^You have the bravest heart(.*)");
            add("^I know you told me not to ask where you have been(.*)");
            add("^I still regret I turned my back on you(.*)");

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
