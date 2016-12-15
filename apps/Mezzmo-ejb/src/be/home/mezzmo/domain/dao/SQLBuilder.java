package be.home.mezzmo.domain.dao;

import be.home.common.database.DatabaseColumn;
import be.home.mezzmo.domain.dao.jdbc.TablesEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 15/12/2016.
 */
public class SQLBuilder {

    public enum Comparator {
        LIKE,
        EQUALS
    }

    public enum SQLTypes {
        SELECT,
        INSERT,
        UPDATE,
        DELETE
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

    private class Relation {
        //TablesEnum table;
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


    private TablesEnum mainTable;
    private List<Column> dbColumns = new ArrayList<>();
    private List<Condition> conditions = new ArrayList<>();
    private List<Relation> relations = new ArrayList<>();
    private SQLTypes sqlType;

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
            dbColumns.add(new Column(column, table.alias()));
        }
        return this;
    }

    public SQLBuilder addColumn (String alias, DatabaseColumn column, String columnAlias) {

        dbColumns.add(new Column(column, alias, columnAlias));
        return this;
    }

    public SQLBuilder addCondition (DatabaseColumn column, Comparator comparator, String value){
        conditions.add(new Condition(column.getColumnName(), comparator, value == null ? "?" : "'" + value + "'"));
        return this;
    }

    public SQLBuilder addCondition (String alias, DatabaseColumn dbColumn, Comparator comparator, String value){
        conditions.add(new Condition(alias + "." + dbColumn.getColumnName(), comparator, "'" + value + "'"));
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

    private String renderSelect(){
        String sql = "";
        StringBuilder sb = new StringBuilder();
        sb
                .append("SELECT ")
                .append(fields())
                .append(" FROM ")
                .append(this.mainTable.tableAlias())
                .append(relations())
                .append(conditions());
        return sb.toString();
    }

    private String renderDelete(){
        String sql = "";
        StringBuilder sb = new StringBuilder();
        sb
                .append("DELETE FROM ")
                .append(this.mainTable)
                .append(conditions());
        return sb.toString();
    }

    public String render(){
        String sql = null;
        switch (this.sqlType){
            case SELECT:
                sql = renderSelect();
                break;
            case UPDATE:
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
            String columnAlias = column.columnAlias == null ? column.column.getColumnName() : column.columnAlias;
            sb
                    .append(first ? "" : ", ")
                    .append(column.dbAlias)
                    .append(".")
                    .append(column.column.name())
                    .append( " AS ")
                    .append(columnAlias);
            first = false;
        }
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
                    .append(condition.comparator == Comparator.EQUALS ? "=" : "LIKE")
                    .append(" ")
                    .append(condition.field2);
            first = false;
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
                    .append(relation.column1.name())
                    .append(" = ")
                    .append(relation.table2.alias())
                    .append(".")
                    .append(relation.column2.name())
                    .append(")");
        }
        return sb.toString();
    }

    public static SQLBuilder getInstance(){
        return new SQLBuilder();
    }

}
