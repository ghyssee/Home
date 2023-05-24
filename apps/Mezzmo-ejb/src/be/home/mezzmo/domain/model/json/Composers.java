package be.home.mezzmo.domain.model.json;

import java.util.List;

public class Composers {

        public List<be.home.mezzmo.domain.model.json.Composers.Composer> composers;
    public List<be.home.mezzmo.domain.model.json.Composers.Publisher> publishers;
    public List<Composers.FramePattern> list;

    public abstract class Pattern {
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

        public class Composer extends Pattern {
            public Composer (String id, String pattern){
                this.id = id;
                this.pattern = pattern;
            }
    }

    public class Publisher extends Pattern {

        public Publisher (String id, String pattern){
            this.id = id;
            this.pattern = pattern;
        }


    }
    public class FramePattern {
        public String id;
        public String pattern;
        public String frameId;
        public FramePattern (String id, String frameId, String pattern){
            this.id = id;
            this.frameId = frameId;
            this.pattern = pattern;
        }
        public String getFrameId() {
            return frameId;
        }

        public void setFrameId(String frameId) {
            this.frameId = frameId;
        }

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
