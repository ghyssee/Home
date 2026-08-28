package be.home.selenium.to;

import be.home.common.model.TransferObject;

public class Role extends TransferObject {
    String role;
    String umrRole;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUmrRole() {
        return umrRole;
    }

    public void setUmrRole(String umrRole) {
        this.umrRole = umrRole;
    }
}
