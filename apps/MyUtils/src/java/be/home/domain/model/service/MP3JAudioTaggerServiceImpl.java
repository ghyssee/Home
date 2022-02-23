package be.home.domain.model.service;

import be.home.common.mp3.MP3Utils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import java.io.IOException;

public class MP3JAudioTaggerServiceImpl implements MP3Service {

    MP3File mp3File;
    ID3v24Tag tag;

    private MP3JAudioTaggerServiceImpl(){

    }

    public MP3JAudioTaggerServiceImpl(String file) throws MP3Exception {

        try {
            this.mp3File = new MP3File(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            throw new MP3Exception(e);
        } catch (ReadOnlyFileException e) {
            throw new MP3Exception(e);
        } catch (CannotReadException e) {
            throw new MP3Exception(e);
        } catch (InvalidAudioFrameException e) {
            throw new MP3Exception(e);
        }
        this.tag = mp3File.getID3v2TagAsv24();
    }

    @Override
    public String getArtist() {
        String artist = tag.getFirst(FieldKey.ARTIST);
        return artist;
    }

    @Override
    public String getTitle() {
        String title = tag.getFirst(FieldKey.TITLE);
        return title;
    }

    @Override
    public String getTrack() {
        String track = tag.getFirst(FieldKey.TRACK);
        return track;
    }
    @Override
    public String getDisc() {
        String disc = tag.getFirst(FieldKey.DISC_NO);
        return disc;
    }

    @Override
    public String getAlbum() {
        String album = tag.getFirst(FieldKey.ALBUM);
        return album;
    }

    @Override
    public String getAlbumArtist() {
        String albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
        return albumArtist;
    }
    @Override
    public String getYear() {
        String year = tag.getFirst(FieldKey.YEAR);
        return year;
    }

    @Override
    public String getComment() {
        String comment = tag.getFirst(FieldKey.COMMENT);
        return comment;
    }

    @Override
    public int getRating() {
        String Rating = tag.getFirst(FieldKey.RATING);
        MP3Utils mp3Utils = new MP3Utils();
        return mp3Utils.convertRating(Rating);
    }

    public String getStringRating() {
        String rating = tag.getFirst(FieldKey.RATING);
        return rating;
    }
    @Override
    public boolean isCompilation() {
        String compilation = tag.getFirst(FieldKey.IS_COMPILATION);
        return "1".compareTo(compilation) ==  0;
    }

    @Override
    public void setArtist(String artist) throws MP3Exception {
        this.tag.deleteField(FieldKey.ARTIST);
        try {
            this.tag.addField(FieldKey.ARTIST, artist);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setTitle(String title) throws MP3Exception {
        this.tag.deleteField(FieldKey.TITLE);
        try {
            this.tag.addField(FieldKey.TITLE, title);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setTrack(String track) throws MP3Exception {
        this.tag.deleteField(FieldKey.TRACK);
        try {
            this.tag.addField(FieldKey.TRACK, track);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setDisc(String disc) throws MP3Exception {
        this.tag.deleteField(FieldKey.DISC_NO);
        try {
            this.tag.addField(FieldKey.DISC_NO, disc);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setAlbum(String album) throws MP3Exception {
        this.tag.deleteField(FieldKey.ALBUM);
        try {
            this.tag.addField(FieldKey.ALBUM, album);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setAlbumArtist(String albumArtist) throws MP3Exception {
        this.tag.deleteField(FieldKey.ALBUM_ARTIST);
        try {
            this.tag.addField(FieldKey.ALBUM_ARTIST, albumArtist);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setYear(String year) throws MP3Exception {
        this.tag.deleteField(FieldKey.YEAR);
        try {
            this.tag.addField(FieldKey.ARTIST, year);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setComment(String comment) throws MP3Exception {
        this.tag.deleteField(FieldKey.COMMENT);
        try {
            this.tag.addField(FieldKey.COMMENT, comment);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setRating(int rating) throws MP3Exception {
        String str = "0";
        switch (rating){
            case 0:
                str = "0";
                break;
            case 1:
                str = "1";
                break;
            case 2:
                str = "64";
                break;
            case 3:
                str = "128";
                break;
            case 4:
                str = "196";
                break;
            case 5:
                str = "255";
                break;
            default:
                str = "0";
        }
        this.tag.deleteField(FieldKey.RATING);
        try {
            this.tag.addField(FieldKey.RATING,str);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setCompilation(boolean compilation) throws MP3Exception {
        String comp = "0";
        if (compilation){
            comp = "1";
        }
        try {
            tag.setField(FieldKey.IS_COMPILATION,comp);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    public void commit() throws MP3Exception {
        this.mp3File.setID3v2Tag(tag);
        try {
            this.mp3File.save();
        } catch (IOException e) {
            throw new MP3Exception(e);
        } catch (TagException e) {
            throw new MP3Exception(e);
        }

    }

}
