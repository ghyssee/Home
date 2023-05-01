package be.home.domain.model.service;

import be.home.common.mp3.MP3Utils;
import be.home.common.utils.FileUtils;
import be.home.main.mezzmo.MP3TagChecker;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.*;

import org.jaudiotagger.tag.id3.*;
import org.jaudiotagger.tag.id3.framebody.*;
import org.jaudiotagger.tag.reference.ID3V2Version;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static be.home.common.logging.LoggingConfiguration.getMainLog;

public class MP3JAudioTaggerServiceImpl implements MP3Service {

    private static final Logger log = getMainLog(MP3TagChecker.class);

    final String GENRE_X = "GENRE_X";

    MP3File mp3File;
    Tag tag;
    ID3v24Tag idv24Tag;
    ArrayList<String> warnings = new ArrayList<String>();

    boolean warning = false;
    boolean save = false;

    String file;

    private MP3JAudioTaggerServiceImpl() {

    }

    public MP3JAudioTaggerServiceImpl(String file, boolean analyze
    ) throws MP3Exception {

        TagOptionSingleton.getInstance().setId3v2PaddingWillShorten(true);
//        TagOptionSingleton.getInstance().setOriginalSavedAfterAdjustingID3v2Padding(true);
        TagOptionSingleton.getInstance().setID3V2Version(ID3V2Version.ID3_V24);
        TagOptionSingleton.getInstance().setRemoveTrailingTerminatorOnWrite(true);
        try {
            FileUtils.makeFileWriteable(file);
            this.file = file;
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
        if (analyze) {
            analyze();
        }
    }

    public MP3JAudioTaggerServiceImpl(String file) throws MP3Exception {

        this(file, true);
    }

    public ArrayList<String> getWarnings() {
        return warnings;
    }

    private void addWarning(String warningMessage){
        warnings.add(warningMessage);
        this.warning = true;
        log.warn(warningMessage);
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag){
        this.tag = tag;
    }

    public String getFile(){
        return file;
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
        TagOptionSingleton.getInstance().setWriteMp3GenresAsText(true);
        this.tag.deleteField(FieldKey.GENRE);
        if (genre != null) {
            try {
                this.tag.addField(FieldKey.GENRE, genre);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
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
        try {
            //this.tag.addField(FieldKey.RATING, str);
            TagField tagField = this.tag.createField(FieldKey.RATING, str);
            AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
            FrameBodyPOPM framePop = (FrameBodyPOPM) frame.getBody();
            framePop.setEmailToUser("Windows Media Player 9 Series");
            this.tag.addField(tagField);
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
        cleanupTag(ID3v24Frames.FRAME_ID_PUBLISHER); // TPUB
        cleanupTag(ID3v24Frames.FRAME_ID_RADIO_NAME); // TRSN
        cleanupTag(ID3v24Frames.FRAME_ID_RADIO_OWNER); // TRSO
        cleanupTag(ID3v24Frames.FRAME_ID_MOOD); // TMOD
        cleanupTag(ID3v24Frames.FRAME_ID_BPM); // TBPM
        cleanupTag(ID3v24Frames.FRAME_ID_COMPOSER_SORT_ORDER_ITUNES); // TSOC
        cleanupTag(ID3v24Frames.FRAME_ID_ALBUM_ARTIST_SORT_ORDER_ITUNES); // TSOC
        cleanupTag(ID3v24Frames.FRAME_ID_COMPOSER); // TCOM
        cleanupTag(ID3v24Frames.FRAME_ID_COPYRIGHTINFO); // TCOP
        cleanupTag(ID3v24Frames.FRAME_ID_COMPOSER_SORT_ORDER_ITUNES); // TSOC
        cleanupTag(ID3v24Frames.FRAME_ID_ALBUM_SORT_ORDER); // TSOA
        cleanupTag(ID3v24Frames.FRAME_ID_ARTIST_SORT_ORDER); // TSO9
        cleanupTag(ID3v24Frames.FRAME_ID_MUSIC_CD_ID); // MCDI
        cleanupTag(ID3v24Frames.FRAME_ID_COMMERCIAL_FRAME); // COMR
        cleanupTag(ID3v24Frames.FRAME_ID_FILE_OWNER); // TOWN
        cleanupTag(ID3v24Frames.FRAME_ID_FILE_TYPE); // TFLT
        cleanupTag(ID3v24Frames.FRAME_ID_CONDUCTOR); // TPE3
        cleanupTag(ID3v24Frames.FRAME_ID_HW_SW_SETTINGS); // TSSE
        cleanupTag(ID3v24Frames.FRAME_ID_ISRC); // TSRC
        if (REMOVE_LENGTH_TAG){
            if (this.tag.hasField(ID3v24Frames.FRAME_ID_LENGTH)) {
                save = true;
                addWarning("Cleaning Length tag " + ID3v24Frames.FRAME_ID_LENGTH +
                        " (value=" + this.tag.getFirst(ID3v24Frames.FRAME_ID_LENGTH) + ")");
                this.tag.deleteField(ID3v24Frames.FRAME_ID_LENGTH);
            }
        }
    }

    public void cleanupTag(String frameId) {
        if (this.tag != null) {
            List<TagField> tags = this.tag.getFields(frameId);
            TagField tagField = null;
            String value = null;
            if (tags != null && tags.size() > 0) {
                tagField = tags.get(0);
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                value = frame.getContent();
                if (isCleanable(value)) {
                    //tag.removeFrame(frameId);
                    save = true;
                    addWarning("Cleaning tag " + frameId + " (value=" + value + ")");
                    tag.deleteField(frameId);
                }
            }
        }
    }

    public void cleanComment(){
        List<TagField> tags = this.tag.getFields(FieldKey.COMMENT);
        List<TagField> commentTagsToKeep = new ArrayList<TagField>();
        boolean saveCommentTag = false;
        if (tags != null) {
            if (tags.size() > 0) {
                for (TagField tagField : tags) {
                    AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                    FrameBodyCOMM frameBody = (FrameBodyCOMM) frame.getBody();
                    String description = frameBody.getDescription();
                    List<String> values = frameBody.getValues();
                    if (isCustomTagRemovable(description)) {
                        addWarning("Delete Comment Tag: " + description + "=" + values.toString());
                        saveCommentTag = true;
                        save = true;
                    } else if (StringUtils.isBlank(frameBody.getText())){
                        // empty Comment tag found. Skipping this for now
                        // needs to be investigated further
                        if (REMOVE_EMPTY_COMMENT || this.save){
                            // delete empty tag if there are other errors found
                            addWarning("Empty Comment Tag deleted: " + frameBody.getLongDescription().trim() + "=" + values.toString());
                            saveCommentTag = true;
                            save = true;
                        }
                    } else {
                        addWarning("Comment Tag found: " + description + "=" + values.toString());
                        commentTagsToKeep.add(tagField);
                    }
                }
            }
            else {
                // comment tag found, but is empty. Skipping for now
                // candidate for removal
            }
            if (saveCommentTag) {
                this.tag.deleteField(FieldKey.COMMENT);
                for (TagField tagField : commentTagsToKeep) {
                    try {
                        this.tag.addField(tagField);
                    } catch (FieldDataInvalidException e) {
                        AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                        FrameBodyCOMM frameBody = (FrameBodyCOMM) frame.getBody();
                        List<String> values = frameBody.getValues();
                        addWarning("There was a problem saving custom tag " + frameBody.getDescription() +
                                "=" + values.toString());
                    }
                }
            }
        }
    }

    private boolean isTagExcludedForCleanup(FieldKey fieldKey) {
        ArrayList<FieldKey> excludedCleanupList = new <String> ArrayList();
        excludedCleanupList.add(FieldKey.ARTIST);
        excludedCleanupList.add(FieldKey.ALBUM);
        excludedCleanupList.add(FieldKey.ALBUM_ARTIST);
        excludedCleanupList.add(FieldKey.TITLE);
        for (FieldKey excludedKey : excludedCleanupList){
            if (excludedKey != null && excludedKey == fieldKey) {
                return true;
            }
        }
        return false;
    }

    public boolean isCleanable(String value){
        ArrayList<String> cleanupList = new <String> ArrayList();
        cleanupList.add("^RJ/SNWTJE");
        cleanupList.add("mSm ?. ?[0-9]{1,4} ?Productions BV");
        cleanupList.add("(.*)Salvatoro(.*)");
        cleanupList.add("(.*)Scorpio(.*)");
        for (String cleanupValue : cleanupList){
            if ( Pattern.matches(cleanupValue.toUpperCase(), value.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public void clearAlbumImage() {
        this.tag.deleteArtworkField();
    }

    private void clearLanguage() {
        if (this.tag.hasField(FieldKey.LANGUAGE)) {
            addWarning("Language Tag deleted: Value = " + this.tag.getFirst(FieldKey.LANGUAGE));
            this.tag.deleteField(FieldKey.LANGUAGE);
            this.save = true;
        }
    }

    public void commit() throws MP3Exception {
        this.mp3File.setID3v2Tag((AbstractID3v2Tag) tag);
        try {
            this.mp3File.save();
        } catch (IOException e) {
            throw new MP3Exception(e);
        }
        catch (TagException e) {
            throw new MP3Exception(e);
        }

    }

    private void removeMultipleValues(FieldKey fieldKey){
        if (this.tag.getAll(fieldKey).size()> 1) {
            if (fieldKey != FieldKey.COMMENT) {
                addWarning("Deleting Multiple values for " + fieldKey.name());
                String val = this.tag.getFirst(fieldKey);
                this.tag.deleteField(fieldKey);
                try {
                    this.tag.setField(fieldKey, val);
                    save = true;
                    addWarning("Multiple values successfully deleted");
                } catch (FieldDataInvalidException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                // Skipping Multiple Comment value for now. Not sure what to do with iTunes Comments
                addWarning("Multiple Comment Field Values found");
            }
        }
    }

    private void removeEmptyField(FieldKey fieldKey){
        if (this.tag.hasField(fieldKey)) {
                String value = this.tag.getFirst(fieldKey);
                if (StringUtils.isBlank(value)) {
                    addWarning("Deleting empty value for " + fieldKey.name());
                    this.tag.deleteField(fieldKey);
                    save = true;
                }
        }
    }

    public boolean isWarning() {
        return warning;
    }

    public boolean isSave() {
        return save;
    }

    public boolean convertInvalidFID3v2frame(AbstractID3v2Tag tag, String wrongId, String goodId, FieldKey fieldKey){
        boolean wrongFrame = false;
        if (tag.hasFrame(wrongId)) {

            if (!this.tag.hasField(fieldKey) || StringUtils.isBlank(this.tag.getFirst(fieldKey))) {
                // no value found for the goodId in the converted frame
                warning = true;
                wrongFrame = true;

                addWarning("Invalid Frame found in MP3 " + mp3File.getFile());
                addWarning("Destination Tag Version: " + this.tag.getClass());
                addWarning("TagId: " + goodId + " / Invalid TagId: " + wrongId);
                addWarning("Original Tag Version: " + tag.getClass());

                String value = null;
                List<TagField> myList = tag.getFrame(wrongId);
                AbstractID3v2Frame frame = (AbstractID3v2Frame) myList.get(0);
                AbstractTagFrameBody frameBody = frame.getBody();
                addWarning("FrameBody: " + tag.getClass());
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
                else if (StringUtils.isNotBlank(tag.getFirst(wrongId))){
                    value = tag.getFirst(wrongId);
                }
                else {
                    addWarning("No Value found");
                }
                if (value != null) {
                    save = true;
                    addWarning("Value found: " + value);
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
                // do nothing. Original Frame does not contain the wrong Id
            }
        }
        if (tag.hasFrame(wrongId) && tag.hasFrame(goodId)) {
            // frame is correctly converted
            // just delete the redundant frame. Corresponding id3v24 frame exists
            save = true;
            addWarning("Remove double frame: " + wrongId);
            if (this.tag.hasField(wrongId)) {
                // normally, this should never occur. The frame is correctly converted, but just to be sure
                this.tag.deleteField(wrongId);
            }
            removeMultipleValues(fieldKey);
        }

        return wrongFrame;

    }

    private boolean checkCustomTagGenreX(String description, String value) throws MP3Exception {
        if (description != null && description.compareToIgnoreCase(GENRE_X) == 0) {
            String genre = this.getGenre();
            if (StringUtils.isBlank(genre)){
                addWarning ("Custom Genre found, no default genre filled in. Setting genre to " + value);
                this.setGenre(value);
            }
            else {
                addWarning ("Custom Genre found, but default genre is filled in. Custom: " + value + " => Default Genre: " + genre);
            }
            return true;
        }
        return false;
    }

    private boolean isCustomTagRemovable(String value){
        ArrayList<String> cleanupList = new <String> ArrayList();
        cleanupList.add("ARTISTS");
        cleanupList.add("DISCID");
        /* removing DISCOGS tags
           MP3s can be tagged with CUSTOM discogs tags via a plugin
           For now, we remove the discogs custom tags
         */
        cleanupList.add("^DISCOGS(.*)");
        /* Music Brainz Custom tags */
        cleanupList.add("^MUSICBRAINZ(.*)");
        cleanupList.add("^COUNTRY");
        cleanupList.add("^NOTES");
        cleanupList.add("^RATING");
        cleanupList.add("^AccurateRip(.*)");
        cleanupList.add("^PZTagEditor(.*)");
        cleanupList.add("^replaygain(.*)");
        cleanupList.add("^major_brand");
        cleanupList.add("^minor_version");
        cleanupList.add("^compatible_brands");
        cleanupList.add("^Rip date");
        cleanupList.add("^Catalog(.*)");
        cleanupList.add("^UPC");
        cleanupList.add("^FeeAgency");
        cleanupList.add("^XFade");
        cleanupList.add("^SongType");
        cleanupList.add("^Overlay");
        cleanupList.add("^SongRights");
        cleanupList.add("^Source");
        cleanupList.add("^Ripping tool");
        cleanupList.add("^Release(.*)");
        cleanupList.add("^Supplier");
        cleanupList.add("^ITUNES(.*)");
        cleanupList.add("^SETSUBTITLE");
        cleanupList.add("^(.*)date");
        cleanupList.add("^GN_ExtData");
        cleanupList.add("^Purchase Date");
        cleanupList.add("^GN_ExtData");
        cleanupList.add("^Content Rating");
        cleanupList.add("^iTunMOVI");
        cleanupList.add("^Media(.*)");
        cleanupList.add("^Play Gap");
        cleanupList.add("^TOTALTRACKS");
        cleanupList.add("^COVER(.*)");
        cleanupList.add("^TPW");
        cleanupList.add("^UPLOADER");
        cleanupList.add("^BARCODE");
        cleanupList.add("^PMEDIA");
        cleanupList.add("^Provider");
        cleanupList.add("^album(.*)");
        cleanupList.add("^(.*)artist(.*)");
        cleanupList.add("^Track(.*)");
        cleanupList.add("^MusicMatch(.*)");
        cleanupList.add("^Credits");
        cleanupList.add("^Aan ?Geboden ?Door");
        cleanupList.add("^Work");
        cleanupList.add("^comment");
        cleanupList.add("^INFO");
        cleanupList.add("^AMGID");
        cleanupList.add("^Engineer");
        cleanupList.add("^Encoder");
        cleanupList.add("^ENCODED?(.*)");
        cleanupList.add("^HISTORY");
        cleanupList.add("^WEBSTORE");
        cleanupList.add("^ENCODINGTIME");
        cleanupList.add("^Year");
        cleanupList.add("^Language");
        cleanupList.add("^Related");
        cleanupList.add("^iTunSMPB");
        cleanupList.add("^Style");
        cleanupList.add("^Tagging time");
        cleanupList.add("^PLine");
        cleanupList.add("^CT_GAPLESS_DATA");
        cleanupList.add("^CDDB(.*)");
        cleanupList.add("^last_played_timestamp");
        cleanupList.add("^added_timestamp");
        cleanupList.add("^play_count");
        cleanupList.add("^first_played_timestamp");
        cleanupList.add("^User defined text information");
        cleanupList.add("^Mp3gain(.*)");
        cleanupList.add("^ZN");
        cleanupList.add("^fBPM(.*)");
        cleanupList.add("^EpisodeID");
        cleanupList.add("^audiodata(.*)");
        cleanupList.add("^canseekontime");
        cleanupList.add("^framerate");
        cleanupList.add("^pmsg");
        cleanupList.add("^EpisodeID");
        cleanupList.add("^purl");
        cleanupList.add("^starttime");
        cleanupList.add("^totaldata(.*)");
        cleanupList.add("^totalduration");
        cleanupList.add("^totaldisc(.*)");
        cleanupList.add("^totaltrack(.*)");
        cleanupList.add("^videodata(.*)");
        cleanupList.add("^width");
        cleanupList.add("^duration");
        cleanupList.add("^height");
        cleanupList.add("^bytelength");
        cleanupList.add("^sourcedata");
        cleanupList.add("^ORGANIZATION");
        cleanupList.add("^T?V?EPISODE(.*)");
        cleanupList.add("^Key");
        cleanupList.add("^OrigDate");
        cleanupList.add("^OrigTime");
        cleanupList.add("^TimeReference");
        cleanupList.add("^Language(.*)");
        cleanupList.add("^EnergyLevel");
        cleanupList.add("^PERFORMER");
        cleanupList.add("^RIPPER");
        cleanupList.add("^SPDY");
        /* COMMENT descriptions */
        cleanupList.add("^iTunPGAP");
        cleanupList.add("^PLine");
        cleanupList.add("^iTunNORM");
        cleanupList.add("^MUSICMATCH_MOOD");
        cleanupList.add("^ITUNES_CD(.*)");
        /* end comment descriptions */

        for (String cleanupValue : cleanupList){
            if ( Pattern.matches(cleanupValue.toUpperCase(), value.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    private void checkCustomTags() throws MP3Exception {
        List<TagField> customTags = this.tag.getFields(ID3v24Frames.FRAME_ID_USER_DEFINED_INFO);
        List<TagField> customTagsToKeep = new ArrayList<TagField>();
        boolean saveCustomTags = false;
        if (customTags != null) {
            for (TagField tagField : customTags){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyTXXX frameBody = (FrameBodyTXXX) frame.getBody();
                String description = frameBody.getDescription();
                String value = frameBody.getText();
                if (isCustomTagRemovable(description)){
                    addWarning("Delete Custom Tag: " + description + "=" + value);
                    saveCustomTags = true;
                    save = true;
                }
                else if (checkCustomTagGenreX(description, value)){
                    addWarning("Delete Custom Tag: " + description + "=" + value);
                    saveCustomTags = true;
                    save = true;
                }
                else {
                    addWarning("Custom Tag found: " + description + "=" + value);
                    customTagsToKeep.add(tagField);
                }
            }
        }
        if (saveCustomTags){
            this.tag.deleteField(ID3v24Frames.FRAME_ID_USER_DEFINED_INFO);
            for (TagField tagField : customTagsToKeep){
                try {
                    this.tag.addField(tagField);
                } catch (FieldDataInvalidException e) {
                    AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                    FrameBodyTXXX frameBody = (FrameBodyTXXX) frame.getBody();
                    addWarning("There was a problem saving custom tag " + frameBody.getDescription() +
                            "=" + frameBody.getText());
                }
            }
        }
    }

    public void clearID3v23SpecificTag(AbstractID3v2Tag tag, String idToRemove) {
        if (tag.hasFrame(idToRemove)){
            addWarning("Remove ID3v23 tag: " + idToRemove +
            " value=(" + tag.getFirst(idToRemove) + ")");
            this.tag.deleteField(idToRemove);
            this.save = true;
        }
    }

    public void clearID3v24SpecificTag(AbstractID3v2Tag tag, String idToRemove) {
        if (tag.hasFrame(idToRemove)){
            addWarning("Remove ID3v24 tag: " + idToRemove +
                    " value=(" + tag.getFirst(idToRemove) + ")");
            this.tag.deleteField(idToRemove);
            this.save = true;
        }
    }

    private void deleteInvalidFrame(String error, String id){
        addWarning(error + id);
        this.tag.deleteField(id);
        this.save = true;
    }

    public void clearInvalidFrame() {
        Map id3v24map = ID3v24Frames.getInstanceOf().getIdToValueMap();
        Map id3v23map = ID3v23Frames.getInstanceOf().getIdToValueMap();
        Iterator<TagField> it = this.tag.getFields();
        while(it.hasNext()){
            TagField field = it.next();

            /* remove ids that are not an id23v23 or id23v24 tag */
            if (id3v24map.get(field.getId()) == null
               && id3v23map.get(field.getId()) == null) {
                deleteInvalidFrame("Unknown frame found: " , field.getId());
            }
            else if (this.tag instanceof ID3v23Tag) {
                /* remove non id23v23 frames */
                if (id3v23map.get(field.getId()) == null) {
                    deleteInvalidFrame("non id3v23 frame found: " , field.getId());
                }
            }
            else if (this.tag instanceof ID3v24Tag) {
                    /* remove non id23v24 frames */
                    if (id3v24map.get(field.getId()) == null) {
                        deleteInvalidFrame("non id3v24 frame found: " , field.getId());
                    }
            }
        }
    }

    public void analyze() {
        /* there is a problem if tag is ID3v24, and the year tag is TYER instead of TDOR,
           the year is not fetched. This is a way to convert frame{
         */
        AbstractID3v2Tag tag = this.mp3File.getID3v2Tag();

        /* remove ID3V23 specific frames */

        clearID3v23SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_TDAT);
        clearID3v23SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_INVOLVED_PEOPLE);
        clearID3v23SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_TORY);

        /* remove ID3V24 specific frames */

        clearID3v24SpecificTag(tag, ID3v24Frames.FRAME_ID_INVOLVED_PEOPLE);
        clearID3v24SpecificTag(tag, ID3v24Frames. FRAME_ID_ORIGINAL_RELEASE_TIME);
        clearID3v24SpecificTag(tag, ID3v24Frames.FRAME_ID_MOOD);
        clearID3v24SpecificTag(tag, ID3v24Frames.FRAME_ID_MUSICIAN_CREDITS);
        clearID3v24SpecificTag(tag, ID3v24Frames.FRAME_ID_SET_SUBTITLE);
        clearID3v24SpecificTag(tag, ID3v24Frames.FRAME_ID_TAGGING_TIME);

        if (this.tag instanceof ID3v23Tag){
            if (tag instanceof ID3v24Tag ) {
                if (convertInvalidFID3v2frame(tag, ID3v24Frames.FRAME_ID_YEAR, ID3v23Frames.FRAME_ID_V3_TYER, FieldKey.YEAR)) {
                    addWarning("ID3v24Tag -> ID3v23Tag, original ID3v24Tag contains an ID3v23Tag");
                }
            }
            else if (tag instanceof ID3v23Tag ) {
                if (convertInvalidFID3v2frame(tag, ID3v24Frames.FRAME_ID_YEAR, ID3v23Frames.FRAME_ID_V3_TYER, FieldKey.YEAR)){
                    addWarning("ID3v23Tag -> ID3v23Tag, but original contains frame of ID3v24Tag");
                }
            }
        }
        else if (this.tag instanceof ID3v24Tag) {
            if (tag instanceof ID3v24Tag) {
                if (convertInvalidFID3v2frame(tag, ID3v23Frames.FRAME_ID_V3_TYER, ID3v24Frames.FRAME_ID_YEAR, FieldKey.YEAR)){
                    addWarning("ID3v24Tag -> ID3v24Tag, but original contains frame of ID3v23Tag");
                }
            }
            if (tag instanceof ID3v23Tag) {
                if (convertInvalidFID3v2frame(tag, ID3v23Frames.FRAME_ID_V3_TYER, ID3v24Frames.FRAME_ID_YEAR, FieldKey.YEAR)){
                    addWarning("ID3v23Tag -> ID3v24Tag, original ID3v23Tag contains an ID3v24Tag");
                }
            }
        }
        /* check for multiple values in a field */
        for (FieldKey checkKey : FieldKey.values()){
            // skipping COMMENT for now. Not sure what to do with ITUNES Comments
                removeMultipleValues(checkKey);
            if (checkKey != FieldKey.COMMENT) {
                removeEmptyField(checkKey);
            }
        }
        cleanupTags();
        try {
            checkCustomTags();
        } catch (MP3Exception e) {
            // this should never occur
            throw new RuntimeException(e);
        }
        clearLanguage();
        clearInvalidFrame();
        /* this should always be the last test
        it is only cleared when there are changes to be saved, unless switch to clean comment is set to true
        */
        if (this.mp3File.getID3v2Tag().getEmptyFrameBytes() > 0){
            this.save = true;
            addWarning("Empty Frame Bytes found. Saving the file...");
        }

       cleanComment();

        /*
        while(tagFieldIterator.hasNext()) {
            TagField element = tagFieldIterator.next();
            String id = element.getId();
            System.out.println(id);
        }
        */
    }
}

