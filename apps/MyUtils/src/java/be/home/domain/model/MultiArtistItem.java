package be.home.domain.model;

/**
 * Created by ghyssee on 28/09/2017.
 */
public class MultiArtistItem {
    private String id;
    private String name;

    MultiArtistItem(){

    }

    MultiArtistItem(String name){
        this.name = name;
    }

    MultiArtistItem(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
