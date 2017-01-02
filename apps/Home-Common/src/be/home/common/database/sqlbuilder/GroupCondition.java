package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;

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

    public GroupCondition add(String alias, DatabaseColumn dbColumn, Comparator comparator, String alias2, DatabaseColumn dbColumn2, ConditionType conditionType){
        this.conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator,
                alias2 + "." + dbColumn2.getColumnName(), conditionType));
        return this;
    }
    public GroupCondition add(String alias, DatabaseColumn dbColumn, Comparator comparator, Object object, ConditionType conditionType){
        this.conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator,
                SQLBuilderUtils.getValue(object), conditionType));
        return this;
    }
    public SQLBuilder close(){
        current.addGroupCondition(this);
        return current;
    }
}
