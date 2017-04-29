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
        String[] tmp = "A Feat. B & C".split("( Feat. | & | With )");

        MP3Prettifier mp3Prettifier = MP3Helper.getInstance().getMp3Prettifier();
        ArtistBO artistBO = ArtistBO.getInstance();
        ArtistConfigBO multiArtistBO = ArtistConfigBO.getInstance();
        ArtistSongRelationship artistSongRelationship = ArtistSongRelationshipBO.getInstance().getArtistSongRelationship();
        String path = Setup.getFullPath(Constants.Path.MP3PRETTIFIER) + "/";
        MyFileWriter goodFile = new MyFileWriter(path + "ConvertedToArtistSongRelationship." +
                DateUtils.formatYYYYMMDD() + ".txt", MyFileWriter.APPEND);
        MyFileWriter badFile = new MyFileWriter(path + "ErrorsArtistSongRelationship." +
                DateUtils.formatYYYYMMDD() + ".txt", MyFileWriter.NO_APPEND);
        List<MP3Prettifier.ArtistSongExceptions.ArtistSong> newArtistSongExceptionsList = new ArrayList<>();
        for (MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong : mp3Prettifier.artistSongExceptions.items){
            String artistName = artistSong.oldArtist.replaceAll("\\(\\.\\*\\)$", "");
            Artists.Artist oldArtist = artistBO.findArtistByName(artistName);
            if (oldArtist == null){
                printInfo(badFile, artistSong);
                newArtistSongExceptionsList.add(artistSong);
                log.info("Artist Not Found: " + artistSong.oldArtist);
                continue;
            }
            else {
                log.info("Artist Found: Id= " + oldArtist.getId());
            }
            String[] artists = artistSong.newArtist.split(multiArtistBO.getSplitterString());
            boolean remove = false;
            if (artists.length > 1){
                remove = convertToMultiArtist(artistSongRelationship.items, artists, oldArtist, artistSong);
            }
            else if (artists.length == 1){
                remove = convertToArtist(artistSongRelationship.items, artists[0], oldArtist, artistSong);
            }
            if (!remove){
                printInfo(badFile, artistSong);
                newArtistSongExceptionsList.add(artistSong);
            }
            else {
                printInfo(goodFile, artistSong);
            }
        }
        System.out.println("test");
        goodFile.close();
        badFile.close();
        mp3Prettifier.artistSongExceptions.items = newArtistSongExceptionsList;
        //writeJsonFile(mp3Prettifier, Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW");
        //writeJsonFile(artistSongRelationship, Setup.getFullPath(Constants.JSON.ARTISTSONGRELATIONSHIP) + ".NEW");
        writeJsonFileWithCode(mp3Prettifier, Constants.JSON.MP3PRETTIFIER);
        writeJsonFileWithCode(artistSongRelationship, Constants.JSON.ARTISTSONGRELATIONSHIP);

    }

    private void printInfo(MyFileWriter fileWriter, MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong) throws IOException {
        fileWriter.append("oldArtist: " + artistSong.oldArtist);
        fileWriter.append("newArtist: " + artistSong.newArtist);
        fileWriter.append("oldSong: " + artistSong.oldSong);
        fileWriter.append("newSong: " + artistSong.newSong);
        fileWriter.append(StringUtils.repeat("=", 100));
    }

    private boolean convertToMultiArtist(List<ArtistSongRelationship.ArtistSongRelation> list,
                                      String[] artists,
                                      Artists.Artist oldArtist,
                                      MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong){
        MultiArtistConfig.Item multiArtistItem = ArtistConfigBO.getInstance().findMultiArtist(artists);
        if (multiArtistItem != null){
            ArtistSongRelationship.ArtistSongRelation artistSongRelation = new ArtistSongRelationship().new ArtistSongRelation();
            artistSongRelation.oldSong = artistSong.oldSong;
            artistSongRelation.newSong = artistSong.newSong;
            artistSongRelation.oldArtistId = oldArtist.getId();
            artistSongRelation.exact = false;
            artistSongRelation.newMultiArtistId = multiArtistItem.getId();
            artistSongRelation.id = artistSong.id;
            list.add(artistSongRelation);
            // everything ok, convert it to ArtistSongRelationship;
            return true;

        }
        return false;

    }

    private boolean convertToArtist(List<ArtistSongRelationship.ArtistSongRelation> list,
                                         String artist,
                                         Artists.Artist oldArtist,
                                         MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong){
        Artists.Artist newArtist = ArtistBO.getInstance().findArtistByName(artist);
        if (newArtist != null){
            ArtistSongRelationship.ArtistSongRelation artistSongRelation = new ArtistSongRelationship().new ArtistSongRelation();
            artistSongRelation.oldSong = artistSong.oldSong;
            artistSongRelation.newSong = artistSong.newSong;
            artistSongRelation.oldArtistId = oldArtist.getId();
            artistSongRelation.exact = false;
            artistSongRelation.newArtistId = newArtist.getId();
            artistSongRelation.id = artistSong.id;
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
