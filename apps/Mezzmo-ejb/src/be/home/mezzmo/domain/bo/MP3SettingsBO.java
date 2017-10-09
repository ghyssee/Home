package be.home.mezzmo.domain.bo;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.model.json.MP3Settings;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by ghyssee on 9/10/2017.
 */
public class MP3SettingsBO {
    private static MP3SettingsBO mp3SettingsBO = new MP3SettingsBO();
    private static MP3Settings mp3Settings;
    private static final Logger log = Logger.getLogger(MP3SettingsBO.class);

    private MP3SettingsBO() {
        mp3Settings = (MP3Settings) JSONUtils.openJSONWithCode(Constants.JSON.MP3SETTINGS, MP3Settings.class);
    }

    public MP3Settings.Mezzmo.Mp3Checker.RelativePath findRelativePath(String id){
        for (MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath : mp3Settings.mezzmo.mp3Checker.relativePaths){
            if (relativePath.id.equals(id)){
                return relativePath;
            }
        }
        return null;
    }

    public void save() throws IOException {
        //String file = Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW";
        //JSONUtils.writeJsonFile(mp3Prettifier, file);
        JSONUtils.writeJsonFileWithCode(mp3Settings, Constants.JSON.MP3SETTINGS);
    }

    public static MP3SettingsBO getInstance() {
        return mp3SettingsBO;
    }
}
