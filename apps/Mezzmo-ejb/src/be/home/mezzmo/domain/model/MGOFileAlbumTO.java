package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MGOFileAlbumTO {
    private Long id;
    private String name;

    private String coverArt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverArt() {
        return coverArt;
    }

    public void setCoverArt(String coverArt) {
        this.coverArt = coverArt;
    }
}
