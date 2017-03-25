package be.home.main;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.mp3.MP3Utils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
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
        testMP3Prettifier();
        //updateMP3();


        //testAlbumArtist();

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
        System.out.println(mp3Helper.prettifyArtist("T.A.T.U"));
        System.out.println(mp3Helper.prettifyArtist("T.A.T.U."));
        System.out.println(mp3Helper.prettifyArtist("Axwell Ingrosso"));
        System.out.println(mp3Helper.prettifyArtist("Axwell & Ingrosso"));
        System.out.println(mp3Helper.prettifyArtist("Axwell ? Ingrosso"));
        System.out.println(mp3Helper.prettifyArtist("Axwell A Ingrosso"));
        System.out.println(mp3Helper.prettifyArtist("Jessie J Feat. B.O.B"));
        System.out.println(mp3Helper.prettifyArtist("B.o.B. F"));
        System.out.println(mp3Helper.prettifyArtist("B.o.B T"));
        System.out.println(mp3Helper.prettifyArtist("B.o.B. F"));
        System.out.println(mp3Helper.prettifyArtist("D-Cup"));
        System.out.println(mp3Helper.prettifyArtist("DCup Feat. Jessy"));
        System.out.println(mp3Helper.prettifyArtist("D.J Bobo"));
        System.out.println(mp3Helper.prettifyArtist("D.J. Bobo"));
        System.out.println(mp3Helper.prettifyArtist("DJ Bobo"));
        System.out.println(mp3Helper.prettifyArtist("L.L. Cool J."));
        System.out.println(mp3Helper.prettifyArtist("L.L. Cool J"));
        System.out.println(mp3Helper.prettifyArtist("Ll Cool J"));
        System.out.println(mp3Helper.prettifyArtist("Lilly Wood & The Prick & Robin Schulz"));
        System.out.println(mp3Helper.prettifyArtist("Lilly Wood, The Prick & Robin Schulz"));
        System.out.println(mp3Helper.prettifyArtist("Mr Robert"));
        System.out.println(mp3Helper.prettifyArtist("SFB, Ronnie Flex, Lil' Kleine & Bokoesam"));
        System.out.println(mp3Helper.prettifyArtist("Mr Robert"));
        System.out.println(mp3Helper.prettifyArtist("Iio"));
        System.out.println(mp3Helper.prettifyArtist("Iio Test"));
        System.out.println(mp3Helper.prettifyArtist("Mn 8"));
        System.out.println(mp3Helper.prettifyArtist("Mn 8 Test"));
        System.out.println(mp3Helper.prettifyArtist("Pm Project"));
        System.out.println(mp3Helper.prettifyArtist("K.P & Envyi"));
        System.out.println(mp3Helper.prettifyArtist("LL Cool J. Feat. Kelly Price"));
        System.out.println(mp3Helper.prettifyArtist("Jessie J Feat. B.o.B."));
        System.out.println(mp3Helper.prettifyArtist("Die Srv-Männer"));
        System.out.println(mp3Helper.prettifyArtist("Mackenzie ft Jessy"));
        System.out.println(mp3Helper.prettifyArtist("Mackenzie ft. Jessy"));
        System.out.println(mp3Helper.prettifyArtist("Riba & JMK!"));

        System.out.println(mp3Helper.prettifySong("Ok, That's it"));
        System.out.println(mp3Helper.prettifySong("AAA Anthem"));
        System.out.println(mp3Helper.prettifySong("Mambo Nr. 5"));
        System.out.println(mp3Helper.prettifySong("Mambo Nr5"));
        System.out.println(mp3Helper.prettifySong("Oops! I Did It Again"));
        System.out.println(mp3Helper.prettifySong("Oops! ... I Did It Again"));
        System.out.println(mp3Helper.prettifySong("Oops!.. I Did It Again"));
        System.out.println(mp3Helper.prettifyAlbum("Yorin Fm"));
        System.out.println("Oops! I Did It Again".replaceAll("Oops!? ?.?.?.? ?I Did It Again","Oops!... I Did It Again"));

        System.out.println(mp3Helper.stripFilename("D*Note"));
        System.out.println(mp3Helper.stripFilename("B**ch!"));
        System.out.println(mp3Helper.stripFilename("A+"));
        System.out.println(mp3Helper.stripFilename("+1"));
        System.out.println(mp3Helper.stripFilename("8^Y"));
        System.out.println(mp3Helper.stripFilename("M.I.L.F. $"));
        System.out.println(mp3Helper.stripFilename("A$AP"));
        System.out.println(mp3Helper.stripFilename("X!nk"));
        System.out.println(mp3Helper.stripFilename("ELV1S: 30 #1 Hits"));
        System.out.println(mp3Helper.stripFilename("Mambo No. 5 (A Little Bit of...)"));
        System.out.println(mp3Helper.prettifyArtist("Partysquad"));
        System.out.println(mp3Helper.prettifyArtist("The Underdog Project Vs The Sunclub"));
        System.out.println(mp3Helper.prettifyArtist("Rag 'n‘ Bone Man"));
        System.out.println(mp3Helper.prettifyAlbum("ELV1S: 30 #1 Hits"));
        System.out.println(mp3Helper.prettifySong("Lovin, Livin And Givin"));
        System.out.println("Flo Rida Feat. Ke$ha".replaceAll("((Flo Rida|Ke\\$ha)( Feat\\.,? | ?, &? ?| ?&([a|A]mp)? ?| Vs\\. | (Duet )?With | Presenting | Presents | En | \\+ | X | & | X |$)){2}", "blabla"));
        System.out.println(mp3Helper.prettifyArtist("Elektrochemie LK"));
        System.out.println(mp3Helper.prettifyArtist("Jay-Z, Rihanna & Kanye West"));
        //System.out.println("The Partysquad Feat. Sjaak, Dio, Sef".replaceAll("((Sef|Dio|Sjaak)( ?& ?|, ?| |\\.|$)){3,}", "Dio, Sef & Sjaak"));
        //System.out.println(mp3Helper.prettifyArtist("Ll Cool J Feat. 7 Aurelius"));

    }

    private static void updateMP3(){
        Mp3File mp3file = null;
        String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\01 Belle Perez - Light Of My Life.mp3";
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
