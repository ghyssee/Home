package be.home.main.test;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.exceptions.ApplicationException;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.bo.ArtistBO;
import be.home.mezzmo.domain.bo.ArtistConfigBO;
import be.home.mezzmo.domain.bo.ArtistSongRelationshipBO;
import be.home.mezzmo.domain.bo.MP3PrettifierBO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.json.*;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static be.home.common.utils.JSONUtils.writeJsonFile;
import static be.home.common.utils.JSONUtils.writeJsonFileWithCode;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class ConvertArtistPattern extends BatchJobV2 {

    private static final Logger log = getMainLog(ConvertArtistSong.class);

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {

        ConvertArtistPattern instance = new ConvertArtistPattern();
        instance.convert();
    }

    private void convert() throws IOException {
        //String[] tmp = "Santa Rosa (m.m.v.) Hanny".split(" \\(?[M|m]\\.[M|m]\\.[V|v]\\.\\)? | & ");

        MP3Prettifier mp3Prettifier = MP3Helper.getInstance().getMp3Prettifier();
        String path = Setup.getFullPath(Constants.Path.MP3PRETTIFIER) + "/";
        //MyFileWriter goodFile = new MyFileWriter(path + "ConvertedToArtistSongRelationship." +
          //      DateUtils.formatYYYYMMDD() + ".txt", MyFileWriter.APPEND);
        boolean save = false;
        for (MP3Prettifier.Word word :  mp3Prettifier.artist.names){
            Artists.Artist artist = ArtistBO.getInstance().findArtistByName(word.newWord);
            if (artist != null){
                if (artist.getPattern() != null){
                    log.warn("Pattern already exist: " + artist.getPattern());
                }
                else {
                    //int count = org.apache.commons.lang3.StringUtils.countMatches( word.newWord, " " );
                    ArtistBO.getInstance().updateArtistPattern(artist, word);
                    //log.info("Artist Found: " + word.newWord);
                    boolean found = MP3PrettifierBO.getInstance().removeArtistName(word.id);
                    if (!found){
                        throw new ApplicationException("Artist Name Not Found With Id; " + word.id);
                    }
                    else {
                        save = true;
                        //break;
                    }
                    //ArtistBO.getInstance().save();
                }
            }
        }
        if (save){
            ArtistBO.getInstance().save();
            MP3PrettifierBO.getInstance().save();
        }
    }

    private void convertToArtistPattern(MP3Prettifier.Word word){

    }

    @Override
    public void run() {

    }
}
