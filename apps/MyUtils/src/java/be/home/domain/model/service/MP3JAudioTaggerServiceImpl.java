package be.home.domain.model.service;

import be.home.common.mp3.MP3Utils;
import be.home.common.utils.FileUtils;
import be.home.main.mezzmo.MP3TagChecker;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.*;

import org.jaudiotagger.tag.id3.*;
import org.jaudiotagger.tag.id3.framebody.AbstractID3v2FrameBody;
import org.jaudiotagger.tag.id3.framebody.FrameBodyDeprecated;
import org.jaudiotagger.tag.id3.framebody.FrameBodyUnsupported;
import org.jaudiotagger.tag.reference.ID3V2Version;

import java.io.IOException;
import java.util.List;

import static be.home.common.logging.LoggingConfiguration.getMainLog;

public class MP3JAudioTaggerServiceImpl implements MP3Service {

    private static final Logger log = getMainLog(MP3TagChecker.class);
    MP3File mp3File;
    Tag tag;
    ID3v24Tag idv24Tag;

    boolean warning = false;
    boolean save = false;

    private MP3JAudioTaggerServiceImpl() {

    }

    public MP3JAudioTaggerServiceImpl(String file) throws MP3Exception {

        TagOptionSingleton.getInstance().setId3v2PaddingWillShorten(true);
        TagOptionSingleton.getInstance().setID3V2Version(ID3V2Version.ID3_V24);
        TagOptionSingleton.getInstance().setRemoveTrailingTerminatorOnWrite(true);
        try {
            FileUtils.makeFileWriteable(file);
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
        this.tag = mp3File.getTagAndConvertOrCreateDefault();
        this.idv24Tag = mp3File.getID3v2TagAsv24();
        analyze();
    }

    public int getDuration() {
        int length = mp3File.getAudioHeader().getTrackLength();
        return length;
    }

    public boolean hasTag() {
        return mp3File.hasID3v2Tag() || mp3File.hasID3v1Tag();
    }

    @Override
    public String getArtist() {
        String artist = tag.getFirst(FieldKey.ARTIST);
        return artist;
    }

    @Override
    public String getGenre() {
        String genre = tag.getFirst(FieldKey.GENRE);
        return genre;
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

    public String getRatingAsString() {
        String rating = tag.getFirst(FieldKey.RATING);
        return rating;
    }

    @Override
    public boolean isCompilation() {
        String compilation = tag.getFirst(FieldKey.IS_COMPILATION);
        return "1".compareTo(compilation) == 0;
    }

    @Override
    public String getUrl() {
        //  WXXX:DISCOGS_ARTIST
        String url = tag.getFirst(FieldKey.URL_DISCOGS_ARTIST_SITE);
        return url;
    }

    @Override
    public String getEncoder() {
        // TENC
        String encoder = tag.getFirst(FieldKey.ENCODER);
        return encoder;
    }

    @Override
    public String getKey() {
        // TKEY
        String key = tag.getFirst(FieldKey.KEY);
        return key;
    }

    @Override
    public String getLyrics() {
        // USLT
        String lyrics = tag.getFirst(FieldKey.LYRICS);
        return lyrics;
    }

    @Override
    public String getAudioSourceUrl() {
        // WOAS
        String audioSourceUrl = null;
        if (mp3File.getID3v2Tag() != null) {
            List<TagField> tags = mp3File.getID3v2Tag().getFrame((ID3v24Frames.FRAME_ID_URL_SOURCE_WEB));
            TagField tagField = null;
            if (tags != null && tags.size() > 0) {
                tagField = tags.get(0);
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                audioSourceUrl = frame.getContent();
            }
        }
        return audioSourceUrl;
    }

    @Override
    public String getAudiofileUrl() {
        // WOAF
        return null;
    }

    @Override
    public String getArtistUrl() {
        // WOAR
        String artistUrl = tag.getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE);
        return artistUrl;
    }

    @Override
    public String getCommercialUrl() {
        String commercialUrl = tag.getFirst(FieldKey.URL_WIKIPEDIA_RELEASE_SITE);
        return commercialUrl;
    }

    @Override
    public String getPaymentUrl() {
        String paymentUrl = tag.getFirst(FieldKey.URL_WIKIPEDIA_ARTIST_SITE);
        return paymentUrl;
    }

    @Override
    public String getPublisherUrl() {
        String publisherUrl = tag.getFirst(FieldKey.URL_WIKIPEDIA_ARTIST_SITE);
        return publisherUrl;
    }

    @Override
    public String getRadiostationUrl() {
        String radioStationUrl = tag.getFirst(FieldKey.URL_WIKIPEDIA_ARTIST_SITE);
        return radioStationUrl;
    }

    @Override
    public String getDiscTotal() {
        String discTotal = tag.getFirst(FieldKey.DISC_TOTAL);
        return discTotal;
    }

    @Override
    public void setArtist(String artist) throws MP3Exception {
        this.tag.deleteField(FieldKey.ARTIST);
        //this.tag.deleteField(ID3v24FieldKey.ARTIST);
        if (artist != null) {
            try {
                this.tag.addField(FieldKey.ARTIST, artist);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setTitle(String title) throws MP3Exception {
        this.tag.deleteField(FieldKey.TITLE);
        //this.tag.deleteField(ID3v24FieldKey.TITLE);
        if (title != null) {
            try {
                this.tag.addField(FieldKey.TITLE, title);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setGenre(String genre) throws MP3Exception {
        // when genre is saved, it's replaced by id. Ex. Pop = 13
        // that's why we use frame to alter genre
        // update: use setWriteMp3GenresAsText to prevent that genre is replaced by id
        // Ex. Pop = 13
        // TOTEST
        TagOptionSingleton.getInstance().setWriteMp3GenresAsText(true);
        this.tag.deleteField(FieldKey.GENRE);
        //this.tag.deleteField(ID3v24FieldKey.GENRE);
        if (genre != null) {
            try {
                this.tag.addField(FieldKey.GENRE, genre);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
/*
        this.tag.deleteField(FieldKey.GENRE);
        if (genre != null){
            ID3v23Tag v23Tag = new ID3v23Tag();
            ID3v23Frame frame = v23Tag.createFrame(ID3v24Frames.FRAME_ID_GENRE);
            FrameBodyTCON framebody = (FrameBodyTCON) frame.getBody();
            framebody.setText(genre);
            this.tag.addFrame(frame);
        }
*/
    }

    @Override
    public void setTrack(String track) throws MP3Exception {
        this.tag.deleteField(FieldKey.TRACK);
        //this.tag.deleteField(ID3v24FieldKey.TRACK);
        if (track != null) {
            try {
                this.tag.addField(FieldKey.TRACK, track);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setDisc(String disc) throws MP3Exception {
        this.tag.deleteField(FieldKey.DISC_NO); //dos not delete if for example 1/0
        //this.tag.deleteField(ID3v24FieldKey.DISC_NO);
        if (disc != null) {
            try {
                this.tag.addField(FieldKey.DISC_NO, disc);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setAlbum(String album) throws MP3Exception {
        this.tag.deleteField(FieldKey.ALBUM);
        //this.tag.deleteField(ID3v24FieldKey.ALBUM);
        if (album != null) {
            try {
                this.tag.addField(FieldKey.ALBUM, album);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setAlbumArtist(String albumArtist) throws MP3Exception {
        this.tag.deleteField(FieldKey.ALBUM_ARTIST);
        //this.tag.deleteField(ID3v24FieldKey.ALBUM_ARTIST);
        if (albumArtist != null) {
            try {
                this.tag.addField(FieldKey.ALBUM_ARTIST, albumArtist);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setYear(String year) throws MP3Exception {
        this.tag.deleteField(FieldKey.YEAR);
        //this.tag.deleteField(ID3v24FieldKey.YEAR);

        if (year != null) {
            try {
                this.tag.addField(FieldKey.YEAR, year);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setComment(String comment) throws MP3Exception {
        this.tag.deleteField(FieldKey.COMMENT);
        //this.tag.deleteField(ID3v24FieldKey.COMMENT);
        if (comment != null) {
            try {
                this.tag.addField(FieldKey.COMMENT, comment);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setRating(int rating) throws MP3Exception {
        String str = "0";
        switch (rating) {
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
        //this.tag.deleteField(ID3v24FieldKey.RATING);
        try {
            this.tag.addField(FieldKey.RATING, str);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setCompilation(boolean compilation) throws MP3Exception {
        String comp = "0";
        if (compilation) {
            comp = "1";
        }
        try {
            tag.setField(FieldKey.IS_COMPILATION, comp);
        } catch (FieldDataInvalidException e) {
            throw new MP3Exception(e);
        }
    }

    @Override
    public void setDiscTotal(String discTotal) throws MP3Exception {
        this.tag.deleteField(FieldKey.DISC_TOTAL);
        //this.tag.deleteField(ID3v24FieldKey.DISC_TOTAL);
        if (discTotal != null) {

            try {
                this.tag.addField(FieldKey.DISC_TOTAL, discTotal);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }

        }
    }

    private void ClearAudioSourceUrl() {
        // WOAS
        cleanupTag(ID3v24Frames.FRAME_ID_URL_SOURCE_WEB);

        //if (audioSourceUrl != null){
//            ID3v24Frame newFrame = tag.createFrame(ID3v24Frames.FRAME_ID_URL_SOURCE_WEB);
//            newFrame.setContent(audioSourceUrl);
//            tag.setFrame(newFrame);
        //}
//        List<TagField> tags = tag.getFrame(ID3v24Frames.FRAME_ID_URL_SOURCE_WEB);
    }

    @Override
    public void cleanupTags() {
        cleanupTag(ID3v24Frames.FRAME_ID_URL_SOURCE_WEB); // WOAS
        cleanupTag(ID3v24Frames.FRAME_ID_COMMENT); // COMM
        cleanupTag(ID3v24Frames.FRAME_ID_USER_DEFINED_URL); // WXXX
        cleanupTag(ID3v24Frames.FRAME_ID_ENCODEDBY); // TENC
        cleanupTag(ID3v24Frames.FRAME_ID_INITIAL_KEY); // TKEY
        cleanupTag(ID3v24Frames.FRAME_ID_UNSYNC_LYRICS); // USLT
        cleanupTag(ID3v24Frames.FRAME_ID_URL_SOURCE_WEB); // WOAS
        cleanupTag(ID3v24Frames.FRAME_ID_URL_FILE_WEB);  // WOAF
        cleanupTag(ID3v24Frames.FRAME_ID_URL_ARTIST_WEB); // WOAR
        cleanupTag(ID3v24Frames.FRAME_ID_URL_COMMERCIAL); // WCOM
        cleanupTag(ID3v24Frames.FRAME_ID_URL_PAYMENT); // WPAY
        cleanupTag(ID3v24Frames.FRAME_ID_URL_PUBLISHERS); // WPUB
        cleanupTag(ID3v24Frames.FRAME_ID_URL_OFFICIAL_RADIO); // WORS
        cleanupTag(ID3v24Frames.FRAME_ID_RADIO_NAME); // TRSN
        cleanupTag(ID3v24Frames.FRAME_ID_MOOD); // TMOD
        cleanupTag(ID3v24Frames.FRAME_ID_RADIO_OWNER); // TRSO
        cleanupTag(ID3v24Frames.FRAME_ID_BPM); // TBPM
    }

    public void cleanupTag(String frameId) {
        //List<TagField> tags = this.tag.getFrame(frameId);
        if (this.mp3File.getID3v2Tag() != null) {
            List<TagField> tags = this.mp3File.getID3v2Tag().getFrame(frameId);
            TagField tagField = null;
            String value = null;
            if (tags != null && tags.size() > 0) {
                tagField = tags.get(0);
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                value = frame.getContent();
                if (value != null && value.compareToIgnoreCase(TAG_TO_DELETE) == 0) {
                    //tag.removeFrame(frameId);
                    this.mp3File.getID3v2Tag().removeFrame(frameId);
                }
            }
        }
    }

    public void cleanupTagOld(String frameId) {
        /*
        List<TagField> tags = this.tag.getFrame(frameId);
        TagField tagField = null;
        String value = null;
        if (tags != null && tags.size() > 0) {
            tagField = tags.get(0);
            ID3v24Frame frame = (ID3v24Frame) tagField;
            value = frame.getContent();
            if (value != null && value.compareToIgnoreCase(TAG_TO_DELETE) == 0){
                tag.removeFrame(frameId);
            }
        }*/
    }

    public void clearAlbumImage() {
        this.tag.deleteArtworkField();
    }

    public void commit() throws MP3Exception {
        //TagOptionSingleton.getInstance().setId3v2PaddingWillShorten(true);
        //this.mp3File.setID3v2Tag(tag);
        setComment("Test");
        //this.mp3File.
        this.mp3File.setID3v2Tag((AbstractID3v2Tag) tag);
        try {
            this.mp3File.save();
        } catch (IOException e) {
            throw new MP3Exception(e);
        } catch (TagException e) {
            throw new MP3Exception(e);
        }

    }

    public void convertInvalidFID3v2frameOld(AbstractID3v2Tag tag, String wrongId, String goodId, FieldKey fieldKey){
        if (tag.hasFrame(wrongId)) {
            System.out.println("problem in tag found");
            if (!tag.hasFrame(goodId)) {
                List<TagField> myList = tag.getFrame(wrongId);
                AbstractID3v2Frame frame = (AbstractID3v2Frame) myList.get(0);
                byte[] array = frame.getRawContent();
                String rawText = new String(array);
                rawText = rawText.replaceAll("^TDRC","");
                StringBuffer regex = new StringBuffer("[");
                for (int i=0; i < 8; i++) {
                    if (i > 0) {
                        regex.append("|");
                    }
                    char ascii = (char) i;
                    regex.append(ascii);
                }
                regex.append("]");
                rawText = rawText.replaceAll(regex.toString(), "");
                this.tag.deleteField(wrongId);
                this.tag.deleteField(goodId);
                try {
                    this.tag.addField(fieldKey, rawText);
                } catch (FieldDataInvalidException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                // just delete the redundant frame. Corresponding frame exists
                this.tag.deleteField(wrongId);
            }
        }

    }

    public void convertInvalidFID3v2frame(AbstractID3v2Tag tag, String wrongId, String goodId, FieldKey fieldKey){
        if (tag.hasFrame(wrongId)) {
            warning = true;
            log.warn("Invalid Frame found in MP3 " + mp3File.getFile());
            log.warn("Destination Tag Version: " + this.tag.getClass());
            log.warn("TagId: " + goodId + " / Invalid TagId: " + wrongId);
            log.warn("Original Tag Version: " + tag.getClass());

            if (!tag.hasFrame(goodId)) {
                String value = null;
                List<TagField> myList = tag.getFrame(wrongId);
                AbstractID3v2Frame frame = (AbstractID3v2Frame) myList.get(0);
                AbstractTagFrameBody frameBody = frame.getBody();
                log.warn("FrameBody: " + tag.getClass());
                if (frameBody instanceof FrameBodyDeprecated) {
                    FrameBodyDeprecated dep = (FrameBodyDeprecated) frameBody;
                    AbstractID3v2FrameBody oFrameBody = dep.getOriginalFrameBody();
                    value = oFrameBody.getUserFriendlyValue();
                }
                else if (frameBody instanceof FrameBodyUnsupported){
                    byte[] array = frame.getRawContent();
                    String rawText = new String(array);
                    rawText = rawText.replaceAll("^" + wrongId,"");
                    StringBuffer regex = new StringBuffer("[");
                    for (int i=0; i < 8; i++) {
                        if (i > 0) {
                            regex.append("|");
                        }
                        char ascii = (char) i;
                        regex.append(ascii);
                    }
                    regex.append("]");
                    rawText = rawText.replaceAll(regex.toString(), "");
                    value = rawText;
                }
                if (value != null) {
                    save = = true;
                    log.warn("Value found: " + value);
                    this.tag.deleteField(wrongId);
                    this.tag.deleteField(goodId);
                    try {
                        this.tag.addField(fieldKey, value);
                    } catch (FieldDataInvalidException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                // just delete the redundant frame. Corresponding id3v24 frame exists
                save = true;
                this.tag.deleteField(wrongId);
            }
        }

    }
    public void analyze() {
        /* there is a problem if tag is ID3v24, and the year tag is TYER instead of TDOR,
           the year is not fetched. This is a way to convert frame
         */
        AbstractID3v2Tag tag = this.mp3File.getID3v2Tag();
        if (tag instanceof ID3v24Tag ) {
            convertInvalidFID3v2frame(tag, ID3v23Frames.FRAME_ID_V3_TYER, ID3v24Frames.FRAME_ID_YEAR, FieldKey.YEAR);
        }
        if (tag instanceof ID3v23Tag ) {
            convertInvalidFID3v2frame(tag, ID3v24Frames.FRAME_ID_YEAR, ID3v23Frames.FRAME_ID_V3_TYER, FieldKey.YEAR);
        }


        /*
        while(tagFieldIterator.hasNext()) {
            TagField element = tagFieldIterator.next();
            String id = element.getId();
            System.out.println(id);
        }
        */
    }
}

