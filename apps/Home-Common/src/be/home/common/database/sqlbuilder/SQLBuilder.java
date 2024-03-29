package be.home.common.database.sqlbuilder;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 15/12/2016.
 */
public class SQLBuilder implements Cloneable, Serializable {

    private DatabaseTables mainTable;
    private List<Column> dbColumns = new ArrayList<>();
    private List<UpdateColumn> updateColumns = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();
    private List<OrderBy> orderByColumns = new ArrayList<>();
    private SQLTypes sqlType;
    private LimitBy limitBy = null;
    private List<Option> options = new ArrayList<>();
    private boolean distinct = false;
    private List<GroupBy> groupColumns = new ArrayList<>();
    private List<GroupCondition> groupConditions = new ArrayList<>();
    public static final Object PARAMETER = null;

    private boolean firstWhere = true;

    public List<Relation> getRelations () {
        return this.relations;
    }


    public SQLBuilder addTable(DatabaseTables table){
        this.mainTable = table;
        return this;
    }

    public Object clone() {
        Object object = null;
        try {
            object = super.clone();
        } catch (CloneNotSupportedException e) {
            // nothing to do
        }
        return object;
    }

    public SQLBuilder select(){
        sqlType = SQLTypes.SELECT;
        return this;
    }

    public SQLBuilder insert(){
        sqlType = SQLTypes.INSERT;
        return this;
    }

    public SQLBuilder update(){
        sqlType = SQLTypes.UPDATE;
        return this;
    }

    public SQLBuilder delete(){
        sqlType = SQLTypes.DELETE;
        return this;
    }

    public SQLBuilder addColumns (DatabaseTables table){

        for (DatabaseColumn column : table.columns()){
            if (isTableField(column)) {
                String field = getField(table.alias(), SQLFunction.NONE, column, null);
                //dbColumns.add(new Column(column, table.alias()));
                dbColumns.add(new Column(field));
            }
        }
        return this;
    }

    private boolean isTableField(DatabaseColumn column){
        boolean tableField = false;
        switch (column.getFieldType()) {
            case NORMAL:
                tableField = true;
                break;
            case PRIMARYKEY:
                tableField = true;
                break;
            case SEQUENCE:
                tableField = true;
                break;
        }
        return tableField;
    }


    public SQLBuilder addColumns (DatabaseTables table, SQLTypes sqlType){

        for (DatabaseColumn column : table.columns()){
            if (isTableField(column)) {
                if (column.getFieldType() != FieldType.SEQUENCE || sqlType != SQLTypes.INSERT) {
                    String field = getField(table.alias(), SQLFunction.NONE, column, null);
                    //dbColumns.add(new Column(column, table.alias()));
                    dbColumns.add(new Column(field));
                }
            }
        }
        return this;
    }

    private String getField(String tableAlias, SQLFunction function, DatabaseColumn dbColumn, String columnAlias){
        StringBuilder sb = new StringBuilder();
        sb
                .append (tableAlias)
                .append (".")
                .append ( dbColumn.getColumnName())
                .append( " AS ")
                .append ( columnAlias == null ? dbColumn.name() : columnAlias);
        return sb.toString();
    }


    public GroupCondition groupCondition (){
        SQLBuilder current = this;
        GroupCondition gc = new GroupCondition(current);
        return gc;
    }

    public SQLBuilder addGroupCondition (GroupCondition group){
        this.groupConditions.add(group);
        return this;
    }

    public SQLBuilder addColumn (String alias, SQLFunction function, DatabaseColumn dbColumn){
        StringBuilder sb = new StringBuilder();
        sb
                .append(function == SQLFunction.NONE ? "" : function.name() + "(")
                .append(alias + "." + dbColumn.getColumnName())
                .append(function.getParameters() > 0 ?
                        StringUtils.repeat(",?", function.getParameters()) :
                        "")
                .append(function == SQLFunction.NONE ? "" : ")")
                .append(" AS " + dbColumn.name());

        //dbColumns.add(new Column(sb.toString(), alias, columnAlias);
        dbColumns.add(new Column(sb.toString()));
        return this;
    }


    public SQLBuilder addColumn (String alias, DatabaseColumn column, String columnAlias) {

        String getField = getField(alias, SQLFunction.NONE, column, columnAlias);
        //dbColumns.add(new Column(column, alias, columnAlias));
        dbColumns.add(new Column(getField));
        return this;
    }


    public SQLBuilder addColumn (String alias, DatabaseColumn column) {
        return addColumn(alias, column, column.name());
    }

    public SQLBuilder updateColumn (DatabaseColumn column, Type type, Object object) {

        updateColumns.add(new UpdateColumn(column, SQLBuilderUtils.getValue(object), type));
        return this;
    }

