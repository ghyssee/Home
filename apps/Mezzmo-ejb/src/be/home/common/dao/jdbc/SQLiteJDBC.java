package be.home.common.dao.jdbc;

/**
 * Created by ghyssee on 9/02/2016.
 */
import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.database.DatabaseColumn;
import be.home.common.model.DataBaseConfiguration;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.WinUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class SQLiteJDBC
{
    public static DataBaseConfiguration config = null;
    public static Map <String, DataBaseConfiguration.DataBase> dbMap = new HashMap <String, DataBaseConfiguration.DataBase>();
    protected Connection c = null;
    protected NamedParameterJdbcTemplate jdbcTemplate = null;
    private static final Logger log = Logger.getLogger(SQLiteJDBC.class);

    protected SQLiteJDBC() {
    }

    public void openDatabase(String db)  {

        DataBaseConfiguration.DataBase database = getDatabase(db);
        if (database == null){
            throw new RuntimeException("DB Id Not Found: " + db);
        }
        String path = database.path == null ? SQLiteJDBC.config.defaultPath : database.path;
        File file = new File(path + database.name);
        if (!file.exists()){
            throw new RuntimeException("DB Not Found: " + file.getAbsolutePath());
        }
        log.info("DB: " + file.getAbsolutePath());

        /*

        try {t
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            c.setAutoCommit(false);
            linkDatabase(database, c);
            log.info("Opened database successfully");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }*/

        /* Spring */
        SingleConnectionDataSource dataSource = jdbcDataSource();
        String url = "";
        if (config.sqlLogging.enabled){
            url = "p6spy:";
        }
        dataSource.setUrl("jdbc:" + url + "sqlite:" + file.getAbsolutePath());
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        linkDatabase(database, jdbcTemplate);

        log.info("Initializing done");
    }

    public SingleConnectionDataSource jdbcDataSource() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        //ds.setDriverClassName("org.sqlite.JDBC");
        if (config.sqlLogging.enabled) {
            ds.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver");
        }
        else {
            ds.setDriverClassName("org.sqlite.JDBC");
        }
        return ds;
    }

    /*
    public Connection getConnection() throws SQLException {

        Connection con= jdbcTemplate.getDataSource().getConnection();
        return con;
    }*/

    public JdbcOperations getJDBCTemplate(){
        return jdbcTemplate.getJdbcOperations();
    }

    public NamedParameterJdbcTemplate getNamedParameterJDBCTemplate(){
        return jdbcTemplate;
    }

    public void commit() throws SQLException {
        if (c != null){
            c.commit();
        }
    }

    public void linkDatabase (DataBaseConfiguration.DataBase db, NamedParameterJdbcTemplate jdbcTemplate)  {
        if (db.linkDB != null && db.linkDB.name != null) {
            Object[] params = {db.path + db.linkDB.name, db.linkDB.alias};
            jdbcTemplate.getJdbcOperations().update("ATTACH database ? AS ?", params);
        }
    }

    public void linkDatabaseOld (DataBaseConfiguration.DataBase db, Connection c) throws SQLException {
        if (db.linkDB != null && db.linkDB.name != null) {
            PreparedStatement stmt = c.prepareStatement("ATTACH database ? AS ?");
            boolean autoCommit = c.getAutoCommit();
            stmt.setString(1, db.path + db.linkDB.name);
            log.info("Attaching DB " + db.linkDB.name + " with Alias " + db.linkDB.alias);
            stmt.setString(2, db.linkDB.alias);
            c.setAutoCommit(true);
            stmt.execute();
            c.setAutoCommit(autoCommit);
            stmt.close();
        }
    }

    public static Integer getInteger(ResultSet rs, DatabaseColumn column) throws SQLException {
        return new Integer(rs.getInt(column.name()));
    }

    public static Long getLong(ResultSet rs, DatabaseColumn column) throws SQLException {
        return new Long(rs.getLong(column.name()));
    }

    public static Date getDate(ResultSet rs, DatabaseColumn column) throws SQLException {
        Long f = rs.getLong(column.name());
        return SQLiteUtils.convertToDate(f);
    }

    public static String getString(ResultSet rs, DatabaseColumn column) throws SQLException {
        return rs.getString(column.name());
    }


    public static void setInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null){
            ps.setNull(index, java.sql.Types.INTEGER);
        }
        else {
            ps.setInt(index, value.intValue());
        }
    }


    private void setValue(PreparedStatement pst, int index, Object object) throws SQLException {
        if (object == null){
            pst.setNull(index, Types.NULL);
        }
        else if (object instanceof String){
            pst.setString(index, (String) object);
        }
        else if (object instanceof Integer){
            pst.setInt(index, (Integer) object);
        }
        else if (object instanceof Long){
            pst.setLong(index, (Long) object);
        }
        else if (object instanceof Float){
            pst.setFloat(index, (Float) object);
        }
        else if (object instanceof BigDecimal){
            pst.setBigDecimal(index, (BigDecimal) object);
        }
        else if (object instanceof Boolean){
            pst.setBoolean(index, (Boolean) object);
        }
        else if (object instanceof Double){
            pst.setDouble(index, (Double) object);
        }
        else if (object instanceof java.util.Date){
            java.sql.Date tmpDate = new java.sql.Date(((Date) object).getTime());
            pst.setDate(index, tmpDate);
        }
        else if (object instanceof Timestamp){
            pst.setTimestamp(index, (Timestamp) object);
        }
    }

    protected int insertJDBC(JdbcOperations template, final Object[] params, final String sql, final String id){

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst =
                                con.prepareStatement(sql, new String[] {id});
                        int index = 1;
                        for (Object object : params) {
                            setValue(pst, index++, object);
                        }
                        return pst;
                    }
                },
                keyHolder);
        return (Integer)keyHolder.getKey();

    }

    protected int insertJDBC2(NamedParameterJdbcTemplate template, final Object[] params, final String sql, final String id){

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        /*
        template.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst =
                                con.prepareStatement(sql, new String[] {id});
                        int index = 1;
                        for (Object object : params) {
                            setValue(pst, index++, object);
                        }
                        return pst;
                    }
                },
                keyHolder);*/
        return (Integer)keyHolder.getKey();

    }

    public static void initialize(String workingDir){
        initialize();
    }

    public static void initialize(){
        InputStream i = null;
        File file = new File (Setup.getInstance().getFullPath(Constants.Path.LOCAL_CONFIG) + File.separator + "localDatabases.json");
        Map <String, DataBaseConfiguration.DataBase> map = new HashMap <String, DataBaseConfiguration.DataBase>();
        DataBaseConfiguration localConfig = null;
        if (file.exists()){
            log.debug("Local Database Configuration File found: " + file.getAbsolutePath());
            localConfig = (DataBaseConfiguration) JSONUtils.openJSON(file.getAbsolutePath(), DataBaseConfiguration.class);
            map = localConfig.getMap();
        }
        file = new File(Setup.getInstance().getFullPath(Constants.Path.CONFIG) + File.separator + "databases.json");
        log.debug("Master Database Configuration File found: " + file.getAbsolutePath());
        config = (DataBaseConfiguration) JSONUtils.openJSON(file.getAbsolutePath(), DataBaseConfiguration.class);
        List <DataBaseConfiguration.DataBase> newList = new ArrayList<DataBaseConfiguration.DataBase>();
        if (localConfig != null && localConfig.sqlLogging != null){
            config.sqlLogging = localConfig.sqlLogging;
        }
        log.info("SQL Logging = " + config.sqlLogging.enabled);
        for (DataBaseConfiguration.DataBase db : config.databases) {
            DataBaseConfiguration.DataBase tmpDB = null;
            if (map.containsKey(db.id)) {
                tmpDB = map.get(db.id);
                log.debug("Overriding DB Config with Local DB Config for id: " + db.id);
            }
            else {
                tmpDB = db;
            }
            replaceEnvrionmentVariables(tmpDB);
            newList.add(tmpDB);
        }
        config.databases = newList;



        log.info("Database Config file = " + file.getAbsolutePath());

    }

    private static void replaceEnvrionmentVariables(DataBaseConfiguration.DataBase db){
        final String ONEDRIVE = "%ONEDRIVE%";
        if (db.path.contains(ONEDRIVE)){
            db.path = db.path.replace(ONEDRIVE, WinUtils.getOneDrivePath());
        }
    }


    private DataBaseConfiguration.DataBase getDatabase (String Id){

        for (DataBaseConfiguration.DataBase db : config.databases){
            if (db.id.equals(Id)){
                return db;
            }
        }
        return null;
    }

}