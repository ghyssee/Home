package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.json.ArtistSongTest;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
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

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {


        //System.out.println(MP3TagUtils.stripFilename("(Hot S+++)"));

        //processArtistFile();
        //testMP3Prettifier();
        //System.out.println(MP3Helper.getInstance().test("A\\$2AP$2Test$2Test$3Test$4", "\\$2", "\\$3", 2));
        //System.out.println(MP3Helper.getInstance().checkRegExpDollar("$1Text$1", 1));
        //updateMP3();
        //batchProcess();
        testMP3Prettifier();
        //testAlbumArtist();

    }

    private static void batchProcess() throws IOException {
        ArtistSongTest artistSongTest = (ArtistSongTest) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTSONGTEST, ArtistSongTest.class);
        for (ArtistSongTest.AristSongTestItem item : artistSongTest.items){
            System.out.println(item.oldArtist);
            System.out.println(item.oldSong);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(item.oldArtist)) {
                item.newArtist = getArtistTitleException(item.oldArtist, item.oldSong);
            }
            else {
                item.newArtist = MP3Helper.getInstance().prettifyArtist(item.oldArtist);
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(item.oldSong)) {
                item.newSong = getTitleArtistException(item.oldArtist, item.oldSong);
            }
            else {
                item.newSong = MP3Helper.getInstance().prettifySong(item.oldSong);
            }
        }
        JSONUtils.writeJsonFileWithCode(artistSongTest, Constants.JSON.ARTISTSONGTEST);
    }

private static void testAlbumArtist(){
    SQLiteJDBC.initialize(workingDir);
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
        System.out.println(mp3Helper.stripFilename("B.A.D. (Big Audio Dynamite) - E = MC2"));
        //System.out.println(getArtistTitleException("Malvin Big", "Here Comes The Devil"));
        //System.out.println("The Partysquad Feat. Sjaak, Dio, Sef".replaceAll("((Sef|Dio|Sjaak)( ?& ?|, ?| |\\.|$)){3,}", "Dio, Sef & Sjaak"));
        //System.out.println(mp3Helper.prettifyArtist("Ll Cool J Feat. 7 Aurelius"));
        //System.out.println("Fuck It! (I Don't Want You Back)".replaceAll("F(?:uc|\\\\*\\\\*)k It!?(?:\\\\(I Don't Want You Back\\\\))?(.*)",
          //      "Fuck It (I Don't Want You Back)$1"));
        System.out.println(mp3Helper.prettifySong("Voulez Vous Coucher Avec Moi Ce Soir? (Lady Marmalade)"));
        System.out.println(mp3Helper.prettifyAlbum("The Flying Dutch 2017 Edition NL", null));
        System.out.println(getTitleArtistException("Lost Frequencies", "Reality"));
        System.out.println(getArtistTitleException("Brunner Und Brunner", "Im Namen Der Liebe"));
        System.out.println(mp3Helper.prettifySong("LOL (Radio Mix FT Fast Edit)"));
        System.out.println(mp3Helper.prettifyArtist("Person Z"));
        System.out.println(mp3Helper.prettifyArtist("Person Z, (a"));
        System.out.println(mp3Helper.prettifyArtist("Person Y"));

    }

    private static String getArtistTitleException(String artist, String title){
        String prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        prettifiedArtist = MP3Helper.getInstance().checkForArtistExceptions(prettifiedArtist, prettifiedTitle);
        return MP3Helper.getInstance().checkForArtistExceptions2(prettifiedArtist, prettifiedTitle);
    }

    private static String getTitleArtistException(String artist, String title){
        String prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        prettifiedTitle = MP3Helper.getInstance().checkForTitleExceptions(prettifiedArtist, prettifiedTitle);
        return MP3Helper.getInstance().checkForTitleExceptions2(prettifiedArtist, prettifiedTitle);
    }

    private static void updateMP3(){
        Mp3File mp3file = null;
        String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\New\\10 Sylver - Hungry Heart.mp3";
        //String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\test.mp3";
        //String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\108 Di-Rect - Hungry For Love.mp3";
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
            //id3v2Tag.setTitle("test2");
            //id3v2Tag.setArtist("Axwell Λ Ingrosso");
                mp3file.setId3v2Tag(id3v2Tag);
                String newFile = file + ".MP3";
                mp3file.save(newFile);
        } catch (Exception e) {
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

    @Override
    public void run() {

    }
}
