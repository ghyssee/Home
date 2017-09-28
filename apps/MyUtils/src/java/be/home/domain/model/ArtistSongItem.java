package be.home.domain.model;

/**
 * Created by ghyssee on 26/09/2017.
 */
public class ArtistSongItem {
    private boolean matched = false;
    private String artist;
    private String song;
    private Rules rule;
    private String ruleId;

    public ArtistSongItem() {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public boolean isMatched() {
        return matched;
    }

    public void enableMatched() {
        this.matched = true;
    }

    public Rules getRule() {
        return rule;
    }

    public void setRule(Rules rule) {
        this.rule = rule;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
