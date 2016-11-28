package be.home.mezzmo.domain.bo;

import be.home.common.model.BusinessObject;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.jdbc.MediaMonkeyDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOImpl;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class MediaMonkeyBO extends BusinessObject {

    public MediaMonkeyBO (){
    }


    public List<MGOFileAlbumCompositeTO> getTop20(){
        return getMediaMonkeyDAO().getTop20();
    }

    public int resetPlayCount(){
        return getMediaMonkeyDAO().resetPlayCount();
    }

    public MediaMonkeyDAOImpl getMediaMonkeyDAO(){
        return new MediaMonkeyDAOImpl();
    }

}
