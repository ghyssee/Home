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
import be.home.mezzmo.domain.bo.ArtistBO;
import be.home.mezzmo.domain.bo.ArtistConfigBO;
import be.home.mezzmo.domain.bo.ArtistSongRelationshipBO;
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
public class ConvertArtistSong extends BatchJobV2 {

    private static final Logger log = getMainLog(ConvertArtistSong.class);

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {

        ConvertArtistSong instance = new ConvertArtistSong();
            instance.convert();
    }

    private void convert() throws IOException {
        //String[] tmp = "Santa Rosa (m.m.v.) Hanny".split(" \\(?[M|m]\\.[M|m]\\.[V|v]\\.\\)? | & ");

        MP3Prettifier mp3Prettifier = MP3Helper.getInstance().getMp3Prettifier();
        ArtistSongRelationship artistSongRelationship = ArtistSongRelationshipBO.getInstance().getArtistSongRelationship();
        String path = Setup.getFullPath(Constants.Path.MP3PRETTIFIER) + "/";
        MyFileWriter goodFile = new MyFileWriter(path + "ConvertedToArtistSongRelationship." +
                DateUtils.formatYYYYMMDD() + ".txt", MyFileWriter.APPEND);
        MyFileWriter badFile = new MyFileWriter(path + "ErrorsArtistSongRelationship." +
                DateUtils.formatYYYYMMDD() + ".txt", MyFileWriter.NO_APPEND);
        List<MP3Prettifier.ArtistSongExceptions.ArtistSong> newArtistSongExceptionsList = new ArrayList<>();
        for (MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong : mp3Prettifier.artistSongExceptions.items){
            ArtistSongRelationship.ArtistSongRelation artistSongRelation = new ArtistSongRelationship().new ArtistSongRelation();
            boolean converted = convertArtist(artistSong.oldArtist, ArtistType.Old, artistSongRelation);
            if (!converted){
                printInfo(badFile, artistSong);
                newArtistSongExceptionsList.add(artistSong);
                log.info("Artist/MultiArtist Not Found: " + artistSong.oldArtist);
                continue;
            }
            else {
                converted = convertArtist(artistSong.newArtist, ArtistType.New, artistSongRelation);
                if (!converted){
                    printInfo(badFile, artistSong);
                    newArtistSongExceptionsList.add(artistSong);
                    log.info("Artist/MultiArtist Not Found: " + artistSong.newArtist);
                    continue;
                }
                else {
                    // artist song can be converted
                    artistSongRelation.oldSong = artistSong.oldSong;
                    artistSongRelation.newSong = artistSong.newSong;
                    artistSongRelation.exactMatchTitle = artistSong.exactMatchTitle;
                    artistSongRelation.id = artistSong.id;
                    artistSongRelation.priority = artistSong.priority;
                    artistSongRelationship.items.add(artistSongRelation);                }
                    printInfo(goodFile, artistSong);
            }
        }
        goodFile.close();
        badFile.close();
        mp3Prettifier.artistSongExceptions.items = newArtistSongExceptionsList;
        //writeJsonFile(mp3Prettifier, Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW");
        //writeJsonFile(artistSongRelationship, Setup.getFullPath(Constants.JSON.ARTISTSONGRELATIONSHIP) + ".NEW");
        writeJsonFileWithCode(mp3Prettifier, Constants.JSON.MP3PRETTIFIER);
        writeJsonFileWithCode(artistSongRelationship, Constants.JSON.ARTISTSONGRELATIONSHIP);

    }

    private  MultiArtistConfig.Item findMultiArtist(String multiArtistName){
        ArtistConfigBO multiArtistBO = ArtistConfigBO.getInstance();
        MultiArtistConfig.Item multiArtistItem = null;
        String[] artists = multiArtistName.split(multiArtistBO.getSplitterString());
        if (artists.length > 1) {
            multiArtistItem = ArtistConfigBO.getInstance().findMultiArtist(artists);
        }
        return multiArtistItem;
    }

