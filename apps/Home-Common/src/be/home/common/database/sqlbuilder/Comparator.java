package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
public enum Comparator implements Serializable {
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQUALS("="),
    GREATER(">"),
    GREATEREQUALS(">="),
    LESS("<"),
    LESSEQUALS("<="),
    IN("IN"),
    NOT_IN("NOT IN");

    String comparator;

    Comparator(String s) {
        comparator = s;
    }

    public String comparator() {
        return comparator;
    }
}
