package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.model.eric.MezzmoFileTO;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class EricDAOImpl extends EricRowMappers {

    private String db = null;

    public EricDAOImpl (String db){
        this.db = db;
    }


    public void insertMezzmoFile(final MezzmoFileTO mezzmoFile){
        Object[] params = {mezzmoFile.getId(),
                mezzmoFile.getArtistId(),
                mezzmoFile.getArtistName(),
                mezzmoFile.isNew(),
                mezzmoFile.getStatus(),
                mezzmoFile.getRuleId(),
                mezzmoFile.getSong(),
                mezzmoFile.getNewSong(),
                mezzmoFile.getNewArtist()
        };
        getInstance().getJDBCTemplate().update(INSERT_MEZZMOFILE, params);
    }


}
