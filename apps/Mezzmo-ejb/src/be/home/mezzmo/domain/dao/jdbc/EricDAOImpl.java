package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.EricDB;
import be.home.mezzmo.domain.model.eric.MezzmoFileTO;
import org.apache.log4j.Logger;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class EricDAOImpl extends EricRowMappers {

    private static final Logger log = Logger.getLogger(EricDAOImpl.class);
    private String db = null;

    public EricDAOImpl (String db){
        this.db = db;
    }


    public void insertMezzmoFile(final MezzmoFileTO mezzmoFile){
        Object[] params = {mezzmoFile.getId(), mezzmoFile.getArtistId(), mezzmoFile.getArtistName()};
        getInstance().getJDBCTemplate().update(INSERT_MEZZMOFILE, params);
    }


}
