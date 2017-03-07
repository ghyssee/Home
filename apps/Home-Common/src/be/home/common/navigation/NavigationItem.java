package be.home.common.navigation;

/**
 * Created by ghyssee on 2/03/2017.
 */
public class NavigationItem {

    public Integer level;
    public String url;

    public boolean active = false;

    public String description;

    public NavigationItem(String url, String description){
        this.level = null;
        this.url = url;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }


}
