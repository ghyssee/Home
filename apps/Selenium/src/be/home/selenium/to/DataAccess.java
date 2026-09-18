package be.home.selenium.to;


public class DataAccess  {
    String role;
    String securityContext;
    String securityContextValue;

    public String getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(String securityContext) {
        this.securityContext = securityContext;
    }

    public String getSecurityContextValue() {
        return securityContextValue;
    }

    public void setSecurityContextValue(String securityContextValue) {
        this.securityContextValue = securityContextValue;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
