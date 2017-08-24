package be.home.main.test;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
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
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Tester extends BatchJobV2 {

    private static final Logger log = getMainLog(Tester.class);

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {

        batchProcess();
        //convertArtistSongArtist();
        //convertArtistSongRelationship();
    }

    private static void convertArtistSong() throws IOException {
        MP3Prettifier mp3Prettifer = (MP3Prettifier) JSONUtils.openJSONWithCode(Constants.JSON.MP3PRETTIFIER, MP3Prettifier.class);
        for (MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong : mp3Prettifer.artistSongExceptions.items){
            if (artistSong.oldSong.endsWith("(.*)")){
                artistSong.exactMatchTitle = false;
                artistSong.oldSong = artistSong.oldSong.replaceFirst("\\(\\.\\*\\)$", "");
                artistSong.newSong = artistSong.newSong.replaceFirst("\\$1$", "");
                System.out.println("old: " + artistSong.oldSong);
                System.out.println("new: " + artistSong.newSong);
            }
            else {
                artistSong.exactMatchTitle = true;
            }
        }
        String file = Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW";
        JSONUtils.writeJsonFile(mp3Prettifer, file);
    }

    private static void convertArtistSongArtist() throws IOException {
        MP3Prettifier mp3Prettifer = (MP3Prettifier) JSONUtils.openJSONWithCode(Constants.JSON.MP3PRETTIFIER, MP3Prettifier.class);
        for (MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong : mp3Prettifer.artistSongExceptions.items){
            if (artistSong.oldArtist.endsWith("(.*)")){
                artistSong.exactMatchArtist = false;
                artistSong.oldArtist = artistSong.oldArtist.replaceFirst("\\(\\.\\*\\)$", "");
                //artistSong.newSong = artistSong.newSong.replaceFirst("\\$1$", "");
                System.out.println("old: " + artistSong.oldArtist);
                System.out.println("new: " + artistSong.newArtist);
            }
            else {
                artistSong.exactMatchArtist = true;
            }
        }
        String file = Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW";
        JSONUtils.writeJsonFile(mp3Prettifer, file);
    }
    private static void convertArtistSongRelationship() throws IOException {
        ArtistSongRelationship artistSongRelationship = ArtistSongRelationshipBO.getInstance().getArtistSongRelationship();
        for (ArtistSongRelationship.ArtistSongRelation artistSong : artistSongRelationship.items){
            if (artistSong.oldSong.endsWith("(.*)")){
                artistSong.exactMatchTitle = false;
                artistSong.oldSong = artistSong.oldSong.replaceFirst("\\(\\.\\*\\)$", "");
                artistSong.newSong = artistSong.newSong.replaceFirst("\\$1$", "");
                System.out.println("old: " + artistSong.oldSong);
                System.out.println("new: " + artistSong.newSong);
            }
            else {
                artistSong.exactMatchTitle = true;
            }
        }
        String file = Setup.getFullPath(Constants.JSON.ARTISTSONGRELATIONSHIP) + ".NEW";
        JSONUtils.writeJsonFile(artistSongRelationship, file);
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

    private static String getArtistTitleException(String artist, String title){
        String prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        prettifiedArtist = MP3Helper.getInstance().prettifyArtistSong(prettifiedArtist, prettifiedTitle);
        return prettifiedArtist;
    }

    private static String getTitleArtistException(String artist, String title){
        String prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        prettifiedTitle = MP3Helper.getInstance().prettifySongArtist(prettifiedArtist, prettifiedTitle);
        return prettifiedTitle;
    }

    @Override
    public void run() {

    }
}
