package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum ColumnNum {

    UserRating(27), Title(7), AlbumName(55), AlbumArtist(56), ArtistActors(57);

    private final int value;
    private ColumnNum(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (ColumnNum s : EnumSet.allOf(ColumnNum.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static ColumnNum get(String tmp) {
        return (ColumnNum) lookup.get(tmp);
    }

}
