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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }
}
