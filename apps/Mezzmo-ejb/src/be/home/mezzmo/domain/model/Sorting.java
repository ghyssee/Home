package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 22/07/2016.
 */
public enum Sorting {
    Ascending(1), Descending(0);

    private final int value;
    Sorting(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (Sorting s : EnumSet.allOf(Sorting.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static Sorting get(String tmp) {
        return (Sorting) lookup.get(tmp);
    }
}
