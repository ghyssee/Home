package be.home.common.utils;

import org.apache.logging.log4j.Logger;

/**
 * Created by Gebruiker on 22/12/2016.
 */
public class LogUtils {
    public static void logError(Logger log, Throwable t, String msg){
        t.printStackTrace();
        log.error(msg, t);
    }
    public static void logError(Logger log, Throwable t){
        t.printStackTrace();
        log.error(t);
    }
}
