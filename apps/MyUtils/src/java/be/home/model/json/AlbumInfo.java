package be.home.model.json;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class AlbumInfo {

    public AlbumInfo(){

    }

    public class Track {

        public String getTrack() {
            return track;
        }

        public void setTrack(String track) {
            this.track = track;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCd() {
            return cd;
        }

        public void setCd(String cd) {
            this.cd = cd;
        }

        public List<ExtraArtist> getExtraArtists() {
            return extraArtists;
        }

        public void setExtraArtists(List<ExtraArtist> extraArtists) {
            this.extraArtists = extraArtists;
        }

        public String track;
        public String artist;
        public String title;
        public String cd;
        public List <ExtraArtist> extraArtists = new ArrayList <ExtraArtist>();
        public String toCustomString() {
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                    .append("track", track)
                    .append("artist", artist)
                    .append("title", title)
                    .append("cd", cd)
                    .toString();
        }    }

    public class ExtraArtist {
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getExtraArtist() {
            return extraArtist;
        }

        public void setExtraArtist(String extraArtist) {
            this.extraArtist = extraArtist;
        }

        public String type;
        public String extraArtist;
    }

    private static String getMessage(Object o){

        if (o == null){
            return "Error";
        }
        else {
            return o.toString();
        }
    }

    public class Config {
        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public String getAlbumArtist() {
            return albumArtist;
        }

        public void setAlbumArtist(String albumArtist) {
            this.albumArtist = albumArtist;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public boolean isRenum() {
            return renum;
        }

        public void setRenum(boolean renum) {
            this.renum = renum;
        }

        public int getTrackSize() {
            return trackSize;
        }

        public void setTrackSize(int trackSize) {
            this.trackSize = trackSize;
        }

        public List<Track> getTracks() {
            return tracks;
        }

        public void setTracks(List<Track> tracks) {
            this.tracks = tracks;
        }

        public boolean isCompilation() {
            return compilation;
        }

        public void setCompilation(boolean compilation) {
            this.compilation = compilation;
        }

        public int getCurrentTrack() {
            return currentTrack;
        }

        public void setCurrentTrack(int currentTrack) {
            this.currentTrack = currentTrack;
        }

        public String album;
        public String albumArtist;
        public boolean compilation;

        public int total = 0;
        public boolean renum = false;
        public int trackSize;

        public int currentTrack = 0;

        public List <Track> tracks = new ArrayList <Track>();

        public void Config(){};

        public String toCustomString() {
            return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                    .append("album", album)
                    .append("albumArtist", albumArtist)
                    .append("compilation", compilation)
                    .append("total", total)
                    .toString();
        }

    }
}
