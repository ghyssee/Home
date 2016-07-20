package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum LimitType {

    Items(0), Min(1), Hours(2);

    private final int value;
    private LimitType(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (LimitType s : EnumSet.allOf(LimitType.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static LimitType get(String tmp) {
        return (LimitType) lookup.get(tmp);
    }


}
