package be.home.common.database.sqlbuilder;

/**
 * Created by Gebruiker on 20/12/2016.
 */
public enum Comparator {
    LIKE("LIKE"),
    EQUALS("="),
    GREATER(">"),
    GREATEREQUALS(">="),
    LESSEQUALS("<=");

    String comparator;

    Comparator(String s) {
        comparator = s;
    }

    public String comparator() {
        return comparator;
    }
}
