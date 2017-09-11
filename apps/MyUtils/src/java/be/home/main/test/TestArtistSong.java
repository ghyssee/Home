package be.home.main.test;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.bo.ArtistSongRelationshipBO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.json.ArtistSongRelationship;
import be.home.mezzmo.domain.model.json.ArtistSongTest;
import be.home.mezzmo.domain.model.json.MP3Prettifier;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class TestArtistSong extends BatchJobV2 {

    private static final Logger log = getMainLog(TestArtistSong.class);

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {

        batchProcess();
    }

    private static void batchProcess() throws IOException {
        testArtistSong();
    }

    private static void testArtistSong(){
        log.info("Test ArtistSong Started: " + DateUtils.formatDate(new Date(), DateUtils.DD_MM_YYYY_HH_MM_SS));
        ArtistSongRelationshipBO bo  = ArtistSongRelationshipBO.getInstance();
        log.info("Nr Of Records found: " + bo.getArtistSongRelationship().items.size());
        logFindArtistSong(bo, "uwTgKP3RWFfKJo0GYCFjajx50GFZkXt7"); // must always exist
        logFindArtistSong(bo, "G94fIdRcYfBqXOHtHF4q7omo3sunlkLN");
        logFindArtistSong(bo, "BROL");
        log.info("Test ArtistSong Ended: " + DateUtils.formatDate(new Date(), DateUtils.DD_MM_YYYY_HH_MM_SS));
    }

    private static void logFindArtistSong(ArtistSongRelationshipBO bo, String id){
        ArtistSongRelationship.ArtistSongRelation item = bo.findArtistSongRelationshipById(id);
        if (item ==  null){
            log.info("Item Not Found: " + id);
        }
        else {
            log.info("Item Found: " + id);
        }
    }

    /*
    private static void convertArtistSong() throws IOException {
        ArtistSongRelationshipBO bo = ArtistSongRelationshipBO.getInstance();
        boolean save = false;
        for (ArtistSongRelationship.ArtistSongRelation item : bo.getArtistSongRelationship().items) {
            List<ArtistSongRelationship.ArtistItem> artistItems = new ArrayList<>();
            if (StringUtils.isNotBlank(item.oldArtistId)){
                ArtistSongRelationship.ArtistItem artistItem = new ArtistSongRelationship(). new ArtistItem();
                artistItem.setId(item.oldArtistId);
                artistItems.add(artistItem);
                item.oldArtistId = null;
                item.oldArtistList = artistItems;
                save = true;
            }
        }
        if (save){
            bo.save();
        }
    }*/

    @Override
    public void run() {

    }
}
