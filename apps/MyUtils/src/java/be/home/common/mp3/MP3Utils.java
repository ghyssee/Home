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
        Map<String, ID3v2FrameSet> map = mp3File.getId3v2Tag().getFrameSets();
        for (ID3v2FrameSet set : map.values()) {
            ID3v2FrameSet newFrame = new ID3v2FrameSet(set.getId());
            for (ID3v2Frame f : set.getFrames()) {
                newFrame.addFrame(f);
            }
            id3v2Tag.getFrameSets().put(set.getId(), newFrame);
        }
        return id3v2Tag;
    }

}
