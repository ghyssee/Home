package be.home.selenium.to;

import java.util.ArrayList;
import java.util.List;

public class UMRRequest {

    private String userId;
    private String name;
    private String email;

    private String requestId;

    private List<Role> roles = new ArrayList<>();

    private List<DataAccess> dataAccessList = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(String role){
        Role newRole = new Role();
        newRole.setRole(role);

        roles.add(newRole);
    }

    public List<DataAccess> getDataAccessList() {
        return dataAccessList;
    }

    public void setDataAccessList(List<DataAccess> dataAccessList) {
        this.dataAccessList = dataAccessList;
    }

    public void addDataAccess(String dataAccess){
        DataAccess newDataAccess = new DataAccess();
        newDataAccess.setSecurityContext("Business unit");
        newDataAccess.setSecurityContextValue(dataAccess);
        dataAccessList.add(newDataAccess);

    }
}
