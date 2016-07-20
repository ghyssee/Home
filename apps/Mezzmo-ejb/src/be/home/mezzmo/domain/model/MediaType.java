package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum MediaType {


    MP3(1), Video(4), Photo(2), Alles(7);

    private final int value;
    private MediaType(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (MediaType s : EnumSet.allOf(MediaType.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static MediaType get(String tmp) {
        return (MediaType) lookup.get(tmp);
    }


}
