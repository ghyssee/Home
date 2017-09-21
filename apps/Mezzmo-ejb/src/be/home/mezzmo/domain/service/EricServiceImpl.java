package be.home.mezzmo.domain.service;

import be.home.common.dao.jdbc.Databases;
import be.home.mezzmo.domain.bo.EricBO;
import be.home.mezzmo.domain.model.eric.MezzmoFileTO;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class EricServiceImpl {
    private static Map<Databases, EricServiceImpl> instances = new HashMap<>();
    private static final Logger log = Logger.getLogger(MezzmoServiceImpl.class);
    private Databases db = null;

    private EricServiceImpl() {
    }

    protected EricServiceImpl(Databases db) {
        this.db = db;
    }

    private String getDatabase() {
        return db.name();
    }

    public void insertMezzmoFile(MezzmoFileTO mezzmoFile) throws SQLException {
        EricBO bo = getEricBO();
        bo.insertMezzmoFile(mezzmoFile);
    }

    private EricBO getEricBO() {
        return new EricBO(db.name());
    }

    public synchronized static EricServiceImpl getInstance() {
        Databases db = Databases.ERIC;
        if (instances.get(db) == null) {
            EricServiceImpl instance = new EricServiceImpl(db);
            instances.put(db, instance);
        }
        return instances.get(db);
    }

    public synchronized static EricServiceImpl getInstance(Databases db) {
        if (instances.get(db) == null) {
            EricServiceImpl instance = new EricServiceImpl(db);
            instances.put(db, instance);
        }
        return instances.get(db);

    }

    public String getDatabaseName(){
        return db.name();
    }



}
