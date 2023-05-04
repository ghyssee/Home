package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.main.BatchJobV2;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.ArtistSongItem;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.MP3TagUtils;
import be.home.domain.model.MezzmoUtils;
import be.home.domain.model.service.MP3Exception;
import be.home.domain.model.service.MP3JAudioTaggerServiceImpl;
import be.home.domain.model.service.MP3Service;
import be.home.main.test.ConvertArtistSong;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.VersionTO;
import be.home.mezzmo.domain.model.json.MultiArtistConfig;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.MovieBO;
import be.home.model.MovieTO;
import be.home.model.json.AlbumError;
import be.home.model.json.MP3Settings;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.reference.ID3V2Version;
import org.springframework.dao.EmptyResultDataAccessException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class HelloWorld extends BatchJobV2 {

    private static final Logger log = getMainLog(HelloWorld.class);
    public static MezzmoServiceImpl mezzmoService = null;

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {


        //processArtistFile();
        //testMP3Prettifier();
        //System.out.println(MP3Helper.getInstance().test("A\\$2AP$2Test$2Test$3Test$4", "\\$2", "\\$3", 2));
        //System.out.println(MP3Helper.getInstance().checkRegExpDollar("$1Text$1", 1));
        //updateMP3();
        //batchProcess();
        //testMP3Prettifier();
        //TestMovieFile();
        //testAlbumArtist();
        //fileNotFound();
        //testVersion();
        testJAudioTagger();

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

private static void TestMovieFile(){
    try {
        List<MovieTO> listOfMoviesFromCSV = MovieBO.getListOfMoviesFromCSV("C:/My Programs/OneDrive/Movies/emdbV3.csv");
        for (MovieTO movieTO: listOfMoviesFromCSV){
            System.out.println(movieTO.getTitle());
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }

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

    private static void processArtistFile(){
        File file = new File(Setup.getInstance().getFullPath(Constants.Path.MAIN_CONFIG) +
                File.separator + "ListOfArtistsToCheck.txt");
        MP3Helper mp3Helper =MP3Helper.getInstance();
        try {
            MyFileWriter newFile = new MyFileWriter(Setup.getInstance().getFullPath(Constants.Path.MAIN_CONFIG) +
                    File.separator + "ArtistResult.txt", MyFileWriter.NO_APPEND);
            List<String> lines = FileUtils.getContents(file);
            for (String line : lines){
                if (StringUtils.isNotBlank(line)){
                    System.out.println(line + " => " + mp3Helper.prettifyArtist(line));
                    newFile.append(line + " => " + mp3Helper.prettifyArtist(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testMP3Prettifier(){


        MP3Helper mp3Helper =MP3Helper.getInstance();


        //System.out.println(getArtistTitleException("Kellis Feat. Too Short", "bossy"));
        //System.out.println(getArtistTitleException("Malvin Big", "Here Comes The Devil"));
        //System.out.println("The Partysquad Feat. Sjaak, Dio, Sef".replaceAll("((Sef|Dio|Sjaak)( ?& ?|, ?| |\\.|$)){3,}", "Dio, Sef & Sjaak"));
        //System.out.println(mp3Helper.prettifyArtist("Ll Cool J Feat. 7 Aurelius"));
        //System.out.println("Fuck It! (I Don't Want You Back)".replaceAll("F(?:uc|\\\\*\\\\*)k It!?(?:\\\\(I Don't Want You Back\\\\))?(.*)",
          //      "Fuck It (I Don't Want You Back)$1"));

        //System.out.println(mp3Helper.stripFilename("B.A.D. (Big Audio Dynamite) - E = MC2"));

        /*System.out.println(mp3Helper.prettifySong("Voulez Vous Coucher Avec Moi Ce Soir? (Lady Marmalade)"));
        System.out.println(mp3Helper.prettifyAlbum("The Flying Dutch 2017 Edition NL", null));
        System.out.println(getTitleArtistException("Lost Frequencies", "Reality"));
        System.out.println(getArtistTitleException("Lost Frequencies", "Reality"));
        System.out.println(mp3Helper.prettifySong("LOL (Radio Mix FT Fast Edit)"));
        System.out.println(mp3Helper.prettifyArtist("Axwell ^ Ingrosso"));
        System.out.println(mp3Helper.prettifyArtist("Person Z, (a"));
        System.out.println(mp3Helper.prettifyArtist("M.A.R.R.S"));
        System.out.println(getTitleArtistException("Tina Turner", "Teach Me Again"));
        */
        //System.out.println(mp3Helper.prettifyArtist("Dorothee Vegas & Like Maarten Feat. Sam Gooris"));
        //System.out.println("Axwell ^ Ingrosso".replaceAll("Axwell \\^ Ingrosso", "test"));
        //System.out.println(getTitleArtistException("Kontra K", "Zwischen Himmel Hlle"));
        System.out.println(mp3Helper.prettifyArtist("Pink Sweat$"));
        System.out.println(mp3Helper.prettifyArtist("Galantis Feat. Ship Wrek & Pink Sweat$"));
        //System.out.println(mp3Helper.prettifyArtist("Ella Henderson Feat. Roger Sanchez"));
        System.out.println(mp3Helper.prettifyAlbum("Billboard Year-End Hot 100 singles of 2022", "Various Artists"));
        //System.out.println(getArtistTitleException("Galantis Feat. Ship Wrek & Pink Sweat$", "Only a Fool"));
        //System.out.println(getArtistTitleException("Reflekt ft. Delline Bass", "Need To Feel Loved (Cristoph Remix)"));
        //System.out.println(getTitleArtistException("Taylor Swift", "Me"));
        //System.out.println(getTitleArtistException("Taylor Swift", "Message in a bottle"));
        //System.out.println(getArtistTitleException("Dna", "blabla"));
        //System.out.println(getArtistTitleException("Michael Patrick Kelly", "Love Goes On (Live) [aus \"Sing meinen Song, Vol. 7\"]"));

        //updateMP3();

        String tmp = "Odj Team";
        //ConvertArtistSong test = new ConvertArtistSong();
        //MultiArtistConfig.Item item = test.findMultiArtist("Raf Marchesini Feat. D'Amico, Valax Vs. Gabin");
        //System.out.println(mp3Helper.prettifySong(tmp));
        //System.out.println(mp3Helper.prettifyArtist("Willy Sommers Feat. Dorothee Vegas & Like Maarten"));
        //System.out.println(mp3Helper.prettifyArtist("Raf Marchesini Feat. D'Amico, Valax Vs Gavin"));
        //System.out.println(tmp.replaceAll("O\\.?[D|d]\\.?[J|j]\\.? Team", "Bla"));
        //System.out.println(getArtistTitleException("Emeli Sandé", "Read All About It (Part III)"));
        //System.out.println(getTitleArtistException("Eurythmics", "Sweet Dreams"));
        //System.out.println(getTitleArtistException("Tones And I", "Ur So F**kInG cOoL"));
        //System.out.println(mp3Helper.prettifyAlbum("...Baby One More Time", "Britney Spears"));
        //System.out.println(mp3Helper.stripFilename("...Baby One More Time/Britney - test.mp3"));

        //System.out.println(getArtistTitleException("Sarah Brightman", "Time To Say Goodbye (Con Te Partiro) (Sarah's Intimate Version)"));


    }

    private static String getArtistTitleException(String artist, String title){
        ArtistSongItem item =  MP3Helper.getInstance().formatSong(artist, title, true);
        return item.getArtist();
    }

    private static String getTitleArtistException(String artist, String title){
        ArtistSongItem item = MP3Helper.getInstance().formatSong(artist, title, true);
        return item.getSong();
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

    private static void tmp() throws SAXException, DocumentException, IOException {
        SAXReader reader = new SAXReader();

        reader.setValidation(true);

        // specify the schema to use
        URI uri = new File("c:/reports/contacts.xsd").toURI();

        String file = uri.toString();
        reader.setProperty(
                "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                file
        );
        reader.setFeature("http://xml.org/sax/features/validation", true);
        reader.setFeature("http://apache.org/xml/features/validation/schema", true );
        reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

        // add error handler which turns any errors into XML
        XMLErrorHandler errorHandler = new XMLErrorHandler();
        reader.setErrorHandler( errorHandler );

        //HelloWorld m = new HelloWorld();
        //reader.setEntityResolver(m);

        //reader.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://example/url/contacts.xsd");
        // parse the document
        Document document = reader.read(new InputSource("C:\\reports\\contacts.xml"));

        // output the errors XML
        XMLWriter writer = new XMLWriter( OutputFormat.createPrettyPrint() );
        writer.write( errorHandler.getErrors() );
    }

    private static void testJAudioTagger(){
        // File file = new File("c:\\My Data\\tmp\\Java\\MP3Processor\\test\\02 Test Double Artist TXXX.mp3");
        File file = new File("c:\\My Data\\tmp\\Java\\MP3Processor\\test\\TestCases\\19 MCDI.mp3");
        File newFile = new File("c:\\My Data\\tmp\\Java\\MP3Processor\\test\\new.mp3");
       // TagOptionSingleton.getInstance().setOriginalSavedAfterAdjustingID3v2Padding(false);
       // TagOptionSingleton.getInstance().setRemoveTrailingTerminatorOnWrite(true);
        try {
            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MP3Service mp3File = null;
            try {
                mp3File = new MP3JAudioTaggerServiceImpl(newFile.getAbsolutePath());
                System.out.println("year: " + mp3File.getYear());
                System.out.println("Duration: " + mp3File.getDuration());
                System.out.println(mp3File.getComment());
                System.out.println(mp3File.getArtist());
                System.out.println(mp3File.getUrl());
                System.out.println("Is Cleanable: " + mp3File.isCleanable("mSm © 2021 Productions BV"));
                //mp3File.setArtist("Kings Of Leon");
                //mp3File.setTitle("On The Fly");
                //mp3File.setCompilation(true);
                //mp3File.setYear("2022");
                //mp3File.setRating(4);
                //mp3File.setDisc("1");
                //mp3File.setTrack("100");
                //mp3File.clearAlbumImage();
                //mp3File.setGenre("Pop");
                //mp3File.setAlbum("Test Album");
                //mp3File.setAlbumArtist("Various ArtistsXXX");
               // mp3File.clearAlbumImage();
                System.out.println(mp3File.getRatingAsString());
                System.out.println(mp3File.getAudioSourceUrl());
                //mp3File.cleanupTags();
                System.out.println(mp3File.getDuration());
                mp3File.analyze();
                mp3File.commit();
            } catch (MP3Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        private static void test() throws IOException, NoSuchFieldException, IllegalAccessException, SAXException, ParserConfigurationException, DocumentException {

        SAXParserFactory factory = SAXParserFactory.newInstance();

        SchemaFactory schemaFactory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        URI uri = new File("c:/reports/contacts.xsd").toURI();

            String file = uri.toString();

        SAXParser parser = factory.newSAXParser();

        SAXReader reader = new SAXReader(parser.getXMLReader());
            reader.setProperty(
                    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                    file
            );
        reader.setValidation(true);
            XMLErrorHandler errorHandler = new XMLErrorHandler();
            reader.setErrorHandler( errorHandler );
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setFeature("http://apache.org/xml/features/validation/schema", true );
        reader.read((new InputSource("C:\\reports\\contacts.xml")));
            // output the errors XML
            XMLWriter writer = new XMLWriter( OutputFormat.createPrettyPrint() );
            writer.write( errorHandler.getErrors() );
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
