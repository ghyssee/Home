package be.home.domain.model.reconciliation;

import java.util.List;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class Stream extends Base {
    private String inputType;
    private String launchMatchEngine;
    private Datasource datasource;

    private List<FileType> fileTypes;

    public Stream(String description, String inputType, String launchMatchEngine, Datasource datasource, List<FileType> fileTypes){
        super("", description);
        this.inputType = inputType;
        this.launchMatchEngine = launchMatchEngine;
        this.datasource = datasource;
        this.fileTypes = fileTypes;
    }

    public String getLaunchMatchEngine() {
        return launchMatchEngine;
    }

    public void setLaunchMatchEngine(String launchMatchEngine) {
        this.launchMatchEngine = launchMatchEngine;
    }
    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
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


}
