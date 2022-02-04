package be.home.main.test;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.ArtistSongItem;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.model.json.ArtistSongTest;
import be.home.mezzmo.domain.model.json.MultiArtistConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Tester extends BatchJobV2 {

    private static final Logger log = getMainLog(Tester.class);

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {

        batchProcess();
    }

    private static void test(){
        MultiArtistConfig multiArtistConfig = (MultiArtistConfig) JSONUtils.openJSONWithCode(Constants.JSON.MULTIARTISTCONFiG, MultiArtistConfig.class);
        Map map = new HashMap();
        for (MultiArtistConfig.Item item : multiArtistConfig.list){
            System.out.println(item.getId());
            if (map.get(item.getId()) != null){
                System.out.println("Already Exit");
            }
            map.put(item.id, item);
        }

    }

    private static void batchProcess() throws IOException {
        ArtistSongTest artistSongTest = (ArtistSongTest) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTSONGTEST, ArtistSongTest.class);
        for (ArtistSongTest.AristSongTestItem item : artistSongTest.items){
            System.out.println(item.oldArtist);
            System.out.println(item.oldSong);
            boolean artistSong = false;
            if (org.apache.commons.lang3.StringUtils.isNotBlank(item.oldArtist)
                    && org.apache.commons.lang3.StringUtils.isNotBlank(item.oldSong)) {
                ArtistSongItem artistSongItem = getPrettifiedArtistTitle(item.oldArtist, item.oldSong);
                item.newArtist = artistSongItem.getArtist();
                item.newSong = artistSongItem.getSong();
            }
            else if (org.apache.commons.lang3.StringUtils.isNotBlank(item.oldArtist)){
                item.newArtist = MP3Helper.getInstance().prettifyArtist(item.oldArtist);
            }
            else if (org.apache.commons.lang3.StringUtils.isNotBlank(item.oldSong)){
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

    private static ArtistSongItem getPrettifiedArtistTitle(String artist, String title){
        String prettifiedArtist = "";
        prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        ArtistSongItem item =  MP3Helper.getInstance().prettifyRuleArtistSong(prettifiedArtist, prettifiedTitle, true);
        return item;
    }

    /*

    private static String getArtistTitleException(String artist, String title){
        String prettifiedArtist = "";
        prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        ArtistSongItem item =  MP3Helper.getInstance().prettifyRuleArtistSong(prettifiedArtist, prettifiedTitle, true);
        return item.getItem();
    }

    private static String getTitleArtistException(String artist, String title){
        String prettifiedArtist = MP3Helper.getInstance().prettifyArtist(artist);
        String prettifiedTitle = MP3Helper.getInstance().prettifySong(title);
        ArtistSongItem item = MP3Helper.getInstance().prettifyRuleSongArtist(prettifiedArtist, prettifiedTitle, true);
        return item.getItem();
    }*/

    @Override
    public void run() {

    }
}
