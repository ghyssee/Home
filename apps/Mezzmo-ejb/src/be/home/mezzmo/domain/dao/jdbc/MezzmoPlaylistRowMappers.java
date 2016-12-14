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
                playlistTO.setID(getInt(rs, PlaylistEnum.ID));
                playlistTO.setName(getString(rs, PlaylistEnum.NAME));
                playlistTO.setType(getInt(rs, PlaylistEnum.TYPE));
                playlistTO.setDescription(getString(rs, PlaylistEnum.DESCRIPTION));
                playlistTO.setParentID(getInt(rs, PlaylistEnum.PARENTID));
                playlistTO.setAuthor(getInt(rs, PlaylistEnum.AUTHOR));
                playlistTO.setIcon(getInt(rs, PlaylistEnum.ICON));
                playlistTO.setFile(getString(rs, PlaylistEnum.FILE));
                playlistTO.setTraverseFolder(getString(rs, PlaylistEnum.TRAVERSEFOLDER));
                playlistTO.setFolderPath(getString(rs, PlaylistEnum.FOLDERPATH));
                playlistTO.setFilter(getString(rs, PlaylistEnum.FILTER));
                playlistTO.setDynamicTreeToken(getString(rs, PlaylistEnum.DYNAMICTREETOKEN));
                playlistTO.setRunTime(getString(rs, PlaylistEnum.RUNTIME));
                playlistTO.setStreamNum(getString(rs, PlaylistEnum.STREAMNUM));
                playlistTO.setOrderByColumn(getInt(rs, PlaylistEnum.ORDERBYCOLUMN));
                playlistTO.setOrderByDirection(getInt(rs, PlaylistEnum.ORDERBYDIRECTION));
                playlistTO.setLimitBy(getInt(rs, PlaylistEnum.LIMITBY));
                playlistTO.setCombineAnd(getInt(rs, PlaylistEnum.COMBINEAND));
                playlistTO.setLimitType(getInt(rs, PlaylistEnum.LIMITTYPE));
                playlistTO.setPlaylistOrder(getInt(rs, PlaylistEnum.PLAYLISTORDER));
                playlistTO.setMediaType(getInt(rs, PlaylistEnum.MEDIATYPE));
                playlistTO.setThumbnailID(getInt(rs, PlaylistEnum.THUMBNAILID));
                playlistTO.setThumbnailAuthor(getInt(rs, PlaylistEnum.THUMBNAILAUTHOR));
                playlistTO.setContentRatingID(getInt(rs, PlaylistEnum.CONTENTRATINGID));
                playlistTO.setBackdropArtworkID(getInt(rs, PlaylistEnum.BACKDROPARTWORKID));
                playlistTO.setDisplayTitleFormat(getString(rs, PlaylistEnum.DISPLAYTITLEFORMAT));
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

