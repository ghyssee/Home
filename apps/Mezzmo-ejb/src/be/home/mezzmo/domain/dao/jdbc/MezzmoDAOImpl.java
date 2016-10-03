package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.MezzmoDB;
import be.home.common.dao.jdbc.QueryBuilder;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.exceptions.MultipleOccurencesException;
import be.home.common.model.TransferObject;
import be.home.mezzmo.domain.model.*;
import org.apache.log4j.Logger;

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
            "MGOFile.Ranking as RANKING",
            "MGOFileAlbum.ID as FILEALBUMID", "MGOFileAlbum.Data as ALBUMNAME" };

    private static final String COLUMNS_FILE[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};

    private static final String COLUMNS_ALBUMS[] = {"MGOFile.ID as FILEID", "MGOFile.File as FILE",
            "MGOFile.PlayCount as PLAYCOUNT", "MGOFile.Ranking as RANKING"};


    private static final String FILEALBUM_SELECT = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?"; //"'Ultratop 50 2015%'";

    private static final String FILE_FIND_TAGINFO = "SELECT " + getColumns(COLUMNS) + " FROM MGOFile " +
            " INNER JOIN MGOFileAlbumRelationship ON (MGOFileAlbumRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.FileID = MGOFILE.id)" +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.ID = MGOFileArtistRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.FileID = MGOFILE.id)" +
            " WHERE MGOFileExtension.data = 'mp3'" +
            " AND MGOFile.Track like ?" +
            " AND MGOFileArtist.data like ?" +
            " AND MGOFile.Title like ?" +
            " AND MGOFileAlbum.data like ?";

    private static final String FILE_SELECT_TITLE_OLD = "SELECT " + getColumns(COLUMNS_FILE) + " FROM MGOFile " +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?"; //"'Ultratop 50 2015%'";

    private static final String FILE_SELECT_TITLE = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFile.FileTitle like ?" +
            " AND MGOFileAlbum.data like ?";

    private static final String FILE_FIND = "SELECT " + getColumns(COLUMNS) + " FROM MGOFileAlbumRelationship " +
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
            " AND MGOFile.PlayCount > 0" +
            " ORDER BY datetime(MGOFile.DateLastPlayed, 'unixepoch', 'localtime')  ASC" +
            " LIMIT ?,?";

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

    private static final String FILE_SYNC_PLAYCOUNT = "UPDATE MGOFile " +
            " SET PlayCount = ? " +
            " WHERE FileTitle = ? " +
            " AND ID IN (" +
            " SELECT FileID FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFile ON (MGOFileAlbumRelationship.FileID = MGOFile.ID)" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " WHERE 1=1" +
            " AND MGOFileAlbum.data like ?" +
            " AND MGOFile.FileTitle like ?" +
            ")";

    private static final String LIST_ALBUMS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
                                " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
                                " MGOFileAlbum.id AS ALBUMID," +
                                " MAX(MGOFile.Year) AS YEAR" +
                                " FROM MGOFileAlbumRelationship" +
                                " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
                                " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID)" +
                                " INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID)" +
                                " INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID)" +
                                " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
                                " WHERE 1=1" +
                                " AND MGOFileExtension.data = 'mp3'" +
                                " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
                                " ORDER BY MGOFileAlbum.data";

    private static final String LIST_ALBUMS_TRACKS = "SELECT DISTINCT MGOFileAlbum.data AS ALBUMNAME," +
            " MGOAlbumArtist.data AS ALBUMARTISTNAME," +
            " MGOFileAlbum.id AS ALBUMID," +
            " MGOFile.Track AS TRACK, " +
            " MGOFileArtist.data AS ARTIST, " +
            " MGOFile.title AS TITLE" +
            " FROM MGOFileAlbumRelationship" +
            " INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID)" +
            " INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID)" +
            " INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID)" +
            " INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID)" +
            " INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID)" +
            " INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = MGOFile.id) " +
            " INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id)" +
            " WHERE 1=1" +
            " AND MGOFileExtension.data = 'mp3'" +
            " GROUP BY ALBUMNAME, ALBUMARTISTNAME, ALBUMID" +
            " ORDER BY MGOFileAlbum.data" +
            " LIMIT 0,1000";

    private static final String LIST_TOP20 = "SELECT FileTitle AS FILETITLE, PlayCount AS PLAYCOUNT, Title AS TITLE, FA.DATA AS ARTIST, PLL.ID AS PLAYLIST_ID, MGoFile.ID AS FILE_ID, MGOFile.File AS FILE, MGOFile.duration AS DURATION" +
                                             " FROM MGOPlaylist_To_File AS PLF" +
                                             " INNER JOIN MGOFile ON (PLF.FileID = MGOFile.ID)" +
                                             " INNER JOIN MGOPlaylist AS PLL ON (PLF.PlayListID = PLL.ID)" +
                                             " INNER JOIN MGOFileArtistRelationship AS FAR ON (MGOFile.ID = FAR.FileID)" +
                                             " INNER JOIN MGOFileArtist AS FA ON (FAR.ID = FA.ID)" +
                                             " WHERE PLL.type = 32" +
                                             " AND PLL.Name = '11 Top Of The Moment'" +
                                             " ORDER BY PLF.rowid " +
                                             " LIMIT 0,20";

    private static final String FIND_BY_FILE = "SELECT ID AS FILEID " +
                                               "FROM MGOfile " +
                                               "WHERE UPPER(FILE) LIKE UPPER(?)";

    private static final String FIND_COVER_ART = "SELECT MGOFile.File AS FILE " +
                                                  "FROM MGOFileAlbumRelationship " +
                                                  "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
                                                  "INNER JOIN MGOFile ON (MGOFile.ID = MGOFileAlbumRelationship.fileID) " +
                                                  "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
                                                  "WHERE 1=1 " +
                                                  "AND MGOFileExtension.data = 'mp3' " +
                                                  "AND MGOFileAlbum.id like ? " +
                                                  "LIMIT 0,1";

    private static final String LIST_CUSTOM = "SELECT MGOFile.File AS FILE, " +
                                                "MGOFile.FileTitle AS FILETITLE, " +
                                                "MGOFile.Duration AS DURATION, " +
                                                "MGOFile.Playcount AS PLAYCOUNT, " +
                                                "MGOFile.Title AS TITLE, " +
                                                "MGOFile.Id AS FILE_ID, " +
                                                "MGOFileAlbum.data AS ALBUM, " +
                                                "MGOFileArtist.data AS ARTIST " +
                                                "FROM MGOFileAlbumRelationship " +
                                                "INNER JOIN MGOFileAlbum ON (MGOFileAlbum.ID = MGOFileAlbumRelationship.ID) " +
                                                "INNER JOIN MGOAlbumArtistRelationship ON (MGOAlbumArtistRelationship.fileID = MGOFileAlbumRelationship.fileID) " +
                                                "INNER JOIN MGOAlbumArtist ON (MGOAlbumArtist.ID = MGOAlbumArtistRelationship.ID) " +
                                                "INNER JOIN MGOFile ON (MGOFile.ID = MGOAlbumArtistRelationship.fileID) " +
                                                "INNER JOIN MGOFileExtension ON (MGOFileExtension.ID = MGOFILE.extensionID) " +
                                                "INNER JOIN MGOFileArtistRelationship ON (MGOFileArtistRelationship.fileID = MGOFile.id) " +
                                                "INNER JOIN MGOFileArtist ON (MGOFileArtist.id = MGOFileArtistRelationship.id) " +
                                                "WHERE 1=1 " +
                                                "AND MGOFileExtension.data = 'mp3' " +
                                                "AND ( " +
                                                "{WHERE} " +
                                                ") " +
                                                "ORDER BY RANDOM() " +
                                                "LIMIT 0,? ";

    private static final String FILE_UPDATE_RATING = "UPDATE MGOFile " +
            " SET Ranking = ? " +
            " WHERE ID = ?";

    private static final Logger log = Logger.getLogger(MezzmoDAOImpl.class);


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
                System.err.println("Transaction is being rolled back");
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

    public int synchronizePlayCount(String fileID, String album, int playCount) throws SQLException {
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
                    System.err.println("Transaction is being rolled back");
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

    public List<MGOFileAlbumCompositeTO> getMP3FilesWithPlayCount(TransferObject to)
    {
        //System.out.println("Get List of Mp3's for specific FileTitle");
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FILE_PLAYCOUNT);
            stmt.setLong(1, to.getIndex());
            stmt.setLong(2, to.getLimit());
            //System.out.println(FILE_SELECT_TITLE);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setFile(rs.getString("FILE"));
                Long f= rs.getLong("DATELASTPLAYED");
                fileTO.setDateLastPlayed(SQLiteUtils.convertToDate(f));
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        if (list.size() == 0 || list.size() < to.getLimit()){
            to.setEndOfList(true);
        }
        to.addIndex(list.size());
        //System.out.println("Number of rows retrieved: " + list.size());
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getAlbums(TransferObject to)
    {
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(LIST_ALBUMS);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                MGOAlbumArtistTO albumArtistTO = fileAlbumComposite.getAlbumArtistTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setYear(rs.getInt("YEAR"));
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
                fileAlbumTO.setId(rs.getInt("ALBUMID"));
                albumArtistTO.setName(rs.getString("ALBUMARTISTNAME"));
                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getAlbumTracks(TransferObject to)
    {
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(LIST_ALBUMS_TRACKS);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                MGOAlbumArtistTO albumArtistTO = fileAlbumComposite.getAlbumArtistTO();
                MGOFileArtistTO artistTO = fileAlbumComposite.getFileArtistTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setTrack(rs.getInt("TRACK"));
                fileTO.setTitle(rs.getString("TITLE"));
                artistTO.setArtist(rs.getString("ARTIST"));
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
                fileAlbumTO.setId(rs.getInt("ALBUMID"));
                albumArtistTO.setName(rs.getString("ALBUMARTISTNAME"));
                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return list;
    }

    public List<MGOFileAlbumCompositeTO> getTop20()
    {
        PreparedStatement stmt = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(LIST_TOP20);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setId(rs.getInt("FILE_ID"));
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setTitle(rs.getString("TITLE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setDuration(rs.getInt("DURATION"));
                MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
                fileArtistTO.setArtist(rs.getString("ARTIST"));
                MGOPlaylistTO playlistTO = fileAlbumComposite.getPlaylistTO();
                playlistTO.setID(rs.getInt("PLAYLIST_ID"));
                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return list;
    }

    public MGOFileTO findByFile(String file){
        MGOFileTO fileTO = null;
        PreparedStatement stmt = null;
        boolean error = false;
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FIND_BY_FILE);
            stmt.setString(1, file);
            ResultSet rs = stmt.executeQuery();
            int counter = 0;
            while ( rs.next() ) {
                if (counter > 0){
                    error = true;
                    break;
                }
                fileTO = new MGOFileTO();
                fileTO.setId(rs.getInt("FILEID"));
                counter++;
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        if (error){
            throw new MultipleOccurencesException("FILE: " + file);
        }
        return fileTO;
    }

    public MGOFileTO findByTitleAndAlbum(MGOFileAlbumCompositeTO comp){
        MGOFileTO fileTO = null;
        PreparedStatement stmt = null;
        boolean error = false;
        try {
            Connection c = getInstance().getConnection();

            stmt = c.prepareStatement(FILE_FIND_TAGINFO);
            int index = 1;
            stmt.setLong(index++, comp.getFileTO().getTrack());
            stmt.setString(index++, comp.getFileArtistTO().getArtist());
            stmt.setString(index++, comp.getFileTO().getTitle());
            stmt.setString(index++, comp.getFileAlbumTO().getName());
            ResultSet rs = stmt.executeQuery();
            int counter = 0;
            while ( rs.next() ) {
                if (counter > 0){
                    error = true;
                    break;
                }
                fileTO = new MGOFileTO();
                fileTO.setId(rs.getInt("FILEID"));
                fileTO.setRanking(rs.getInt("RANKING"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setDateLastPlayed(SQLiteUtils.convertToDate(rs.getLong("DATELASTPLAYED")));
                counter++;
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        if (error){
            throw new MultipleOccurencesException("FILE: " + comp.getFileTO().getTrack() + "/" +
                    comp.getFileArtistTO().getArtist() + "/" + comp.getFileTO().getTitle());
        }
        return fileTO;
    }

    public MGOFileTO findCoverArt(int albumId){
        MGOFileTO fileTO = null;
        PreparedStatement stmt = null;
        boolean error = false;
        try {
            Connection c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FIND_COVER_ART);
            stmt.setInt(1, albumId);
            ResultSet rs = stmt.executeQuery();
            int counter = 0;
            while ( rs.next() ) {
                if (counter > 0){
                    error = true;
                    break;
                }
                fileTO = new MGOFileTO();
                fileTO.setFile(rs.getString("FILE"));
                counter++;
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        if (error){
            throw new MultipleOccurencesException("AlbumId: " + albumId);
        }
        return fileTO;
    }

    public List<MGOFileAlbumCompositeTO> getCustomPlayListSongs(List <MGOFileAlbumCompositeTO> albums, int limit)
    {
        PreparedStatement stmt = null;
        Statement st = null;
        List<MGOFileAlbumCompositeTO> list = new ArrayList<MGOFileAlbumCompositeTO>();
        try {
            Connection c = getInstance().getConnection();

            //stmt.
            String query = LIST_CUSTOM;
            String orClause = "(MGOFileAlbum.data like ? AND MGOFile.ranking > ? AND MGOAlbumArtist.data like ? AND MGOFile.playcount < 2) ";
            query = QueryBuilder.buildOrCondition(query, orClause, albums);
            log.debug("Custom Playlist Query: " + query);
            stmt = c.prepareStatement(query);
            int index = 1;
            for (MGOFileAlbumCompositeTO album : albums){
                stmt.setString(index++, album.getFileAlbumTO().getName());
                stmt.setLong(index++, 0);
                String albumArtist = album.getAlbumArtistTO().getName();
                if (albumArtist != null){
                    System.out.println("albumArtist " + albumArtist);
                }
                stmt.setString(index++, albumArtist == null ? "%" : albumArtist);
            }
            stmt.setInt(index++, limit);
            ResultSet rs = stmt.executeQuery();
            while ( rs.next() ) {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setId(rs.getInt("FILE_ID"));
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setTitle(rs.getString("TITLE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setDuration(rs.getInt("DURATION"));

                MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
                fileArtistTO.setArtist(rs.getString("ARTIST"));

                MGOFileAlbumTO fileAlbum = fileAlbumComposite.getFileAlbumTO();
                fileAlbum.setName(rs.getString("ALBUM"));

                list.add(fileAlbumComposite);
            }
            rs.close();
            stmt.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return list;
    }

    public int updateRanking(int fileID, int ranking) throws SQLException {
        //System.out.println("Get List of Mp3's for specific FileTitle");
        PreparedStatement stmt = null;
        List<MGOFileTO> list = new ArrayList<MGOFileTO>();
        Connection c = null;
        int rec = 0;
        try {
            c = getInstance().getConnection();

            //stmt = c.createStatement();
            stmt = c.prepareStatement(FILE_UPDATE_RATING);
            int idx = 1;
            stmt.setInt(idx++, ranking);
            stmt.setLong(idx++, fileID);
            rec =  stmt.executeUpdate();
            c.commit();
        }
        catch (SQLException e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            if (c != null) {
                try {
                    System.err.println("Transaction is being rolled back");
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
    }



}
