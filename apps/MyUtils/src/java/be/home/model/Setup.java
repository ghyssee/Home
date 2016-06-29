package be.home.model;

import be.home.common.exceptions.ApplicationException;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.NetUtils;
import be.home.common.utils.WinUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 29/06/2016.
 */
public class Setup {


    public static Map<String, Object> map = new HashMap<String, Object>();

    private static Setup setup = new Setup();

    private Setup() {

    }

    public static Setup getInstance() {
        return setup;
    }

    public static void init() {
        map = (Map<String, Object>) JSONUtils.openJSON(WinUtils.getOneDrivePath() + File.separator + "config/Setup.json", map.getClass());
    }

    public static String getFullPath(String type) {
        Map<String, Object> tmp = (Map<String, Object>) map.get(type);
        String path = "";
        String parent = null;
        if (tmp != null) {
            do {
                path = tmp.get("path") + File.separator + path;
                parent = (String) tmp.get("parent");
                if (parent != null) {
                    //path = tmp.get("path") + File.separator + path;
                    tmp = (Map<String, Object>) map.get(parent);
                    if (tmp == null){
                        throw new ApplicationException("Parent Not Found: " + parent);
                    }
                }
            }
            while (parent != null);
        }
        if (StringUtils.isNotBlank(path)){
            path = path.replace("%ONEDRIVE%", WinUtils.getOneDrivePath());
            path = path.replace("%HOST%", NetUtils.getHostName());
        }
        return path;

    }



}
