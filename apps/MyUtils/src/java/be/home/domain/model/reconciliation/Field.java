package be.home.domain.model.reconciliation;

public class Field {
    public String name;
    public String type;
    public String length;
    public boolean required;
    public String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getType2(){
        if (this.type.equals("VARCHAR")){
            return "CHAR";
        }
        else if (this.type.equals("VARCHAR2")){
            return "CHAR";
        }
        return "";
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Field(String name, String type, String length, boolean required, String description){
        this.name = name;
        this.type = type;
        this.length = length;
        this.required = required;
        this.description = description;
    }
}