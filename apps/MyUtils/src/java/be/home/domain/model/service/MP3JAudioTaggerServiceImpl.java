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
import org.jaudiotagger.tag.datatype.AbstractDataType;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.*;

import org.jaudiotagger.tag.id3.*;
import org.jaudiotagger.tag.id3.framebody.*;
import org.jaudiotagger.tag.reference.ID3V2Version;

import java.io.IOException;
import java.time.Year;
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
        TagOptionSingleton.getInstance().setWriteMp3GenresAsText(true);
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

        this(file, false);
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
    public String getBPM() {
        String bpm = tag.getFirst(FieldKey.BPM);
        return bpm;
    }
    @Override
    public void setArtist(String artist) throws MP3Exception {
        deleteField(FieldKey.ARTIST);
        if (StringUtils.isNotBlank(artist)) {
            try {
                this.tag.addField(FieldKey.ARTIST, artist);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setTitle(String title) throws MP3Exception {
        deleteField(FieldKey.TITLE);
       if (StringUtils.isNotBlank(title)) {
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
       deleteField(FieldKey.GENRE);
        if (StringUtils.isNotBlank(genre)) {
            try {
                this.tag.addField(FieldKey.GENRE, genre);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setTrack(String track) throws MP3Exception {
        deleteField(FieldKey.TRACK);
        if (StringUtils.isNotBlank(track)) {
            try {
                this.tag.addField(FieldKey.TRACK, track);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    private String getTagCode(FieldKey fieldKey){
        String tagCode = null;
        if (this.tag instanceof ID3v24Tag){
            ID3v24FieldKey id = ID3v24Frames.getInstanceOf().getId3KeyFromGenericKey(fieldKey);
            tagCode = id.getFrameId();
        }
        else if (this.tag instanceof ID3v23Tag) {
            ID3v23FieldKey id = ID3v23Frames.getInstanceOf().getId3KeyFromGenericKey(fieldKey);
            tagCode = id.getFrameId();
        }
        return tagCode;
    }

    private void deleteField(FieldKey fieldKey){
        String tagCode = getTagCode(fieldKey);
        if (tagCode == null){
            this.tag.deleteField(fieldKey);
        }
        else {
            this.tag.deleteField(tagCode);
        }
    }

    @Override
    public void setDisc(String disc) throws MP3Exception {
        deleteField(FieldKey.DISC_NO);
        if (StringUtils.isNotBlank(disc)) {
            try {
                this.tag.addField(FieldKey.DISC_NO, disc);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setAlbum(String album) throws MP3Exception {
        deleteField(FieldKey.ALBUM);
        if (StringUtils.isNotBlank(album)) {
            try {
                this.tag.addField(FieldKey.ALBUM, album);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setAlbumArtist(String albumArtist) throws MP3Exception {
        deleteField(FieldKey.ALBUM_ARTIST);
        if (StringUtils.isNotBlank(albumArtist)) {
            try {
                this.tag.addField(FieldKey.ALBUM_ARTIST, albumArtist);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setYear(String year) throws MP3Exception {
        deleteField(FieldKey.YEAR);
        if (StringUtils.isNotBlank(year)) {
            try {
                this.tag.addField(FieldKey.YEAR, year);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }
        }
    }

    @Override
    public void setComment(String comment) throws MP3Exception {
        deleteField(FieldKey.COMMENT);
        if (StringUtils.isNotBlank(comment)) {
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
        deleteField(FieldKey.RATING);
        try {
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
        deleteField(FieldKey.DISC_TOTAL);
        if (StringUtils.isNotBlank(discTotal)) {

            try {
                this.tag.addField(FieldKey.DISC_TOTAL, discTotal);
            } catch (FieldDataInvalidException e) {
                throw new MP3Exception(e);
            }

        }
    }

    @Override
    public void setBPM(String bpm) throws MP3Exception {
        deleteField(FieldKey.BPM);
        if (StringUtils.isNotBlank(bpm)) {
            try {
                this.tag.addField(FieldKey.BPM, bpm);
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

    private String getDescription(Tag tag, String frameId){
        String description = "";
        if (tag instanceof ID3v24Tag){
            description = ID3v24Frames.getInstanceOf().getValueForId(frameId);
        }
        else {
            description = ID3v23Frames.getInstanceOf().getValueForId(frameId);
        }
        return description;
    }

    public boolean isExcluded(Tag tag, String frameId) {
        ArrayList<FieldKey> tagsToExcludeForCleanup = new ArrayList<FieldKey>() {
            {
                add(FieldKey.TRACK);
                add(FieldKey.ARTIST);
                add(FieldKey.TITLE);
                add(FieldKey.ALBUM);
                add(FieldKey.ALBUM_ARTIST);
                add(FieldKey.IS_COMPILATION);
                add(FieldKey.GENRE);
                add(FieldKey.YEAR);
                add(FieldKey.DISC_NO);
                add(FieldKey.RATING);
                add(FieldKey.BPM);
                add(FieldKey.ISRC);
                add(FieldKey.COMMENT);
            }
        };
        // Exclude PRIV tags from the cleanup Procedure. There is a separate cleanup for Private Tags
        if (frameId.equalsIgnoreCase(ID3v24Frames.FRAME_ID_PRIVATE)){
            return true;
        }
        // Exclude UFID tags from the cleanup Procedure. There is a separate cleanup for UFID Tags
        else if (frameId.equalsIgnoreCase(ID3v24Frames.FRAME_ID_UNIQUE_FILE_ID)){
            return true;
        }
        // Exclude TXXX tags from the cleanup Procedure. There is a separate cleanup for TXXX Tags
       else if (frameId.equalsIgnoreCase(ID3v24Frames.FRAME_ID_USER_DEFINED_INFO)){
            return true;
        }
        // Exclude MCDI tags from the cleanup Procedure. There is a separate cleanup for MCDI Tags
        else if (frameId.equalsIgnoreCase(ID3v24Frames.FRAME_ID_MUSIC_CD_ID)){
            return true;
        }
        // Exclude GEOB tags from the cleanup Procedure. There is a separate cleanup for GEOB Tags
        else if (frameId.equalsIgnoreCase(ID3v24Frames.FRAME_ID_GENERAL_ENCAPS_OBJECT)){
            return true;
        }
        // Exclude WXXX tags from the cleanup Procedure. There is a separate cleanup for WXXX Tags
        else if (frameId.equalsIgnoreCase(ID3v24Frames.FRAME_ID_USER_DEFINED_URL)){
            return true;
        }
        else {
            for (FieldKey fieldKey : tagsToExcludeForCleanup) {
                String excludedFrameId = "";
                if (tag instanceof ID3v24Tag) {
                    ID3v24FieldKey id3FieldKey = ID3v24Frames.getInstanceOf().getId3KeyFromGenericKey(fieldKey);
                    excludedFrameId = id3FieldKey.getFrameId();
                } else {
                    ID3v23FieldKey id3FieldKey = ID3v23Frames.getInstanceOf().getId3KeyFromGenericKey(fieldKey);
                    excludedFrameId = id3FieldKey.getFrameId();
                }
                if (excludedFrameId.equalsIgnoreCase(frameId)) {
                    return true;
                }
            }
        }
        return false;

    }

    private boolean validYear(String year) {
        try {
            int iYear = Integer.parseInt(year);
            if (iYear <= 1900 || iYear > Year.now().getValue()) {
                return false;
            }
        } catch (NumberFormatException ex) {
                return false;
        }
        return true;
    }

    private void CheckYear() throws MP3Exception {
        String year = getYear();
        if (StringUtils.isBlank(year)){
            addWarning("YEAR is empty");
        }
        else {
            if (!validYear(year)){
                String subYear = year.substring(0, 4);
                if (validYear(subYear)) {
                    // save year with correct format
                    setYear(subYear);
                    addWarning("YEAR had an incorrect format: Old Format: " + year +
                            " => new format: " + subYear);
                    this.save = true;
                }
                else {
                    addWarning("YEAR is invalid: " + year);
                }
            }
        }
    }

    private void cleanupPrivateTags() {

        // Tag PRIV: no FieldKey for this tag, but code is the same for ID3v23 and ID3v24
        List<TagField> tagFields = this.tag.getFields(ID3v24Frames.FRAME_ID_PRIVATE);
        List<TagField> tagFieldsToKeep = new ArrayList();
        boolean saveTag = false;
        if (tagFields != null && tagFields.size() > 0){
            for (TagField tagField : tagFields){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyPRIV frameBody = (FrameBodyPRIV) frame.getBody();
                String owner = frameBody.getOwner();
                byte[] data = frameBody.getData();
                if (isCustomTagRemovable(owner)){
                    addWarning ("Cleanup of Private Frame: Owner=" + owner + " / " + "Data=" + be.home.common.utils.StringUtils.toHex(data));
                    saveTag = true;
                }
                else {
                    addWarning ("Private Frame: Owner=" + owner + " / " + "Data=" + be.home.common.utils.StringUtils.toHex(data));
                    tagFieldsToKeep.add(tagField);
                }
            }
            if (saveTag) {
                this.save = true;
                this.tag.deleteField(ID3v24Frames.FRAME_ID_PRIVATE);
                for (TagField tagField : tagFieldsToKeep) {
                    try {
                        this.tag.addField(tagField);
                    } catch (FieldDataInvalidException e) {
                        AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                        FrameBodyPRIV frameBody = (FrameBodyPRIV) frame.getBody();
                        addWarning("There was a problem saving custom tag " + frameBody.getOwner());
                    }
                }
            }
        }
    }

    private void cleanupUFIDTags() {

        // Tag PRIV: no FieldKey for this tag, but code is the same for ID3v23 and ID3v24
        List<TagField> tagFields = this.tag.getFields(ID3v24Frames.FRAME_ID_UNIQUE_FILE_ID);
        List<TagField> tagFieldsToKeep = new ArrayList();
        boolean saveTag = false;
        if (tagFields != null && tagFields.size() > 0){
            for (TagField tagField : tagFields){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyUFID frameBody = (FrameBodyUFID) frame.getBody();
                String owner = frameBody.getOwner();
                byte[] data = frameBody.getUniqueIdentifier();
                if (isCleanable(owner)){
                    addWarning ("Cleanup of UFID Frame: Owner=" + owner + " / " + "Data=" + be.home.common.utils.StringUtils.toHex(data));
                    saveTag = true;
                }
                else {
                    addWarning ("UFID Frame: Owner=" + owner + " / " + "Data=" + String.valueOf(data));
                    tagFieldsToKeep.add(tagField);
                }
            }
            if (saveTag) {
                this.save = true;
                this.tag.deleteField(ID3v24Frames.FRAME_ID_UNIQUE_FILE_ID);
                for (TagField tagField : tagFieldsToKeep) {
                    try {
                        this.tag.addField(tagField);
                    } catch (FieldDataInvalidException e) {
                        AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                        FrameBodyPRIV frameBody = (FrameBodyPRIV) frame.getBody();
                        addWarning("There was a problem saving custom tag " + frameBody.getOwner());
                    }
                }
            }
        }
    }

    private void cleanupWXXX() {

        // Tag WXXX: no FieldKey for this tag, but code is the same for ID3v23 and ID3v24
        List<TagField> tagFields = this.tag.getFields(ID3v24Frames.FRAME_ID_USER_DEFINED_URL);
        List<TagField> tagFieldsToKeep = new ArrayList();
        boolean saveTag = false;
        if (tagFields != null && tagFields.size() > 0){
            for (TagField tagField : tagFields){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyWXXX frameBody = (FrameBodyWXXX) frame.getBody();
                String urlLink = frameBody.getUrlLinkWithoutTrailingNulls().replaceAll("(\0|\f)", "");
                if (isCleanable(urlLink)){
                    addWarning ("Cleanup of WXXX Frame: URLLink=" + urlLink);
                    saveTag = true;
                }
                else {
                    addWarning ("WXXX Frame value found: URLLink=" + urlLink);
                    tagFieldsToKeep.add(tagField);
                }
            }
            if (saveTag) {
                this.save = true;
                this.tag.deleteField(ID3v24Frames.FRAME_ID_USER_DEFINED_URL);
                for (TagField tagField : tagFieldsToKeep) {
                    try {
                        this.tag.addField(tagField);
                    } catch (FieldDataInvalidException e) {
                        AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                        FrameBodyWXXX frameBody = (FrameBodyWXXX) frame.getBody();
                        addWarning("There was a problem saving WXXX tag " + frameBody.getUrlLinkWithoutTrailingNulls());
                    }
                }
            }
        }
    }

    private void cleanupGEOB() {
        // GEOB General encapsulated datatype
        String frameId = ID3v24Frames.FRAME_ID_GENERAL_ENCAPS_OBJECT;
        if (this.tag instanceof ID3v23Tag) {
            frameId = ID3v23Frames.FRAME_ID_V3_GENERAL_ENCAPS_OBJECT;
        }
        List<TagField> tagFields = this.tag.getFields(frameId);
        boolean saveTag = false;
        if (tagFields != null && tagFields.size() > 0){
            for (TagField tagField : tagFields){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyGEOB frameBody = (FrameBodyGEOB) frame.getBody();
                String description = frameBody.getDescription();
                AbstractDataType ab = frameBody.getObject("Data");
                String data = be.home.common.utils.StringUtils.toHex((byte[]) ab.getValue());
                addWarning ("Cleanup of GEOB Frame: Descriotion=" + description +
                " value=" + data);
                saveTag = true;
            }
            if (saveTag) {
                this.save = true;
                this.tag.deleteField(frameId);
            }
        }
    }

    private void cleanupTSRC() {
        // TSRC - The 'ISRC' frame should contain the International Standard Recording Code (ISRC) (12 characters).
        List<TagField> tagFields = this.tag.getFields(FieldKey.ISRC);
        boolean saveTag = false;
        if (tagFields != null && tagFields.size() > 0){
            for (TagField tagField : tagFields){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyTSRC frameBody = (FrameBodyTSRC) frame.getBody();
                String value = frameBody.getFirstTextValue();
                addWarning ("Cleanup of TSRC Frame: Value=" + value);
                saveTag = true;
            }
            if (saveTag) {
                this.save = true;
                this.tag.deleteField(getTagCode(FieldKey.ISRC));
            }
        }
    }

    private void cleanupMCDI() {

        // Tag MCDI: no FieldKey for this tag, but code is the same for ID3v23 and ID3v24
        List<TagField> tagFields = this.tag.getFields(ID3v24Frames.FRAME_ID_MUSIC_CD_ID);
        List<TagField> tagFieldsToKeep = new ArrayList();
        boolean saveTag = false;
        if (tagFields != null && tagFields.size() > 0){
            for (TagField tagField : tagFields){
                AbstractID3v2Frame frame = (AbstractID3v2Frame) tagField;
                FrameBodyMCDI frameBody = (FrameBodyMCDI) frame.getBody();
                AbstractDataType ab = frameBody.getObject("Data");
                String data = be.home.common.utils.StringUtils.toHex((byte[]) ab.getValue());
                addWarning ("Cleanup of MCDI Frame:  Data=" + data);
                saveTag = true;
            }
            if (saveTag) {
                this.save = true;
                this.tag.deleteField(ID3v24Frames.FRAME_ID_MUSIC_CD_ID);
            }
        }
    }
    private void checkBPM() throws MP3Exception {
        String bpm = getBPM();
        if (StringUtils.isNotBlank(bpm)){
            try {
                int iBPM = Integer.parseInt(bpm);
            }
            catch (NumberFormatException ex){
                addWarning("BPM: Delete Invalid value: " + bpm);
                this.save = true;
                setBPM(null);
            }
        }
    }

    @Override
    public void cleanupTags() {

        if (REMOVE_LENGTH_TAG){
            String tagCode = ID3v23Frames.FRAME_ID_V3_LENGTH;
            if (this.tag instanceof ID3v24Tag){
                tagCode = ID3v24Frames.FRAME_ID_LENGTH;
            }
            if (this.tag.hasField(tagCode)) {
                save = true;
                addWarning("Cleaning Length tag " + tagCode +
                        " (value=" + this.tag.getFirst(tagCode) + ")");
                this.tag.deleteField(tagCode);
            }
        }
        Iterator<TagField> tagFieldIterator = this.tag.getFields();
        while(tagFieldIterator.hasNext()) {
            TagField element = tagFieldIterator.next();
            String id = element.getId();
            if (!isExcluded(this.tag, id)){
                cleanupTag(id);
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
                if (!frame.isBinary()) {
                    value = frame.getContent();
                    if (frame.getBody() instanceof FrameBodyUnsupported){
                        addWarning("Unsupoorted Frame: " + frame.getId());
                    }
                    else if (StringUtils.isNotBlank(value)) {
                        if (isCleanable(value)) {
                            //tag.removeFrame(frameId);
                            save = true;
                            addWarning("Cleaning tag " + frameId + " " + getDescription(this.tag, frameId) +
                                    " (value=" + value + ")");
                            tag.deleteField(frameId);
                        } else {
                            // show value unless it contains specific words
                            if (!isExcludedWord(value)) {
                                // do not show values of BPM
                                    addWarning("Value found for tag: " + frameId + " " + getDescription(this.tag, frameId) +
                                            " (value=" + value + ")");
                            }
                        }
                    }
                }
                else {
                    addWarning("Binary frame found: : " + frameId + " " + getDescription(this.tag, frameId));
                }
            }
        }
    }

    private void cleanEmptyComment(){
        if (this.tag.hasField(FieldKey.COMMENT)  && this.save){
            if (StringUtils.isBlank(getComment())){
                addWarning("Empty Comment Tag deleted when there is something to save");
            }
        }
    }

    public void cleanCustomizedComments(){
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
                        if (!isExcludedWord(frameBody.getText())){
                            if (isCleanable(frameBody.getText())){
                                saveCommentTag = true;
                                save = true;
                                addWarning("Comment Tag Deleted: " + description + "=" + values.toString());
                            }
                            else {
                                addWarning("Comment Tag found: " + description + "=" + values.toString());
                                commentTagsToKeep.add(tagField);
                            }
                        }
                        else {
                            commentTagsToKeep.add(tagField);
                        }
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

    public boolean isCleanable(String value){

        for (String cleanupValue : cleanupWords){
            if ( Pattern.matches("(?s)" + cleanupValue.toUpperCase(), value.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isExcludedWord(String value){

        for (String excludedValue : excludeWords){
            if ( Pattern.matches("(?s)" + excludedValue.toUpperCase(), value.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    public void clearAlbumImage() {
        this.tag.deleteArtworkField();
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

    private void clearLanguage() {
        if (this.tag.hasField(FieldKey.LANGUAGE)) {
            addWarning("Language Tag deleted: Value = " + this.tag.getFirst(FieldKey.LANGUAGE));
            this.tag.deleteField(FieldKey.LANGUAGE);
            this.save = true;
        }
    }

    private void removeMultipleValues(FieldKey fieldKey){
        if (this.tag.getAll(fieldKey).size()> 1) {
            if (fieldKey == FieldKey.COVER_ART)
            {
                addWarning("Multiple Cover Arts found");
            }
            else if (fieldKey != FieldKey.COMMENT) {
                addWarning("Deleting Multiple values for " + fieldKey.name());
                String val = this.tag.getFirst(fieldKey);
                deleteField(fieldKey);
                try {
                    this.tag.addField(fieldKey, val);
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
                    deleteField(fieldKey);
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
        /* Note: tag is removed with checkCustomTags */
        return false;
    }

    private boolean isCustomTagRemovable(String value){

        for (String cleanupValue : customTags){
            if ( Pattern.matches(cleanupValue.toUpperCase(), value.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    private void checkCustomTags() throws MP3Exception {
        // cleans customized TXXX frames, like TXXX:compatible_brands
        // it does not check the value, only the description
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

    public void clearID3SpecificTag(AbstractID3v2Tag tag, String idToRemove, String tagType) {
        if (tag != null && tag.hasFrame(idToRemove)){
            addWarning("Remove " + tagType + ": " + idToRemove +
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
                deleteInvalidFrame("Unknown frame deleted: " , field.getId());
            }
            else if (this.tag instanceof ID3v23Tag) {
                /* remove non id23v23 frames */
                if (id3v23map.get(field.getId()) == null) {
                    deleteInvalidFrame("non id3v23 frame deleted: " , field.getId());
                }
            }
            else if (this.tag instanceof ID3v24Tag) {
                    /* remove non id23v24 frames */
                    if (id3v24map.get(field.getId()) == null) {
                        deleteInvalidFrame("non id3v24 frame deleted: " , field.getId());
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

        String tagType = "iD3v23";

        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_TDAT, tagType);
        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_INVOLVED_PEOPLE, tagType);
        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_TORY, tagType);
        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_EQUALISATION, tagType);
        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_TIME, tagType);
        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_TRDA, tagType);
        clearID3SpecificTag(tag, ID3v23Frames.FRAME_ID_V3_RELATIVE_VOLUME_ADJUSTMENT, tagType);


        /* remove ID3V24 specific frames */
        tagType = "iD3v24";

        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_INVOLVED_PEOPLE, tagType);
        clearID3SpecificTag(tag, ID3v24Frames. FRAME_ID_ORIGINAL_RELEASE_TIME, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_MOOD, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_MUSICIAN_CREDITS, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_SET_SUBTITLE, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_TAGGING_TIME, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_EQUALISATION2, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_TAGGING_TIME, tagType);
        clearID3SpecificTag(tag, ID3v24Frames.FRAME_ID_RELEASE_TIME, tagType);

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
        cleanupPrivateTags();
        cleanupUFIDTags();
        cleanupMCDI();
        cleanupTSRC();
        cleanupGEOB();
        cleanupWXXX();
        /* check for multiple values in a field */
        for (FieldKey checkKey : FieldKey.values()){
            // skipping COMMENT for now. Not sure what to do with ITUNES Comments
                removeMultipleValues(checkKey);
            if (checkKey != FieldKey.COMMENT) {
                removeEmptyField(checkKey);
            }
        }
        try {
            checkCustomTags();
            CheckYear();
            checkBPM();
        } catch (MP3Exception e) {
            // this should never occur
            throw new RuntimeException(e);
        }
        clearLanguage();
        clearInvalidFrame();
        cleanupTags();
        if (this.mp3File.getID3v2Tag() != null && this.mp3File.getID3v2Tag().getEmptyFrameBytes() > 0){
            this.save = true;
            addWarning("Empty Frame Bytes found. Saving the file...");
        }
        /* this should always be the last test
        it is only cleared when there are changes to be saved, unless switch to clean comment is set to true
        */
        cleanCustomizedComments();
        cleanEmptyComment();
        /*
        while(tagFieldIterator.hasNext()) {
            TagField element = tagFieldIterator.next();
            String id = element.getId();
            System.out.println(id);
        }
        */
    }
}

