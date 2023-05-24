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
    private static Map<String, Composers.Publisher> mapPublishers;


    private ComposerBO() {
        composers = (Composers) JSONUtils.openJSONWithCode(Constants.JSON.COMPOSERS, Composers.class);
        mapComposers = composers.composers.stream().collect(Collectors.toMap(Composers.Composer::getId, Function.identity()));
        mapPublishers = composers.publishers.stream().collect(Collectors.toMap(Composers.Publisher::getId, Function.identity()));
    }

    public Collection<Composers.Composer> getComposers() {
        return this.mapComposers.values();
    }

    public Collection<Composers.Publisher> getPublishers() {
        return this.mapPublishers.values();
    }

    public Composers.Composer getComposer(String id){
        Composers.Composer composer = mapComposers.get(id);
        return composer;
    }
    public Composers.Publisher getPublisher(String id){
        Composers.Publisher publisher = mapPublishers.get(id);
        return publisher;
    }
    private String strip(String value){
        String strippedValue = value.replaceAll("^\\^?\\(\\.\\*\\)(.*)\\(\\.\\*\\)", "$1");
        return strippedValue;
    }
    public boolean alreadyExistComposer(String pattern){
        for (Composers.Composer composer : composers.composers){
            if (composer.getPattern().equalsIgnoreCase(pattern))
                return true;
        }
        return false;
    }
    public boolean alreadyExistPublisher(String pattern){
        for (Composers.Publisher publisher : composers.publishers){
            if (publisher.getPattern().equalsIgnoreCase(pattern))
                return true;
        }
        return false;
    }
    public void addComposer(String pattern){
        String strippedComposer = strip(pattern);
        if (!alreadyExistComposer(strippedComposer)){
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

    public void addPublisher(String pattern){
        String strippedPublisher = strip(pattern);
        if (!alreadyExistPublisher(strippedPublisher)){
            String uuid = composerBO.getUniqueId();
            Composers.Publisher newPublisher = new Composers().new Publisher(uuid, strippedPublisher);
            mapPublishers.put(uuid, newPublisher);
            composers.publishers.add(newPublisher);
            log.info("Adding publisher: " + strippedPublisher);
        }
        else {
            log.warn("Publisher already exists: " + strippedPublisher);
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
