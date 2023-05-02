package be.home.common.mp3;

import be.home.domain.model.service.MP3Service;
import com.mpatric.mp3agic.*;
import com.mpatric.mp3agic.ID3v24Tag;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 16/12/2016.
 */
public class MP3Utils {

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

        mp3File.setId3v2Tag(id3v2Tag);

        return id3v2Tag;
    }

    public static boolean checkId3v2Tag(MP3Service mp3Service){
        if (mp3Service.getArtist() == null ||
                mp3Service.getTitle() == null ||
                mp3Service.getAlbumArtist() == null ||
                mp3Service.getAlbum() == null){
            return false;
        }
        return true;

    }

    public int convertRating (String rating) {
        int rat = 0;
        if (StringUtils.isNotBlank(rating)){
            rat = Integer.valueOf(rating);
        }
        return convertRating(rat);
    }

    public int convertRating (int rating){
        int stars = 0;
        if (rating >= 1 && rating < 63){
            stars = 1;
        }
        else if (rating >= 64 && rating < 128){
            stars = 2;
        }
        else if (rating >= 128 && rating < 196){
            stars = 3;
        }
        else if (rating >= 196 && rating < 255){
            stars = 4;
        }
        else if (rating == 255){
            stars = 5;
        }
        return stars;
    }

    public int getRatingOld(ID3v2 id3v2Tag){
        int rating = 0;
        ID3v2FrameSet frameSet = id3v2Tag.getFrameSets().get("POPM");
        if (frameSet != null) {
            ID3v2Frame frame = frameSet.getFrames().get(0);
            byte[] array = frame.getData();
            if (array.length > 5) {
                if (array[array.length - 1] == 0) {
                    // find first non null character
                    for (int i = array.length - 1; i > 0; i--) {
                        if (array[i] != 0) {
                            byte rat = array[i];
                            rating = rat & 0xFF; // mask off the sign bits
                            break;
                        }
                    }
                } else if (array[array.length - 2] == 0) {
                    byte rat = array[array.length - 1];
                    rating = rat & 0xFF; // mask off the sign bits
                }
            }
        }
        return rating;
    }

    public static long getDurationOld(Mp3File mp3File) {
        BigDecimal d = new BigDecimal(mp3File.getEndOffset());
        d = d.subtract(new BigDecimal(mp3File.getStartOffset()));
        d = d.multiply(new BigDecimal(8));
        BigDecimal length = new BigDecimal(mp3File.getLength());
        length = length.divide(new BigDecimal(mp3File.getEndOffset()), 8, RoundingMode.HALF_UP);
        if (length.doubleValue() > 1.1){
            // set to 1.01 09 Joeri Fransen - Ya 'Bout To Find Ou
            //set to 1.001 02 Meat Loaf - Paradise By The Dashboard Light.mp3
            // set to 1.008 De Nostalgie 1000 Van 2016\0001-0100\0077 Queen - Radio Ga Ga.mp3
            // set to 1.1 Nostalgie - De 890 Van 80 Of 90 (2016)/ 245 Vaya Con Dios - Heading For A Fall.mp3
            d = new BigDecimal(mp3File.getLength());
            d = d.subtract(new BigDecimal(mp3File.getStartOffset()));
            d = d.multiply(new BigDecimal(8));
        }
        BigDecimal kbps = new BigDecimal(mp3File.getBitrate());
        if (mp3File.isVbr()) {
            //kbps = kbps.subtract(new BigDecimal(0.5));
            kbps = kbps.multiply(new BigDecimal(1000));
        } else {
            kbps = kbps.multiply(new BigDecimal(1000));
        }
        //long secs = (long) (Math.round((d / kbps)));
        d = d.divide(kbps, 4, RoundingMode.HALF_UP);
        return d.longValue();
    }
}
