package be.home.common.model;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class FirefoxProfilesBO {

    private static final String fileCode = Constants.JSON.FIREFOXPROFILES;
    private static FirefoxProfilesBO firefoxProfilesBO = new FirefoxProfilesBO();
    private static FirefoxProfiles firefoxProfiles;
    private static final Logger log = Logger.getLogger(FirefoxProfilesBO.class);

    private FirefoxProfilesBO() {
        firefoxProfiles = (FirefoxProfiles) JSONUtils.openJSONWithCode(fileCode, FirefoxProfiles.class);
    }

    public void save() throws IOException {
        //String file = Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW";
        //JSONUtils.writeJsonFile(mp3Prettifier, file);
        JSONUtils.writeJsonFileWithCode(firefoxProfiles, fileCode);
    }

    public static FirefoxProfilesBO getInstance() {
        return firefoxProfilesBO;
    }

    public FirefoxProfiles.Computer findComputer(String computerId){
        for (FirefoxProfiles.Computer item : this.firefoxProfiles.computers) {
            if (item.name.equals(computerId)){
                return item;
            }
        }
        return null;
    }

    public FirefoxProfiles.FirefoxInstance findFirefoxInstance(List<FirefoxProfiles.FirefoxInstance> instances, String instanceId){
        for (FirefoxProfiles.FirefoxInstance item : instances) {
            if (item.id.equals(instanceId)){
                return item;
            }
        }
        return null;
    }

    public FirefoxProfiles.FirefoxInstance findInstance(String computerId, String firefoxInstanceId){
        FirefoxProfiles.Computer computer = findComputer(computerId);
        if (computer != null){
            FirefoxProfiles.FirefoxInstance instance = findFirefoxInstance(computer.firefoxInstances, firefoxInstanceId);
            return instance;
        }
        return null;
    }

}
