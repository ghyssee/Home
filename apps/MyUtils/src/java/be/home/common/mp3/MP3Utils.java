package be.home.common.mp3;

import com.mpatric.mp3agic.*;
import com.mpatric.mp3agic.ID3v24Tag;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 16/12/2016.
 */
public class MP3Utils {

    public static String getMP3Field(ID3v2 id3v2Tag, String field, String id){
        if (!StringUtils.isBlank(field)){
            id3v2Tag.clearFrameSet(id);
        }
        return field;
    }

    public static int getMP3Field(ID3v2 id3v2Tag, int field, String id){
            id3v2Tag.clearFrameSet(id);
            return field;
    }

    public static List getMP3Field(ID3v2 id3v2Tag, List field, String id){
        if (field != null && field.size() > 0) {
            id3v2Tag.clearFrameSet(id);
        }
        return field;
    }

    public static boolean getMP3Field(ID3v2 id3v2Tag, boolean field, String id){
            id3v2Tag.clearFrameSet(id);
        return field;
    }

    private static String getTagId(String id){
        Map map = new HashMap();
        map.put("TYER", "TDRC");
        map.put("IPLS", "TIPL");
        map.put("TORY", "TDOR");
        String newId = (String) map.get(id);
        if (newId == null) {
            return id;
        }
        return newId;

    }

    private static void addFrame(ID3v2 id3v2Tag, ID3v2FrameSet set){
        ID3v2FrameSet newFrame = new ID3v2FrameSet(set.getId());
        for (ID3v2Frame f : set.getFrames()) {
            newFrame.addFrame(f);
        }
        id3v2Tag.getFrameSets().put(getTagId(set.getId()), set);
        return;
    }

