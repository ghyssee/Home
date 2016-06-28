package be.home.mezzmo.domain.service;

import be.home.mezzmo.domain.bo.MediaMonkeyBO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;

import java.util.List;

/**
 * Created by ghyssee on 28/06/2016.
 */
public class MediaMonkeyServiceImpl {
    private static MediaMonkeyServiceImpl instance = null;

    protected MediaMonkeyServiceImpl(){
    }

    public List<MGOFileAlbumCompositeTO> getTop20(){
        MediaMonkeyBO bo = new MediaMonkeyBO();
        return bo.getTop20();
    }

    public synchronized static MediaMonkeyServiceImpl getInstance() {
        if(instance == null) {
            instance = new MediaMonkeyServiceImpl();
        }
        return instance;
    }
}
