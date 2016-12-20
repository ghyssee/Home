package be.home.common.database.sqlbuilder;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class Condition {
    String field1;
    Comparator comparator;
    String field2;

    Condition(String field1, Comparator comparator, String field2) {
        this.field1 = field1;
        this.comparator = comparator;
        this.field2 = field2;
    }
}
