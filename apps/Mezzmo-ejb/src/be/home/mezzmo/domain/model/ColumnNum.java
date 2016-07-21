package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum ColumnNum {

    UserRating(27, ColumnType.Number),
    Title(7, ColumnType.String),
    AlbumName(55, ColumnType.String),
    AlbumArtist(56, ColumnType.String),
    ArtistActors(57, ColumnType.String),
    PlayCount(5, ColumnType.Number),
    Year(16, ColumnType.Number);

    private int value;
    private ColumnType columnType;
    private ColumnNum(int value, ColumnType number) {

        this.value = value;
        this.columnType = number;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    public ColumnType getColumnType() { return columnType;}

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
