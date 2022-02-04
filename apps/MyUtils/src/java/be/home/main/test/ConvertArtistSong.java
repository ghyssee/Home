package be.home.main.test;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.common.utils.MyFileWriter;
import be.home.domain.model.MP3Helper;
import be.home.mezzmo.domain.bo.ArtistBO;
import be.home.mezzmo.domain.bo.ArtistConfigBO;
import be.home.mezzmo.domain.bo.ArtistSongRelationshipBO;
import be.home.mezzmo.domain.model.json.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.home.common.utils.JSONUtils.writeJsonFile;
import static be.home.common.utils.JSONUtils.writeJsonFileWithCode;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class ConvertArtistSong extends BatchJobV2 {

    private static final Logger log = getMainLog(ConvertArtistSong.class);

    public static void main(String args[]) throws SAXException, DocumentException, IOException, IllegalAccessException, NoSuchFieldException, ParserConfigurationException {

        ConvertArtistSong instance = new ConvertArtistSong();
            //instance.convert();
        instance.checkArtistNamesExistInArtists();
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
            boolean converted = convertArtist(artistSong.oldArtist, artistSong.oldFreeArtist, ArtistType.Old, artistSongRelation);
            if (!converted){
                printInfo(badFile, artistSong);
                newArtistSongExceptionsList.add(artistSong);
                log.info("Artist/MultiArtist Not Found: " + artistSong.oldArtist);
                continue;
            }
            else {
                converted = convertArtist(artistSong.newArtist, artistSong.oldFreeArtist, ArtistType.New, artistSongRelation);
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
        //writeJsonFile(mp3Prettifier, Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW");
        //writeJsonFile(artistSongRelationship, Setup.getFullPath(Constants.JSON.ARTISTSONGRELATIONSHIP) + ".NEW");
        writeJsonFileWithCode(mp3Prettifier, Constants.JSON.MP3PRETTIFIER);
        //writeJsonFileWithCode(artistSongRelationship, Constants.JSON.ARTISTSONGRELATIONSHIP);
        ArtistSongRelationshipBO.getInstance().save();
        //checkOldArtists();
        checkArtistStageNames();
        checkArtistsWithThe();
        checkMultiArtistNames();

    }

    private void checkArtistStageNames() throws IOException {
        ArtistBO artistBO = ArtistBO.getInstance();
        boolean save = false;
        for (Artists.Artist artist : artistBO.getArtistList()){
            if (artist.getStageName() == null){
                artist.setStageName(artist.getName());
                log.info("Stagename not filled in: " + artist.getStageName());
                save = true;
            }
        }
        if (save){
            artistBO.save();
        }

    }

    private void checkMultiArtistNames() throws IOException {
        ArtistConfigBO artistConfigBO = ArtistConfigBO.getInstance();
        Map<String,MultiArtistConfig.Item> map = new HashMap();
        for (MultiArtistConfig.Item item : artistConfigBO.getMultiArtistList()){
            String name = artistConfigBO.constructNewArtistName(item.artistSequence, false);
            MultiArtistConfig.Item searchItem = map.get(name);
            if (searchItem == null){
                map.put(name, item);
            }
            else {
                log.warn("Multi Artist Already Exist: " + item.getId() + " / " + name);
            }
        }
    }

    private void checkArtistsWithThe() throws IOException {
        ArtistBO artistBO = ArtistBO.getInstance();
        for (Artists.Artist artist : artistBO.getArtistList()){
            if (artist.getName().startsWith("The ")){
                // ignore the following list
                if (artist.getName().equals("The Vanguard")
                        || artist.getName().equals("The Mackenzie") // used in  Apollo vs. Mackenzie
                        || artist.getName().equals("The Jam") // used in Jam & Spoon
                        || artist.getName().equals("The Sunclub") // used in The Underdog Project Vs. Sunclub
                        || artist.getName().equals("The Wulf") // used in Sam Feldt x Lucas & Steve ft. Wulf
                        ){
                    continue;
                }
                String searchArtist = artist.getName().replaceFirst("^The ", "");
                Artists.Artist foundArtist = artistBO.findArtistByName(searchArtist);
                if (foundArtist != null){
                    log.warn("Double Artist With/Without The Prefix 'The' Found: " + foundArtist.getName());
                }
            }
        }
    }


    private void checkArtistNamesExistInArtists() throws IOException {
        ArtistBO artistBO = ArtistBO.getInstance();
        MP3Prettifier mp3Prettifier = MP3Helper.getInstance().getMp3Prettifier();
        for (MP3Prettifier.Word artistName : mp3Prettifier.artist.names){
            //System.out.println(artistName.newWord);
            Artists.Artist artist = artistBO.findArtistByName(artistName.newWord);
            if (artist != null){
                System.out.println("FOUND:" + artistName.newWord);
            }
        }
    }


    private void checkOldArtists() throws IOException {
        ArtistSongRelationship artistSongRelationship = ArtistSongRelationshipBO.getInstance().getArtistSongRelationship();
        ArtistConfigBO artistConfigBO = ArtistConfigBO.getInstance();
        boolean save= false;
        for (ArtistSongRelationship.ArtistSongRelation artistSong :  artistSongRelationship.items){
            // if old artist is exact, don't add any other artists anymore
            if (artistSong.exact) continue;
            if (StringUtils.isNotBlank(artistSong.newMultiArtistId)){
                if (StringUtils.isBlank(artistSong.oldMultiArtistId)){
                    MultiArtistConfig.Item multiArtistItem = artistConfigBO.getMultiArtist(artistSong.newMultiArtistId);
                    if (multiArtistItem == null){
                        throw new ApplicationException("Multi Artist Id Not Found: " + artistSong.newMultiArtistId);
                    }
                    else {
                        if (!artistSong.noCheckOnNewMultiArtist) {
                            save = checkNewMultiArtistExistInOldArtistList(multiArtistItem.artistSequence, artistSong.oldArtistList) || save;
                        }
                    }
                }
            }
        }
        if (save){
            ArtistSongRelationshipBO.getInstance().save();
        }

    }

    private boolean checkNewMultiArtistExistInOldArtistList(List<MultiArtistConfig.Item.ArtistSequenceItem> artistSequenceItems, List<ArtistSongRelationship.ArtistItem> artistItems) throws IOException {
        ArtistBO artistBO = ArtistBO.getInstance();
        boolean save = false;
        for (MultiArtistConfig.Item.ArtistSequenceItem artistSequenceItem : artistSequenceItems){
            if (!findArtistInOldArtistList(artistSequenceItem.getArtistId(), artistItems)){
                Artists.Artist artist = artistBO.getArtist(artistSequenceItem.getArtistId());
                if (artist != null) {
                    log.info("Artist Not Found In Old Artist List: " + artist.getName());
                    ArtistSongRelationship.ArtistItem newArtistItem = new ArtistSongRelationship().new ArtistItem();
                    newArtistItem.setId(artist.getId());
                    artistItems.add(newArtistItem);
                    save = true;
                }
            }
        }
        return save;
    }

    private boolean findArtistInOldArtistList(String id, List<ArtistSongRelationship.ArtistItem> artistItems){
        boolean found = false;
        for (ArtistSongRelationship.ArtistItem artistItem : artistItems){
            if (StringUtils.isNotBlank(artistItem.getId()) && artistItem.getId().equals(id)){
                found = true;
                break;
            }
        }
        return found;
    }

    public  MultiArtistConfig.Item findMultiArtist(String multiArtistName){
        MultiArtistConfig.Item multiArtistItem = null;
        List<String> splitters = constructSplitters();
        for (String splitter : splitters) {
            String[] artists = multiArtistName.split(splitter);
            if (artists.length > 1) {
                multiArtistItem = ArtistConfigBO.getInstance().findMultiArtist(artists);
            }
            if (multiArtistItem != null){
                break;
            }
        }
        return multiArtistItem;
    }

    private List<String> constructSplitters(){
        List<String> splitters = new ArrayList<>();
        ArtistConfigBO multiArtistBO = ArtistConfigBO.getInstance();
        splitters.add(multiArtistBO.getSplitterString());
        splitters.add(ArtistConfigBO.getInstance().constructSplitter(new String[]{MultiArtistConfig.AMP}));
        splitters.add(ArtistConfigBO.getInstance().constructSplitter(new String[] {MultiArtistConfig.AND}));
        splitters.add(ArtistConfigBO.getInstance().constructSplitter(new String[] {MultiArtistConfig.AMP, MultiArtistConfig.KOMMA}));
        splitters.add(ArtistConfigBO.getInstance().constructSplitter(new String[] {MultiArtistConfig.MIT}));
        splitters.add(ArtistConfigBO.getInstance().constructSplitter(new String[] {MultiArtistConfig.WITH}));
        return splitters;
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

    private boolean convertArtist(String artistName, boolean oldFreeArtist, ArtistType artistType, ArtistSongRelationship.ArtistSongRelation artistSongRelation){
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
            else if (artistType.equals(ArtistType.Old)){
                // artist can be (?:Artist A|Artist B)
                converted = checkForMultiArtistList(artistName, oldFreeArtist, artistSongRelation);
                if (converted) {
                    log.info("Artist List Found For " + artistName);
                }
                else if (oldFreeArtist){
                    artistSongRelation.oldArtistList = ArtistSongRelationshipBO.getInstance().convertToArtistList(null, artistName);
                    log.info("Artist (Free Text) Found: Text= " + artistName);
                    converted = true;
                }
            }
        }
        else {
            converted = true;
            if (artistType == ArtistType.Old) {
                artistSongRelation.oldArtistList = ArtistSongRelationshipBO.getInstance().convertToArtistList(artist.getId(), null);
            }
            else {
                artistSongRelation.newArtistId = artist.getId();
            }
            log.info("Artist Found: Id= " + artist.getId());
        }
        return converted;
    }

    private boolean checkForMultiArtistList(String artistName, boolean oldFreeArtist, ArtistSongRelationship.ArtistSongRelation artistSongRelation){
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
                if (oldFreeArtist){
                    ArtistSongRelationship.ArtistItem artistItem = new ArtistSongRelationship(). new ArtistItem();
                    artistItem.setText(artist);
                    artistList.add(artistItem);
                    found = true;
                }
                else {
                    log.warn("Could Not Find Artist From List: " + artist);
                    found = false;
                    break;
                }
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
