package be.home.common.dao.jdbc;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * Created by Gebruiker on 10/07/2016.
 */
public class QueryBuilder {

    public static String buildOrCondition (String query, String clause, List values ) {
        String buildCondition = "";
        for (int i=0; i < values.size(); i++){
            if (i > 0){
                buildCondition += " OR ";
            }
            buildCondition += clause;
        }
        query = query.replace("{WHERE}", buildCondition);
        return query;
    }

}
