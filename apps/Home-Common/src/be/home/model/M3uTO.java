package be.home.model;

import java.io.File;

/**
 * Created by ghyssee on 10/03/2015.
 */
public class M3uTO {

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public M3uTO(){
    }

    public M3uTO (String line, String song, String artist){
        this.song = song;
        this.artist = artist;
        this.line = line;
    }

    public M3uTO (String line, String track, String song, String artist){
        this.song = song;
        this.artist = artist;
        this.line = line;
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getM3uSong() {
        return m3uSong;
    }

    public void setM3uSong(String m3uSong) {
        this.m3uSong = m3uSong;
    }


    private String song;
    private String artist;
    private String line;
    private String track;
    private int status;
    private String m3uSong;
    private boolean matched;

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}
