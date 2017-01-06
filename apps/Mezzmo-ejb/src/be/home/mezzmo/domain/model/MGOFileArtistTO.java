package be.home.mezzmo.domain.model;

import java.lang.reflect.Field;

/**
 * Created by ghyssee on 27/06/2016.
 */
public class MGOFileArtistTO {

    private Long ID;
    private String artist;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
