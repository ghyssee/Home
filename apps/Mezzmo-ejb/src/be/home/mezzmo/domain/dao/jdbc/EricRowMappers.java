package be.home.mezzmo.domain.dao.jdbc;

import be.home.mezzmo.domain.dao.definition.eric.MezzmoFileColumns;
import be.home.mezzmo.domain.model.eric.MezzmoFileTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static be.home.common.dao.jdbc.SQLiteJDBC.getLong;

/**
 * Created by ghyssee on 21/09/2017.
 */
public class EricRowMappers extends EricDAOQueries {

    public static class MezzmoFileRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            MezzmoFileTO fileTO = mapFileTO(rs, rowNum);
            return fileTO;
        }
    }

    private static MezzmoFileTO mapFileTO(ResultSet rs, int rowNum)throws SQLException {
        MezzmoFileTO fileTO = new MezzmoFileTO();
        fileTO.setId(getLong(rs, MezzmoFileColumns.ID));
        fileTO.setArtistId(getLong(rs, MezzmoFileColumns.ARTISTID));
        return fileTO;
    }


}
