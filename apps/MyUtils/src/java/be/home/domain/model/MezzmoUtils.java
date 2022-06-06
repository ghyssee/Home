package be.home.domain.model;

import be.home.common.utils.NetUtils;
import be.home.model.json.MP3Settings;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by Gebruiker on 16/12/2016.
 */
public class MezzmoUtils {

    public static String mezzmoBase = null;
    private static final Logger log = LogManager.getLogger(MezzmoUtils.class);

    public static String getMezzmoBase(MP3Settings.Mezzmo mezzmo){

        if (mezzmoBase == null) {

            String host = NetUtils.getHostName();
            String id = findMezzmoBaseId(mezzmo.activeBase, host);
            mezzmoBase = findMezzmoBase(mezzmo.baseList, id);
        }
        return mezzmoBase;
    }

    private static String findMezzmoBaseId(MP3Settings.MezzmoActiveBase mezzmoBase, String host){
        String id = mezzmoBase.defaultId;
        if (StringUtils.isNotBlank(host)) {
            for (MP3Settings.MezzmoActiveBaseItem item : mezzmoBase.list) {
                if (host.toLowerCase().equals(item.host.toLowerCase())){
                    id = item.id;
                    break;
                }
            }
        }
        return id;
    }

    private static String findMezzmoBase(List<MP3Settings.MezzmoBase> list, String id){
        String path = null;
        for ( MP3Settings.MezzmoBase mezzmoBase : list){
            if (id.equals(mezzmoBase.id)){
                log.info("MezzmoBase found: " + mezzmoBase.description);
                log.info("Base: " + mezzmoBase.path);
                path = mezzmoBase.path;
                break;
            }

        }
        return path;
    }

    public static MP3Settings.Mezzmo.Mp3Checker.RelativePath getRelativePath (MP3Settings mp3Settings) {
        String id = mp3Settings.mezzmo.mp3Checker.currentRelativePath;
        MP3Settings.Mezzmo.Mp3Checker.RelativePath currentRelativePath = null;
        for (MP3Settings.Mezzmo.Mp3Checker.RelativePath relativePath : mp3Settings.mezzmo.mp3Checker.relativePaths) {
            if (relativePath.id.equals(id)) {
                currentRelativePath = relativePath;
            }
        }
        return currentRelativePath;
    }

}
