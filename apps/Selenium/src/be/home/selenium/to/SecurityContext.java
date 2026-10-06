package be.home.selenium.to;

import java.util.ArrayList;
import java.util.List;

public class SecurityContext {
    String description;
    List<String> values = new ArrayList<>();

    public SecurityContext(String description){
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getValues(){
        return this.values;
    }

    public void addValue(String value){
       this.values.add(value);
    }




}
