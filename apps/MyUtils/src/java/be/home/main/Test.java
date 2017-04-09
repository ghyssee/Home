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
public class Test extends BatchJobV2 {

    private static final Logger log = getMainLog(HelloWorld.class);

    public static void main(String args[]) {
        String path = "Test - 0001-0003";
        if (path.matches("(.* - )?[0-9]{3,4} ?- ?[0-9]{3,4}")){
            System.out.println("Not Main Path");
        }
        else {
            System.out.println(path);

        }

    }

    @Override
    public void run() {

    }
}
