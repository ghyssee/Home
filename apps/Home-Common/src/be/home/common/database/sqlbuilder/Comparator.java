package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
public enum Comparator implements Serializable {
    LIKE("LIKE"),
    EQUALS("="),
    GREATER(">"),
    GREATEREQUALS(">="),
    LESS("<"),
    LESSEQUALS("<="),
    IN("IN");

    String comparator;

    Comparator(String s) {
        comparator = s;
    }

    public String comparator() {
        return comparator;
    }
}
