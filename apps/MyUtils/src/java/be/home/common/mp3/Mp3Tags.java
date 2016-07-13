package be.home.common.mp3;

import be.home.domain.model.MP3Helper;
import be.home.model.AlbumInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ghyssee on 13/07/2016.
 */
public enum Mp3Tags {

    TRACK {
        @Override
        public void method(AlbumInfo.Track track, String item, boolean duration) {
            item = removeDuration(item, duration);
            track.track = StringUtils.leftPad(item, 2, "0");
        }
    },
    ARTIST {
        @Override
        public void method(AlbumInfo.Track track, String item, boolean duration) {

            item = removeDuration(item, duration);
            track.artist = item;
        }
    },
    TITLE {
        @Override
        public void method(AlbumInfo.Track track, String item, boolean duration) {
            item = removeDuration(item, duration);
            track.title = item;
        }
    }; // note the semi-colon after the final constant, not just a comma!

    public String removeDuration(String item, boolean duration) {
        if (duration) {
            item = MP3Helper.getInstance().removeDurationFromString(item);
        }
        return item;
    }


    public abstract void method(AlbumInfo.Track track, String item, boolean duration); // could also be in an interface that MyEnum implements
}
