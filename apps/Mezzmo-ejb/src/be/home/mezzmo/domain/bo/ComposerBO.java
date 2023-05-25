package be.home.mezzmo.domain.bo;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.json.Composers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class ComposerBO {
    private static ComposerBO composerBO = new ComposerBO();
    private static Composers composers;
    private static final Logger log = LogManager.getLogger();
    private static Map<String, ArrayList<Composers.FramePattern>> mapExclusions;
    private static Map<String, ArrayList<Composers.FramePattern>> mapCleanups;
    private static Map<String, ArrayList<Composers.FramePattern>> mapCustomTags;
    private static Map<String, String> mapKeys;


    private ComposerBO() {
        composers = (Composers) JSONUtils.openJSONWithCode(Constants.JSON.COMPOSERS, Composers.class);
        //mapComposers = composers.composers.stream().collect(Collectors.toMap(Composers.Composer::getId, Function.identity()));
        mapExclusions = new <Composers.FramePattern> HashMap();
        mapCleanups = new <Composers.FramePattern> HashMap();
        mapCustomTags = new <Composers.FramePattern> HashMap();
        mapKeys = new <String> HashMap();
        for (Composers.FramePattern pattern : composers.exclusionList) {
            ArrayList<Composers.FramePattern> myList = mapExclusions.get(pattern.getFrameId());
            mapKeys.put(pattern.getId(), pattern.getId());
            if (myList == null) {
                ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern>ArrayList();
                newList.add(pattern);
                mapExclusions.put(pattern.getFrameId(), newList);
            } else {
                myList.add(pattern);
            }
        }

        for (Composers.FramePattern pattern : composers.cleanupList) {
            ArrayList<Composers.FramePattern> myList = mapCleanups.get(pattern.getFrameId());
            mapKeys.put(pattern.getId(), pattern.getId());
            if (myList == null) {
                ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern>ArrayList();
                newList.add(pattern);
                mapCleanups.put(pattern.getFrameId(), newList);
            } else {
                myList.add(pattern);
            }
        }

        for (Composers.FramePattern pattern : composers.customTagList) {
            ArrayList<Composers.FramePattern> myList = mapCustomTags.get(pattern.getFrameId());
            mapKeys.put(pattern.getId(), pattern.getId());
            if (myList == null) {
                ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern>ArrayList();
                newList.add(pattern);
                mapCustomTags.put(pattern.getFrameId(), newList);
            } else {
                myList.add(pattern);
            }
        }    }


    public ArrayList<Composers.FramePattern> getExclusionList(String frameId){
       return mapExclusions.get(frameId);
    }


    public ArrayList<Composers.FramePattern> getCleanupList(String frameId){
        return mapCleanups.get(frameId);
    }
    public ArrayList<Composers.FramePattern> getCustomTagList(String frameId){
        return mapCustomTags.get(frameId);
    }
    public ArrayList<Composers.FramePattern> getGlobalExclusionList(String frameId){

        return mapExclusions.get(frameId);
    }
    public ArrayList<Composers.FramePattern> getGlobalCleanupList(String frameId){

        return mapCleanups.get(frameId);
    }

    public ArrayList<Composers.FramePattern> getGlobalCustomTagList(String frameId){

        return mapCustomTags.get(frameId);
    }
    private String strip(String value){
        String strippedValue = value.replaceAll("^\\^?\\(\\.\\*\\)(.*)\\(\\.\\*\\)", "$1");
        return strippedValue;
    }

    private void addMapExclusions(Composers.FramePattern framePattern){
        ArrayList<Composers.FramePattern> myList = mapExclusions.get(framePattern.getFrameId());
        if (myList == null){
            ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern> ArrayList();
            newList.add(framePattern);
            mapExclusions.put(framePattern.getFrameId(), newList );
        }
        else {
            myList.add(framePattern);
        }
    }

    private void addMapCleanups(Composers.FramePattern framePattern){
        ArrayList<Composers.FramePattern> myList = mapCleanups.get(framePattern.getFrameId());
        if (myList == null){
            ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern> ArrayList();
            newList.add(framePattern);
            mapCleanups.put(framePattern.getFrameId(), newList );
        }
        else {
            myList.add(framePattern);
        }
    }

    private void addMapCustomTags(Composers.FramePattern framePattern){
        ArrayList<Composers.FramePattern> myList = mapCustomTags.get(framePattern.getFrameId());
        if (myList == null){
            ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern> ArrayList();
            newList.add(framePattern);
            mapCustomTags.put(framePattern.getFrameId(), newList );
        }
        else {
            myList.add(framePattern);
        }
    }
    public boolean alreadyExistExclusion(String frameId, String pattern){
        ArrayList<Composers.FramePattern> myList = mapExclusions.get(frameId);
        if (myList != null && myList.size() > 0) {
            for (Composers.FramePattern framePattern : myList) {
                if (framePattern.getPattern().equalsIgnoreCase(pattern))
                    return true;
            }
        }
        return false;
    }
    public boolean alreadyExistCleanup(String frameId, String pattern){
        ArrayList<Composers.FramePattern> myList = mapCleanups.get(frameId);
        if (myList != null && myList.size() > 0) {
            for (Composers.FramePattern framePattern : myList) {
                if (framePattern.getPattern().equalsIgnoreCase(pattern))
                    return true;
            }
        }
        return false;
    }
    public boolean alreadyExistCustomTag(String frameId, String pattern){
        ArrayList<Composers.FramePattern> myList = mapCustomTags.get(frameId);
        if (myList != null && myList.size() > 0) {
            for (Composers.FramePattern framePattern : myList) {
                if (framePattern.getPattern().equalsIgnoreCase(pattern))
                    return true;
            }
        }
        return false;
    }
    public void addExclusion(String frameId, String pattern){
        addExclusion(frameId, pattern, false);
    }

    public void addCleanup(String frameId, String pattern){
        addCleanup(frameId, pattern, false);
    }

    public void addCustomTag(String frameId, String pattern){
        addCustomTag(frameId, pattern, false);
    }
    public void addExclusion(String frameId, String pattern, boolean contains){
        String strippedValue = pattern;
        if (!alreadyExistExclusion(frameId, strippedValue)){
            String uuid = composerBO.getUniqueId();
            Composers.FramePattern newFramePattern = new Composers().new FramePattern(uuid, frameId, strippedValue, contains);
            addMapExclusions(newFramePattern);
            composers.exclusionList.add(newFramePattern);
            log.info("Adding exclusion line: " + frameId + ": " + strippedValue);
        }
        else {
            log.warn("Exclusion line already exists: "  + frameId + ": " + strippedValue);
        }
    }

    public void addCleanup(String frameId, String pattern, boolean contains){
        String strippedValue = pattern;
        if (!alreadyExistCleanup(frameId, strippedValue)){
            String uuid = composerBO.getUniqueId();
            Composers.FramePattern newFramePattern = new Composers().new FramePattern(uuid, frameId, strippedValue, contains);
            addMapCleanups(newFramePattern);
            composers.cleanupList.add(newFramePattern);
            log.info("Adding cleanup line: " + frameId + ": " + strippedValue);
        }
        else {
            log.warn("Cleanup line already exists: "  + frameId + ": " + strippedValue);
        }
    }

    public void addCustomTag(String frameId, String pattern, boolean contains){
        String strippedValue = pattern;
        if (!alreadyExistCustomTag(frameId, strippedValue)){
            String uuid = composerBO.getUniqueId();
            Composers.FramePattern newFramePattern = new Composers().new FramePattern(uuid, frameId, strippedValue, contains);
            addMapCustomTags(newFramePattern);
            composers.customTagList.add(newFramePattern);
            log.info("Adding custom tag line: " + frameId + ": " + strippedValue);
        }
        else {
            log.warn("Custom Tag line already exists: "  + frameId + ": " + strippedValue);
        }
    }
    public String getUniqueId(){
        String uuid = null;
        do {
            uuid = UUID.randomUUID().toString();
        }
        while (mapKeys.get(uuid) != null && mapKeys.get(uuid) != null);
        mapKeys.put(uuid, uuid);
        return uuid;
    }

    public void save() throws IOException {
        JSONUtils.writeJsonFileWithCode(composers, Constants.JSON.COMPOSERS);
    }
    public static ComposerBO getInstance() {
        return composerBO;
    }

}
