package be.home.mezzmo.domain.model.eric;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class MezzmoFileTO {
    private long id;
    private long artistId;
    private String artistName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
