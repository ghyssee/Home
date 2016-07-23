package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 22/07/2016.
 */
public enum OrderByColumn {
    AlbumSeries(13), Title(7), RandomShuffle(38), NoOrder(0);

    private final int value;
    private OrderByColumn(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (OrderByColumn s : EnumSet.allOf(OrderByColumn.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static OrderByColumn get(String tmp) {
        return (OrderByColumn) lookup.get(tmp);
    }
}
