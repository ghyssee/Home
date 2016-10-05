package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.model.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Gebruiker on 5/10/2016.
 */
public class MezzmoRowMappers {
    public static class FileRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileTO fileTO = new MGOFileTO();
            fileTO.setId(rs.getLong("FILEID"));
            fileTO.setFile(rs.getString("FILE"));
            fileTO.setPlayCount(rs.getInt("PLAYCOUNT"));
            fileTO.setRanking(rs.getInt("RANKING"));
            return fileTO;
        }
    }

    public static class AlbumRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MGOFileAlbumCompositeTO fileAlbumComposite = new MGOFileAlbumCompositeTO();
            MGOFileAlbumTO fileAlbumTO = fileAlbumComposite.getFileAlbumTO();
            MGOAlbumArtistTO albumArtistTO = fileAlbumComposite.getAlbumArtistTO();
            MGOFileTO fileTO = fileAlbumComposite.getFileTO();
            fileTO.setYear(rs.getInt("YEAR"));
            fileAlbumTO.setName(rs.getString("ALBUMNAME"));
            fileAlbumTO.setId(rs.getInt("ALBUMID"));
            albumArtistTO.setName(rs.getString("ALBUMARTISTNAME"));
            return fileAlbumComposite;
        }
    }

    public static class AlbumTrackRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
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
}
