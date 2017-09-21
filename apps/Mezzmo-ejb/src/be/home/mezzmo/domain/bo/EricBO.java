package be.home.mezzmo.domain.bo;

import be.home.common.model.BusinessObject;
import be.home.mezzmo.domain.dao.jdbc.EricDAOImpl;
import be.home.mezzmo.domain.model.eric.MezzmoFileTO;

import java.sql.SQLException;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class EricBO extends BusinessObject {

    private String db = null;

    public EricBO (){
    }

    public EricBO (String db){
        this.db = db;
    }


    public void insertMezzmoFile(MezzmoFileTO mezzmoFile) throws SQLException {
        getEricDAOImpl().insertMezzmoFile(mezzmoFile);
    }


    public EricDAOImpl getEricDAOImpl(){
        return new EricDAOImpl(db);
    }

}
