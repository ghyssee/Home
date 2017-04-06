package be.home.domain.model.reconciliation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 6/04/2017.
 */
public class OracleDriver {

    private String scheme;

    private String filename;
    private List<String> schemes = new ArrayList();


    public OracleDriver(String scheme, String filename){
        this.scheme = scheme;
        this.filename = filename;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public List<String> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }

    public OracleDriver addScheme(String scheme){
        this.schemes.add(scheme);
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }



}
