package be.home.mezzmo.domain.service;

import be.home.mezzmo.domain.bo.IPodBO;
import be.home.mezzmo.domain.dao.jdbc.IPodDAOImpl;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;

import java.util.List;

/**
 * Created by ghyssee on 28/06/2016.
 */
public class IPodServiceImpl {
    private static IPodServiceImpl instance = null;

    protected IPodServiceImpl(){
    }

    public List<MGOFileAlbumCompositeTO> getListPlayCount(){
        IPodBO bo = new IPodBO();
        return bo.listPlayCount();
    }

    public synchronized static IPodServiceImpl getInstance() {
        if(instance == null) {
            instance = new IPodServiceImpl();
        }
        return instance;
    }
}
