package be.home.domain.model.reconciliation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 22/03/2017.
 */
public class FileType {

    String code;
    String mask;
    String relativePath;
    String description;
    String multipleTypes;
    String unmatchedMethod;
    List<FileRuleSet> listOfRules = new ArrayList<>();

    public FileType(String code, String mask, String relativePath, String description, String unmatchedMethod, List<FileRuleSet> listOfRules){
        this.code = code;
        this.mask = mask;
        this.relativePath = relativePath;
        this.unmatchedMethod = unmatchedMethod;
        this.listOfRules = listOfRules;
        this.description = description;
        this.multipleTypes = "N";
    }


    public String getUnmatchedMethod() {
        return unmatchedMethod;
    }

    public void setUnmatchedMethod(String unmatchedMethod) {
        this.unmatchedMethod = unmatchedMethod;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMultipleTypes() {
        return multipleTypes;
    }

    public void setMultipleTypes(String multipleTypes) {
        this.multipleTypes = multipleTypes;
    }

    public void addRule(FileRuleSet fileRuleSet){
        this.listOfRules.add(fileRuleSet);
    }

    public List<FileRuleSet> getListOfRules() {
        return listOfRules;
    }

    public void setListOfRules(List<FileRuleSet> listOfRules) {
        this.listOfRules = listOfRules;
    }


}
