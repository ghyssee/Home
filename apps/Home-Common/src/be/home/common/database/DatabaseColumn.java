package be.home.common.database;

/**
 * Created by ghyssee on 14/12/2016.
 */
public interface DatabaseColumn {
    String name();
    String getColumnName();
    FieldType getFieldType();
}
