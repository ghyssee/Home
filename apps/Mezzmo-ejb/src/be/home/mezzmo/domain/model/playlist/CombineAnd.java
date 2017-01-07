package be.home.mezzmo.domain.model.playlist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum CombineAnd {

    AND(1), OR(0);

    private final int value;
    CombineAnd(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (CombineAnd s : EnumSet.allOf(CombineAnd.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static CombineAnd get(String tmp) {
        return (CombineAnd) lookup.get(tmp);
    }

}
