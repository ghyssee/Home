package be.home.common.model;

import java.util.List;

public class FirefoxProfiles {

    public List<FirefoxSetting> commonFirefoxSettings;
    public List<Computer> computers;

    public class Computer {
        public String id;
        public String name;
        public List<FirefoxInstance> firefoxInstances;
        public GeckoDriverSettings geckoDriverSettings;
    }

    public class FirefoxInstance {
        public String id;
        public String description;
        public String path;
        public List<FirefoxSetting> firefoxSettings;
        public String port;
    }

    public class FirefoxSetting {
        public String key;
        public String value;
    }

    public class GeckoDriverSettings {
        public String path;
        public String log;
    }


}
