package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum ColumnType {

    String(1), Number(2);

    private final int value;
    private ColumnType(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (ColumnType s : EnumSet.allOf(ColumnType.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static ColumnType get(String tmp) {
        return (ColumnType) lookup.get(tmp);
    }
}
