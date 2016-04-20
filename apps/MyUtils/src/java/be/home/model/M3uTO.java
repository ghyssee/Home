package be.home.model;

import java.io.File;

/**
 * Created by ghyssee on 10/03/2015.
 */
public class M3uTO {

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR = 1;
    public static final int RENAME = 101;
    public static final int ERROR_BASE_SONG_INFO = 102;
    public static final int STATUS_BASE_OK = 100;
    public static final int MATCH_FOUND = 103;
    public static final int NO_MATCH_FOUND = 104;

    /* STATUS_BASE_OK: the song from the BASE_DIR has the correct structure <Song> <Artist> - <Title>
                       but does not appear in the ultratop 50 list
       ERROR_BASE_SONG_INFO: the song from the BASE_DIR does not have the correct structure
       RENAME: the song from the BASE_DIR has the correct structure
               but track number does not match the track number in the ultratop 50 List
       STATUS_ERROR: the song from the <YearList> file does not have the correct structure
       NO_MATCH_FOUND: everything is ok with the songs, but no match could be found in the year list and
                       the base folder for the ultratop song
     */

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOriginalLine() {
        return originalLine;
    }

    public String getM3uSong() {
        return m3uSong;
    }

    public void setM3uSong(String m3uSong) {
        this.m3uSong = m3uSong;
    }

    public void setOriginalLine(String originalLine) {
        this.originalLine = originalLine;
    }

    public File getMp3File() {
        return mp3File;
    }

    public void setMp3File(File mp3File) {
        this.mp3File = mp3File;
    }

    private String song;
    private String artist;
    private String line;
    private String track;
    private String errorMessage;
    private int status;
    private String originalLine;
    private String m3uSong;
    private File mp3File;

}
