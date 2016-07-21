package be.home.mezzmo.domain.model;

/**
 * Created by Gebruiker on 21/07/2016.
 */
public class MGOPlaylistSQLTO {

    Integer id;
    Integer playlistId;
    Integer columnNum;
    Integer columnType;
    Integer Operand;
    String valueOneText;
    String valueTwoText;
    Integer valueOneInt;
    Integer valueTwoInt;
    Integer groupNr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getColumnNum() {
        return columnNum;
    }

    public void setColumnNum(Integer columnNum) {
        this.columnNum = columnNum;
    }

    public Integer getColumnType() {
        return columnType;
    }

    public void setColumnType(Integer columnType) {
        this.columnType = columnType;
    }

    public Integer getOperand() {
        return Operand;
    }

    public void setOperand(Integer operand) {
        Operand = operand;
    }

    public String getValueOneText() {
        return valueOneText;
    }

    public void setValueOneText(String valueOneText) {
        this.valueOneText = valueOneText;
    }

    public String getValueTwoText() {
        return valueTwoText;
    }

    public void setValueTwoText(String valueTwoText) {
        this.valueTwoText = valueTwoText;
    }

    public Integer getValueOneInt() {
        return valueOneInt;
    }

    public void setValueOneInt(Integer valueOneInt) {
        this.valueOneInt = valueOneInt;
    }

    public Integer getValueTwoInt() {
        return valueTwoInt;
    }

    public void setValueTwoInt(Integer valueTwoInt) {
        this.valueTwoInt = valueTwoInt;
    }

    public Integer getGroupNr() {
        return groupNr;
    }

    public void setGroupNr(Integer groupNr) {
        this.groupNr = groupNr;
    }
}
