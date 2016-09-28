package be.home.common.dao.jdbc;

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

    public static java.util.Date convertiPodToDate(Long f){
        if (f != null && f.longValue() != 0) {
            java.util.Date date = new java.util.Date(f);
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

    public static long convertDateToLong(java.util.Date date){
        if (date != null) {
            long longDate = date.getTime();
            return longDate;
        }
        return 0;
    }



}
