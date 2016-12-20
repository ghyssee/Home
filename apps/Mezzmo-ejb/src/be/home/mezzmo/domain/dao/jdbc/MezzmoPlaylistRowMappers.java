package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.dao.definition.PlaylistColumns;
import be.home.mezzmo.domain.model.MGOPlaylistTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ghyssee on 14/12/2016.
 */
public class MezzmoPlaylistRowMappers {

        public static class PlaylistRowMapper implements RowMapper {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                MGOPlaylistTO playlistTO = new MGOPlaylistTO();
                playlistTO.setID(getInt(rs, PlaylistColumns.ID));
                playlistTO.setName(getString(rs, PlaylistColumns.NAME));
                playlistTO.setType(getInt(rs, PlaylistColumns.TYPE));
                playlistTO.setDescription(getString(rs, PlaylistColumns.DESCRIPTION));
                playlistTO.setParentID(getInt(rs, PlaylistColumns.PARENTID));
                playlistTO.setAuthor(getInt(rs, PlaylistColumns.AUTHOR));
                playlistTO.setIcon(getInt(rs, PlaylistColumns.ICON));
                playlistTO.setFile(getString(rs, PlaylistColumns.FILE));
                playlistTO.setTraverseFolder(getString(rs, PlaylistColumns.TRAVERSEFOLDER));
                playlistTO.setFolderPath(getString(rs, PlaylistColumns.FOLDERPATH));
                playlistTO.setFilter(getString(rs, PlaylistColumns.FILTER));
                playlistTO.setDynamicTreeToken(getString(rs, PlaylistColumns.DYNAMICTREETOKEN));
                playlistTO.setRunTime(getString(rs, PlaylistColumns.RUNTIME));
                playlistTO.setStreamNum(getString(rs, PlaylistColumns.STREAMNUM));
                playlistTO.setOrderByColumn(getInt(rs, PlaylistColumns.ORDERBYCOLUMN));
                playlistTO.setOrderByDirection(getInt(rs, PlaylistColumns.ORDERBYDIRECTION));
                playlistTO.setLimitBy(getInt(rs, PlaylistColumns.LIMITBY));
                playlistTO.setCombineAnd(getInt(rs, PlaylistColumns.COMBINEAND));
                playlistTO.setLimitType(getInt(rs, PlaylistColumns.LIMITTYPE));
                playlistTO.setPlaylistOrder(getInt(rs, PlaylistColumns.PLAYLISTORDER));
                playlistTO.setMediaType(getInt(rs, PlaylistColumns.MEDIATYPE));
                playlistTO.setThumbnailID(getInt(rs, PlaylistColumns.THUMBNAILID));
                playlistTO.setThumbnailAuthor(getInt(rs, PlaylistColumns.THUMBNAILAUTHOR));
                playlistTO.setContentRatingID(getInt(rs, PlaylistColumns.CONTENTRATINGID));
                playlistTO.setBackdropArtworkID(getInt(rs, PlaylistColumns.BACKDROPARTWORKID));
                playlistTO.setDisplayTitleFormat(getString(rs, PlaylistColumns.DISPLAYTITLEFORMAT));
                return playlistTO;

            }
        }

        public static int getInt(ResultSet rs, Enum enumValue) throws SQLException {
                return rs.getInt(enumValue.name());
        }

        public static String getString(ResultSet rs, Enum enumValue) throws SQLException {
            return rs.getString(enumValue.name());
        }
    }

