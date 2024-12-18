package be.home.common.dao.jdbc;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ghyssee on 25/04/2016.
 */
public class SQLiteUtils {

    public static java.util.Date convertToDate(Long f){
        if (f != null && f.longValue() != 0) {
            java.util.Date date = new java.util.Date(f * 1000);
            return date;
        }
        return null;
    }

    public static Long convertDateToLong(Date date){
        if (date != null) {
            Long value = date.getTime()/1000;
            return value;
        }
        return null;
    }

    public static java.util.Date convertiPodDateToDate(Long f){
        // Apple's Cocoa framework Epoch
        // https://www.epochconverter.com/coredata
        /*
        A Core Data timestamp is the number of seconds (or nanoseconds) since midnight,
        January 1, 2001, GMT (see CFAbsoluteTime).
        The difference between a Core Data timestamp and a Unix timestamp (seconds since 1/1/1970)
        is 978307200 seconds.
         */
        if (f != null && f.longValue() != 0) {
            Date date = new Date((f + 978307200L) * 1000);
            return date;
        }
        return null;
    }

    public static java.util.Date convertStringToDate(String strDate){
        if (strDate != null){
            long longDate = 0L;
            try {
                longDate = Long.parseLong(strDate);
            }
            catch (NumberFormatException e){
                return null;
            }
            java.util.Date date = new java.util.Date(longDate * 1000);
            return date;
        }
        return null;
    }

    /* date should be converted to String
       because SQLite doesn't handle the Long/Int very well when updating it with JDBC
     */
    public static String convertDateToString(java.util.Date date){
        if (date != null) {
            long longDate = date.getTime();
            return String.valueOf(longDate/1000);
        }
        return null;
    }

    public static String getSearchField(Object field){
        if (field == null){
            return "%";
        }
        else {
            return field.toString();
        }
    }

    public static String escape(String text){
        text = text.replace("'", "''");
        return text;
    }



}
