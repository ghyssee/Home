package be.home.common.utils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by ghyssee on 28/04/2016.
 */
public class WinUtils {

    private static String oneDrivePath = null;

    public static String getOneDrivePathOld() {
        if (oneDrivePath == null) {

            try {
                oneDrivePath = WinRegistry.readString(
                        WinRegistry.HKEY_CURRENT_USER,
                        "Software\\Microsoft\\OneDrive",
                        "UserFolder");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return oneDrivePath;
    }

        public static String getOneDrivePath() {
            String oneDrivePath;

            try {
                // Vraag direct de waarde op via de Windows API
                oneDrivePath = Advapi32Util.registryGetStringValue(
                        WinReg.HKEY_CURRENT_USER,
                        "Software\\Microsoft\\OneDrive",
                        "UserFolder"
                );
            } catch (Exception e) {
                // Fallback naar de envrionment variable  als het register faalt
                oneDrivePath = System.getenv("OneDrive");
                if (StringUtils.isBlank(oneDrivePath)){
                    throw new RuntimeException("OneDrive User Folder not found!");
                }
            }
            return oneDrivePath;
        }

}
