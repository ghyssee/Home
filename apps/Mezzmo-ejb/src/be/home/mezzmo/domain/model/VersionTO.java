package be.home.mezzmo.domain.model;

import java.util.Date;

public class VersionTO {

    public String version;
    public Date lastUpdated;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
