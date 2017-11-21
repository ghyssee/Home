package be.home.mezzmo.domain.model.eric;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class MezzmoFileTO {
    private long id;
    private long artistId;
    private String artistName;
    private String status;
    private boolean isNew;
    private String ruleId;
    private String song;
    private String newSong;
    private String newArtist;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getNewSong() {
        return newSong;
    }

    public void setNewSong(String newSong) {
        this.newSong = newSong;
    }

    public String getNewArtist() {
        return newArtist;
    }

    public void setNewArtist(String newArtist) {
        this.newArtist = newArtist;
    }
}
