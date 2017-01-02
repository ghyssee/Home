package be.home.common.database.sqlbuilder;

import java.io.Serializable;

/**
 * Created by Gebruiker on 20/12/2016.
 */
class Condition implements Serializable{
    String field1;
    Comparator comparator;
    String field2;
    ConditionType conditionType;

    Condition(String field1, Comparator comparator, String field2) {
        this.field1 = field1;
        this.comparator = comparator;
        this.field2 = field2;
        this.conditionType = ConditionType.AND;
    }

    Condition(String field1, Comparator comparator, String field2, ConditionType conditionType) {
        this.field1 = field1;
        this.comparator = comparator;
        this.field2 = field2;
        this.conditionType = conditionType;
    }
}
