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
            add("^Gorillaz(.*)");
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
            add("^(.*)Big Machine Records(.*)");
            add("^Universal(.*)");
            add("^Arista(.*)");
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
            add("(.*)Taylor Swift(.*)");
            add("^You have the bravest heart(.*)");
            add("^I know you told me not to ask where you have been(.*)");
            add("^I still regret I turned my back on you(.*)");
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
            add("^EMI TV(.*)");
            add("^Photo Finish Records(.*)");

            /* encode by */
            add("^Online Media Technologies(.*)");
            add("^NetStream AudioLab(.*)");

            /* Composer */

            add("(.*)Elton John(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Gorillaz(.*)");
            add("(.*)Brandon Flowers(.*)");
            add("(.*)Matthew Thiessen(.*)");
            add("(.*)Lindy Robbins(.*)");
            add("(.*)Rodney Crowell(.*)");
            add("(.*)Anna Nalick(.*)");
            add("(.*)Will Adams(.*)");
            add("(.*)Ronald Ray Bryant(.*)");
            add("(.*)Jonathan Smith(.*)");
            add("(.*)Eminem(.*)");
            add("(.*)Bobby Wilson(.*)");
            add("(.*)Ciara Harris(.*)");
            add("(.*)Amerie Rogers(.*)");
            add("(.*)Will Smith(.*)");
            add("(.*)Aslyn(.*)");
            add("(.*)Springfield(.*)");
            add("(.*)Joel Madden(.*)");
            add("(.*)Peter Loeffler(.*)");
            add("(.*)Robb Douglas(.*)");
            add("(.*)Kevin Griffin(.*)");
            add("(.*)Harvey Mason(.*)");
            add("(.*)Anthony Romeo Santos(.*)");
            add("(.*)Kanye West(.*)");
            add("(.*)Keith McMasters(.*)");
            add("(.*)Alan Jay Lerner(.*)");
            add("(.*)Lindsay Lohan(.*)");
            add("(.*)L.? Perry(.*)");
            add("(.*)M.? Powell(.*)");
            add("(.*)G.? Wilson(.*)");
            add("(.*)/J.? Reddick(.*)");
            add("(.*)N.? McCarthy(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)Sloan(.*)");
            add("(.*)J.? Foreman(.*)");
            add("(.*)Anderson(.*)");
            add("(.*)R.? Williams(.*)");
            add("(.*)A.? Thiam(.*)");
            add("(.*)A.? Hamilton(.*)");
            add("(.*)M.? Williams(.*)");
            add("(.*)C.? Hassan(.*)");
            add("(.*)D.? Moore(.*)");
            add("(.*)Beastie Boys(.*)");
            add("(.*)J.? Cartagena(.*)");
            add("(.*)J.? Rich(.*)");
            add("(.*)R.? Garza(.*)");
            add("(.*)T.? Foreman(.*)");
            add("(.*)B.? Harper(.*)");
            add("(.*)L.? Kravitz(.*)");
            add("(.*)D.? Rob(.*)");
            add("(.*)T.? Whitlock(.*)");
            add("(.*)A.? Stamatelatos(.*)");
            add("(.*)The Daughertys(.*)");
            add("(.*)The Cantrells(.*)");
            add("(.*)D.? Castillo(.*)");
            add("(.*)J.? Smith(.*)");
            add("(.*)D.? Kelly(.*)");
            add("(.*)W.? Adams(.*)");
            add("(.*)P.? Poli(.*)");
            add("(.*)C. Bedeau(.*)");
            add("(.*)Beyonc. Knowles(.*)");
            add("(.*)The Benjamins(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)V.? Carlisle(.*)");
            add("(.*)J.? Ondrasik(.*)");
            add("(.*)C.? Bell(.*)");
            add("(.*)J.? Madden(.*)");
            add("(.*)M.? Hoppus(.*)");
            add("(.*)Three Days Grace(.*)");
            add("(.*)F.? Dobson(.*)");
            add("(.*)L.? Alexander(.*)");
            add("(.*)J.? Simpson(.*)");
            add("(.*)Eamon(.*)");
            add("(.*)R.? Bryant(.*)");
            add("(.*)Storch(.*)");
            add("(.*)J.? Gomez(.*)");
            add("(.*)T.? Cottura(.*)");
            add("(.*)The Jacksons(.*)");
            add("(.*)C.? Bridges(.*)");
            add("(.*)C.? Karlsson(.*)");
            add("(.*)M.? Hollis(.*)");
            add("(.*)B.? Arnold(.*)");
            add("(.*)Ryan Peake(.*)");
            add("(.*)Michael Campbell(.*)");
            add("(.*)Joel Madden(.*)");
            add("(.*)Chris Collingwood(.*)");
            add("(.*)Lauren Christy(.*)");
            add("(.*)Lucy Woodward(.*)");
            add("(.*)Bryan Michael Cox(.*)");
            add("(.*)Jerry Vines(.*)");
            add("(.*)R.? Bryant(.*)");
            add("(.*)Gregory Bruno(.*)");
            add("(.*)Clifford Harris(.*)");
            add("(.*)Daugherty(.*)");
            add("(.*)Teddy Mendez(.*)");
            add("(.*)Missy Elliott(.*)");
            add("(.*)Rich Harrison(.*)");
            add("(.*)Berryman(.*)");
            add("(.*)Erna(.*)");
            add("(.*)B.? Arnold(.*)");
            add("(.*)Chevelle(.*)");
            add("(.*)G.? Nori(.*)");
            add("(.*)Elton John(.*)");
            add("(.*)K.? Roe(.*)");
            add("(.*)J.? Reddick(.*)");
            add("(.*)C.? Magness(.*)");
            add("(.*)S.? Orrico(.*)");
            add("(.*)R.? Williams(.*)");
            add("(.*)JJ Mitchell(.*)");
            add("(.*)D.? Bedingfield(.*)");
            add("(.*)F.? Bautista(.*)");
            add("(.*)Gibbs(.*)");
            add("(.*)R.? Westfield(.*)");
            add("(.*)Mr.? Deyo(.*)");
            add("(.*)Harrell(.*)");
            add("(.*)Josey Scott(.*)");
            add("(.*)Pete Loeffler(.*)");
            add("(.*)Vanessa Carlton(.*)");
            add("(.*)Amanda Perez(.*)");
            add("(.*)T.? Bishop(.*)");
            add("(.*)Dane Deviller(.*)");
            add("(.*)Featherstone(.*)");
            add("(.*)Stephan Haeri(.*)");
            add("(.*)P.? Williams(.*)");
            add("(.*)JC Chasez(.*)");
            add("(.*)Calvin Broadus(.*)");
            add("(.*)Jeremiah Lordan(.*)");
            add("(.*)Nelly(.*)");
            add("(.*)B.? Cox(.*)");
            add("(.*)Prince(.*)");
            add("(.*)Raine Maida(.*)");
            add("(.*)Scott Stapp(.*)");
            add("(.*)Chris Hesse(.*)");
            add("(.*)Chad Kroeger(.*)");
            add("(.*)Timothy Mosleyn(.*)");
            add("(.*)Stevie Nicks(.*)");
            add("(.*)Vanessa Carlton(.*)");
            add("(.*)Jennifer Love Hewitt(.*)");
            add("(.*)Gwen Stefani(.*)");
            add("(.*)Dave Stewart(.*)");
            add("(.*)Shakira(.*)");
            add("(.*)Adema(.*)");
            add("(.*)Chris Kilmore(.*)");
            add("(.*)Andrea Carlson(.*)");
            add("(.*)Troy Oliver(.*)");
            add("(.*)Barry Gibb(.*)");
            add("(.*)Nelly Furtado(.*)");
            add("(.*)Jerry Duplessis(.*)");
            add("(.*)Mr.? Cheeks(.*)");
            add("(.*)Tim Mosley(.*)");
            add("(.*)Irv Gotti(.*)");
            add("(.*)Gloria Estefan(.*)");
            add("(.*)Mary J.? Blige(.*)");
            add("(.*)Carl Bell(.*)");
            add("(.*)Dave Baksh(.*)");
            add("(.*)Neil Diamond(.*)");
            add("(.*)JIVE ?jones(.*)");
            add("(.*)Theo Keating(.*)");
            add("(.*)Del Jones(.*)");
            add("(.*)Adam Anders(.*)");
            add("(.*)Jermaine Dupri(.*)");
            add("(.*)Garrett(.*)");
            add("(.*)Ken Gioia(.*)");
            add("(.*)Flores(.*)");
            add("(.*)Sebastian M(.*)");
            add("(.*)Jennifer Lopez(.*)");
            add("(.*)Rob Fusari(.*)");
            add("(.*)Craig Montoya(.*)");
            add("(.*)U2(.*)");
            add("(.*)Brandon Boyd(.*)");
            add("(.*)Mark Tremonti(.*)");
            add("(.*)Carl Bell(.*)");
            add("(.*)Will Champion(.*)");
            add("(.*)Jeff Cohen(.*)");
            add("(.*)Lincoln Browder(.*)");
            add("(.*)Cameron Giles(.*)");
            add("(.*)Orville Burrel(.*)");
            add("(.*)Samuel Barnes(.*)");
            add("(.*)Alexei Potekhin(.*)");
            add("(.*)Kristian Lundin(.*)");
            add("(.*)Arnthor Birgisson(.*)");
            add("(.*)Bon Jovi(.*)");
            add("(.*)Art Alexakis(.*)");
            add("(.*)Todd Harrel(.*)");
            add("(.*)John Hampson(.*)");
            add("(.*)Mark Burns(.*)");
            add("(.*)Shelly Peiken(.*)");
            add("(.*)Kristin Hudson(.*)");
            add("(.*)Brian Kierulf(.*)");
            add("(.*)Traci Hale(.*)");
            add("(.*)Trevor Guthrie(.*)");
            add("(.*)John Mellencamp(.*)");
            add("(.*)Kevin Briggs(.*)");
            add("(.*)Chad Elliott(.*)");
            add("(.*)Arnthor Birgisson(.*)");
            add("(.*)Hanson(.*)");
            add("(.*)Macy Gray(.*)");
            add("(.*)Charlie Colin(.*)");
            add("(.*)Ben Harper(.*)");
            add("(.*)Brian Palmer(.*)");
            add("(.*)Michael Garvin(.*)");
            add("(.*)Tim Mosley(.*)");
            add("(.*)Andre Williams(.*)");
            add("(.*)Darren Hayes(.*)");
            add("(.*)Linus Burdick(.*)");
            add("(.*)Massimo Gabutti(.*)");
            add("(.*)Tony Battaglia(.*)");
            add("(.*)Marc Anthony(.*)");
            add("(.*)Tony Scalzo(.*)");
            add("(.*)Case Woodard(.*)");
            add("(.*)Thomas Flowers(.*)");
            add("(.*)Jeff Pence(.*)");
            add("(.*)Johnta Austin(.*)");
            add("(.*)Duke Erikson(.*)");
            add("(.*)Wes Borland(.*)");
            add("(.*)John Barry(.*)");
            add("(.*)Rory Bennett(.*)");
            add("(.*)Full Force(.*)");
            add("(.*)Jorgen Elofsson(.*)");
            add("(.*)Barry Paul(.*)");
            add("(.*)Tom Delonge(.*)");
            add("(.*)Randy Bachman(.*)");
            add("(.*)Greg Camp(.*)");
            add("(.*)Tim Cox(.*)");
            add("(.*)C.? Strouse(.*)");
            add("(.*)Eugene Wilde(.*)");
            add("(.*)Lindon Roberts(.*)");
            add("(.*)Everclear(.*)");
            add("(.*)Tamara Savage(.*)");
            add("(.*)John Mccrea(.*)");
            add("(.*)Steve Marker(.*)");
            add("(.*)Camille Yarbroug(.*)");
            add("(.*)Sheryl Crow(.*)");
            add("(.*)Paul Hewson(.*)");
            add("(.*)Dan Wilson(.*)");
            add("(.*)Guy Chambers(.*)");
            add("(.*)Gregg Alexander(.*)");
            add("(.*)John Wozniak(.*)");
            add("(.*)Lenny Kravitz(.*)");
            add("(.*)Everclear(.*)");
            add("(.*)Colin Underwood(.*)");
            add("(.*)Johnny Mosegaard(.*)");
            add("(.*)Brian McKnight(.*)");
            add("(.*)R.? Ran(.*)");
            add("(.*)Steve Perry(.*)");
            add("(.*)Isaac Hanson(.*)");
            add("(.*)Emerson Hart(.*)");
            add("(.*)Jazayer(.*)");
            add("(.*)Rory Bennett(.*)");
            add("(.*)Spice Girls(.*)");
            add("(.*)Harvey Danger(.*)");
            add("(.*)Tony Scalzo(.*)");
            add("(.*)A.? Graham(.*)");
            add("(.*)Yoo Gun-Hyung(.*)");
            add("(.*)Timothy McKenzie(.*)");
            add("(.*)Jermaine Scott(.*)");
            add("(.*)Maverick Sabre(.*)");
            add("(.*)Emeli Sand(.*)");
            add("(.*)Jermaine Jackson(.*)");
            add("(.*)Andrew Harr(.*)");
            add("(.*)Jessica Cornish(.*)");
            add("(.*)Jordan Stephens(.*)");
            add("(.*)Isaac Mahmood Noell(.*)");
            add("(.*)Polina Goudieva(.*)");
            add("(.*)Leona Lewis(.*)");
            add("(.*)Costadinos Contostavlos(.*)");
            add("(.*)Jason Desrouleaux(.*)");
            add("(.*)Tom Barnes(.*)");
            add("(.*)Adele Adkins(.*)");
            add("(.*)Gary Barlow(.*)");
            add("(.*)J.? Rzeznik(.*)");
            add("(.*)Aaron Kamin(.*)");
            add("(.*)Damien Rice(.*)");
            add("(.*)Edward Christopher(.*)");
            add("(.*)James Morrison(.*)");
            add("(.*)Will Young(.*)");
            add("(.*)Stefani Germanotta(.*)");
            add("(.*)Brian Higgins(.*)");
            add("(.*)Tebey Ottoh(.*)");
            add("(.*)Alex Smith(.*)");
            add("(.*)Brett James(.*)");
            add("(.*)Christina Perri(.*)");
            add("(.*)Carl Falk(.*)");
            add("(.*)John Deacon(.*)");
            add("(.*)Robert Williams(.*)");
            add("(.*)Tom Odell(.*)");
            add("(.*)Cian Ducrot(.*)");


            add("(.*)Steve Mac(.*)");
            add("(.*)Boris Daenen(.*)");
            add("(.*)David Guetta(.*)");
            add("(.*)Rune Reilly Koelschn(.*)");
            add("(.*)Josh Bruce(.*)");
            add("(.*)Louis Bell(.*)");
            add("(.*)Ozedikus(.*)");
            add("(.*)Tom Grennan(.*)");
            add("(.*)Jorja Douglas(.*)");
            add("(.*)Cat Burns(.*)");
            add("(.*)Rosa Linn(.*)");
            add("(.*)Grace Barker(.*)");
            add("(.*)Marlon Roudette(.*)");

            add("(.*)Elton John(.*)");
            add("(.*)Tymofii Muzychuk(.*)");
            add("(.*)Damiano David(.*)");
            add("(.*)Sigrid Solbakk Raabe(.*)");
            add("(.*)Madonna(.*)");
            add("(.*)Dave Bayley(.*)");
            add("(.*)Paolo Nutini(.*)");
            add("(.*)Mikkel S?.? ?Eriksen(.*)");
            add("(.*)Jonah Shy(.*)");
            add("(.*)James Abrahart(.*)");
            add("(.*)Christian (\"?Bloodshy\"? )?Karlsson(.*)");
            add("(.*)Ida Botten(.*)");
            add("(.*)Kane Welsh(.*)");
            add("(.*)Ben Langmaid(.*)");
            add("(.*)Alyssa Stephens(.*)");
            add("(.*)Camila Cabello(.*)");
            add("(.*)Becky Hill(.*)");
            add("(.*)Charles B?.? ?Simmons(.*)");
            add("(.*)George Ezra(.*)");
            add("^Clarence Coffee Jr(.*)");
            add("^Max Martin");
            add("^(.*)P?nk(.*)");
            add("^Deborah Harry");
            add("^Steven Tyler");
            add("^Desmond Child");
            add("^John Robinson Reid");
            add("^Dan Dare");
            add("^Sam Smith");
            add("^Mark Ronson");
            add("^Iain James");
            add("^Lady GaGa");
            add("^Dolly Parton");
            add("^(.*)Fridolin Walcher(.*)");
            add("^(.*)Mike Posner(.*)");
            add("^(.*)Jess Glynne(.*)");
            add("^(.*)George Reid(.*)");
            add("^(.*)Abel Tesfaye(.*)");
            add("^(.*)Edward Drewett(.*)");
            add("^(.*)Bruno Mars(.*)");
            add("^(.*)Wayne Hector(.*)");
            add("^(.*)Fleur East(.*)");
            add("^(.*)Omar Pasley(.*)");
            add("^(.*)Brandy Norwood(.*)");
            add("^(.*)Adam Levine(.*)");
            add("^(.*)Zara Lawson(.*)");
            add("^(.*)Calvin Harris(.*)");
            add("^(.*)Henry Smithson(.*)");
            add("^(.*)Justin Bieber(.*)");
            add("^(.*)Martina Sorbara(.*)");
            add("^(.*)Elle King(.*)");
            add("^(.*)Bebe Rexha(.*)");
            add("^(.*)Alan Walker(.*)");
            add("^(.*)Thomas Pentz(.*)");
            add("^(.*)Alex Schwartz(.*)");
            add("^(.*)Demi Lovato(.*)");
            add("^(.*)David J Wolinski(.*)");
            add("^(.*)David Zowie(.*)");
            add("^(.*)Stevie Wonder(.*)");
            add("^(.*)Patrick Okogwu(.*)");
            add("^(.*)Berry Gordy(.*)");
            add("^(.*)Martin Solveig(.*)");
            add("^(.*)Tove Lo(.*)");
            add("^(.*)Calvin Harris(.*)");
            add("^(.*)Simon Aldred(.*)");
            add("^(.*)Craig David(.*)");
            add("^(.*)Ricky Hawk(.*)");
            add("^(.*)Tony Fadd(.*)");
            add("^(.*)Theron Thomas(.*)");
            add("^(.*)Karl Wilson(.*)");
            add("^(.*)Priscilla Rene(.*)");
            add("^(.*)Olly Murs(.*)");
            add("^(.*)Sam Smith(.*)");
            add("^(.*)Iggy Azalea(.*)");
            add("^(.*)Maxwell Ansah(.*)");
            add("^(.*)Conrad Sewell(.*)");
            add("^(.*)Peter Svensson(.*)");
            add("^(.*)Eric Frederic(.*)");
            add("^(.*)Nicholas Furlong(.*)");
            add("^(.*)Erick Orrosquieta(.*)");
            add("^(.*)Nicholas Petricca(.*)");
            add("^(.*)Meghan Trainor(.*)");
            add("^(.*)Olajide Olatunji(.*)");
            add("^(.*)Wayne Hector(.*)");
            add("^(.*)Kara DioGuardi(.*)");
            add("^(.*)Max Martin(.*)");
            add("^(.*)Danny Parker(.*)");
            add("^(.*)Thomas Barnes(.*)");
            add("^(.*)Lukasz Gottwald(.*)");
            add("^(.*)R.? Tedder(.*)");
            add("^(.*)Marc Williams(.*)");
            add("^(.*)Tiffany Amber(.*)");
            add("^(.*)Sia Furler(.*)");
            add("^(.*)Starley Hope(.*)");
            add("^(.*)Devon Gallaspy(.*)");
            add("^(.*)Danjahandz(.*)");
            add("^(.*)Robin Fredriksson(.*)");
            add("^(.*)James Napier(.*)");
            add("^(.*)Lance Ferguson(.*)");
            add("^(.*)Tim Bergling(.*)");
            add("^(.*)Perrie Edwards(.*)");
            add("^(.*)Darren Martyn(.*)");
            add("^(.*)Sebastian Ingrosso(.*)");
            add("^(.*)Darren Foremane(.*)");
            add("^(.*)Sandy Vee(.*)");
            add("^(.*)Sean Waugaman(.*)");
            add("^(.*)Adam Wiles(.*)");
            add("^(.*)Emily Warren(.*)");
            add("^(.*)Lou Reed(.*)");
            add("^(.*)Ralph Middlebrooks(.*)");
            add("^(.*)J.? Perry(.*)");
            add("^(.*)Erik Schrody(.*)");
            add("^(.*)Benito Benites(.*)");
            add("^(.*)Thomas Callaway(.*)");
            add("^(.*)Erick More Morillo(.*)");
            add("^(.*)Ester Dean(.*)");
            add("^(.*)L.? Springsteen(.*)");
            add("^(.*)Rob Davis(.*)");
            add("^(.*)Ayalah Bentovim(.*)");
            add("^(.*)Hurby(.*)Azor(.*)");
            add("^(.*)Casio Ware(.*)");
            add("^(.*)Jim Vallance(.*)");
            add("^(.*)Leroy Damien(.*)");
            add("^(.*)Linda Perry(.*)");
            add("^(.*)RedOne(.*)");
            add("^(.*)Timothy Cox(.*)");
            add("^(.*)Kevin O'Toole(.*)");
            add("^(.*)S.? Ferguson(.*)");
            add("^(.*)Andr(.*)Benjamin(.*)");
            add("^(.*)Justin Timberlake(.*)");
            add("^(.*)Dua Lipa(.*)");
            add("^(.*)Eric Prydz(.*)");
            add("^(.*)Lawrence Smith(.*)");
            add("^(.*)Andre Benjamin(.*)");
            add("^(.*)Jadan Andino(.*)");
            add("^(.*)Enrique Elias Garcia(.*)");
            add("^(.*)Lukasz Gottwald(.*)");
            add("^(.*)Jacky Arconte(.*)");
            add("^(.*)Althea Forrest(.*)");
            add("^(.*)Antonio Romero Monge(.*)");
            add("^(.*)A.? Taylor(.*)");
            add("^(.*)George Merrill(.*)");
            add("^(.*)Robert Van Leeuwen(.*)");
            add("^(.*)D.? Pitchford(.*)");
            add("^(.*)Rights Reserved(.*)");
            add("^(.*)Stephen Coy(.*)");
            add("^(.*)Cathal Smyth(.*)");
            add("^(.*)Paul Jabara(.*)");
            add("^(.*)James Hargreavesh(.*)");
            add("^(.*)Prince Rogers Nelson(.*)");
            add("^(.*)Franke Previte(.*)");
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
