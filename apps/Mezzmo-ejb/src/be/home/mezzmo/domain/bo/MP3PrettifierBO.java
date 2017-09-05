package be.home.mezzmo.domain.bo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.utils.JSONUtils;
import be.home.mezzmo.domain.model.json.MP3Prettifier;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Gebruiker on 12/08/2017.
 */
public class MP3PrettifierBO {

    private static MP3PrettifierBO mp3PrettifierBO = new MP3PrettifierBO();
    private static MP3Prettifier mp3Prettifier;
    private static final Logger log = Logger.getLogger(MP3PrettifierBO.class);

    private MP3PrettifierBO() {
        mp3Prettifier = (MP3Prettifier) JSONUtils.openJSONWithCode(Constants.JSON.MP3PRETTIFIER, MP3Prettifier.class);
    }

    public boolean removeArtistName(String id){
        Iterator<MP3Prettifier.Word> iter = mp3Prettifier.artist.names.iterator();
        boolean found = false;
        while (iter.hasNext())
        {
            MP3Prettifier.Word word = iter.next();
            if (word.id.equals(id)){
                iter.remove();
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean removeGlobalSentence(String id){
        Iterator<MP3Prettifier.Word> iter = mp3Prettifier.global.sentences.iterator();
        boolean found = false;
        while (iter.hasNext())
        {
            MP3Prettifier.Word word = iter.next();
            if (word.id.equals(id)){
                iter.remove();
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean removeGlobalWord(String id){
        Iterator<MP3Prettifier.Word> iter = mp3Prettifier.global.words.iterator();
        boolean found = false;
        while (iter.hasNext())
        {
            MP3Prettifier.Word word = iter.next();
            if (word.id.equals(id)){
                iter.remove();
                found = true;
                break;
            }
        }
        return found;
    }

    public boolean removeArtistWord(String id){
        Iterator<MP3Prettifier.Word> iter = mp3Prettifier.artist.words.iterator();
        boolean found = false;
        while (iter.hasNext())
        {
            MP3Prettifier.Word word = iter.next();
            if (word.id.equals(id)){
                iter.remove();
                found = true;
                break;
            }
        }
        return found;
    }

    public void save() throws IOException {
        //String file = Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW";
        //JSONUtils.writeJsonFile(mp3Prettifier, file);
        JSONUtils.writeJsonFileWithCode(mp3Prettifier, Constants.JSON.MP3PRETTIFIER);
    }
    public static MP3PrettifierBO getInstance() {
        return mp3PrettifierBO;
    }

}
