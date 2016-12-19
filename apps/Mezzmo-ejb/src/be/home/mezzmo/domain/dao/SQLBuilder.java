package be.home.mezzmo.domain.dao;

import be.home.common.database.DatabaseColumn;
import be.home.common.database.FieldType;
import be.home.mezzmo.domain.dao.jdbc.SQLFunction;
import be.home.mezzmo.domain.dao.jdbc.TablesEnum;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 15/12/2016.
 */
public class SQLBuilder {

    private TablesEnum mainTable;
    private List<Column> dbColumns = new ArrayList<>();
    private List<UpdateColumn> updateColumns = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();
    private List<OrderBy> orderByColumns = new ArrayList<>();
    private SQLTypes sqlType;
    private LimitBy limitBy = null;
    private List<Option> options = new ArrayList<>();
    private boolean distinct = false;

    public enum Comparator {
        LIKE ("LIKE"),
        EQUALS ("="),
        GREATER (">");

        String comparator;

        Comparator(String s) {
            comparator = s;
        }

        public String comparator(){
            return comparator;
        }
    }

    public enum SORTORDER {
        ASC,
        DESC
    }

    public enum Type {
        VALUE,
        FUNCTION,
        PARAMETER
    }

    public enum SQLTypes {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
    }

    private class Option {
        String option;

        Option(String option){
            this.option = option;
        }
    }

    private class Column {
        DatabaseColumn column;
        String dbAlias;
        String columnAlias;

        Column(DatabaseColumn column, String alias){
            this.column = column;
            this.dbAlias = alias;
        }

        Column(DatabaseColumn column, String alias, String columnAlias){
            this.column = column;
            this.dbAlias = alias;
            this.columnAlias = columnAlias;
        }
    }

    private class UpdateColumn {
        DatabaseColumn column;
        String value;
        Type type;

        UpdateColumn(DatabaseColumn column, String value){
            this.column = column;
            this.value = value;
            this.type = Type.VALUE;
        }

        UpdateColumn(DatabaseColumn column, String value, Type type){
            this.column = column;
            this.value = value;
            this.type = type;
        }    }

    private class Condition {
        String field1;
        Comparator comparator;
        String field2;

        Condition (String field1, Comparator comparator, String field2){
            this.field1 = field1;
            this.comparator = comparator;
            this.field2 = field2;
        }
    }

    private class LimitBy {
        Integer pos;
        Integer total;

        LimitBy (){
            pos = null;
            total = null;
        }
        LimitBy (int pos){
            this.pos = pos;
            total = null;
        }
        LimitBy (int pos, int total){
            this.pos = pos;
            total = total;
        }
    }

    private class Relation {
        TablesEnum table1;
        DatabaseColumn column1;
        String alias1;
        TablesEnum table2;
        DatabaseColumn column2;

        Relation (TablesEnum table1, String alias1, DatabaseColumn column1, TablesEnum table2, DatabaseColumn column2) {
            this.table1 = table1;
            this.column1 = column1;
            this.alias1 = alias1;
            this.table2 = table2;
            this.column2 = column2;
        }
    }


    private class OrderBy {
        String orderField;

        OrderBy (String alias, DatabaseColumn column, SORTORDER sortOrder) {
            this.orderField = alias + " " + sortOrder.name();
        }
        OrderBy (TablesEnum table, DatabaseColumn column, SORTORDER sortOrder) {
            this.orderField = table.alias() + "." + column.getColumnName() + " " + sortOrder.name();
        }
    }


