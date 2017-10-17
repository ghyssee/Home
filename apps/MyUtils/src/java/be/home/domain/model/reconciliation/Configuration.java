package be.home.domain.model.reconciliation;

import java.util.List;

/**
 * Created by ghyssee on 5/10/2017.
 */
public class Configuration {

    public String code;
    public String description;
    public Datatype datatype;
    public Stream leftStream;
    public Stream rightStream;
    public List<MatchAlgorithm> matchAlgorithms;
    public String role;
    public String userId;
    public List<Datasource> datasources;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void setDatatype(Datatype datatype) {
        this.datatype = datatype;
    }

    public Stream getLeftStream() {
        return leftStream;
    }

    public void setLeftStream(Stream leftStream) {
        this.leftStream = leftStream;
    }

    public Stream getRightStream() {
        return rightStream;
    }

    public void setRightStream(Stream rightStream) {
        this.rightStream = rightStream;
    }

    public List<MatchAlgorithm> getMatchAlgorithms() {
        return matchAlgorithms;
    }

    public void setMatchAlgorithms(List<MatchAlgorithm> matchAlgorithms) {
        this.matchAlgorithms = matchAlgorithms;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Datasource> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<Datasource> datasources) {
        this.datasources = datasources;
    }
}
