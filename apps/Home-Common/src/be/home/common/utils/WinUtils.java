package be.home.common.utils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

import java.lang.reflect.InvocationTargetException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by ghyssee on 28/04/2016.
 */
public class WinUtils {

    private static String oneDrivePath = null;

    public static String getOneDrivePath() {
        if (oneDrivePath == null) {

           try {
               oneDrivePath = WinRegistry.readString(
                       WinRegistry.HKEY_CURRENT_USER,
                       "Software\\Microsoft\\OneDrive",
                       "UserFolder");
           }
           catch (IllegalAccessException e) {
               e.printStackTrace();
           }
           catch (InvocationTargetException e) {
               e.printStackTrace();
           }

           // try {
                //oneDrivePath = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                //        "Software\\Microsoft\\OneDrive\\", "UserFolder");
            //} catch (Win32Exception ex) {
            //    try {
                    //oneDrivePath = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                            //"Software\\Microsoft\\SkyDrive\\", "UserFolder");
           //     } catch (Win32Exception ex2) {
            //        throw new RuntimeException("Problem Getting OneDrive Path");
            //    }
           // }
        }
        return oneDrivePath;
    }


}
