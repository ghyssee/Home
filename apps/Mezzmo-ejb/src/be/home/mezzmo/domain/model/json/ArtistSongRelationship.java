package be.home.mezzmo.domain.model.json;

import java.util.List;

/**
 * Created by Gebruiker on 27/04/2017.
 */
public class ArtistSongRelationship {

    public List<ArtistSongRelation> items;

    public class ArtistSongRelation {
        public String id;
        public String oldArtistId;
        public String newMultiArtistId;
        public String newArtistId;
        public String oldSong;
        public String newSong;
        public boolean exact;
        public boolean exactMatchTitle;
        public int priority;
    }
}
