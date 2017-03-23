package be.home.domain.model.reconciliation;

/**
 * Created by ghyssee on 22/03/2017.
 */
public abstract class Base {

    String code;
    String description;

    public Base(String code, String description){
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
