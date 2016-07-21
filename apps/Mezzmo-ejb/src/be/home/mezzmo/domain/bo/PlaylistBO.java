package be.home.mezzmo.domain.bo;

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

    public List<String> validateCondition(PlaylistSetup.Condition condition){
        MGOPlaylistSQLTO playlistSQL = new MGOPlaylistSQLTO();
        log.info("Processing condition for field " + condition.field);
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
}
