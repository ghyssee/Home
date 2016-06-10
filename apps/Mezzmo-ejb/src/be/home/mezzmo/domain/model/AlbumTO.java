package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 9/06/2016.
 */
public class AlbumTO {

    public AlbumTO(String album, String artist) {
        this.album = album;
        this.artist = artist;
    }

    private String album;
    private String artist;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
