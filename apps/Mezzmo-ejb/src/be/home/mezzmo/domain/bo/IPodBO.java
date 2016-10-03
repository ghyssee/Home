package be.home.mezzmo.domain.bo;

import be.home.common.model.BusinessObject;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.dao.jdbc.IPodDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MediaMonkeyDAOImpl;
import be.home.mezzmo.domain.dao.jdbc.MezzmoDAOImpl;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by ghyssee on 13/04/2016.
 */
public class IPodBO extends BusinessObject {

    public IPodBO (){
    }


    public List<MGOFileAlbumCompositeTO> listPlayCount(){
        return getIPodDAO().listPlayCount();
    }

    public int resetPlayCount(Long pid, int playCount) {
        return getIPodDAO().updatePlayCount(pid, playCount);
    }

    public IPodDAOImpl getIPodDAO(){
        return new IPodDAOImpl();
    }


}
