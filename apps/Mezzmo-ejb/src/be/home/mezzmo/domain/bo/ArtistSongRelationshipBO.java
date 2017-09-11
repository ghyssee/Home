package be.home.mezzmo.domain.bo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.json.ArtistSongRelationship;
import be.home.mezzmo.domain.model.json.Artists;
import be.home.mezzmo.domain.model.json.MP3Prettifier;
import be.home.mezzmo.domain.model.json.MultiArtistConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static be.home.common.utils.JSONUtils.writeJsonFile;
import static be.home.common.utils.JSONUtils.writeJsonFileWithCode;

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
        artistSongRelationshipList = artistSongRelationship.items.stream().collect(Collectors.toList());
        Collections.sort(artistSongRelationshipList, (a1, b1) -> b1.priority - a1.priority);
    }

    public ArtistSongRelationship getArtistSongRelationship(){
        return artistSongRelationship;
    }

    public void save() throws IOException {
        writeJsonFileWithCode(artistSongRelationship, Constants.JSON.ARTISTSONGRELATIONSHIP);
        //writeJsonFile(artistSongRelationship, Setup.getFullPath(Constants.JSON.ARTISTSONGRELATIONSHIP) + ".NEW");
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

    private String getArtist(String id){
        Artists.Artist artistObj = ArtistBO.getInstance().getArtistWithException(id);
        String artistName = artistObj.getName();
        return artistName;
    }

    public List<ArtistSongRelationship.ArtistItem> convertToArtistList(String id, String text){
        List<ArtistSongRelationship.ArtistItem> artistItems = new ArrayList<>();
        ArtistSongRelationship.ArtistItem artistItem = new ArtistSongRelationship(). new ArtistItem();
        artistItem.setId(id);
        artistItem.setText(text);
        artistItems.add(artistItem);
        return artistItems;
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
                if (artist.matches(artistName + ".*")) {
                    match = true;
                    break;
                }
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