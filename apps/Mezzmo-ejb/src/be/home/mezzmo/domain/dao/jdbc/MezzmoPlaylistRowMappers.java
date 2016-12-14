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
                playlistTO.setID(getInt(rs, PlayListEnum.ID));
                playlistTO.setName(getString(rs, PlayListEnum.NAME));
                playlistTO.setType(getInt(rs, PlayListEnum.TYPE));
                playlistTO.setDescription(getString(rs, PlayListEnum.DESCRIPTION));
                playlistTO.setParentID(getInt(rs, PlayListEnum.PARENTID));
                playlistTO.setAuthor(getInt(rs, PlayListEnum.AUTHOR));
                playlistTO.setIcon(getInt(rs, PlayListEnum.ICON));
                playlistTO.setFile(getString(rs, PlayListEnum.FILE));
                playlistTO.setTraverseFolder(getString(rs, PlayListEnum.TRAVERSEFOLDER));
                playlistTO.setFolderPath(getString(rs, PlayListEnum.FOLDERPATH));
                playlistTO.setFilter(getString(rs, PlayListEnum.FILTER));
                playlistTO.setDynamicTreeToken(getString(rs, PlayListEnum.DYNAMICTREETOKEN));
                playlistTO.setRunTime(getString(rs, PlayListEnum.RUNTIME));
                playlistTO.setStreamNum(getString(rs, PlayListEnum.STREAMNUM));
                playlistTO.setOrderByColumn(getInt(rs, PlayListEnum.ORDERBYCOLUMN));
                playlistTO.setOrderByDirection(getInt(rs, PlayListEnum.ORDERBYDIRECTION));
                playlistTO.setLimitBy(getInt(rs, PlayListEnum.LIMITBY));
                playlistTO.setCombineAnd(getInt(rs, PlayListEnum.COMBINEAND));
                playlistTO.setLimitType(getInt(rs, PlayListEnum.LIMITTYPE));
                playlistTO.setPlaylistOrder(getInt(rs, PlayListEnum.PLAYLISTORDER));
                playlistTO.setMediaType(getInt(rs, PlayListEnum.MEDIATYPE));
                playlistTO.setThumbnailID(getInt(rs, PlayListEnum.THUMBNAILID));
                playlistTO.setThumbnailAuthor(getInt(rs, PlayListEnum.THUMBNAILAUTHOR));
                playlistTO.setContentRatingID(getInt(rs, PlayListEnum.CONTENTRATINGID));
                playlistTO.setBackdropArtworkID(getInt(rs, PlayListEnum.BACKDROPARTWORKID));
                playlistTO.setDisplayTitleFormat(getString(rs, PlayListEnum.DISPLAYTITLEFORMAT));
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

