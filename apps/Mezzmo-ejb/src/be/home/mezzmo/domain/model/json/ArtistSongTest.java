package be.home.mezzmo.domain.model.json;

import java.util.List;

/**
 * Created by Gebruiker on 14/04/2017.
 */
public class ArtistSongTest {

    public List<AristSongTestItem> items;

    public class AristSongTestItem {
        public Long fileId;
        public String oldArtist;
        public String oldSong;
        public String newArtist;
        public String newSong;
    }
}