    private void printInfo(MyFileWriter fileWriter, MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong) throws IOException {
        fileWriter.append("oldArtist: " + artistSong.oldArtist);
        fileWriter.append("newArtist: " + artistSong.newArtist);
        fileWriter.append("oldSong: " + artistSong.oldSong);
        fileWriter.append("newSong: " + artistSong.newSong);
        fileWriter.append("exactMatchArtist: " + artistSong.exactMatchArtist);
        fileWriter.append("exactMatchtitle: " + artistSong.exactMatchTitle);
        fileWriter.append("priority: " + artistSong.priority);
        fileWriter.append(StringUtils.repeat("=", 100));
    }

    public enum ArtistType {
        Old, New
    }

    private boolean convertArtist(String artistName, ArtistType artistType, ArtistSongRelationship.ArtistSongRelation artistSongRelation){
        log.info("Converting " + artistType.name() + " Artist");
        ArtistBO artistBO = ArtistBO.getInstance();
        Artists.Artist artist = artistBO.findArtistByName(artistName);
        MultiArtistConfig.Item multiArtist = null;
        boolean converted = false;
        if (artist == null){
            multiArtist = findMultiArtist(artistName);
            if (multiArtist != null) {
                if (artistType == ArtistType.Old) {
                    artistSongRelation.oldMultiArtistId = multiArtist.getId();
                }
                else {
                    artistSongRelation.newMultiArtistId = multiArtist.getId();
                }
                converted = true;
                log.info("MultiArtist Found: Id= " + multiArtist.getId());
            }
        }
        else {
            converted = true;
            if (artistType == ArtistType.Old) {
                artistSongRelation.oldArtistId = artist.getId();
            }
            else {
                artistSongRelation.newArtistId = artist.getId();
            }
            log.info("Artist Found: Id= " + artist.getId());
        }
        return converted;
    }

    private boolean convertToMultiArtist(List<ArtistSongRelationship.ArtistSongRelation> list,
                                      String[] artists,
                                      Artists.Artist oldArtist,
                                         MultiArtistConfig.Item oldMultiArtist,
                                      MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong)
    {
        MultiArtistConfig.Item multiArtistItem = ArtistConfigBO.getInstance().findMultiArtist(artists);
        if (multiArtistItem != null){
            ArtistSongRelationship.ArtistSongRelation artistSongRelation = new ArtistSongRelationship().new ArtistSongRelation();
            artistSongRelation.oldSong = artistSong.oldSong;
            artistSongRelation.newSong = artistSong.newSong;
            if (oldArtist != null) {
                artistSongRelation.oldArtistId = oldArtist.getId();
            }
            else {
                artistSongRelation.oldMultiArtistId = oldMultiArtist.getId();
            }
            artistSongRelation.exact = artistSong.exactMatchArtist;
            artistSongRelation.newMultiArtistId = multiArtistItem.getId();
            artistSongRelation.exactMatchTitle = artistSong.exactMatchTitle;
            artistSongRelation.id = artistSong.id;
            artistSongRelation.priority = artistSong.priority;
            list.add(artistSongRelation);
            // everything ok, convert it to ArtistSongRelationship;
            return true;

        }
        return false;

    }

    private boolean convertToArtist(List<ArtistSongRelationship.ArtistSongRelation> list,
                                         String artist,
                                         Artists.Artist oldArtist,
                                         MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong)
    {
        Artists.Artist newArtist = ArtistBO.getInstance().findArtistByName(artist);
        if (newArtist != null){
            ArtistSongRelationship.ArtistSongRelation artistSongRelation = new ArtistSongRelationship().new ArtistSongRelation();
            artistSongRelation.oldSong = artistSong.oldSong;
            artistSongRelation.newSong = artistSong.newSong;
            artistSongRelation.oldArtistId = oldArtist.getId();
            artistSongRelation.exact = artistSong.exactMatchArtist;
            artistSongRelation.newArtistId = newArtist.getId();
            artistSongRelation.exactMatchTitle = artistSong.exactMatchTitle;
            artistSongRelation.id = artistSong.id;
            artistSongRelation.priority = artistSong.priority;
            list.add(artistSongRelation);
            // everything ok, convert it to ArtistSongRelationship;
            return true;

        }
        return false;

    }

    @Override
    public void run() {

    }
}
