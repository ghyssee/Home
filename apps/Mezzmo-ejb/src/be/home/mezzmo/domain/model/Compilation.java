package be.home.mezzmo.domain.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public enum Compilation {

    TRUE("1"), FALSE("0");

    private final String value;
    Compilation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