    public SQLBuilder addTable(TablesEnum table){
        this.mainTable = table;
        return this;
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

    public SQLBuilder addColumns (TablesEnum table){

        for (DatabaseColumn column : table.columns()){
            if (isTableField(column)) {
                dbColumns.add(new Column(column, table.alias()));
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


    public SQLBuilder addColumns (TablesEnum table, SQLTypes sqlType){

        for (DatabaseColumn column : table.columns()){
            if (isTableField(column)) {
                if (column.getFieldType() != FieldType.SEQUENCE || sqlType != SQLTypes.INSERT) {
                    dbColumns.add(new Column(column, table.alias()));
                }
            }
        }
        return this;
    }

    public SQLBuilder addColumn (String alias, DatabaseColumn column, String columnAlias) {

        dbColumns.add(new Column(column, alias, columnAlias));
        return this;
    }

    public SQLBuilder updateColumn (DatabaseColumn column, String value) {

        updateColumns.add(new UpdateColumn(column,value));
        return this;
    }

    public SQLBuilder updateColumn (DatabaseColumn column, Type type, String value) {

        updateColumns.add(new UpdateColumn(column,value, type));
        return this;
    }

    public SQLBuilder addCondition (DatabaseColumn column, Comparator comparator, Object object){
        conditions.add(new Condition(column.getColumnName(), comparator, getValue(object)));
        return this;
    }

    private String getValue(Object object){
        String value = "";
        if (object == null){
            value = "?";
        }
        else if (object instanceof String){
            value = "'" + (String) object + "'";
        }
        else if (object instanceof Integer){
            value = String.valueOf(object);
        }
        return value;
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

        conditions.add(new Condition(sb.toString(), comparator, getValue(object)));
        return this;
    }

    public SQLBuilder addCondition (String alias, DatabaseColumn dbColumn, Comparator comparator, Object object){
        conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator, getValue(object)));
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

    public SQLBuilder addRelation (TablesEnum table, DatabaseColumn dbColumn, TablesEnum table2, DatabaseColumn dbColumn2){
        relations.add(new Relation(table, null, dbColumn, table2, dbColumn2));
        return this;
    }

    public SQLBuilder addRelation (TablesEnum table, String alias1, DatabaseColumn dbColumn, TablesEnum table2, DatabaseColumn dbColumn2){
        relations.add(new Relation(table, alias1, dbColumn, table2, dbColumn2));
        return this;
    }

    public SQLBuilder orderBy (String alias, DatabaseColumn column, SORTORDER sortOrder) {
        orderByColumns.add(new OrderBy(alias, column, sortOrder));
        return this;
    }

    public SQLBuilder orderBy (TablesEnum table, DatabaseColumn column, SORTORDER sortOrder) {
        orderByColumns.add(new OrderBy(table, column, sortOrder));
        return this;
    }

    public SQLBuilder addOption (String option) {
        options.add(new Option(option));
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

    public String fields(){
        if (this.dbColumns.size() == 0){
            addColumns(this.mainTable);
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Column column : this.dbColumns){
            String columnAlias = column.columnAlias == null ? column.column.name() : column.columnAlias;
            sb
                    .append(first ? "" : ", ")
                    .append(column.dbAlias)
                    .append(".")
                    .append(column.column.getColumnName())
                    .append( " AS ")
                    .append(columnAlias);
            first = false;
        }
        return sb.toString();
    }

    public String insertFields(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Column column : this.dbColumns){
            sb
                    .append(first ? "" : ", ")
                    .append(column.column.getColumnName());
            first = false;
        }
        return sb.toString();
    }

    public String updateFields(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (UpdateColumn column : this.updateColumns){
            String sep = "";
            switch (column.type){
                case VALUE :
                    sep = "'";
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

    public String values(){
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        sb.append(StringUtils.repeat(",?", this.dbColumns.size()-1));
        return sb.toString();
    }

    public String conditions(){
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Condition condition : this.conditions){
            sb
                    .append(first ? " WHERE " : " AND ")
                    .append(condition.field1)
                    .append(" ")
                    .append(condition.comparator.comparator())
                    .append(" ")
                    .append(condition.field2);
            first = false;
        }
        return sb.toString();
    }

    public String orderBy(){
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

    public String limitByClause () {
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

    public String options () {
        StringBuilder sb = new StringBuilder();
        for (Option option : this.options){
            sb
                    .append(" ")
                    .append(option.option);
        }
        return sb.toString();
    }

    public String relations(){
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
