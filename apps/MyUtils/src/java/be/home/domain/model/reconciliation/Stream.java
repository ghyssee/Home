package be.home.domain.model.reconciliation;

import java.util.List;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class Stream extends Base {
    public String inputType;
    public String launchMatchEngine;
    public String datasourceCode;
    public List<Field> fields;

    public List<FileType> fileTypes;

    public Stream(String description, String inputType, String launchMatchEngine, String datasourceCode, List<FileType> fileTypes){
        super("", description);
        this.inputType = inputType;
        this.launchMatchEngine = launchMatchEngine;
        this.datasourceCode = datasourceCode;
        this.fileTypes = fileTypes;
    }

    public Stream(String description, String inputType, String launchMatchEngine, Datasource datasource, List<FileType> fileTypes){
        super("", description);
        this.inputType = inputType;
        this.launchMatchEngine = launchMatchEngine;
        this.datasourceCode = datasource.getCode();
        this.fileTypes = fileTypes;
    }

    public String getLaunchMatchEngine() {
        return launchMatchEngine;
    }

    public void setLaunchMatchEngine(String launchMatchEngine) {
        this.launchMatchEngine = launchMatchEngine;
    }
    public String getDatasourceCode() {
        return datasourceCode;
    }

    public void setDatasourcecode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public List<FileType> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<FileType> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
