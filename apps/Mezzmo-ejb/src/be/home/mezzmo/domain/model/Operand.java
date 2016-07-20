package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum Operand {

    Contains(2), IsGreaterThan(4), IsInTheRangeOf(6), Is(1), IsNot(3),
    IsLessThan(5), DoesNotContain(9), DoesNotEndWith(18), DoesNotStartWith(17),
    StartsWith(7);

    private final int value;
    private Operand(int value) {
        this.value = value;
    }
    private static final Map lookup = new HashMap();

    public int getValue() {
        return value;
    }

    // Populate the lookup table on loading time
    static {
        for (Operand s : EnumSet.allOf(Operand.class))
            lookup.put(s.name(), s);
    }

    // This method can be used for reverse lookup purpose
    public static Operand get(String tmp) {
        return (Operand) lookup.get(tmp);
    }

}
