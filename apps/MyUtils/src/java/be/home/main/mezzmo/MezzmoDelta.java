package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.Databases;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
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
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */

public class MezzmoDelta extends BatchJobV2 {

    private static final Logger log = getMainLog(be.home.main.mezzmo.MezzmoDelta.class);
    private MezzmoServiceImpl impl = MezzmoServiceImpl.getInstance(Databases.MEZZMOV1);
    private MezzmoServiceImpl impl2 = MezzmoServiceImpl.getInstance(Databases.MEZZMOV2);

    public static void main(String args[]) {

        MezzmoDelta instance = new MezzmoDelta();
        instance.myInit();

    }

    private void myInit(){
        SQLiteJDBC.initialize();
        MGOFileAlbumCompositeTO comp = getMezzmoV2Instance().findFileById(175435);
        System.out.println(comp.getFileTO().getFile());
        //MGOFileAlbumCompositeTO comp2 = impl.findFileById(175435);
        //System.out.println(comp2.getFileTO().getFile());
        TransferObject to = new TransferObject();
            do {
                List<MGOFileAlbumCompositeTO> list = impl.getAllMP3Files(to);
                log.info("Index = " + to.getIndex());
                checkFile(list);
            }
            while (!to.isEndOfList());
    }

    private void checkFile(List<MGOFileAlbumCompositeTO> list){
        for (MGOFileAlbumCompositeTO comp : list){
            try {
                getMezzmoV1Instance().findFileById(comp.getFileTO().getId());
                log.info("Found: " + comp.getFileTO().getId());
            }
            catch (IncorrectResultSizeDataAccessException ex){
                log.info("Not Found: " + comp.getFileTO().getId());
            }
        }
    }

    private MezzmoServiceImpl getMezzmoV1Instance(){
        return impl;
    }

    private MezzmoServiceImpl getMezzmoV2Instance(){
        return impl2;
    }


    @Override
    public void run() {

    }
}
