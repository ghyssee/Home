package be.home.mezzmo.domain.model.json;

import java.util.List;

public class Composers {

        public List<be.home.mezzmo.domain.model.json.Composers.Composer> composers;
    public List<be.home.mezzmo.domain.model.json.Composers.Publisher> publishers;

        public class Composer {

            public Composer (String id, String pattern){
                this.id = id;
                this.pattern = pattern;
            }

            public String id;
            public String pattern;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPattern() {
                return pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }

        }
    public class Publisher {

        public Publisher (String id, String pattern){
            this.id = id;
            this.pattern = pattern;
        }

        public String id;
        public String pattern;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

    }
}
