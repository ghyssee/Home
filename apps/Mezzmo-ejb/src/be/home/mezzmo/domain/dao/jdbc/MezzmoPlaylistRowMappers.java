package be.home.mezzmo.domain.dao.jdbc;

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
                playlistTO.setID(getInt(rs, PlayListColumns.ID));
                playlistTO.setName(getString(rs, PlayListColumns.NAME));
                playlistTO.setType(getInt(rs, PlayListColumns.TYPE));
                playlistTO.setDescription(getString(rs, PlayListColumns.DESCRIPTION));
                playlistTO.setParentID(getInt(rs, PlayListColumns.PARENTID));
                playlistTO.setAuthor(getInt(rs, PlayListColumns.AUTHOR));
                playlistTO.setIcon(getInt(rs, PlayListColumns.ICON));
                playlistTO.setFile(getString(rs, PlayListColumns.FILE));
                playlistTO.setTraverseFolder(getString(rs, PlayListColumns.TRAVERSEFOLDER));
                playlistTO.setFolderPath(getString(rs, PlayListColumns.FOLDERPATH));
                playlistTO.setFilter(getString(rs, PlayListColumns.FILTER));
                playlistTO.setDynamicTreeToken(getString(rs, PlayListColumns.DYNAMICTREETOKEN));
                playlistTO.setRunTime(getString(rs, PlayListColumns.RUNTIME));
                playlistTO.setStreamNum(getString(rs, PlayListColumns.STREAMNUM));
                playlistTO.setOrderByColumn(getInt(rs, PlayListColumns.ORDERBYCOLUMN));
                playlistTO.setOrderByDirection(getInt(rs, PlayListColumns.ORDERBYDIRECTION));
                playlistTO.setLimitBy(getInt(rs, PlayListColumns.LIMITBY));
                playlistTO.setCombineAnd(getInt(rs, PlayListColumns.COMBINEAND));
                playlistTO.setLimitType(getInt(rs, PlayListColumns.LIMITTYPE));
                playlistTO.setPlaylistOrder(getInt(rs, PlayListColumns.PLAYLISTORDER));
                playlistTO.setMediaType(getInt(rs, PlayListColumns.MEDIATYPE));
                playlistTO.setThumbnailID(getInt(rs, PlayListColumns.THUMBNAILID));
                playlistTO.setThumbnailAuthor(getInt(rs, PlayListColumns.THUMBNAILAUTHOR));
                playlistTO.setContentRatingID(getInt(rs, PlayListColumns.CONTENTRATINGID));
                playlistTO.setBackdropArtworkID(getInt(rs, PlayListColumns.BACKDROPARTWORKID));
                playlistTO.setDisplayTitleFormat(getString(rs, PlayListColumns.DISPLAYTITLEFORMAT));
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