    public static ID3v2 getId3v2Tag(Mp3File mp3File) {
        if ( !mp3File.hasId3v2Tag()){
            return new ID3v24Tag();
        }
        ID3v2 id3v2 = mp3File.getId3v2Tag();
        if (id3v2 instanceof ID3v24Tag){
            return id3v2;
        }
        ID3v2 id3v2Tag = new ID3v24Tag();

        /*
        Map<String, ID3v2FrameSet> map = mp3File.getId3v2Tag().getFrameSets();
        for (ID3v2FrameSet set : map.values()) {
            ID3v2FrameSet newFrame = new ID3v2FrameSet(set.getId());
            for (ID3v2Frame f : set.getFrames()) {
                newFrame.addFrame(f);
            }
            //ID3v2Frame fr = new ID3v2Frame();
            //newFrame.addFrame();

            id3v2Tag.getFrameSets().put(set.getId(), newFrame);
        }
        */
        Map<String, ID3v2FrameSet> map = mp3File.getId3v2Tag().getFrameSets();
        for (ID3v2FrameSet set : map.values()) {
            if (set.getId().equals("PRIV")) {
                // remove Private TAG from the MP3
            }
            else if (set.getId().equals("NCON")) {
                // remove NCON TAG from the MP3
            }
            else if (set.getId().equals("TPIC") || set.getId().equals("PIC")) {
                id3v2Tag.setAlbumImage(id3v2.getAlbumImage(), id3v2.getAlbumImageMimeType());
            }
            else if (set.getId().equals("TALB") || set.getId().equals("TAL")) {
                id3v2Tag.setAlbum(id3v2.getAlbum());
            }
            else if (set.getId().equals("TENC") || set.getId().equals("TEN")) {
                id3v2Tag.setEncoder(id3v2.getEncoder());
            }
            else if (set.getId().equals("TYER") || set.getId().equals("TYE")) {
                id3v2Tag.setYear(id3v2.getYear());
            }
            else if (set.getId().equals("TCOP") || set.getId().equals("TCO")) {
                id3v2Tag.setCopyright(id3v2.getCopyright());
            }
            else if (set.getId().equals("TIT2") || set.getId().equals("TT2")) {
                id3v2Tag.setTitle(id3v2.getTitle());
            }
            else if (set.getId().equals("TPOS") || set.getId().equals("TPA")) {
                id3v2Tag.setPartOfSet(id3v2.getPartOfSet());
            }
            else if (set.getId().equals("TPE1") || set.getId().equals("TP1")) {
                id3v2Tag.setArtist(id3v2.getArtist());
            }
            else if (set.getId().equals("TPE2") || set.getId().equals("TP2")) {
                id3v2Tag.setAlbumArtist(id3v2.getAlbumArtist());
            }
            else if (set.getId().equals("TRCK") || set.getId().equals("TRK")) {
                id3v2Tag.setTrack(id3v2.getTrack());
            }
            else if (set.getId().equals("GEOB")) {
                /* In this frame any type of file can be encapsulated. After the header,
                'Frame size' and 'Encoding' follows 'MIME type' [MIME] represented as
                as a terminated string encoded with ISO 8859-1 [ISO-8859-1]. The
                filename is case sensitive and is encoded as 'Encoding'. Then follows
                    a content description as terminated string, encoded as 'Encoding'.
                            The last thing in the frame is the actual object. The first two
                    strings may be omitted, leaving only their terminations. MIME type is
                    always an ISO-8859-1 text string. There may be more than one "GEOB"
                    frame in each tag, but only one with the same content descriptor.
                 */
            }

            else if (set.getId().equals("COMM") || set.getId().equals("COM")) {
                // remove Comment Tag if value equals "0"
                // causes problem with this api
                String comment = id3v2.getComment().trim();
                if ("0".equals(comment)) {

                } else if (comment.startsWith("0000")) {
                    // ignore the comment
                } else {
                    id3v2Tag.setComment(comment);
                }
            }
            else if (set.getId().equals("TCOM") || set.getId().equals("TCO")) {
                id3v2Tag.setComposer(id3v2.getComposer());
            }
            else if (set.getId().equals("MCDI")) {
                /* remove TAG from the MP3
                 This frame is intended for music that comes from a CD, so that the CD
                can be identified in databases such as the CDDB
                */
            }
            else if (set.getId().equals("TXXX") || set.getId().equals("TXX")) {
                /*
               The text information frames are often the most important frames,
               containing information like artist, album and more. There may only be
               one text information frame of its kind in an tag. All text
               information frames supports multiple strings, stored as a null
               separated list, where null is reperesented by the termination code
               for the charater encoding. All text frame identifiers begin with "T".
               Only text frame identifiers begin with "T", with the exception of the
               "TXXX" frame. All the text information frames have the following
               format:

                 <Header for 'Text information frame', ID: "T000" - "TZZZ",
                 excluding "TXXX" described in 4.2.6.>
                 Text encoding                $xx
                 Information                  <text string(s) according to encoding>
            */

            }
            else {
                addFrame(id3v2Tag, set);
            }

        }

        //id3v2Tag.getFrameSets().putAll(id3v2.getFrameSets());
        /*
        id3v2Tag.setAlbumImage(id3v2.getAlbumImage(), id3v2.getAlbumImageMimeType());
        if (!StringUtils.isBlank(id3v2Tag.getComment())){
            id3v2Tag.clearFrameSet(AbstractID3v2Tag.ID_COMMENT);
            id3v2Tag.setComment(id3v2.getComment());
        }
        id3v2Tag.setAlbum(id3v2.getAlbum());
        id3v2Tag.setAlbumArtist(id3v2.getAlbumArtist());
        id3v2Tag.setArtist(id3v2.getArtist());
        id3v2Tag.setArtistUrl(id3v2.getArtistUrl());
        id3v2Tag.setAudiofileUrl(id3v2.getAudiofileUrl());
        id3v2Tag.setAudioSourceUrl(id3v2.getAudioSourceUrl());
        try {
            id3v2Tag.setBPM(id3v2.getBPM());
        }
        catch (NumberFormatException ex){
            // ignore this error
        }
        id3v2Tag.setChapters(id3v2.getChapters());
        id3v2Tag.setChapterTOC(id3v2.getChapterTOC());
        id3v2Tag.setCommercialUrl(id3v2.getCommercialUrl());
        id3v2Tag.setCompilation(id3v2.isCompilation());
        id3v2Tag.setComposer(id3v2.getComposer());
        id3v2Tag.setCopyright(id3v2.getCopyright());
        id3v2Tag.setDate(id3v2.getDate());
        id3v2Tag.setEncoder(id3v2.getEncoder());
        id3v2Tag.setGenre(id3v2.getGenre());
        try {
            id3v2Tag.setGenreDescription(id3v2.getGenreDescription());
        }
        catch (Exception e) {
            // do nothing
        }
        id3v2Tag.setGrouping(id3v2.getGrouping());
        id3v2Tag.setItunesComment(id3v2.getItunesComment());
        id3v2Tag.setKey(id3v2.getKey());
        id3v2Tag.setOriginalArtist(id3v2.getOriginalArtist());
        id3v2Tag.setPadding(id3v2.getPadding());
        id3v2Tag.setPartOfSet(id3v2.getPartOfSet());
        id3v2Tag.setPaymentUrl(id3v2.getPaymentUrl());
        id3v2Tag.setPublisher(id3v2.getPublisher());
        id3v2Tag.setPublisherUrl(id3v2.getPublisherUrl());
        id3v2Tag.setRadiostationUrl(id3v2.getRadiostationUrl());
        id3v2Tag.setTitle(id3v2.getTitle());
        id3v2Tag.setTrack(id3v2.getTrack());
        id3v2Tag.setUrl(id3v2.getUrl());
        id3v2Tag.setYear(id3v2.getYear());
        */
        mp3File.setId3v2Tag(id3v2Tag);

        return id3v2Tag;
    }

    public static boolean checkId3v2Tag(ID3v2 id3v2Tag){
        if (id3v2Tag.getArtist() == null ||
                id3v2Tag.getTitle() == null ||
                id3v2Tag.getAlbumArtist() == null ||
                id3v2Tag.getAlbum() == null){
            return false;
        }
        return true;

    }

}
