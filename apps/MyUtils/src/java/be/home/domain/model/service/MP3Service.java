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

    // if enabled, Chapter frames will be deleted with cleanup procedure
    public boolean CLEAN_CHAPTER = true;

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

            add("^D?RJ/SNWTJE");
            add("^Team B&J");
            add("^-$");
            add("^4UsOnly.biz"); // found on multiple tags
            add("(.*)www.MzHipHop.Me");
            add("^gortha_ii@ferialaw.com(.*)");
            add("^reserved$");
            add("^AK$");
            add("(.*)www.MustJam.com");
            add("(.*)Www.RnBXclusive.Com(.*)");
            add("(.*)WWW.SOUNDREGGAE.COM(.*)");
            add("(.*)www.pirates4all.com(.*)");
            add("(.*)www.21century-mp3(.*)");
            add("(.*)Www.Nzb-Magic.Com(.*)");
            add("(.*)nimbusmp3.com(.*)");
            add("(.*)www.t.me(.*)");
            add("(.*)Www.Genc.Ws");
            add("^(.*)RUnderground.ru(.*)");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("^http\\://(.*)");
            add("^https?\\://losslessbest.net(.*)");
            add("^\\.$");
            add("(.*)www.MustJam.com(.*)");
            add("(.*)www.musicasparabaixar.org(.*)"); // COMMENT
            add("(.*)www.SongsLover.pk"); // COMMENT
            add("^Www.YourNewMusic.NeT(.*)");
            add("^www.hear-it-first.net"); // + other frames
            add("^www.RnB4U.in");
            add("(.*)vk.com(.*)");
            add("^(.*)dr[O|o]himself(.*)");
            add("(.*)DeMulder(.*)");
            add("(.*)ftn2Day.Nl(.*)");
            add("(.*)Salvatoro(.*)"); // TPUB + TCOP
            add("(.*)www.torrentazos.com(.*)");
            add("^(.*)Mp3Friends(.*)");
            add("^(.*)Oldskoolscouse(.*)");
            add("(.*)www.SongsLover.pk(.*)");
            add("(.*)www.MzHipHop.Me(.*)");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("^(.*)www.israbox.com(.*)");
            add("^(.*)www.updatedmp3s.com(.*)");
            add("^(.*)flacless.com(.*)");
            add("^B2H$");
            add("^(.*)wWw.TopMuzik.info(.*)");
            add("^(.*)skydevilshoekje.clubs.nl(.*)");
            add("^(.*)www.usenetrevolution.info(.*)");
            add("^(.*)va-album.com(.*)");
            add("^(.*)solidriver(.*)");
            add("^newzbin release");
            add("^(.*)www.primemusic.ru(.*)");
            add("^(.*)Mp3muzlo.ru(.*)");
            add("^(.*)HotNewHipHop.com(.*)");
            add("^(.*)Www.M\\-Ex.Mem(.*)");
            add("^iCORM");



            /* non checked globals */
        }
    };

    public ArrayList<MP3FramePattern> cleanupFrameWords = new ArrayList<MP3FramePattern>() {
        {
            /* COMM */
            //add(new MP3FramePattern("COMM", ""));
            add(new MP3FramePattern("COMM", "^music never dies(.*)"));
            add(new MP3FramePattern("COMM", "^deosoft.com(.*)"));
            add(new MP3FramePattern("COMM", "(.*)www.mediahuman.com(.*)"));
            add(new MP3FramePattern("COMM", "^[0-9]{1,2}\\+?\\+?"));
            add(new MP3FramePattern("COMM", "^\\. ?\\. ?\\. ?"));
            add(new MP3FramePattern("COMM", "^ ?^0{7,8}(.*)"));
            add(new MP3FramePattern("COMM", "^\\(Radio (Edit|Mix)\\)"));
            add(new MP3FramePattern("COMM", "^drj"));
            add(new MP3FramePattern("COMM", "^Track(.*)"));
            add(new MP3FramePattern("COMM", "^wallys?"));
            add(new MP3FramePattern("COMM", "^Tsawk"));
            add(new MP3FramePattern("COMM", "^Eddie2011"));
            add(new MP3FramePattern("COMM", "^Aaa$"));
            add(new MP3FramePattern("COMM", "^Pop$"));
            add(new MP3FramePattern("COMM", "^WRZmusic"));
            add(new MP3FramePattern("COMM", "^Artist Partner Group"));
            add(new MP3FramePattern("COMM", "^Freak37"));
            add(new MP3FramePattern("COMM", "^None"));
            add(new MP3FramePattern("COMM", "^Spread The Love"));
            add(new MP3FramePattern("COMM", "^Enjoy!"));
            add(new MP3FramePattern("COMM", "^The Greatest MP3 Collection(.*)"));
            add(new MP3FramePattern("COMM", "^.'\\:SPLiFF\\:'."));
            add(new MP3FramePattern("COMM", "(.*)Released by VOLDiES(.*)"));
            add(new MP3FramePattern("COMM", "^Poor$"));
            add(new MP3FramePattern("COMM", "^Good$"));
            add(new MP3FramePattern("COMM", "^Fair$"));
            add(new MP3FramePattern("COMM", "^Excellent$"));
            add(new MP3FramePattern("COMM", "^Very Good$"));
            add(new MP3FramePattern("COMM", "^AverageLevel(.*)"));
            add(new MP3FramePattern("COMM", "^Media Jukebox$"));
            add(new MP3FramePattern("COMM", ".::Y::S::P::(.*)"));
            add(new MP3FramePattern("COMM", "^Warner Music$"));
            add(new MP3FramePattern("COMM", "^www.goldesel.to"));
            add(new MP3FramePattern("COMM", "^LANÇAMENTO NO BRASIL(.*)"));
            add(new MP3FramePattern("COMM", "^Mixed by Mick Harford"));
            add(new MP3FramePattern("COMM", "^#hardbeats"));
            add(new MP3FramePattern("COMM", "^Fear KTMP3 Powah(.*)"));
            add(new MP3FramePattern("COMM", "^<<LADiES NiGHT>>(.*)"));
            add(new MP3FramePattern("COMM", "^Patjess? Place Music(.*)"));
            add(new MP3FramePattern("COMM", "^Dungeon\\(RNS/MYTH/DVNiSO\\)"));
            add(new MP3FramePattern("COMM", "^Deep House -"));
            add(new MP3FramePattern("COMM", "mSm ?. ?[0-9]{1,4} ?Productions BV"));
            add(new MP3FramePattern("COMM", "^Made by Advanced CD Ripper Pro(.*)"));
            add(new MP3FramePattern("COMM", "^ejdE10"));
            add(new MP3FramePattern("COMM", "^DMC$"));
            add(new MP3FramePattern("COMM", "^^DIG$"));
            add(new MP3FramePattern("COMM", "^Converted with ClipConverter(.*)"));
            add(new MP3FramePattern("COMM", "^aquarius"));
            add(new MP3FramePattern("COMM", "^Sound Of Bass"));
            add(new MP3FramePattern("COMM", "(.*)division of Universal Music(.*)"));
            add(new MP3FramePattern("COMM", "^orangeb"));
            add(new MP3FramePattern("COMM", "^(.*)YEAR ?\\: ?[0-9]{2,4}(.*)"));
            add(new MP3FramePattern("COMM", "^^MP3$"));
            add(new MP3FramePattern("COMM", "^Arcade (.*)")); // Arcade ‎– 01 6382 6
            add(new MP3FramePattern("COMM", "^\\(Wideboys Radio Edit\\)"));

            /* TIT1 */
            //add(new MP3FramePattern("TIT1", ""));
            add(new MP3FramePattern("TIT1", "^Various Artists"));
            add(new MP3FramePattern("TIT1", "^PMEDIA"));
            add(new MP3FramePattern("TIT1", "^Ultimate R&B"));
            add(new MP3FramePattern("TIT1", "^CD-R"));
            add(new MP3FramePattern("TIT1", "^[0-9]{1,3}$"));
            add(new MP3FramePattern("TIT1", "^Dance-Pop(.*)"));
            add(new MP3FramePattern("TIT1", "(.*)Hard Rock(.*)"));

            /* TIT3 Subtitle/Description refinement */
            //add(new MP3FramePattern("TIT3", ""));
            add(new MP3FramePattern("TIT3", "^Live"));
            add(new MP3FramePattern("TIT3", "^WWW.ViPERiAL.COM"));
            add(new MP3FramePattern("TIT3", "^Encoded with(.*)"));
            add(new MP3FramePattern("TIT3", "^Various Artists(.*)"));

            /* TPE4 Interpreted, remixed, or otherwise modified by */
            //add(new MP3FramePattern("TPE4", ""));
            add(new MP3FramePattern("TPE4", "^VA$"));

            /* TKEY */
            //add(new MP3FramePattern("TKEY", ""));
            add(new MP3FramePattern("TKEY", "^A[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^B[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^C[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^D[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^E[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^F[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^G[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^B2Happ"));
            add(new MP3FramePattern("TKEY", "^[0-9]{1,2}[A-Z]$"));
            add(new MP3FramePattern("TKEY", "^ToM$"));
            add(new MP3FramePattern("TKEY", "^B2happin$"));

            /* WXXX */
            //add(new MP3FramePattern("WXXX", ""));
            add(new MP3FramePattern("WXXX", "(.*)www.universalmusic.nl(.*)"));
            add(new MP3FramePattern("WXXX", "^B2H(.*)"));
            add(new MP3FramePattern("WXXX", "^h?ttp://(.*)"));
            add(new MP3FramePattern("WXXX", "^\\?,O\\?(.*)"));
            add(new MP3FramePattern("WXXX", "^\u0014C\u0007(.*)"));
            add(new MP3FramePattern("WXXX", "^www?.virginr(.*)"));
            add(new MP3FramePattern("WXXX", "^www.fb.co(.*)"));
            add(new MP3FramePattern("WXXX", "^rack:Web Page(.*)"));
            add(new MP3FramePattern("WXXX", "^www.usenet-space-cowboys.online(.*)"));
            add(new MP3FramePattern("WXXX", "^BTH  rj/snwtje"));
            add(new MP3FramePattern("WXXX", "(.*)agrmusic.org(.*)"));


            /* TFLT File type */
            add(new MP3FramePattern("TFLT", "^video/mp4"));
            add(new MP3FramePattern("TFLT", "^audio/mp3"));
            add(new MP3FramePattern("TFLT", "^audio/x-ms-wma"));
            add(new MP3FramePattern("TFLT", "^/3"));
            add(new MP3FramePattern("TFLT", "^MPG/3"));
            //add(new MP3FramePattern("TFLT", ""));

            /* TENC File type */
            //add(new MP3FramePattern("TENC", ""));
            add(new MP3FramePattern("TENC", "^allsoundtracks.com(.*)"));
            add(new MP3FramePattern("TENC", "^primemusic.ru(.*)"));
            add(new MP3FramePattern("TENC", "^Oz$"));
            add(new MP3FramePattern("TENC", "^Ripped by(.*)"));
            add(new MP3FramePattern("TENC", "^UNTOUCHED"));
            add(new MP3FramePattern("TENC", "^Gti$"));
            add(new MP3FramePattern("TENC", "^Camps Leo"));
            add(new MP3FramePattern("TENC", "^2MB2$"));
            add(new MP3FramePattern("TENC", "^.\\:D2H\\:."));
            add(new MP3FramePattern("TENC", "^(.*)DJ Bert(.*)"));
            add(new MP3FramePattern("TENC", "(.*)Scorpio(.*)"));
            add(new MP3FramePattern("TENC", "^saneheadache"));

            /* Publisher */
            add(new MP3FramePattern("TPUB", "^Tv Various"));
            add(new MP3FramePattern("TPUB", "^Domino"));
            add(new MP3FramePattern("TPUB", "^Alliance"));
            add(new MP3FramePattern("TPUB", "^Collectables"));
            add(new MP3FramePattern("TPUB", "^UMe"));
            add(new MP3FramePattern("TPUB", "^UMG Records"));
            add(new MP3FramePattern("TPUB", "^ExtremeReleases"));
            add(new MP3FramePattern("TPUB", "^Import"));
            add(new MP3FramePattern("TPUB", "^Legacy"));
            add(new MP3FramePattern("TPUB", "^Www.Past2Present"));
            add(new MP3FramePattern("TPUB", "^Parlophone"));
            add(new MP3FramePattern("TPUB", "^Jive"));
            add(new MP3FramePattern("TPUB", "^News$"));
            add(new MP3FramePattern("TPUB", "^541$"));
            add(new MP3FramePattern("TPUB", "^N\\.?E\\.?W\\.?S\\.?$"));
            add(new MP3FramePattern("TPUB", "^News A - F 541"));
            add(new MP3FramePattern("TPUB", "^Fireman$"));
            add(new MP3FramePattern("TPUB", "^Concept$"));
            add(new MP3FramePattern("TPUB", "^Smash The House"));
            add(new MP3FramePattern("TPUB", "^Pump Up The 90s"));
            add(new MP3FramePattern("TPUB", "^Dance Pool$"));
            add(new MP3FramePattern("TPUB", "^Düff$"));
            add(new MP3FramePattern("TPUB", "^Fergusson$"));
            add(new MP3FramePattern("TPUB", "^100Hits"));
            //add(new MP3FramePattern("TPUB", ""));

            /* composer */
            //add(new MP3FramePattern("TCOM", ""));
            add(new MP3FramePattern("TCOM", "(.*)Janis Ian(.*)"));
            add(new MP3FramePattern("TCOM", "^Batt(.*)"));
            add(new MP3FramePattern("TCOM", "^Now.? [0-9]{1,3}(.*)"));
            add(new MP3FramePattern("TCOM", "^islam(.*)"));
            add(new MP3FramePattern("TCOM", "^maxima fm"));
            add(new MP3FramePattern("TCOM", "^Dj Jean"));
            add(new MP3FramePattern("TCOM", "^Rights Reserved(.*)"));
            add(new MP3FramePattern("TCOM", "^NRJ Hits"));
            add(new MP3FramePattern("TCOM", "^Various Composers"));
            add(new MP3FramePattern("TCOM", "^VA$"));

            /* TCOP */
            add(new MP3FramePattern("TCOP", "^Òîëüêî(.*)"));
            add(new MP3FramePattern("TCOP", "^Stefwan-Team(.*)"));
            add(new MP3FramePattern("TCOP", "^Www.Past2Present(.*)"));
            add(new MP3FramePattern("TCOP", "^Catz$"));
            add(new MP3FramePattern("TCOP", "^Hanterro$"));
            add(new MP3FramePattern("TCOP", "Make Love Not War"));
            add(new MP3FramePattern("TCOP", "^Òîëüêî(.*)"));
            add(new MP3FramePattern("TCOP", "^Stefwan-Team(.*)"));
            add(new MP3FramePattern("TCOP", "^Www.Past2Present(.*)"));
            add(new MP3FramePattern("TCOP", "^Catz$"));
            add(new MP3FramePattern("TCOP", "^Hanterro$"));
            add(new MP3FramePattern("TCOP", "^NRJ France"));
            add(new MP3FramePattern("TCOP", "^ZHK$"));
            add(new MP3FramePattern("TCOP", "^All Right Reserved$"));
            //add(new MP3FramePattern("TCOP", ""));

            /* TSSE */
            //add(new MP3FramePattern("TSSE", ""));
            add(new MP3FramePattern("TSSE", "^JS"));
            add(new MP3FramePattern("TSSE", "^Audio$"));
            add(new MP3FramePattern("TSSE", "^MediaMonkey(.*)"));
            add(new MP3FramePattern("TSSE", "^WEB$"));
            add(new MP3FramePattern("TSSE", "^-V2 \\(Standard\\)"));

            /* TMED */
            //add(new MP3FramePattern("TMED", ""));
            add(new MP3FramePattern("TMED", "^(ANA|DIG|\\(?CD/DD\\)?|CD \\(?Lossless\\)?) >> (.*)"));
            // CD (LP) >> High  (Lossy) [mp3]
            add(new MP3FramePattern("TMED", "^CD( \\(LP\\))? >> (.*)  \\((.*)\\)(.*)\""));
            add(new MP3FramePattern("TMED", "^CD$"));
            add(new MP3FramePattern("TMED", "^our >> (.*)")); // our >> Very High  (Lossy) [mp3]
            add(new MP3FramePattern("TMED", "^Digital Media"));
            add(new MP3FramePattern("TMED", "^WEB Store >>(.*)")); // WEB Store >> Very High  (Lossy) [mp3] >> Very High  (Lossy) [mp3])
            add(new MP3FramePattern("TMED", "^CDS? \\((.*)\\)")); // CDS (1-2 tracks)
            add(new MP3FramePattern("TMED", "^CD$"));
            add(new MP3FramePattern("TMED", "^UNKNOWN"));

            /* USLT */
            //add(new MP3FramePattern("USLT", ""));
            add(new MP3FramePattern("USLT", "Created with mp3Tag(.*)"));
            add(new MP3FramePattern("USLT", "^TRI\\.BE(.*)"));
            add(new MP3FramePattern("USLT", "^NoFS$"));
            add(new MP3FramePattern("USLT", "^Proudly Powered By Use-Next.nl(.*)"));
            add(new MP3FramePattern("USLT", "^Uploaded by Greatone"));
            add(new MP3FramePattern("USLT", "^STaT$"));
            add(new MP3FramePattern("USLT", "^From VA-Album Re"));
            add(new MP3FramePattern("USLT", "(.*)Lomasrankiao.com(.*)"));
            add(new MP3FramePattern("USLT", "(.*)Top Hits Progressive Lights Movers(.*)"));

            /* TOWN */
            //add(new MP3FramePattern("TOWN", ""));
            add(new MP3FramePattern("TOWN", "(.*)Pirate Shovon(.*)"));

            /* TSRN Radio Name */
            //add(new MP3FramePattern("TSRN", ""));
            add(new MP3FramePattern("TSRN", "^Www.Yournewmusic.Net(.*)"));

            /* TDEN Encoding time */
            add(new MP3FramePattern("TDEN", "(.*)"));
        }
    };

    public ArrayList<String> globalExcludeWords = new ArrayList<String>() {
        {
            /* when these words are found in one of the non standard tags, it's not considered as a warning */
            /* also used for comments that should not be deleted an not shown as warning */

       }
    };

    public ArrayList<MP3FramePattern> frameSpecificExcludedWords = new ArrayList<MP3FramePattern>() {
        {
            /* COMM */
            //add(new MP3FramePattern("COMM", ""));
            add(new MP3FramePattern("COMM", "(.*)eurovision(.*)"));
            add(new MP3FramePattern("COMM", "(.*)Them[a|e] song(.*)")); // comment ex: Thema song TV serie  Lisa
            add(new MP3FramePattern("COMM", "^Official(.*)")); // comment ex: Official Uefa Euro 2020 Song
            add(new MP3FramePattern("COMM", "(.*)Liefde voor Muziek(.*)"));
            add(new MP3FramePattern("COMM", "^1.? ?\t? ?Megara vs.? DJ Lee - Into The Future(.*)"));
            add(new MP3FramePattern("COMM", "^1.? ?\t? ?CJ Stone - Intro(.*)"));
            add(new MP3FramePattern("COMM", "^1.? ?\t? ?Future Trance United - Anybody Out There(.*)"));
            add(new MP3FramePattern("COMM", "^1.? ?\t? ?Future Trance United - Future Trance Vol.? 88(.*)"));
            add(new MP3FramePattern("COMM", "^01 Balthazar - Bunker \\(Vuurwerk Endless Summer Remix\\)(.*)"));

            /* USLT */
            //add(new MP3FramePattern("USLT", ""));
            add(new MP3FramePattern("USLT", "^I got my driver's license last week(.*)"));
            add(new MP3FramePattern("USLT", "^You have the bravest heart(.*)"));
            add(new MP3FramePattern("USLT", "^I know you told me not to ask where you have been(.*)"));
            add(new MP3FramePattern("USLT", "^I still regret I turned my back on you(.*)"));
            add(new MP3FramePattern("USLT", "^But somethin' 'bout it still feels strange(.*)"));
            add(new MP3FramePattern("USLT", "^Da, da, da, da, da, da(.*)"));
            add(new MP3FramePattern("USLT", "^Prisoner, prisoner(.*)"));
            add(new MP3FramePattern("USLT", "^I've been going through some things(.*)"));
            add(new MP3FramePattern("USLT", "^Good day in my mind, safe to take a step out(.*)"));
            add(new MP3FramePattern("USLT", "^Loyalty over royalty(.*)"));
            add(new MP3FramePattern("USLT", "^A 40 HP Johnson(.*)"));
            add(new MP3FramePattern("USLT", "^You're just like me only happy(.*)"));
            add(new MP3FramePattern("USLT", "^Hmm \\(If swagg did it, it's depressing\\)(.*)"));
            add(new MP3FramePattern("USLT", "^Is there something I don’t know(.*)"));
            add(new MP3FramePattern("USLT", "^Such a perfect day(.*)"));
            add(new MP3FramePattern("USLT", "^All we ever hear from you is blah blah blah(.*)"));
            add(new MP3FramePattern("USLT", "^I had a dream so big and loud(.*)"));
            add(new MP3FramePattern("USLT", "^I stand here waiting for you to band the gong(.*)"));
            add(new MP3FramePattern("USLT", "^Falling too fast to prepare for this(.*)"));
            add(new MP3FramePattern("USLT", "^Aujourd'hui je suis fatigu(.*)"));
            add(new MP3FramePattern("USLT", "^Say say say, hey hey now baby(.*)"));
            add(new MP3FramePattern("USLT", "^Yeah(.*)"));
            add(new MP3FramePattern("USLT", "^Oh, I see you, see, I see, yeah(.*)"));
            add(new MP3FramePattern("USLT", "^Louis prend son bus, comme tous les matins(.*)"));
            add(new MP3FramePattern("USLT", "^I like digging holes and hiding things inside them(.*)"));
            add(new MP3FramePattern("USLT", "^A sad story, might find it boring(.*)"));
            add(new MP3FramePattern("USLT", "^La da da da da(.*)"));
            add(new MP3FramePattern("USLT", "^Just stop lookin' for love(.*)"));
            add(new MP3FramePattern("USLT", "^Maman m'a dit qu'elle écoutait souvent du(.*)"));
            add(new MP3FramePattern("USLT", "^Huncho(.*)"));
            add(new MP3FramePattern("USLT", "^Treating you well, but I'm caught in the middle(.*)"));
            add(new MP3FramePattern("USLT", "^Maman m'a dit qu'elle écoutait souvent du(.*)"));
            add(new MP3FramePattern("USLT", "^Maman m'a dit qu'elle écoutait souvent du(.*)"));
            add(new MP3FramePattern("USLT", "^Maman m'a dit qu'elle écoutait souvent du(.*)"));



            /* TCOP Copyright  */
            add(new MP3FramePattern("TCOP", "(.*)Universal Music(.*)"));
            add(new MP3FramePattern("TCOP", "^(.*)Sony Music Entertainment(.*)"));
            add(new MP3FramePattern("TCOP", "^(.*)Big Machine Records(.*)")); // ex. (c) 2012 Big Machine Records, LLC)
            add(new MP3FramePattern("TCOP", "^(.*)Power Bass Production(.*)"));
            add(new MP3FramePattern("TCOP", "^(.*)Cnr Music Belgium(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Emi Records(.*)")); // ®&© 2006 Blue Note/Emi Records
            add(new MP3FramePattern("TCOP", "(.*)MJJ Productions(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)UMG Recordings(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Atlantic Recording Corporation(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)WEA International(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Clock Music Sas(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Geffen Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Motown Record(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Tabu Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)The Island Def Jam Music Group(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Warner Bros.? Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Parlophone Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Capitol Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Arista Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Ace Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Virgin Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Sleeping Bag Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Warner Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Unidisc Music(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Mercury Records(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Warner Music UK(.*)"));
            //add(new MP3FramePattern("TCOP", ""));

            // /* WXXX */
            add(new MP3FramePattern("WXXX", "^Stubru.Be$"));
            add(new MP3FramePattern("WXXX", "^Mnm\\.Be"));
            add(new MP3FramePattern("WXXX", "^Www.Futuretrance.De(.*)"));
            add(new MP3FramePattern("WXXX", "Www.Bravo.De"));
            add(new MP3FramePattern("WXXX", "Www.Dreamdance.De"));
            //add(new MP3FramePattern("WXXX", ""));

            /* TOPE */
            add(new MP3FramePattern("TOPE", "^(.*)Samuel Barber(.*)"));
            add(new MP3FramePattern("TOPE", "^(.*) The Knack(.*)"));
            add(new MP3FramePattern("TOPE", "^Judith"));
            add(new MP3FramePattern("TOPE", "^Myl.ne Farmer"));

            /* TPE3 */
            add(new MP3FramePattern("TPE3", "^Larry Gold"));
            add(new MP3FramePattern("TPE3", "^Susie Katayama"));


            /* TENC */
            add(new MP3FramePattern("TENC", "^Online Media Technologies(.*)"));
            add(new MP3FramePattern("TENC", "^Online Media Technologies(.*)"));
            add(new MP3FramePattern("TENC", "^dBpoweramp(.*)"));
            add(new MP3FramePattern("TENC", "^AiR$"));
            add(new MP3FramePattern("TENC", "^Lame(.*)"));
            add(new MP3FramePattern("TENC", "^Lavf5(.*)"));
            add(new MP3FramePattern("TENC", "^iTunes(.*)"));
            add(new MP3FramePattern("TENC", "^Exact Audio Copy(.*)"));
            add(new MP3FramePattern("TENC", "(.*)Ashampoo Music(.*)"));
            add(new MP3FramePattern("TENC", "^Tag&Rename(.*)"));
            add(new MP3FramePattern("TENC", "(.*)FreeRIP(.*)"));

            /* TSSE */
            add(new MP3FramePattern("TSSE", "^Audiograbber(.*)"));
            add(new MP3FramePattern("TSSE", "^Easy CD\\-DA Extractor(.*)"));
            add(new MP3FramePattern("TSSE", "^Lavf5(.*)"));
            add(new MP3FramePattern("TSSE", "^LAME(.*)"));
            add(new MP3FramePattern("TSSE", "^FLAC(.*)"));
            add(new MP3FramePattern("TSSE", "(.*)-b=\"[0-9]{1,3}\"(.*)"));
            // ex. -b="320" -encoding="SLOW" -freq="48000" -channels="stereo"
            // ex. -b="320" -q="0" -channels="stereo"

            /* TPUB */
            add(new MP3FramePattern("TPUB", "^Dino Music Bv"));
            add(new MP3FramePattern("TPUB", "^Cheeky Records"));
            add(new MP3FramePattern("TPUB", "^Warner Music Benelux$"));
            add(new MP3FramePattern("TPUB", "^AWA$"));
            add(new MP3FramePattern("TPUB", "^Rec. 118$"));
            add(new MP3FramePattern("TPUB", "(.*)VL Records(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)VRepublic Records(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)RCA Records(.*)"));
            add(new MP3FramePattern("TPUB", "^Purple Money(.*)"));
            add(new MP3FramePattern("TPUB", "^Polydor Records(.*)"));
            add(new MP3FramePattern("TPUB", "^TF1 Entertainment(.*)"));
            add(new MP3FramePattern("TPUB", "^Wagram Music(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Capitol Music France(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Capitol(.*)"));
            add(new MP3FramePattern("TPUB", "^Past Perfect"));
            add(new MP3FramePattern("TPUB", "^Ultra Records"));
            add(new MP3FramePattern("TPUB", "^Airplay"));
            add(new MP3FramePattern("TPUB", "^Disky(.*)"));
            add(new MP3FramePattern("TPUB", "^3.?5.?7 Music(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Interscope Records(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Play Two(.*)"));
            add(new MP3FramePattern("TPUB", "^Now!"));
            add(new MP3FramePattern("TPUB", "^Now!? Music(.*)"));
            add(new MP3FramePattern("TPUB", "^EMI(.*)"));
            add(new MP3FramePattern("TPUB", "^Photo Finish Records(.*)"));
            add(new MP3FramePattern("TPUB", "^Cloud 9 Records(.*)"));
            add(new MP3FramePattern("TPUB", "^Sheffield Tunes(.*)"));
            add(new MP3FramePattern("TPUB", "^Mostiko(.*)"));
            add(new MP3FramePattern("TPUB", "^Ars Entertainment Belgium(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Mosley Music Group(.*)"));
            add(new MP3FramePattern("TPUB", "^A&M$"));
            add(new MP3FramePattern("TPUB", "^ZYX$"));
            add(new MP3FramePattern("TPUB", "^Warner Sunset(.*)"));
            add(new MP3FramePattern("TPUB", "^SMI$"));
            add(new MP3FramePattern("TPUB", "^SPG$"));
            add(new MP3FramePattern("TPUB", "^Savoy Jazz"));
            add(new MP3FramePattern("TPUB", "^Rhino"));
            add(new MP3FramePattern("TPUB", "^Kontor Records"));
            add(new MP3FramePattern("TPUB", "^Interscope(.*)"));
            add(new MP3FramePattern("TPUB", "^Syco Music (.*)"));
            add(new MP3FramePattern("TPUB", "^Columbia(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Regoli Music(.*)"));
            add(new MP3FramePattern("TPUB", "^Arista(.*)"));
            add(new MP3FramePattern("TPUB", "^Universal(.*)")); // Universal Music Group International
            add(new MP3FramePattern("TPUB", "^Sony(.*)")); // Sony International // Sony Bmg
            add(new MP3FramePattern("TPUB", "^Epic(.*)"));
            add(new MP3FramePattern("TPUB", "^Island(.*)")); // ex. Island (Universal)
            add(new MP3FramePattern("TPUB", "^Mca Nashville(.*)"));
            add(new MP3FramePattern("TPUB", "^(.*)Power Bass Production(.*)"));
            add(new MP3FramePattern("TPUB", "^Big Boy(.*)"));
            add(new MP3FramePattern("TPUB", "^Atlantic(.*)"));
            add(new MP3FramePattern("TPUB", "^Telstar"));
            add(new MP3FramePattern("TPUB", "^Polydor"));
            add(new MP3FramePattern("TPUB", "^RCA(.*)"));
            add(new MP3FramePattern("TPUB", "^Warner Bros(.*)"));
            add(new MP3FramePattern("TPUB", "^Radio 538(.*)"));
            add(new MP3FramePattern("TPUB", "^Jive Epic(.*)"));
            add(new MP3FramePattern("TPUB", "^Cnr$"));
            add(new MP3FramePattern("TPUB", "^Walt Disney(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Universal Music Austria(.*)"));
            add(new MP3FramePattern("TPUB", "^MCA$"));
            add(new MP3FramePattern("TPUB", "^IMS$"));
            add(new MP3FramePattern("TPUB", "(.*)Simco Limited(.*)"));
            add(new MP3FramePattern("TPUB", "^Virgin(.*)"));
            add(new MP3FramePattern("TPUB", "^Camden(.*)"));
            add(new MP3FramePattern("TPUB", "^Armada(.*)"));
            add(new MP3FramePattern("TPUB", "^Radikal(.*)"));
            add(new MP3FramePattern("TPUB", "^Source UK(.*)"));
            add(new MP3FramePattern("TPUB", "^Channel Four(.*)"));
            add(new MP3FramePattern("TPUB", "^Toptrax(.*)"));
            add(new MP3FramePattern("TPUB", "^Ars Produktion(.*)"));
            add(new MP3FramePattern("TPUB", "^Get Physical Music(.*)"));
            add(new MP3FramePattern("TPUB", "^CNR Music Belgium(.*)"));
            add(new MP3FramePattern("TPUB", "^Studio Brussel(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Warner Music(.*)"));
            add(new MP3FramePattern("TPUB", "^Ministry Of Sound$"));
            add(new MP3FramePattern("TPUB", "(.*)Ministry Of Sound Recordings(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Xl Recordings(.*)"));
            add(new MP3FramePattern("TPUB", "(.*)Wea International(.*)"));
            add(new MP3FramePattern("TPUB", "^Roswell"));
            add(new MP3FramePattern("TPUB", "^Roc Nation"));
            add(new MP3FramePattern("TPUB", "^Star Mark"));
            add(new MP3FramePattern("TPUB", "^La Face"));
            add(new MP3FramePattern("TPUB", "^Sbme Import"));
            add(new MP3FramePattern("TPUB", "^Mobile Fidelity"));
            add(new MP3FramePattern("TPUB", "^Elektra"));

            /* TCOM */
            add(new MP3FramePattern("TCOM", "^Lan$"));

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
