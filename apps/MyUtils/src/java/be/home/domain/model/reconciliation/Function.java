package be.home.domain.model.reconciliation;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class Function extends Base {

    private DataType dataType;

    private FunctionType functionType;

    public static enum DataType {NUMBER, DATE, VARCHAR };
    public static enum FunctionType {M, R };


    public Function(String code, String description, DataType dataType, FunctionType functionType) {
        super(code, description);
        this.dataType = dataType;
        this.functionType = functionType;
    }

    public Function(String code, String description) {
        super(code, description);
    }

    public String getFunctionType() {
        return functionType.name();
    }

    public void setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
    }

    public String getDataType() {
        return dataType.name();
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

}
