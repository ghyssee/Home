package be.home.mezzmo.domain.dao.jdbc;

import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.mezzmo.domain.dao.definition.*;
import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gebruiker on 5/10/2016.
 */
public class MezzmoRowMappers extends MezzmoDAOQueries {

    public static class FileNameRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileTO fileTO = new MGOFileTO();
            fileTO.setFile(rs.getString("FILE"));
            return fileTO;
        }
    }

    public static class AlbumRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            MGOAlbumArtistTO albumArtistTO = fileAlbumComposite.getAlbumArtistTO();
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setYear(getInteger(rs, MGOFileColumns.YEAR));
            fileAlbumComposite.setFileAlbumTO(mapFileAlbumTO(rs, rowNum));
            albumArtistTO.setName(getString(rs, MGOAlbumArtistColumns.ALBUMARTIST));
            return fileAlbumComposite;
        }
    }

    public static class AlbumTrackRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            fileAlbumComposite.getFileTO().setYear(getInteger(rs, MGOFileColumns.YEAR));
            fileAlbumComposite.setFileAlbumTO(mapFileAlbumTO(rs, rowNum));
            fileAlbumComposite.setAlbumArtistTO(mapAlbumArtistTO(rs, rowNum));
            fileAlbumComposite.setFileArtistTO(mapFileArtistTO(rs, rowNum));
            return fileAlbumComposite;
        }
    }

    public static class CustomAlbumRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setId(rs.getLong("FILE_ID"));
            fileTO.setFileTitle(rs.getString("FILETITLE"));
            fileTO.setFile(rs.getString("FILE"));
            fileTO.setTitle(rs.getString("TITLE"));
            fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
            fileTO.setDuration(rs.getInt("DURATION"));
            MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
            fileArtistTO.setArtist(rs.getString("ARTIST"));
            MGOFileAlbumTO fileAlbum = fileAlbumComposite.getFileAlbumTO();
            fileAlbum.setName(rs.getString("ALBUM"));
            return fileAlbumComposite;
        }
    }

    public static class FileAlbumRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setId(rs.getLong("FILE_ID"));
            fileTO.setFileTitle(rs.getString("FILETITLE"));
            fileTO.setFile(rs.getString("FILE"));
            fileTO.setTitle(rs.getString("TITLE"));
            fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
            fileTO.setDuration(rs.getInt("DURATION"));
            MGOFileArtistTO fileArtistTO = fileAlbumComposite.getFileArtistTO();
            fileArtistTO.setArtist(rs.getString("ARTIST"));
            MGOPlaylistTO playlistTO = fileAlbumComposite.getPlaylistTO();
            playlistTO.setID(rs.getInt("PLAYLIST_ID"));
            return fileAlbumComposite;
        }
    }

    public static class FileRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileTO fileTO = mapFileTO(rs, rowNum);
            return fileTO;
        }
    }

    public class FileAlbumPlayCountMapper implements RowMapper {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                fileAlbumComposite.setFileTO(mapFileTO(rs, rowNum));
                fileAlbumComposite.setFileAlbumTO(mapFileAlbumTO(rs, rowNum));
                fileAlbumComposite.setFileArtistTO(mapFileArtistTO(rs, rowNum));
                return fileAlbumComposite;
            }
        }

    private static MGOFileTO mapFileTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileTO fileTO = new MGOFileTO();
        fileTO.setId(getLong(rs, MGOFileColumns.ID));
        fileTO.setFileTitle(getString(rs, MGOFileColumns.FILETITLE));
        fileTO.setPlayCount(getInteger(rs, MGOFileColumns.PLAYCOUNT));
        fileTO.setRanking(getInteger(rs, MGOFileColumns.RANKING));
        fileTO.setFile(getString(rs, MGOFileColumns.FILE));
        fileTO.setTitle(getString(rs, MGOFileColumns.TITLE));
        fileTO.setDuration(getInteger(rs, MGOFileColumns.DURATION));
        fileTO.setTrack(getInteger(rs, MGOFileColumns.TRACK));
        fileTO.setDateLastPlayed(getDate(rs, MGOFileColumns.DATELASTPLAYED));
        fileTO.setYear(getInteger(rs, MGOFileColumns.YEAR));
        fileTO.setDisc(getInteger(rs, MGOFileColumns.DISC));
        return fileTO;
    }

    private static MGOFileAlbumTO mapFileAlbumTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileAlbumTO fileAlbumTO = new MGOFileAlbumTO();
        fileAlbumTO.setName(getString(rs, MGOFileAlbumColumns.ALBUM));
        fileAlbumTO.setId(getLong(rs, MGOFileAlbumColumns.ALBUMID));
        return fileAlbumTO;
    }

    private static MGOFileArtistTO mapFileArtistTO(ResultSet rs, int rowNum)throws SQLException{
        MGOFileArtistTO fileArtistTO = new MGOFileArtistTO();
        fileArtistTO.setArtist(getString(rs, MGOFileArtistColumns.ARTIST));
        fileArtistTO.setID(getLong(rs, MGOFileArtistColumns.ARTISTID));
        return fileArtistTO;
    }

    private static MGOAlbumArtistTO mapAlbumArtistTO(ResultSet rs, int rowNum)throws SQLException{
        MGOAlbumArtistTO albumArtistTO = new MGOAlbumArtistTO();
        albumArtistTO.setName(getString(rs, MGOAlbumArtistColumns.ALBUMARTIST));
        albumArtistTO.setId(getLong(rs, MGOAlbumArtistColumns.ALBUMARTISTID));
        return albumArtistTO;
    }

    public static class SongsAlbumRowMapperOld implements RowMapper {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
                MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
                fileAlbumTO.setName(rs.getString("ALBUMNAME"));
                fileAlbumTO.setId(rs.getLong("ALBUMID"));
                MGOFileArtistTO artistTO = fileAlbumComposite.getFileArtistTO();
                MGOFileTO fileTO = fileAlbumComposite.getFileTO();
                fileTO.setId(rs.getLong("FILEID"));
                fileTO.setFile(rs.getString("FILE"));
                fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
                fileTO.setTitle(rs.getString("TITLE"));
                fileTO.setFileTitle(rs.getString("FILETITLE"));
                artistTO.setID(rs.getLong("ARTISTID"));
                artistTO.setArtist(rs.getString("ARTIST"));
                fileTO.setDuration(rs.getInt("DURATION"));
                fileTO.setDisc(rs.getInt("DISC"));
                fileTO.setTrack(rs.getInt("TRACK"));
                fileTO.setRanking(rs.getInt("RANKING"));
                Long f= rs.getLong("DATELASTPLAYED");
                fileTO.setDateLastPlayed(SQLiteUtils.convertToDate(f));
                return fileAlbumComposite;
            }
        }

    public static class SongsAlbumRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            fileAlbumComposite.setFileTO(mapFileTO(rs, rowNum));
            fileAlbumComposite.setFileAlbumTO(mapFileAlbumTO(rs, rowNum));
            fileAlbumComposite.setFileArtistTO(mapFileArtistTO(rs, rowNum));
            fileAlbumComposite.setAlbumArtistTO(mapAlbumArtistTO(rs, rowNum));
            return fileAlbumComposite;
        }
    }

    public class MaxDiscRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
            comp.getFileTO().setDisc(getInteger(rs, MGOFileColumns.DISC));
            comp.getFileAlbumTO().setName(getString(rs, MGOFileAlbumColumns.ALBUM));
            comp.getAlbumArtistTO().setName(getString(rs, MGOAlbumArtistColumns.ALBUMARTIST));
            return comp;
        }
    }

    public static class ArtistRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileArtistTO artist = mapFileArtistTO(rs, rowNum);

            return artist;
        }
    }

    public static class FileArtistRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO comp = new MGOFileAlbumCompositeTO();
            comp.getFileTO().setId(getLong(rs, MGOFileArtistRelationshipColumns.FILEID));
            comp.getFileArtistTO().setID(getLong(rs, MGOFileArtistRelationshipColumns.ID));

            return comp;
        }
    }

}
