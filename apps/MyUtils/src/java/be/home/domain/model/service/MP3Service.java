package be.home.domain.model.service;

import org.jaudiotagger.tag.FieldDataInvalidException;

public interface MP3Service {

    public String getArtist();
    public String getTitle();
    public String getTrack();
    public String getDisc();
    public String getAlbum();
    public String getAlbumArtist();
    public String getYear();
    public String getComment();
    public int getRating();
    public String getStringRating();
    public boolean isCompilation();

    public  void setArtist(String artist) throws MP3Exception;
    public void setTitle(String title) throws MP3Exception;
    public void setTrack(String track) throws MP3Exception;
    public void setDisc(String disc) throws MP3Exception;
    public void setAlbum(String album) throws MP3Exception;
    public void setAlbumArtist(String albumArtist) throws MP3Exception;
    public void setYear(String year) throws MP3Exception;
    public void setComment(String comment) throws MP3Exception;
    public void setRating(int Rating) throws MP3Exception;
    public void setCompilation(boolean compilation) throws MP3Exception;

    public void commit() throws MP3Exception;
}
