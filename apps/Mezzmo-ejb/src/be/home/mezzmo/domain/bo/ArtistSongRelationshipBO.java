package be.home.mezzmo.domain.bo;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.json.ArtistSongRelationship;
import be.home.mezzmo.domain.model.json.Artists;
import be.home.mezzmo.domain.model.json.MP3Prettifier;
import be.home.mezzmo.domain.model.json.MultiArtistConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Gebruiker on 27/04/2017.
 */
public class ArtistSongRelationshipBO {

    private static ArtistSongRelationshipBO artistSongRelationshipBO = null;
    private static ArtistSongRelationship artistSongRelationship;
    private static final Logger log = Logger.getLogger(ArtistSongRelationshipBO.class);
    public static List<MP3Prettifier.ArtistSongExceptions.ArtistSong> artistSongs;


    private ArtistSongRelationshipBO() {
        artistSongRelationship = (ArtistSongRelationship) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTSONGRELATIONSHIP, ArtistSongRelationship.class);
        artistSongs = construct();
    }

    public ArtistSongRelationship getArtistSongRelationship(){
        return artistSongRelationship;
    }

    public static ArtistSongRelationshipBO getInstance() {
        if (artistSongRelationshipBO == null){
            artistSongRelationshipBO = new ArtistSongRelationshipBO();
        }
        return artistSongRelationshipBO;
    }

    public static List<MP3Prettifier.ArtistSongExceptions.ArtistSong> construct() {

        log.info("Started: Constructing ArtistSongRelationship");
        List <MP3Prettifier.ArtistSongExceptions.ArtistSong> items = new ArrayList<>();
        for (ArtistSongRelationship.ArtistSongRelation item : artistSongRelationship.items){
            MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong = new MP3Prettifier().new ArtistSongExceptions().new ArtistSong();
            artistSong.oldSong = item.oldSong;
            artistSong.newSong = item.newSong;
            Artists.Artist artist = ArtistBO.getInstance().getArtist(item.oldArtistId);
            if (artist == null){
                log.warn("Id: " + item.id + "/ ArtistId Not Found: " + item.oldArtistId);
                continue;
            }
            artistSong.oldArtist = artist.name + (item.exact ? "": "(.*)");
            if (StringUtils.isNotBlank(item.newMultiArtistId)){
                MultiArtistConfig.Item multiArtistItem = ArtistConfigBO.getInstance().getMultiArtist(item.newMultiArtistId);
                if (multiArtistItem == null){
                    log.warn("Id: " + item.id + "/ MultiArtistId Not Found: " + item.newMultiArtistId);
                    continue;
                }
                MP3Prettifier.Word word = ArtistConfigBO.getInstance().constructItem(multiArtistItem);
                artistSong.newArtist = word.newWord;
            }
            else {
               Artists.Artist artistItem = ArtistBO.getInstance().getArtist(item.newArtistId) ;
                artistSong.newArtist = ArtistBO.getInstance().getStageName(artistItem);
            }
            items.add(artistSong);
            log.info("Old Artist: " + artistSong.oldArtist);
            log.info("New Artist: " + artistSong.newArtist);
            log.info("Old Song: " + artistSong.oldSong);
            log.info("New Song: " + artistSong.newSong);
            log.info(StringUtils.repeat("=", 150));
        }
        log.info("Ended: Constructing ArtistSongRelationship");

        return items;
    }

}