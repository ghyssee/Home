package be.home.common.configuration;

import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.NetUtils;
import be.home.common.utils.WinUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 29/06/2016.
 */
public class Setup {


    private static Map<String, Object> map = new HashMap<String, Object>();

    private static Setup setup = new Setup();

    private Setup() {
        map = (Map<String, Object>) JSONUtils.openJSON(WinUtils.getOneDrivePath() + File.separator + "config/Setup.json", map.getClass());
    }

    public static Setup getInstance() {
        return setup;
    }

    public static String getFullPath(String type) {
        Map<String, Object> tmp = (Map<String, Object>) map.get(type);
        String path = "";
        String parent = null;
        if (tmp != null) {
            do {
                path = tmp.get("path") + path;
                parent = (String) tmp.get("parent");
                if (parent != null) {
                    if (type.equals(parent)){
                        throw new ApplicationException("Parent Path can not be the same as the current Path: " + parent);
                    }
                    path = File.separator + path;
                    tmp = (Map<String, Object>) map.get(parent);
                    if (tmp == null){
                        throw new ApplicationException("Parent Not Found: " + parent);
                    }
                }
            }
            while (parent != null);
        }
        if (StringUtils.isNotBlank(path)){
            path = replaceEnvironmentVariables(path);
        }
        return path;

    }

    public static String replaceEnvironmentVariables(String var){
        var = var.replace("%ONEDRIVE%", WinUtils.getOneDrivePath());
        var = var.replace("%HOST%", NetUtils.getHostName());
        return var;

    }

    public static String getPath(String type) {
        Map<String, Object> tmp = (Map<String, Object>) map.get(type);
        return (String) tmp.get("path");

    }



}
