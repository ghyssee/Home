package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.enums.MP3Tag;
import be.home.common.main.BatchJobV2;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.ArtistSongItem;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.MP3TagUtils;
import be.home.domain.model.MezzmoUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.VersionTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.json.AlbumError;
import be.home.model.json.MP3Settings;
import com.mpatric.mp3agic.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.springframework.dao.EmptyResultDataAccessException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URI;
import java.sql.SQLException;
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
        testMP3Prettifier();
        //testAlbumArtist();
        //fileNotFound();
        //testVersion();

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
        //System.out.println(mp3Helper.prettifyAlbum("MNM Big Hits 2018 Vol. 3", null));
        //System.out.println(getTitleArtistException("Dna", "blabla"));
        //System.out.println(getArtistTitleException("Dna", "blabla"));

        //updateMP3();

        String tmp = "7-11";
        //System.out.println(mp3Helper.prettifySong(tmp));
        System.out.println(mp3Helper.prettifyArtist("Willy Sommers Feat. Dorothee Vegas & Like Maarten"));
        //System.out.println(mp3Helper.prettifyArtist("\uFEFFAxwell Λ Ingrosso"));
        //System.out.println(tmp.replaceAll("'?7[/|-| |-]?11", "Bla"));
        System.out.println(getArtistTitleException("Philip Oakey & Giorgio Moroder", "Together In Electric Dreams"));
        System.out.println(getTitleArtistException("DJ Paul Elstak", "Rainbow In The Sky (K&A's Radio Blast)"));
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

    private static void updateMP3(){
        MP3Settings mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
        Mp3File mp3file = null;
        MP3Utils mp3Utils = new MP3Utils();
        String file = "C:\\My Data\\tmp\\Java\\MP3Processor\\Test\\35 Luc Steeno - Ik Hoor Nog Steeds Dat Lied.mp3";
        //String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\test.mp3";
        //String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\108 Di-Rect - Hungry For Love.mp3";
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
            //id3v2Tag.setTitle("test2");
            //id3v2Tag.setArtist("Axwell Λ Ingrosso");
            String myString = "Test";
            String newFile = file + ".MP3";
            int i = mp3Utils.convertRating(mp3Utils.getRating(id3v2Tag));
            System.out.println("Rating: " + i);
            System.out.println("Duration: " + MP3Utils.getDuration(mp3file));
                //mp3file.save(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
