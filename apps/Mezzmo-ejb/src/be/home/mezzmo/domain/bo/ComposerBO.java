package be.home.mezzmo.domain.bo;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.enums.MP3CleanupType;
import be.home.mezzmo.domain.model.json.Composers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static java.util.Comparator.comparing;

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

    private Map<String, ArrayList<Composers.FramePattern>> getMap(MP3CleanupType st){
        Map<String, ArrayList<Composers.FramePattern>> map;
        switch (st) {
            case EXCLUDE:
                map = mapExclusions;
                break;
            case CLEAN:
                map = mapCleanups;
                break;
            case CUSTOM_TAG:
                map = mapCustomTags;
                break;
            default:
                map = null;
                break;
        }
        return map;
    }

    private void addMap(MP3CleanupType st, Composers.FramePattern framePattern){
        Map<String, ArrayList<Composers.FramePattern>> map = getMap(st);
        ArrayList<Composers.FramePattern> myList = map.get(framePattern.getFrameId());
        if (myList == null){
            ArrayList<Composers.FramePattern> newList = new <Composers.FramePattern> ArrayList();
            newList.add(framePattern);
            map.put(framePattern.getFrameId(), newList );
        }
        else {
            myList.add(framePattern);
        }
    }
    private void addList(MP3CleanupType st, Composers.FramePattern framePattern){
        switch (st) {
            case EXCLUDE:
                composers.exclusionList.add(framePattern);
                break;
            case CLEAN:
                composers.cleanupList.add(framePattern);
                break;
            case CUSTOM_TAG:
                composers.customTagList.add(framePattern);
                break;
        }
    }

    public boolean alreadyExist(MP3CleanupType st, String frameId, String pattern){
        ArrayList<Composers.FramePattern> myList = getMap(st).get(frameId);
        if (myList != null && myList.size() > 0) {
            for (Composers.FramePattern framePattern : myList) {
                if (framePattern.getPattern().equalsIgnoreCase(pattern))
                    return true;
            }
        }
        return false;
    }

    public void add (MP3CleanupType st, String frameId, String pattern){
        add(st, frameId, pattern, false);

    }
    public void add(MP3CleanupType st, String frameId, String pattern, boolean contains){
        String strippedValue = pattern;
        if (!alreadyExist(st, frameId, strippedValue)){
            String uuid = composerBO.getUniqueId();
            Composers.FramePattern newFramePattern = new Composers().new FramePattern(uuid, frameId, strippedValue, contains);
            addMap(st, newFramePattern);
            addList(st, newFramePattern);
            log.info("Adding " + st.name() + ": " + frameId + ": " + strippedValue);
        }
        else {
            log.warn(st.name() + " line already exists: "  + frameId + ": " + strippedValue);
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

    public void sort () throws IOException {
        composers.exclusionList.sort(comparing(Composers.FramePattern::getFrameId).thenComparing(Composers.FramePattern::getSortPattern));
        composers.cleanupList.sort(comparing(Composers.FramePattern::getFrameId).thenComparing(Composers.FramePattern::getSortPattern));
        composers.customTagList.sort(comparing(Composers.FramePattern::getFrameId).thenComparing(Composers.FramePattern::getSortPattern));
        //list.sort(comparing(Type::getField1).thenComparing(Type::getField2));
    }

    public void save() throws IOException {
        JSONUtils.writeJsonFileWithCode(composers, Constants.JSON.COMPOSERS);
    }
    public static ComposerBO getInstance() {
        return composerBO;
    }

}