    public SQLBuilder updateColumn (DatabaseColumn column, Type type) {

        return updateColumn(column, type, null);
    }

   public SQLBuilder addCondition (DatabaseColumn column, Comparator comparator, Object object){
        conditions.add(new Condition(column.getColumnName(), comparator, SQLBuilderUtils.getValue(object)));
        return this;
    }


   public SQLBuilder addCondition (String alias, SQLFunction function, DatabaseColumn dbColumn, Comparator comparator, Object object){
        StringBuilder sb = new StringBuilder();
        sb
                .append(function == SQLFunction.NONE ? "" : function.name() + "(")
                .append(alias + "." + dbColumn.getColumnName())
                .append(function.getParameters() > 0 ?
                        StringUtils.repeat(",?", function.getParameters()) :
                        "")
                .append(function == SQLFunction.NONE ? "" : ")");

        conditions.add(new Condition(sb.toString(), comparator, SQLBuilderUtils.getValue(object)));
        return this;
    }

    public SQLBuilder addCondition (String alias, DatabaseColumn dbColumn, Comparator comparator, Object object){
        conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator, SQLBuilderUtils.getValue(object)));
        return this;
    }

    public SQLBuilder addCondition (String alias, DatabaseColumn dbColumn, Comparator comparator){
        conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator, "?"));
        return this;
    }

    public SQLBuilder addCondition (String alias, DatabaseColumn dbColumn, Comparator comparator, String alias2, DatabaseColumn dbColumn2){
        conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator, alias2 + "." + dbColumn2.getColumnName()));
        return this;
    }

    public SQLBuilder addRelation( SQLBuilder sqlBuilder){
        this.relations.addAll(sqlBuilder.relations);
        return this;
    }

    public SQLBuilder addRelation (DatabaseTables table, DatabaseColumn dbColumn,
                                   DatabaseTables table2, DatabaseColumn dbColumn2){
        relations.add(new Relation(table, null, dbColumn, table2, dbColumn2));
        return this;
    }

    public SQLBuilder addRelation (DatabaseTables table, String alias1, DatabaseColumn dbColumn,
                                   DatabaseTables table2, DatabaseColumn dbColumn2){
        relations.add(new Relation(table, alias1, dbColumn, table2, dbColumn2));
        return this;
    }

    public SQLBuilder orderBy (String alias, DatabaseColumn column, SortOrder sortOrder) {
        orderByColumns.add(new OrderBy(alias, column, sortOrder));
        return this;
    }

    public SQLBuilder orderBy (DatabaseTables table, DatabaseColumn column, SortOrder sortOrder) {
        orderByColumns.add(new OrderBy(table, column, sortOrder));
        return this;
    }

    public SQLBuilder orderBy (String field) {
        orderByColumns.add(new OrderBy(field));
        return this;
    }

    public SQLBuilder addOption (String option) {
        options.add(new Option(option));
        return this;
    }

    public SQLBuilder addGroup (String alias, DatabaseColumn column){
        groupColumns.add(new GroupBy(alias, column));
        return this;
    }

    public SQLBuilder enableDistinct () {
        distinct = true;
        return this;
    }

    public SQLBuilder limitBy (int pos, int total) {
        limitBy = new LimitBy(pos, total);
        return this;
    }
    public SQLBuilder limitBy (int pos) {
        limitBy = new LimitBy(pos);
        return this;
    }
    public SQLBuilder limitBy () {
        limitBy = new LimitBy();
        return this;
    }


    private String renderSelect(){
        StringBuilder sb = new StringBuilder();
        sb
                .append("SELECT ")
                .append(distinct ? "DISTINCT " : "")
                .append(fields())
                .append(" FROM ")
                .append(this.mainTable.tableAlias())
                .append(relations())
                .append(conditions())
                .append(getGroupConditions())
                .append(groupBy())
                .append(orderBy())
                .append(limitByClause())
                .append(options());
        return sb.toString();
    }

    private String renderDelete(){
        StringBuilder sb = new StringBuilder();
        sb
                .append("DELETE FROM ")
                .append(this.mainTable)
                .append(conditions());
        return sb.toString();
    }


    private String renderInsert(){
        StringBuilder sb = new StringBuilder();
        sb
                .append("INSERT INTO ")
                .append(this.mainTable)
                .append(" (")
                .append(insertFields())
                .append(") VALUES (")
                .append(values())
                .append(")");
        return sb.toString();
    }


    private String renderUpdate(){
        StringBuilder sb = new StringBuilder();
        sb
                .append("UPDATE ")
                .append(this.mainTable)
                .append(" ")
                .append(updateFields())
                .append(conditions());
        return sb.toString();
    }

    public String render(){
        String sql = null;
        switch (this.sqlType){
            case SELECT:
                sql = renderSelect();
                break;
            case INSERT:
                sql = renderInsert();
                break;
            case UPDATE:
                sql = renderUpdate();
                break;
            case DELETE:
                sql = renderDelete();
                break;
        }
        return sql;
    }

    private String fields(){
        if (this.dbColumns.size() == 0){
            addColumns(this.mainTable);
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Column column : this.dbColumns){
            sb
                    .append(first ? "" : ", ")
                    .append(column.field);

            first = false;
        }
        return sb.toString();
    }

    private String insertFields(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (DatabaseColumn column : this.mainTable.columns()) {
            if (!isSequence(column)){
                sb
                        .append(first ? "" : ", ")
                        .append(column.getColumnName());
                first = false;
            }
        }
        return sb.toString();
    }

    private boolean isSequence(DatabaseColumn column){
        return (column.getFieldType() == FieldType.SEQUENCE);
    }

    private String updateFields(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (UpdateColumn column : this.updateColumns){
            String sep = "";
            switch (column.type){
                case VALUE :
                    break;
                case PARAMETER :
                    column.value = "?";
                    break;
                case FUNCTION :
                    break;
            }
            sb
                    .append(first ? "SET " : ", ")
                    .append(column.column.getColumnName())
                    .append("=")
                    .append(sep)
                    .append(column.value)
                    .append(sep);
            first = false;
        }
        return sb.toString();
    }

    private String values(){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (DatabaseColumn column : this.mainTable.columns()) {
            if (!isSequence(column)){
                sb
                        .append(first ? "" : ", ")
                        .append("?");
                first = false;
            }
        }
        return sb.toString();
    }

    public String conditions(){
        StringBuilder sb = new StringBuilder();
        for (Condition condition : this.conditions){
            sb
                    .append(firstWhere ? " WHERE " : " AND ")
                    .append(condition.field1)
                    .append(" ")
                    .append(condition.comparator.comparator())
                    .append(" ")
                    .append(condition.field2);
            firstWhere = false;
        }
        return sb.toString();
    }

    public String getChildConditions(List <Condition> list){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Condition condition : list){
            sb
                    .append(" ")
                    .append(first ? "" : condition.conditionType.name())
                    .append(" ")
                    .append(condition.field1)
                    .append(" ")
                    .append(condition.comparator.comparator())
                    .append(" ")
                    .append(condition.field2);
            first = false;
        }
        return sb.toString();
    }

    public String getGroupConditions(){
        boolean first = true;
        StringBuilder sb = new StringBuilder("");

        for (GroupCondition group : this.groupConditions){
            if (first){
                sb.append(firstWhere ? " WHERE " : " AND ")
                        .append("(");
                firstWhere = false;
            }
            sb
                    .append(first ? "" : " OR ")
                    .append("(")
                    .append(getChildConditions(group.conditions))
                    .append(")");
            first = false;
        }
        if (this.groupConditions.size() > 0){
            sb.append(")");
        }
        return sb.toString();
    }

    private String orderBy(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (OrderBy orderByColumn : this.orderByColumns){
            sb
                    .append(first ? " ORDER BY " : ", ")
                    .append(orderByColumn.orderField);
            first = false;
        }
        return sb.toString();
    }

    private String groupBy(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (GroupBy column : this.groupColumns){
            sb
                    .append(first ? " GROUP BY " : ", ")
                    .append(column.groupbyField);
            first = false;
        }
        return sb.toString();
    }

    private String limitByClause () {
        StringBuilder sb = new StringBuilder();
        if (this.limitBy != null) {
            sb
                    .append(" LIMIT ")
                    .append(this.limitBy.pos == null ? "?" : this.limitBy.pos)
                    .append(",")
                    .append(this.limitBy.total == null ? "?" : this.limitBy.total);
        }
        return sb.toString();
    }

    private String options () {
        StringBuilder sb = new StringBuilder();
        for (Option option : this.options){
            sb
                    .append(" ")
                    .append(option.option);
        }
        return sb.toString();
    }

    private String relations(){
        StringBuilder sb = new StringBuilder();
        for (Relation relation : this.relations){
            String alias = relation.alias1 == null ? relation.table1.alias() : relation.alias1;

            sb
                    .append(" INNER JOIN ")
                    .append(relation.table1.name() + " AS " + alias)
                    .append(" ON (")
                    .append(alias)
                    .append(".")
                    .append(relation.column1.getColumnName())
                    .append(" = ")
                    .append(relation.table2.alias())
                    .append(".")
                    .append(relation.column2.getColumnName())
                    .append(")");
        }
        return sb.toString();
    }

    public static SQLBuilder getInstance(){
        return new SQLBuilder();
    }

}
