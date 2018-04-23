package be.home.model;

/**
 * Created by ghyssee on 23/03/2015.
 */
public class ParamTO {
    public static final boolean REQUIRED = true;
    public static final boolean OPTIONAL = false;

    private String id;
    private String [] description;
    private boolean required;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String description []) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public ParamTO(String id, String [] description, boolean required){
        this.id = id;
        this.description = description;
        this.required = required;
    }

    public ParamTO(String id, String [] description){
        this.id = id;
        this.description = description;
        this.required = OPTIONAL;
    }

    public ParamTO(){
    }
}
