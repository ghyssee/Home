package be.home.selenium.bo;

import be.home.selenium.to.*;

import java.util.ArrayList;
import java.util.List;

public class RoleBO {

    public List<CSVRole> csvRoles = new ArrayList<>();

    public RoleBO(List<CSVRole> csvRoles){
        this.csvRoles = csvRoles;
    }


    public List<SecurityContext> getSecurityContexts(String role){
        List<SecurityContext> securityContexts = new ArrayList();
        securityContexts.add(new SecurityContext("Business unit"));
        if (roleRequiresDataAccessSet(role)){
            SecurityContext sc = new SecurityContext("Data access set");
            securityContexts.add(sc);
            // add data accesss sets here
        }
        if (role.equalsIgnoreCase("Receiving all Users bpost")){
            SecurityContext sc = new SecurityContext("Inventory organization");
            sc.addValue("BPJ");
            securityContexts.add(sc);
        }
        return securityContexts;
    }

    public CSVRole getRoleInCSV(String umrRole){
        CSVRole foundRole = null;
        for (CSVRole role : this.csvRoles){
            if (!umrRole.equalsIgnoreCase(role.getOracleRole())){
                if (role.getUmrRole() != null){
                    if (!umrRole.equalsIgnoreCase(role.getUmrRole())){
                        // role not found
                    }
                    else {
                        foundRole = role;
                        break;
                    }
                }
                else{
                    throw new RuntimeException( "Following Role not found in csv: " + umrRole);
                    // role not found
                }
            }
            else {
                foundRole = role;
            }
        }
        return foundRole;
    }
    public boolean roleRequiresDataAccessSet(String role){
        CSVRole csvRole = getRoleInCSV(role);
        if (csvRole.getDataAccessSet().equalsIgnoreCase("Y")){
            return true;
        }
        else {
            return false;
        }
    }

    public List<String> getSecurityContextValues(String securityContext, OracleEntity entity){
       List<String> values = new ArrayList<>();
        if (securityContext.equalsIgnoreCase("BUSINESS UNIT")){
            values.add(entity.getName());
        }
        else if (securityContext.equalsIgnoreCase("DATA ACCESS SET")){
            values.add("PL "+ entity.getCode());
            values.add("SL "+ entity.getCode());
        }
        else if (securityContext.equalsIgnoreCase("REFERENCE DATA SET")){
            values.add("PL "+ entity.getCode());
            values.add("SL "+ entity.getCode());
        }
        return values;
    }

    enum CONTEXT_TYPE {
        BU,
        PL,
        SL,
        PLSL
    }

    public List<String> getSecurityContextValues(List<OracleEntity> oracleEntities, CONTEXT_TYPE contextType){
        List<String> values = new ArrayList<>();
        for (OracleEntity oracleEntity : oracleEntities){
            switch (contextType){
                case BU :
                    values.add(oracleEntity.getName());
                    break;
                case PL :
                    values.add("PL "+ oracleEntity.getCode());
                    break;
                case SL :
                    values.add("SL "+ oracleEntity.getCode());
                    break;
                case PLSL :
                    values.add("PL "+ oracleEntity.getCode());
                    values.add("SL "+ oracleEntity.getCode());
                    break;
            }
        }
        return values;
    }

    public List<String> getSecurityContextValues(String securityContext, List<OracleEntity> oracleEntities){
        List<String> values = new ArrayList<>();
        if (securityContext.equalsIgnoreCase("BUSINESS UNIT")){
            values.addAll(getSecurityContextValues(oracleEntities, CONTEXT_TYPE.BU));
        }
        else if (securityContext.equalsIgnoreCase("DATA ACCESS SET")){
            values.addAll(getSecurityContextValues(oracleEntities, CONTEXT_TYPE.PLSL));
        }
        else if (securityContext.equalsIgnoreCase("REFERENCE DATA SET")){
            values.addAll(getSecurityContextValues(oracleEntities, CONTEXT_TYPE.PLSL));
        }
        return values;
    }
    public List<DataAccess> getDatAccessSet(UMRRequest umrRequest){
        List<Role> umrRoles = umrRequest.getRoles();
        List<OracleEntity> oracleEntities = umrRequest.getEntities();
        List<DataAccess> dataAccessSet = new ArrayList<>();
        for (Role umrRole : umrRoles){
            CSVRole role = getRoleInCSV(umrRole.getRole());
            List<SecurityContext> securityContexts = getSecurityContexts(role.getOracleRole());
            for (SecurityContext securityContext : securityContexts){
                List<String> values = getSecurityContextValues(securityContext.getDescription(), oracleEntities);
                for (String value : values){
                    dataAccessSet.add(new DataAccess(role.getOracleRole(), securityContext.getDescription(), value));
                }
            }
            if (role.getOracleRole().equalsIgnoreCase("Employee_Approver bpost")){
                dataAccessSet.add(new DataAccess(role.getOracleRole(), "Reference data set", "Common Set"));
            }
            else if (role.getOracleRole().equalsIgnoreCase("Receiving all Users")){
                dataAccessSet.add(new DataAccess(role.getOracleRole(), "Inventory organization", "BPJ"));
            }
        }
        return dataAccessSet;

    }

}
