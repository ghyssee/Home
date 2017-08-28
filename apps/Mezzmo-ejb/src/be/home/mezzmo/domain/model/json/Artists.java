package be.home.mezzmo.domain.model.json;

import java.util.List;

/**
 * Created by Gebruiker on 10/03/2017.
 */
public class Artists {

    public List<Artist> list;

    public class Artist {
        public String id;
        public String name;
        public String stageName;
        public String pattern;
        public int priority;
        public boolean global;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStageName() {
            return stageName;
        }

        public void setStageName(String stageName) {
            this.stageName = stageName;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public boolean isGlobal() {
            return global;
        }

        public void setGlobal(boolean global) {
            this.global = global;
        }
    }
}
