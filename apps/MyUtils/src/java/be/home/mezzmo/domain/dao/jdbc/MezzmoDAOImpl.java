package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileAlbumTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import be.home.model.DataBaseConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MezzmoDAOImpl extends MezzmoDB {

    private static final String FILE_SELECT = "select file from MGOFile where File like '%Boyzone%';";

    private static final String COLUMNS[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING",
            "MGOFile.FileTitle as FILETITLE", "MGOFile.DateLastPlayed as DATELASTPLAYED",
            "MGOFileAlbum.ID as FILEALBUMID", "MGOFileAlbum.Data as ALBUMNAME" };

    private static final String COLUMNS_FILE[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};

    private static final String FILEALBUM_SELECT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
     " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
     " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
     " WHERE 1=1" +
     " AND MGOFileAlbum.data like ?"; //"'Ultratop 50 2015%'";

    private static final String FILE_SELECT_TITLE_OLD = "SELECT " + getColumns(COLUMNS_FILE) + " FROM MGOFile " +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?"; //"'Ultratop 50 2015%'";

    private static final String FILE_SELECT_TITLE = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?" +
            " AND MGOFileAlbum.data like ?";

    private static final String FILE_PLAYCOUNT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFile.extensionID)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " AND MGOFile.PlayCount > 0";

    private static final String FILE_UPDATE_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ? " +
            " ,DateLastPlayed = ? " +
            " WHERE FileTitle = ? " +
            " AND PlayCount < ? " +
            " AND ID IN (" +
            " SELECT FileID FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?" +
            " AND MGOFile.FileTitle like ?" +
            ")";

    public static String getColumns(String[] columns){
        String col = "";
        for (int i=0; i < columns.length; i++){
            if (i > 0){
                col += ", ";
            }
            col += columns[i];
        }
        return col;
    }

    public void selectFile()
    {
        Statement stmt = null;
        try {
            Connection c = getInstance().getConnection();

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( FILE_SELECT );
            while ( rs.next() ) {
                String  file = rs.getString("file");
                System.out.println( "File = " + file );
                System.out.println();
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }

    public List<MGOFileAlbumCompositeTO> getFileAlbum(String album)
    {
        System.out.println("Get List of Mp3's for specific album(s)");
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FILEALBUM_SELECT);
            stmt.setString(1, album);
            System.out.println(FILEALBUM_SELECT);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                fileTO.setId(rs.getInt("FILEID"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setRanking(rs.getInt("RANKING"));
                fileTO.setDateLastPlayed(SQLiteUtils.convertToDate(rs.getLong("DATELASTPLAYED")));
                Long f= rs.getLong("DATELASTPLAYED");
                SQLiteUtils.convertToDate(f);
                fileAlbumTO.setId(rs.getInt("FILEALBUMID"));
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Number of rows retrieved: " + list.size());
        return list;
    }

    public List<MGOFileTO> getFiles(MGOFileAlbumCompositeTO compSearchTO)
    {
        //System.out.println("Get List of Mp3's for specific FileTitle");
        PreparedStatement stmt = null;
        List<MGOFileTO> list = new ArrayList<MGOFileTO>();
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FILE_SELECT_TITLE);
            stmt.setString(1, compSearchTO.getFileTO().getFileTitle());
            if (compSearchTO.getFileAlbumTO() != null && compSearchTO.getFileAlbumTO().getName() != null){
                stmt.setString(2, compSearchTO.getFileAlbumTO().getName());
            }
            else {
                stmt.setString(2, "%");
            }
            //System.out.println(FILE_SELECT_TITLE);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileTO fileTO = new MGOFileTO();
                fileTO.setId(rs.getInt("FILEID"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setRanking(rs.getInt("RANKING"));
                list.add(fileTO);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        //System.out.println("Number of rows retrieved: " + list.size());
        return list;
    }

    public int updatePlayCount(String fileID, String album, int playCount, java.util.Date dateLastPlayed) throws SQLException {
        //System.out.println("Get List of Mp3's for specific FileTitle");
        PreparedStatement stmt = null;
        List<MGOFileTO> list = new ArrayList<MGOFileTO>();
        Connection c = null;
        int rec = 0;
        try {
            c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FILE_UPDATE_PLAYCOUNT);
            int idx = 1;
            stmt.setInt(idx++, playCount);
            stmt.setLong(idx++, SQLiteUtils.convertDateToLong(dateLastPlayed));
            stmt.setString(idx++, fileID);
            stmt.setInt(idx++, playCount);
            stmt.setString(idx++, album == null ? "%" : album);
            stmt.setString(idx++, fileID);
            //System.out.println(FILE_SELECT_TITLE);
            rec =  stmt.executeUpdate();
            c.commit();
        }
    catch (SQLException e ) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        if (c != null) {
            try {
                System.err.print("Transaction is being rolled back");
                c.rollback();
            } catch(SQLException excep) {
                System.err.println( excep.getClass().getName() + ": " + excep.getMessage() );
            }
        }
    } finally {
        if (stmt != null) {
            stmt.close();
        }
    }
        return rec;
        //System.out.println("Number of rows retrieved: " + list.size());
    }

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount()
    {
        //System.out.println("Get List of Mp3's for specific FileTitle");
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FILE_PLAYCOUNT);
            //System.out.println(FILE_SELECT_TITLE);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setFile(rs.getString("DATELASTPLAYED"));
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        //System.out.println("Number of rows retrieved: " + list.size());
        return list;
    }


}
