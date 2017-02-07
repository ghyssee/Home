package be.home.main;

import be.home.common.mp3.MP3Utils;
import be.home.domain.model.MP3Helper;
import be.home.domain.model.MP3TagUtils;
import com.mpatric.mp3agic.AbstractID3v2Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.Mp3FileExt;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URI;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class HelloWorld {
    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {



        //System.out.println(MP3TagUtils.stripFilename("(Hot S+++)"));
        testMP3Prettifier();
        String test = "Lilly Wood & The Prick & Robin Schulz";
        replace2k(test);
        test = "Lilly Wood, The Prick & Robin Schulz";
        replace2k(test);

    }

    private static void replace2k(String text){
        // 2k followed by 1 or more digit
        System.out.println(text.replaceAll("Lilly Wood(,| &) The Prick & Robin Schulz", "Replaced"));
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
        System.out.println(mp3Helper.prettifyArtist("HiBeatz"));
        System.out.println(mp3Helper.prettifyArtist("Riba & JMK!"));
        System.out.println(mp3Helper.prettifyArtist("Nutty Buddy Feat. M.U.G. (Of VSA)"));
        System.out.println(mp3Helper.prettifyArtist("R-CANE Feat. NOWLIVE.EU "));

        System.out.println(mp3Helper.prettifySong("Ok, That's it"));
        System.out.println(mp3Helper.prettifySong("AAA Anthem"));
        System.out.println(mp3Helper.prettifySong("Hype It! (Original TTF Mix)"));
        System.out.println(mp3Helper.prettifySong("Coco Flanel"));
        System.out.println(mp3Helper.prettifyArtist("Run-D.M.C Vs. Jason Nevin"));
        System.out.println(mp3Helper.prettifySong("L.a Song"));
        System.out.println(mp3Helper.prettifySong("Test Words 2k11 Test"));
        System.out.println(mp3Helper.prettifyAlbum("Yorin Fm"));
    }

    private static void updateMP3(){
        Mp3File mp3file = null;
        String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\49 Sly and Robbie Presents Shaggy feat. Melissa Musique - If U Slip U Slide (You Could Be Mine).mp3";
        //String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\test.mp3";
        //String file = "c:\\My Data\\tmp\\Java\\MP3Processor\\_test\\108 Di-Rect - Hungry For Love.mp3";
        try {
            mp3file = new Mp3File(file);
            ID3v2 id3v2Tag = MP3Utils.getId3v2Tag(mp3file);
            id3v2Tag.setTitle("test2");
            id3v2Tag.setArtist("Axwell Λ Ingrosso");
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

}
