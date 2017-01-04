package be.home.common.database.sqlbuilder;

/**
 * Created by Gebruiker on 2/01/2017.
 */
public class SQLBuilderUtils {


    /* Object can be:
    1. String
    2. Integer
    3. null, which means it is a parameter (?)
    4. SQLBuilder combined with comparator IN
     */

    public static String getValue(Object object){
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
        else if (object instanceof Long){
            value = String.valueOf(object);
        }
        else if (object instanceof SQLBuilder){
            SQLBuilder subSQL = (SQLBuilder) object;
            value = "(" + subSQL.render() + ")";
        }
        return value;
    }
}
