package be.home.main.test;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.bo.ArtistBO;
import be.home.mezzmo.domain.bo.ArtistConfigBO;
import be.home.mezzmo.domain.bo.ArtistSongRelationshipBO;
import be.home.mezzmo.domain.model.json.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static be.home.common.utils.JSONUtils.writeJsonFile;

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
                    artistSongRelation.indexTitle = artistSong.indexTitle;
                    artistSongRelationship.items.add(artistSongRelation);                }
                    printInfo(goodFile, artistSong);
            }
        }
        goodFile.close();
        badFile.close();
        mp3Prettifier.artistSongExceptions.items = newArtistSongExceptionsList;
        writeJsonFile(mp3Prettifier, Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW");
        writeJsonFile(artistSongRelationship, Setup.getFullPath(Constants.JSON.ARTISTSONGRELATIONSHIP) + ".NEW");
        //writeJsonFileWithCode(mp3Prettifier, Constants.JSON.MP3PRETTIFIER);
        //writeJsonFileWithCode(artistSongRelationship, Constants.JSON.ARTISTSONGRELATIONSHIP);

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
            else {
                if (artistType.equals(ArtistType.Old)){
                    // artist can be (?:Artist A|Artist B)
                    converted = checkForMultiArtistList(artistName, artistSongRelation);
                    if (converted) {
                        log.info("Artist List Found For " + artistName);
                    }
                }
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

    private boolean checkForMultiArtistList(String artistName, ArtistSongRelationship.ArtistSongRelation artistSongRelation){
        String tmp = artistName.replaceAll("^\\(\\?:", "");
        ArtistBO artistBO = ArtistBO.getInstance();
        tmp = tmp.replaceAll("\\)$", "");
        String[] artists = tmp.split("\\|");
        List<ArtistSongRelationship.ArtistItem> artistList = new ArrayList<>();
        boolean found = false;
        for ( String artist : artists){
            Artists.Artist artistObj = artistBO.findArtistByName(artist);
            if (artistObj != null){
                ArtistSongRelationship.ArtistItem artistItem = new ArtistSongRelationship(). new ArtistItem();
                artistItem.setId(artistObj.getId());
                artistList.add(artistItem);
                found = true;
            }
            else {
                log.warn("Could Not Find Artist From List: " + artist);
                found = false;
                break;
            }
        }
        if (found){
            artistSongRelation.oldArtistList = artistList;
        }
        return found;
    }

    @Override
    public void run() {

    }
}
