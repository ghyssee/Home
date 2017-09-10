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
    public static List<ArtistSongRelationship.ArtistSongRelation> artistSongRelationshipList;


    private ArtistSongRelationshipBO() {
        artistSongRelationship = (ArtistSongRelationship) JSONUtils.openJSONWithCode(Constants.JSON.ARTISTSONGRELATIONSHIP, ArtistSongRelationship.class);
        artistSongRelationshipList = artistSongRelationship.items;
        Collections.sort(artistSongRelationshipList, (a1, b1) -> b1.priority - a1.priority);
    }

    public ArtistSongRelationship getArtistSongRelationship(){
        return artistSongRelationship;
    }

    public ArtistSongRelationship.ArtistSongRelation findArtistSongRelationshipById(String id){
        for (ArtistSongRelationship.ArtistSongRelation item : artistSongRelationship.items) {
            if (item.id.equals(id)){
                return item;
            }
        }
        return null;
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

   public static List<MP3Prettifier.ArtistSongExceptions.ArtistSong> oldConstruct() {

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

    private static String getArtist(String id){
        Artists.Artist artistObj = ArtistBO.getInstance().getArtistWithException(id);
        String artistName = artistObj.getName();
        return artistName;
    }

    public boolean matchArtist(String artist, ArtistSongRelationship.ArtistSongRelation artistSong){
        boolean match = false;
        String artistName = null;
        if (artistSong.oldArtistList != null && artistSong.oldArtistList.size() > 0) {
            for (ArtistSongRelationship.ArtistItem artistItem : artistSong.oldArtistList) {
                if (StringUtils.isBlank(artistItem.getId())) {
                    artistName = artistItem.text;
                } else {
                    artistName = getArtist(artistItem.id);
                }
                if (artist.startsWith(artistName)) {
                    match = true;
                    break;
                }
            }
        }
        else if (artistSong.oldArtistId != null){
            artistName = getArtist(artistSong.oldArtistId);
            if (artist.startsWith(artistName)) {
                match = true;
            }
        }
        else if (artistSong.oldMultiArtistId != null){
            MultiArtistConfig.Item multiArtistItem = ArtistConfigBO.getInstance().getMultiArtist(artistSong.oldMultiArtistId);
            artistName = ArtistConfigBO.getInstance().constructNewArtistName(multiArtistItem.artistSequence);
            if (artist.startsWith(artistName)) {
                match = true;
            }
        }
        return match;

    }

}