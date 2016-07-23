package be.home.mezzmo.domain.bo;

import be.home.mezzmo.domain.dao.jdbc.MezzmoPlaylistDAOImpl;
import be.home.mezzmo.domain.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gebruiker on 20/07/2016.
 */
public class PlaylistBO {

    private static final Logger log = Logger.getLogger(PlaylistBO.class);

    public List<String> validatePlaylist(PlaylistSetup.PlaylistRecord pr){
        log.info("Validating " + pr.name);
        List<String> errors = new ArrayList <String>();
        validateOrderByColumn(errors, pr.orderByColumn);
        validateSorting(errors, pr.sorting);
        return errors;
    }

    public List<String> validateAndInsertCondition(PlaylistSetup.Condition condition, Integer playlistID){
        log.info("Processing condition for field " + condition.field);
        MGOPlaylistSQLTO playlistSQL = new MGOPlaylistSQLTO();
        playlistSQL.setPlaylistId(playlistID);
        List<String> errors = validateAndFillCondition(playlistSQL, condition);
        if (errors.size() == 0){
            getMezzmoPlaylistDAO().insertPlaylistSQL(playlistSQL);
        }
        return errors;
    }

    private List<String> validateAndFillCondition(MGOPlaylistSQLTO playlistSQL, PlaylistSetup.Condition condition){
        String field = condition.field;
        List<String> errors = new ArrayList<String>();
        if (StringUtils.isBlank(field)){
            errors.add("Field can't be empty");
        }
        else if (ColumnNum.get(field) == null){
            errors.add("Invalid ColumnNum: " + field);
        }
        else {
            ColumnNum column = ColumnNum.get(field);
            playlistSQL.setColumnNum(column.getValue());
            ColumnType type = column.getColumnType();
            playlistSQL.setColumnType(type.getValue());
            if (StringUtils.isBlank(condition.valueOne)) {
                errors.add("Value One can't be empty");
            }
            else if (type.equals(ColumnType.Number)) {
                playlistSQL.setValueOneInt(validateValueInt(errors, condition.valueOne, Value.One));
                if (!StringUtils.isBlank(condition.valueTwo)) {
                    playlistSQL.setValueTwoInt(validateValueInt(errors, condition.valueTwo, Value.Two));
                }
                else {
                    playlistSQL.setValueTwoInt(new Integer(0));
                }
            }
            else {
                playlistSQL.setValueOneText(condition.valueOne);
                playlistSQL.setValueTwoText(condition.valueTwo == null ? "" : condition.valueTwo);
            }
        }
        playlistSQL.setOperand(validateOperand(errors, condition.operand));
        playlistSQL.setGroupNr(0);
        return errors;

    }

    private Integer validateValueInt(List <String> errors, String strValue, Value value){
        Integer intValue = new Integer(0);
        try {
            int tmpValue = Integer.parseInt(strValue);
            intValue = new Integer(tmpValue);
        } catch (NumberFormatException pe) {
            errors.add("Value " + value.name() + " must be numeric: " + strValue);
        }
        return intValue;
    }

    public Integer validateOrderByColumn(List <String> errors, String orderBy){
        Integer retParam = OrderByColumn.Title.getValue();
        if (StringUtils.isNotBlank(orderBy)) {
            OrderByColumn o = OrderByColumn.get(orderBy);
            if (o == null) {
                errors.add("Invalid orderBy: " + orderBy);
            } else {
                retParam = o.getValue();
            }
        }

        return retParam;
    }

    public Integer validateSorting(List <String> errors, String sorting){
        Integer retParam = Sorting.Ascending.getValue();
        if (StringUtils.isNotBlank(sorting)) {
            Sorting s = Sorting.get(sorting);
            if (s == null) {
                errors.add("Invalid sorting: " + sorting);
            } else {
                retParam = s.getValue();
            }
        }

        return retParam;
    }

    public Integer validateMediaType(List <String> errors, String mediaType){
        Integer retParam = null;
        if (StringUtils.isNotBlank(mediaType)) {
            MediaType m = MediaType.get(mediaType);
            if (m == null) {
                errors.add("Invalid mediaType: " + mediaType);
            } else {
                retParam = m.getValue();
            }
        }

        return retParam;
    }

    public Integer validateCombineAnd(List <String> errors, String combineAnd){
        Integer retParam = null;
        if (StringUtils.isNotBlank(combineAnd)) {
            CombineAnd m = CombineAnd.get(combineAnd);
            if (m == null) {
                errors.add("Invalid combineAnd: " + combineAnd);
            } else {
                retParam = m.getValue();
            }
        }

        return retParam;
    }

    private Integer validateOperand(List <String> errors, String operand){
        Integer retOperand = null;
        if (StringUtils.isBlank(operand)){
            errors.add("Operand can't be empty");
        }
        else if (Operand.get(operand) == null){
            errors.add("Invalid Operand: " + operand);
        }
        else {
            retOperand = Operand.get(operand).getValue();
        }

        return retOperand;

    }

    public int cleanUpPlaylist (Integer playlistId) {
        return getMezzmoPlaylistDAO().cleanUpPlaylistSQL(playlistId);

    }


    public MezzmoPlaylistDAOImpl getMezzmoPlaylistDAO(){
        return new MezzmoPlaylistDAOImpl();
    }

}
