package be.home.domain.model.reconciliation;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class MatchPredicate {
    public String leftColumnName;
    public String fieldOperator;
    public String rightColumnName;
    public String relOperator;
    public String resultValue;
    public Function leftFunction;
    public Function rightFunction;
    public String warning;

    private String description;

    public MatchPredicate (String description, String leftColumnName, String fieldOperator, String rightColumnName, String relOperator,
            String resultValue, Function leftFunction, Function rightFunction, String warning){
        this.description = description;
        this.leftColumnName = leftColumnName;
        this.fieldOperator = fieldOperator;
        this.rightColumnName = rightColumnName;
        this.relOperator = relOperator;
        this.resultValue = resultValue;
        this.leftFunction = leftFunction;
        this.rightFunction = rightFunction;
        this.warning = warning;

    }

    public Function getRightFunction() {
        return rightFunction;
    }

    public void setRightFunction(Function rightFunction) {
        this.rightFunction = rightFunction;
    }

    public String getLeftColumnName() {
        return leftColumnName;
    }

    public void setLeftColumnName(String leftColumnName) {
        this.leftColumnName = leftColumnName;
    }

    public String getFieldOperator() {
        return fieldOperator;
    }

    public void setFieldOperator(String fieldOperator) {
        this.fieldOperator = fieldOperator;
    }

    public String getRightColumnName() {
        return rightColumnName;
    }

    public void setRightColumnName(String rightColumnName) {
        this.rightColumnName = rightColumnName;
    }

    public String getRelOperator() {
        return relOperator;
    }

    public void setRelOperator(String relOperator) {
        this.relOperator = relOperator;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public Function getLeftFunction() {
        return leftFunction;
    }

    public void setLeftFunction(Function leftFunction) {
        this.leftFunction = leftFunction;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
