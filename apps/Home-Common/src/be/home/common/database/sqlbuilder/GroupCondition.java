package be.home.common.database.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 2/01/2017.
 */
public class GroupCondition {
    public List<Condition> conditions = new ArrayList<>();
    public SQLBuilder current;

    public GroupCondition(SQLBuilder current){
        this.current = current;
    }

    public GroupCondition add(String field1, Comparator comparator, String field2, ConditionType conditionType){
        this.conditions.add(new Condition(field1, comparator, field2, conditionType));
        return this;
    }
    public SQLBuilder close(){
        current.addGroupCondition(this);
        return current;
    }
}
