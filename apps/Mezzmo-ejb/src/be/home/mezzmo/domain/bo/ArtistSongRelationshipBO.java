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
import java.util.Collections;
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
    public static List<ArtistSongRelationship.ArtistSongRelation> artistSongRelationshipList;


    private ArtistSongRelationshipBO() {
        artistSongRelationship = (ArtistSongRelationship) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTSONGRELATIONSHIP, ArtistSongRelationship.class);
        artistSongs = construct();
        Collections.sort(artistSongs, (a1, b1) -> b1.priority - a1.priority);
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

    public List<ArtistSongRelationship.ArtistSongRelation> getArtistSongRelationshipList(){
        return artistSongRelationshipList;
    }

    private static void constructArtistSong(){

    }

    public static List<MP3Prettifier.ArtistSongExceptions.ArtistSong> construct() {

        log.info("Started: Constructing ArtistSongRelationship");
        List <MP3Prettifier.ArtistSongExceptions.ArtistSong> items = new ArrayList<>();
        artistSongRelationshipList = new ArrayList<>();
        for (ArtistSongRelationship.ArtistSongRelation item : artistSongRelationship.items){
            MP3Prettifier.ArtistSongExceptions.ArtistSong artistSong = new MP3Prettifier().new ArtistSongExceptions().new ArtistSong();
            artistSong.oldSong = item.oldSong;
            artistSong.newSong = item.newSong;

            // old artist

            if (StringUtils.isNotBlank(item.oldMultiArtistId)){
                MultiArtistConfig.Item multiArtistItem = ArtistConfigBO.getInstance().getMultiArtist(item.oldMultiArtistId);
                if (multiArtistItem == null){
                    log.warn("Id: " + item.id + "/ Old MultiArtistId Not Found: " + item.oldMultiArtistId);
                    continue;
                }
                MP3Prettifier.Word word = ArtistConfigBO.getInstance().constructItem(multiArtistItem);
                artistSong.oldArtist = word.newWord;
            }
            else if (StringUtils.isNotBlank(item.oldArtistId)) {
                Artists.Artist artistItem = ArtistBO.getInstance().getArtistWithException(item.oldArtistId) ;
                artistSong.oldArtist = artistItem.getName();
            }
            else if (item.oldArtistList != null) {
                artistSongRelationshipList.add(item);
            }

            artistSong.exactMatchArtist = item.exact;
            artistSong.exactMatchTitle = item.exactMatchTitle;
            artistSong.indexTitle = item.indexTitle;
            artistSong.priority = item.priority;

            // new artist

            if (StringUtils.isNotBlank(item.newMultiArtistId)){
                MultiArtistConfig.Item multiArtistItem = ArtistConfigBO.getInstance().getMultiArtist(item.newMultiArtistId);
                if (multiArtistItem == null){
                    log.warn("Id: " + item.id + "/ New MultiArtistId Not Found: " + item.newMultiArtistId);
                    continue;
                }
                MP3Prettifier.Word word = ArtistConfigBO.getInstance().constructItem(multiArtistItem);
                artistSong.newArtist = word.newWord;
            }
            else {
               Artists.Artist artistItem = ArtistBO.getInstance().getArtistWithException(item.newArtistId) ;
                artistSong.newArtist = ArtistBO.getInstance().getStageName(artistItem);
            }
            items.add(artistSong);
            /*
            log.info("Old Artist: " + artistSong.oldArtist);
            log.info("New Artist: " + artistSong.newArtist);
            log.info("Old Song: " + artistSong.oldSong);
            log.info("New Song: " + artistSong.newSong);
            log.info(StringUtils.repeat("=", 150));
            */

        }
        log.info("Ended: Constructing ArtistSongRelationship");

        return items;
    }

}