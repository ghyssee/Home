package be.home.common.mp3;

import com.mpatric.mp3agic.*;

import java.util.Map;

/**
 * Created by ghyssee on 16/12/2016.
 */
public class MP3Utils {

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
            */
            //id3v2Tag.getFrameSets().put(set.getId(), newFrame);
        id3v2Tag.getFrameSets().putAll(id3v2.getFrameSets());
        id3v2Tag.setAlbumImage(id3v2.getAlbumImage(), id3v2.getAlbumImageMimeType());
        id3v2Tag.setComment(id3v2.getComment());

        id3v2Tag.setAlbum(id3v2.getAlbum());
        id3v2Tag.setAlbumArtist(id3v2.getAlbumArtist());
        id3v2Tag.setArtist(id3v2.getArtist());
        id3v2Tag.setArtistUrl(id3v2.getArtistUrl());
        id3v2Tag.setAudiofileUrl(id3v2.getAudiofileUrl());
        id3v2Tag.setAudioSourceUrl(id3v2.getAudioSourceUrl());
        id3v2Tag.setBPM(id3v2.getBPM());
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
