package be.home.mezzmo.domain.model.json;

import java.util.List;

/**
 * Created by Gebruiker on 27/04/2017.
 */
public class ArtistSongRelationship {

    public List<ArtistSongRelation> items;

    public class ArtistSongRelation {
        public String id;
        public String oldMultiArtistId;
        public List<ArtistItem> oldArtistList;
        public String newMultiArtistId;
        public String newArtistId;
        public String oldSong;
        public String newSong;
        public boolean exact;
        public boolean exactMatchTitle;
        public int priority;
        public int indexTitle;
    }

    public class ArtistItem {
        public String id;
        public String text;
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
