package be.home.mezzmo.domain.bo;

import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.json.Composers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComposerBO {
    private static ComposerBO composerBO = new ComposerBO();
    private static Composers composers;
    private static final Logger log = LogManager.getLogger();
    private static Map<String, Composers.Composer> mapComposers;


    private ComposerBO() {
        composers = (Composers) JSONUtils.openJSONWithCode(Constants.JSON.COMPOSERS, Composers.class);
        mapComposers = composers.composers.stream().collect(Collectors.toMap(Composers.Composer::getId, Function.identity()));
    }

    public Collection<Composers.Composer> getComposers() {
        return this.mapComposers.values();
    }

    public Composers.Composer getComposer(String id){
        Composers.Composer composer = mapComposers.get(id);
        return composer;
    }
    private String stripComposer(String composer){
        String strippedComposer = composer.replaceAll("^\\^?\\(\\.\\*\\)(.*)\\(\\.\\*\\)", "$1");
        return strippedComposer;
    }
    public boolean alreadyExist(String pattern){
        for (Composers.Composer composer : composers.composers){
            if (composer.getPattern().equalsIgnoreCase(pattern))
                return true;
        }
        return false;
    }

    public void add(String pattern){
        String strippedComposer = stripComposer(pattern);
        if (!alreadyExist(strippedComposer)){
            String uuid = composerBO.getUniqueId();
            Composers.Composer newComposer = new Composers().new Composer(uuid, strippedComposer);
            mapComposers.put(uuid, newComposer);
            composers.composers.add(newComposer);
            log.info("Adding composer: " + strippedComposer);
        }
        else {
            log.warn("Composer already exists: " + strippedComposer);
        }
    }

    public String getUniqueId(){
        String uuid = null;
        do {
            uuid = UUID.randomUUID().toString();
        }
        while (mapComposers.get(uuid) != null);
        return uuid;
    }

    public void save() throws IOException {
        JSONUtils.writeJsonFileWithCode(composers, Constants.JSON.COMPOSERS);
    }
    public static ComposerBO getInstance() {
        return composerBO;
    }

}
