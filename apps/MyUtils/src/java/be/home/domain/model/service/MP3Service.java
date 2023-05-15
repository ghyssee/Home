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

            /* non checked globals */

            add("mSm ?. ?[0-9]{1,4} ?Productions BV"); // COMM

            add("^Rights Reserved"); // COMPOSER

            add("(.*)Salvatoro(.*)"); // TPUB + TCOP
            add("(.*)Scorpio(.*)"); // TENC
            add("^#hardbeats"); // COMMENT
            add("^Fear KTMP3 Powah(.*)");
            add("^<<LADiES NiGHT>>(.*)");
            add("^Patjess? Place Music(.*)"); // COMMENT
            add("^Dungeon\\(RNS/MYTH/DVNiSO\\)"); // COMMENT
            add("^Deep House -"); // COMMENT

            add("^Digital Media");
            add("(.*)Pirate Shovon(.*)");
            add("^Poor$");  // COMMENT
            add("^Good$"); // COMMENT
            add("^Fair$"); // COMMENT
            add("^Excellent$"); // COMMENT
            add("^Very Good$"); // COMMENT
            add("^AverageLevel(.*)"); // COMMENT
            add("^Warner Bros(.*)");

            /* TENC encoded by */
            add("^allsoundtracks.com(.*)");
            add("^primemusic.ru(.*)");
            add("^(.*)Oldskoolscouse(.*)");
            add("^Oz$");
            add("^Ripped by(.*)");
            add("^UNTOUCHED");
            add("^Gti$");
            add("^Camps Leo");
            add("^2MB2$");
            add("^.\\:D2H\\:.");

            /* USLT */
            add("^TRI\\.BE(.*)");
            add("^NoFS$");
            add("^Proudly Powered By Use-Next.nl(.*)");
            add("^Uploaded by Greatone");
            add("^STaT$");
            add("^From VA-Album Re");
            add("(.*)Lomasrankiao.com(.*)");

            /* TCOP */
            add("^Òîëüêî(.*)");
            add("^Stefwan-Team(.*)");
            add("^Www.Past2Present(.*)");
            add("^Catz$");
            add("^Hanterro$");

            /* WXXX */
            add("^h?ttp://(.*)");
            add("^\\?,O\\?(.*)");
            add("^\u0014C\u0007(.*)");
            add("^www?.virginr(.*)");
            add("^newzbin release");
            add("^www.fb.co(.*)");
            add("^rack:Web Page(.*)");
            add("^Mnm\\.Be");
            add("^Www.Futuretrance.De(.*)");
            add("^www.usenet-space-cowboys.online(.*)");
            add("^BTH  rj/snwtje");



            /* TSRN Radio Name */
            add("^Www.Yournewmusic.Net(.*)");

            /* COMMENT descriptions */
            add("^Media Jukebox$");
            add(".::Y::S::P::(.*)");
            add("^Warner Music$");
            add("^www.goldesel.to");
            add("^LANÇAMENTO NO BRASIL(.*)");

            add("^*(.*)Mp3Friends(.*)");
            add("(.*)DJ.lexus(.*)");
            add("^ejdE10");
            add("(.*)www.SongsLover.pk");
            add("(.*)www.MzHipHop.Me");
            add("(.*)RnBXclusive.se(.*)");
            add("(.*)URBANMUSiCDAiLY.NET(.*)");
            add("^DMC$");
            add("^(.*)flacless.com(.*)");
            add("^(.*)www.israbox.com");
            add("^(.*)www.updatedmp3s.com(.*)");
            add("^DIG$");
            add("(.*)www.torrentazos.com(.*)");
            add("(.*)PeakValue(.*)");
            add("(.*)ftn2Day.Nl(.*)");
            add("^Spread The Love");
            add("^Enjoy!");
            add("^The Greatest MP3 Collection(.*)");
            add("^.'\\:SPLiFF\\:'.");
            add("(.*)Released by VOLDiES(.*)");
        }
    };

    public ArrayList<MP3FramePattern> cleanupFrameWords = new ArrayList<MP3FramePattern>() {
        {
            add(new MP3FramePattern("COMM", "^music never dies(.*)"));
            add(new MP3FramePattern("COMM", "^deosoft.com(.*)"));
            add(new MP3FramePattern("COMM", "(.*)www.mediahuman.com(.*)"));
            add(new MP3FramePattern("COMM", "^[0-9]{1,2}\\\\+?\\\\+?\""));
            add(new MP3FramePattern("COMM", "^\\. ?\\. ?\\. ?"));
            add(new MP3FramePattern("COMM", "^ ?^0{7,8}(.*)"));
            add(new MP3FramePattern("COMM", "^\\(Radio Edit\\)"));
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
            //add(new MP3FramePattern("COMM", ""));


            add(new MP3FramePattern("TIT1", "^Various Artists"));
            add(new MP3FramePattern("TIT1", "^PMEDIA"));
            add(new MP3FramePattern("TIT1", "^Ultimate R&B"));
            add(new MP3FramePattern("TIT1", "^CD-R"));

            add(new MP3FramePattern("TENC", "^(.*)DJ Bert(.*)"));

            //add(new MP3FramePattern("TKEY", ""));
            add(new MP3FramePattern("TKEY", "^A[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^B[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^C[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^D[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^E[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^F[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^G[#| ]m$"));
            add(new MP3FramePattern("TKEY", "^B2Happ"));

            /* WXXX */
            //add(new MP3FramePattern("WXXX", ""));
            add(new MP3FramePattern("WXXX", "(.*)www.universalmusic.nl(.*)"));
            add(new MP3FramePattern("WXXX", "^B2H(.*)"));

            /* TFLT File type */
            add(new MP3FramePattern("TFLT", "^video/mp4"));
            add(new MP3FramePattern("TFLT", "^audio/mp3"));
            add(new MP3FramePattern("TFLT", "^audio/x-ms-wma"));
            add(new MP3FramePattern("TFLT", "^/3"));
            //add(new MP3FramePattern("TFLT", ""));

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
            //add(new MP3FramePattern("TPUB", ""));

            /* composer */
            add(new MP3FramePattern("TCOM", "(.*)Janis Ian(.*)"));
            add(new MP3FramePattern("TCOM", "^Batt(.*)"));
            add(new MP3FramePattern("TCOM", "^Now.? [0-9]{1,3}(.*)"));
            add(new MP3FramePattern("TCOM", "^islam(.*)"));
            add(new MP3FramePattern("TCOM", "^maxima fm"));
            add(new MP3FramePattern("TCOM", "^Dj Jean"));

            /* TIT3 Subtitle/Description refinement */
            add(new MP3FramePattern("TIT3", "^WWW.ViPERiAL.COM"));

            /* TCOP */
            add(new MP3FramePattern("TCOP", "^Òîëüêî(.*)"));
            add(new MP3FramePattern("TCOP", "^Stefwan-Team(.*)"));
            add(new MP3FramePattern("TCOP", "^Www.Past2Present(.*)"));
            add(new MP3FramePattern("TCOP", "^Catz$"));
            add(new MP3FramePattern("TCOP", "^Hanterro$"));
            add(new MP3FramePattern("TCOP", "Make Love Not War"));
            //add(new MP3FramePattern("TCOP", ""));

            /* TSSE */
            //add(new MP3FramePattern("TSSE", ""));
            add(new MP3FramePattern("TSSE", "^JS"));
            add(new MP3FramePattern("TSSE", "^Audio$"));
            add(new MP3FramePattern("TSSE", "^MediaMonkey(.*)"));
            add(new MP3FramePattern("TSSE", "^iCORM"));
            add(new MP3FramePattern("TSSE", "^WEB$"));

            /* TMED */
            //add(new MP3FramePattern("TMED", ""));
            add(new MP3FramePattern("TMED", "^(ANA|DIG|\\(?CD/DD\\)?|CD \\(?Lossless\\)?) >> (.*)"));
            // CD (LP) >> High  (Lossy) [mp3]
            add(new MP3FramePattern("TMED", "^CD( \\\\(LP\\\\))? >> (.*)  \\\\((.*)\\\\)(.*)\""));
            add(new MP3FramePattern("TMED", "^CD$"));
            add(new MP3FramePattern("TMED", "^our >> (.*)")); // our >> Very High  (Lossy) [mp3]

            /* USLT */
            //add(new MP3FramePattern("USLT", ""));
            add(new MP3FramePattern("USLT", "Created with mp3Tag(.*)"));
        }
    };

    public ArrayList<String> globalExcludeWords = new ArrayList<String>() {
        {
            /* when these words are found in one of the non standard tags, it's not considered as a warning */
            /* also used for comments that should not be deleted an not shown as warning */
            add("^Telstar");
            add("^Copyright(.*)");
            add("^Credits(.*)");
            add("^Lavf5(.*)");
            add("^iTunes(.*)");
            add("^Exact Audio Copy(.*)");
            add("(.*)Ashampoo Music(.*)");
            add("^Polydor");
            add("^Audiograbber(.*)");
            add("^lame(.*)");
            add("^Judith");
            add("^RCA(.*)");
            add("^Tag&Rename(.*)");
            add("(.*)FreeRIP(.*)");
            add("(.*)Concept(.*)");

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
            add(new MP3FramePattern("COMM", "^Mixed by Mick Harford"));

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


            /* TCOP Copyright  */
            add(new MP3FramePattern("TCOP", "(.*)Universal Music(.*)"));
            add(new MP3FramePattern("TCOP", "^(.*)Sony Music Entertainment(.*)"));
            add(new MP3FramePattern("TCOP", "^(.*)Big Machine Records(.*)")); // ex. (c) 2012 Big Machine Records, LLC)
            add(new MP3FramePattern("TCOP", "^(.*)Power Bass Production(.*)"));
            add(new MP3FramePattern("TCOP", "^(.*)Cnr Music Belgium(.*)"));
            add(new MP3FramePattern("TCOP", "(.*)Emi Records(.*)")); // ®&© 2006 Blue Note/Emi Records
            //add(new MP3FramePattern("TCOP", ""));

            // /* WXXX */
            add(new MP3FramePattern("WXXX", "^Stubru.Be$"));
            //add(new MP3FramePattern("COMM", ""));

            /* TOPE */
            add(new MP3FramePattern("TOPE", "^(.*)Samuel Barber(.*)"));
            add(new MP3FramePattern("TOPE", "^(.*) The Knack(.*)"));

            /* TENC */
            add(new MP3FramePattern("TENC", "^Online Media Technologies(.*)"));
            add(new MP3FramePattern("TENC", "^Online Media Technologies(.*)"));
            add(new MP3FramePattern("TENC", "^dBpoweramp(.*)"));
            add(new MP3FramePattern("TENC", "^AiR$"));
            add(new MP3FramePattern("TENC", "^Lame(.*)"));

            /* TSSE */
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
            add(new MP3FramePattern("TPUB", "^Capitol"));
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

        }
    };

    public static ArrayList<String> composers = new ArrayList<String>() {
        {
            /* when these words are found in composer tag, it's not considered as a warning
               This is used by import procedure to insert composers to the composers.json file */

            /* Composer */
            add("Antonio Stith");
            add("Maxi Jazz");
            add("Def Jam");
            add("Leon Russell");
            add("Burt Bacharach");
            add("Wieger Hoogendorp");
            add("Billy Joel");
            add("Luisa Gerstein");

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
