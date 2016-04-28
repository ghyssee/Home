package be.home.common.utils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

/**
 * Created by ghyssee on 28/04/2016.
 */
public class WinUtils {

    public static String getOneDrivePath() {
        String path = null;
        try {
            path = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                    "Software\\Microsoft\\OneDrive\\", "UserFolder");
        }
        catch (Win32Exception ex){
            try {
                path = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                        "Software\\Microsoft\\SkyDrive\\", "UserFolder");
            }
            catch (Win32Exception ex2){
            }
        }
        return path;
    }


}
