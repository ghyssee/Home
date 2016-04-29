package be.home.common.model;

import be.home.common.dao.jdbc.SQLiteJDBC;

/**
 * Created by ghyssee on 28/04/2016.
 */
public class BusinessObject {
    SQLiteJDBC dbInstance = null;
    public SQLiteJDBC getDbInstance() {
        return dbInstance;
    }
    public void setDbInstance(SQLiteJDBC dbInstance) {
        this.dbInstance = dbInstance;
    }


}
