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

    public static java.util.Date convertiPodDateToDate(Long f){
        if (f != null && f.longValue() != 0) {
            Date date = new Date(f*1000);
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, 31);
            // calendar.add(Calendar.HOUR, -24);

            return calendar.getTime();
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

    public static String getSearchField(String field){
        if (field == null){
            return "%";
        }
        else {
            return field;
        }
    }

    public static String getSearchField(Integer field){
        if (field == null){
            return "%";
        }
        else {
            return field.toString();
        }
    }



}
