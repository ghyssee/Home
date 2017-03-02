package be.home.common.navigation;

/**
 * Created by ghyssee on 2/03/2017.
 */
public class NavigationItem {

    public int level;
    public String url;

    public String description;

    public NavigationItem(int level, String url, String description){
        this.level = level;
        this.url = url;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}
