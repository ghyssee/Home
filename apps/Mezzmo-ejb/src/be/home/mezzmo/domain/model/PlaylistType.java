package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 18/07/2016.
 */
public enum PlaylistType {
    NORMAL(16), EXTERNAL(32), SMART(64), FOLDER(128);

    private final int value;
    PlaylistType(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (PlaylistType s : EnumSet.allOf(PlaylistType.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static PlaylistType get(String tmp) {
        return (PlaylistType) lookup.get(tmp);
    }


}
