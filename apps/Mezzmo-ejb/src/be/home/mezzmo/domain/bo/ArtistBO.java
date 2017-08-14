package be.home.mezzmo.domain.bo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.json.Artists;
import be.home.mezzmo.domain.model.json.MP3Prettifier;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Gebruiker on 27/04/2017.
 */
public class ArtistBO {

    private static ArtistBO artistBO = new ArtistBO();
    private static Artists artists;
    private static final Logger log = Logger.getLogger(ArtistBO.class);
    private static Map<String, Artists.Artist> mapArtists;


    private ArtistBO() {
        artists = (Artists) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTS, Artists.class);
        mapArtists =
                artists.list.stream().collect(Collectors.toMap(Artists.Artist::getId, artist -> artist));
    }

    public Artists.Artist getArtist(String id){
        Artists.Artist artist = mapArtists.get(id);
        return artist;
    }

    public Artists.Artist getArtistWithException(String id){
        Artists.Artist artist = mapArtists.get(id);
        if (artist == null){
            throw new ApplicationException("ArtistId Not Found: " + id);
        }
        return artist;
    }

    public Artists.Artist findArtistByName(String artistName){
        for (Artists.Artist artistItem: artists.list){
            if (getStageName(artistItem).equals(artistName)){
                return artistItem;
            }
        }
        return null;
    }

    public void updateArtistPattern(Artists.Artist artist, MP3Prettifier.Word word){
        artist.setPattern(word.oldWord);
        artist.setPriority(word.priority);
    }

    public List<MP3Prettifier.Word> constructArtistPatterns(){
        List<MP3Prettifier.Word> list = new ArrayList<MP3Prettifier.Word>();
        for (Artists.Artist artistItem: artists.list){
            if (artistItem.getPattern() != null ) {
                MP3Prettifier.Word word = new MP3Prettifier().new Word();
                word.oldWord = artistItem.getPattern();
                word.newWord = artistItem.getName();
                word.endOfWord = 1;
                word.priority = artistItem.priority;
                list.add(word);
            }
        }
        return list;
    }

    public String getStageName(Artists.Artist artist){
        if (StringUtils.isBlank(artist.stageName)){
            return artist.name;
        }
        return artist.stageName;
    }

    public void save() throws IOException {
        String file = Setup.getFullPath(Constants.JSON.ARTISTS) + ".NEW";
        //JSONUtils.writeJsonFileWithCode(artists, Constants.JSON.ARTISTS);
        JSONUtils.writeJsonFile(artists, file);
    }

    public static ArtistBO getInstance() {
        return artistBO;
    }

}